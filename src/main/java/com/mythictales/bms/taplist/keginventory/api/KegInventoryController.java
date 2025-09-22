package com.mythictales.bms.taplist.keginventory.api;

import static com.mythictales.bms.taplist.api.ApiMappers.toDto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mythictales.bms.taplist.api.dto.KegDto;
import com.mythictales.bms.taplist.api.dto.KegInventorySummaryDto;
import com.mythictales.bms.taplist.api.dto.KegInventorySummaryDto.ActivityItem;
import com.mythictales.bms.taplist.api.dto.KegInventorySummaryDto.Hero;
import com.mythictales.bms.taplist.api.dto.KegInventorySummaryDto.QueueItem;
import com.mythictales.bms.taplist.api.dto.KegInventorySummaryDto.QuickAction;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegEvent;
import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.domain.Role;
import com.mythictales.bms.taplist.keginventory.api.dto.AssignRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.KegMovementDto;
import com.mythictales.bms.taplist.keginventory.api.dto.MoveRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.ReceiveRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.ReconciliationDto;
import com.mythictales.bms.taplist.keginventory.api.dto.ReturnRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.StatusCountsDto;
import com.mythictales.bms.taplist.keginventory.repo.KegMovementHistoryRepository;
import com.mythictales.bms.taplist.keginventory.service.KegInventoryService;
import com.mythictales.bms.taplist.repo.KegPlacementRepository;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.KegEventRepository;
import com.mythictales.bms.taplist.security.CurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${bms.keginventory.apiBasePath:/api/v1/keg-inventory}")
@Tag(name = "Keg Inventory")
@Validated
public class KegInventoryController {

  private final KegInventoryService service;
  private final KegMovementHistoryRepository history;
  private final KegRepository kegs;
  private final KegPlacementRepository placements;
  private final KegEventRepository kegEvents;

  public KegInventoryController(
      KegInventoryService service,
      KegMovementHistoryRepository history,
      KegRepository kegs,
      KegPlacementRepository placements,
      KegEventRepository kegEvents) {
    this.service = service;
    this.history = history;
    this.kegs = kegs;
    this.placements = placements;
    this.kegEvents = kegEvents;
  }

  @GetMapping("/summary")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  @Operation(summary = "Enterprise keg inventory summary")
  public KegInventorySummaryDto summary(
      @AuthenticationPrincipal CurrentUser currentUser,
      @RequestParam(value = "breweryId", required = false) Long breweryId,
      @RequestParam(value = "queueSize", defaultValue = "6") int queueSize,
      @RequestParam(value = "activitySize", defaultValue = "5") int activitySize) {

    Long scopedBreweryId = determineBreweryScope(currentUser, breweryId);

    List<Keg> unassigned = kegs.findByBreweryIdAndAssignedVenueIsNull(scopedBreweryId);
    List<Keg> assigned = kegs.findByBreweryIdAndAssignedVenueIsNotNull(scopedBreweryId);
    List<Keg> returned = kegs.findByBreweryIdAndStatus(scopedBreweryId, KegStatus.RETURNED);

    int availableCount =
        (int) unassigned.stream().filter(k -> k.getStatus() != KegStatus.RETURNED).count();
    int distributedCount = assigned.size();
    int returnedCount = returned.size();

    Hero hero = new Hero(availableCount, distributedCount, returnedCount);

    List<QueueItem> queue = buildQueue(unassigned, assigned, returned, queueSize);
    List<ActivityItem> activity = buildActivity(scopedBreweryId, activitySize);
    List<QuickAction> quickActions = buildQuickActions(unassigned, assigned, returned);

    return new KegInventorySummaryDto(hero, queue, activity, quickActions);
  }

  @PostMapping("/assign")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(
      summary = "Assign a keg to a venue (status→DISTRIBUTED)",
      security = {@SecurityRequirement(name = "sessionCookie")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Assigned",
        content =
            @Content(
                schema =
                    @Schema(implementation = com.mythictales.bms.taplist.api.dto.KegDto.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "422", description = "Unsupported transition", content = @Content)
  })
  public ResponseEntity<KegDto> assign(
      @AuthenticationPrincipal CurrentUser user, @RequestBody @Validated AssignRequest req) {
    var keg = service.assignToVenue(user, req.kegId(), req.venueId());
    return ResponseEntity.ok(toDto(keg));
  }

  @PostMapping("/receive")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(
      summary = "Receive a keg at a venue (status=RECEIVED)",
      security = {@SecurityRequirement(name = "sessionCookie")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Received",
        content =
            @Content(
                schema =
                    @Schema(implementation = com.mythictales.bms.taplist.api.dto.KegDto.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  })
  public ResponseEntity<KegDto> receive(
      @AuthenticationPrincipal CurrentUser user, @RequestBody @Validated ReceiveRequest req) {
    var keg = service.receiveAtVenue(user, req.kegId(), req.venueId());
    return ResponseEntity.ok(toDto(keg));
  }

  @PostMapping("/move")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(
      summary = "Move a keg between venues (status→DISTRIBUTED unless RECEIVED)",
      security = {@SecurityRequirement(name = "sessionCookie")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Moved",
        content =
            @Content(
                schema =
                    @Schema(implementation = com.mythictales.bms.taplist.api.dto.KegDto.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "422", description = "Unsupported transition", content = @Content)
  })
  public ResponseEntity<KegDto> move(
      @AuthenticationPrincipal CurrentUser user, @RequestBody @Validated MoveRequest req) {
    var keg = service.move(user, req.kegId(), req.fromVenueId(), req.toVenueId());
    return ResponseEntity.ok(toDto(keg));
  }

  @PostMapping("/return")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(
      summary = "Return a keg to brewery (status=EMPTY, clears assignment)",
      security = {@SecurityRequirement(name = "sessionCookie")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Returned",
        content =
            @Content(
                schema =
                    @Schema(implementation = com.mythictales.bms.taplist.api.dto.KegDto.class))),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  })
  public ResponseEntity<KegDto> returnKeg(
      @AuthenticationPrincipal CurrentUser user, @RequestBody @Validated ReturnRequest req) {
    var keg = service.returnToBrewery(user, req.kegId());
    return ResponseEntity.ok(toDto(keg));
  }

  public record AssignExternalRequest(Long kegId, String partner) {}

  @PostMapping("/assignExternal")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  @Operation(
      summary = "Assign a keg to an external partner (status→DISTRIBUTED)",
      security = {@SecurityRequirement(name = "sessionCookie")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Assigned externally",
        content =
            @Content(
                schema =
                    @Schema(implementation = com.mythictales.bms.taplist.api.dto.KegDto.class))),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "422", description = "Unsupported transition", content = @Content)
  })
  public ResponseEntity<KegDto> assignExternal(
      @AuthenticationPrincipal CurrentUser user, @RequestBody AssignExternalRequest req)
      throws org.springframework.validation.BindException {
    if (req == null || req.kegId() == null || req.partner() == null || req.partner().isBlank()) {
      throw new org.springframework.validation.BindException(new Object(), "assignExternal");
    }
    var keg = service.assignToExternal(user, req.kegId(), req.partner().trim());
    return ResponseEntity.ok(toDto(keg));
  }

  @GetMapping("/history")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(
      summary = "List keg movement history (paged)",
      security = {@SecurityRequirement(name = "sessionCookie")})
  public org.springframework.data.domain.Page<KegMovementDto> history(
      @RequestParam(value = "kegId", required = false) Long kegId,
      @RequestParam(value = "toVenueId", required = false) Long toVenueId,
      @RequestParam(value = "from", required = false) java.time.Instant from,
      @RequestParam(value = "to", required = false) java.time.Instant to,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "sort", defaultValue = "movedAt,desc") String sort) {
    var pageable =
        org.springframework.data.domain.PageRequest.of(
            Math.max(0, page), Math.max(1, Math.min(200, size)), parseSort(sort));
    org.springframework.data.domain.Page<
            com.mythictales.bms.taplist.keginventory.domain.KegMovementHistory>
        data;
    boolean hasRange = from != null && to != null;
    if (kegId != null && hasRange)
      data = history.findByKeg_IdAndMovedAtBetween(kegId, from, to, pageable);
    else if (kegId != null) data = history.findByKeg_Id(kegId, pageable);
    else if (toVenueId != null && hasRange)
      data = history.findByToVenue_IdAndMovedAtBetween(toVenueId, from, to, pageable);
    else if (toVenueId != null) data = history.findByToVenue_Id(toVenueId, pageable);
    else if (hasRange) data = history.findByMovedAtBetween(from, to, pageable);
    else data = history.findAll(pageable);
    return data.map(
        h ->
            new KegMovementDto(
                h.getId(),
                h.getKeg() != null ? h.getKeg().getId() : null,
                h.getFromVenue() != null ? h.getFromVenue().getId() : null,
                h.getToVenue() != null ? h.getToVenue().getId() : null,
                h.getExternalPartner(),
                h.getActorUserId(),
                h.getMovedAt()));
  }

  private org.springframework.data.domain.Sort parseSort(String sortParam) {
    try {
      String[] parts = sortParam.split(",");
      String prop = parts[0];
      String dir = parts.length > 1 ? parts[1] : "desc";
      return "asc".equalsIgnoreCase(dir)
          ? org.springframework.data.domain.Sort.by(prop).ascending()
          : org.springframework.data.domain.Sort.by(prop).descending();
    } catch (Exception e) {
      return org.springframework.data.domain.Sort.by("movedAt").descending();
    }
  }

  @GetMapping("/reconciliation")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  @Operation(summary = "Reconciliation report: basic anomalies")
  public java.util.List<ReconciliationDto> reconcile(
      @RequestParam(value = "breweryId", required = false) Long breweryId) {
    var list = new java.util.ArrayList<ReconciliationDto>();
    java.util.List<com.mythictales.bms.taplist.domain.Keg> all =
        breweryId != null ? kegs.findByBreweryId(breweryId) : kegs.findAll();
    for (var k : all) {
      if (k.getStatus() == com.mythictales.bms.taplist.domain.KegStatus.RECEIVED
          && k.getAssignedVenue() == null) {
        list.add(new ReconciliationDto("ReceivedNoVenue", k.getId(), "No assigned venue"));
      }
      if (k.getStatus() == com.mythictales.bms.taplist.domain.KegStatus.EMPTY
          && k.getAssignedVenue() != null) {
        list.add(
            new ReconciliationDto(
                "EmptyButAssigned", k.getId(), "VenueId=" + k.getAssignedVenue().getId()));
      }
    }
    return list;
  }

  @GetMapping("/reports/statusCounts")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(summary = "Counts by status; optional brewery/venue filters")
  public StatusCountsDto statusCounts(
      @RequestParam(value = "breweryId", required = false) Long breweryId,
      @RequestParam(value = "venueId", required = false) Long venueId) {
    java.util.List<com.mythictales.bms.taplist.domain.Keg> list;
    if (venueId != null) list = kegs.findByAssignedVenueId(venueId);
    else if (breweryId != null) list = kegs.findByBreweryId(breweryId);
    else list = kegs.findAll();
    java.util.Map<String, Long> counts = new java.util.HashMap<>();
    for (var k : list) {
      String s = k.getStatus() != null ? k.getStatus().name() : "UNKNOWN";
      counts.merge(s, 1L, Long::sum);
    }
    return new StatusCountsDto(breweryId, venueId, counts);
  }

  private Long determineBreweryScope(CurrentUser currentUser, Long requestedBreweryId) {
    if (currentUser != null && currentUser.getRole() == Role.BREWERY_ADMIN) {
      Long breweryId = currentUser.getBreweryId();
      if (breweryId == null) {
        throw new AccessDeniedException("Brewery scope required for brewery admin");
      }
      if (requestedBreweryId != null && !Objects.equals(breweryId, requestedBreweryId)) {
        throw new AccessDeniedException("Cannot access another brewery's inventory");
      }
      return breweryId;
    }
    if (currentUser != null && currentUser.getRole() == Role.SITE_ADMIN) {
      if (requestedBreweryId == null) {
        throw new AccessDeniedException("breweryId parameter is required for site admins");
      }
      return requestedBreweryId;
    }
    throw new AccessDeniedException("Insufficient privileges for keg inventory summary");
  }

  private List<QueueItem> buildQueue(
      List<Keg> unassigned, List<Keg> assigned, List<Keg> returned, int queueSize) {
    List<QueueItem> queue = new ArrayList<>();

    unassigned.stream()
        .filter(keg -> keg.getStatus() != KegStatus.RETURNED)
        .limit(queueSize)
        .forEach(keg -> queue.add(toQueueItem(keg, "Ready", "default")));

    if (queue.size() < queueSize) {
      assigned.stream()
          .limit(queueSize - queue.size())
          .forEach(keg -> queue.add(toQueueItem(keg, "Distributed", "warning")));
    }

    if (queue.size() < queueSize) {
      returned.stream()
          .limit(queueSize - queue.size())
          .forEach(keg -> queue.add(toQueueItem(keg, "Returned", "alert")));
    }

    if (queue.isEmpty()) {
      queue.add(new QueueItem(null, "--", "No kegs", "Action", "alert", ""));
    }

    return queue;
  }

  private QueueItem toQueueItem(Keg keg, String statusLabel, String severity) {
    String beerName = keg.getBeer() != null ? keg.getBeer().getName() : "Unknown";
    String location =
        keg.getAssignedVenue() != null
            ? keg.getAssignedVenue().getName()
            : (keg.getBrewery() != null ? keg.getBrewery().getName() : "");
    return new QueueItem(
        keg.getId(),
        safeSerial(keg),
        beerName,
        statusLabel,
        severity,
        location);
  }

  private List<ActivityItem> buildActivity(Long breweryId, int activitySize) {
    var page = kegEvents.findEventsFiltered(breweryId, null, PageRequest.of(0, Math.max(1, activitySize)));
    List<ActivityItem> items = new ArrayList<>();
    DateTimeFormatter iso = DateTimeFormatter.ISO_INSTANT.withLocale(Locale.US);

    page.getContent()
        .forEach(
            event -> {
              String summary = describeEvent(event);
              String actor = event.getActor() != null ? event.getActor().getUsername() : null;
              items.add(
                  new ActivityItem(
                      event.getId(),
                      event.getType() != null ? event.getType().name() : "EVENT",
                      summary,
                      actor,
                      event.getAtTime() != null ? iso.format(event.getAtTime()) : null));
            });

    if (items.isEmpty()) {
      items.add(new ActivityItem(null, "INFO", "No recent keg events", null, null));
    }

    return items;
  }

  private List<QuickAction> buildQuickActions(
      List<Keg> unassigned, List<Keg> assigned, List<Keg> returned) {
    List<QuickAction> actions = new ArrayList<>();

    findFirstKeg(unassigned).ifPresent(
        keg ->
            actions.add(
                new QuickAction(
                    "Assign Keg",
                    null,
                    new QuickAction.Command("assign", keg.getId(), Boolean.TRUE))));

    findFirstKeg(assigned).ifPresent(
        keg ->
            actions.add(
                new QuickAction(
                    "Log Return",
                    null,
                    new QuickAction.Command("return", keg.getId(), Boolean.FALSE))));

    findFirstKeg(returned).ifPresent(
        keg ->
            actions.add(
                new QuickAction(
                    "Schedule Cleaning",
                    null,
                    new QuickAction.Command("clean", keg.getId(), Boolean.FALSE))));

    actions.add(new QuickAction("Create Transfer", "/admin/brewery?tab=assigned", null));
    return actions;
  }

  private java.util.Optional<Keg> findFirstKeg(List<Keg> kegs) {
    return kegs.stream().filter(k -> k != null && k.getId() != null).findFirst();
  }

  private String describeEvent(KegEvent event) {
    String serial = safeSerial(event.getPlacement() != null ? event.getPlacement().getKeg() : null);
    String tapName =
        event.getPlacement() != null && event.getPlacement().getTap() != null
            ? "Tap #" + event.getPlacement().getTap().getNumber()
            : "Tap";
    return switch (event.getType()) {
      case TAP -> serial + " tapped on " + tapName;
      case POUR ->
          (event.getOunces() != null
              ? String.format(Locale.US, "%.1foz poured from %s", event.getOunces(), serial)
              : "Pour recorded for " + serial);
      case BLOW -> serial + " blown on " + tapName;
      case UNTAP -> serial + " untapped from " + tapName;
      default -> "Keg event for " + serial;
    };
  }

  private String safeSerial(Keg keg) {
    if (keg == null) {
      return "--";
    }
    return keg.getSerialNumber() != null ? keg.getSerialNumber() : "Keg " + keg.getId();
  }
}
