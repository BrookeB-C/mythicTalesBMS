package com.mythictales.bms.taplist.keginventory.api;

import static com.mythictales.bms.taplist.api.ApiMappers.toDto;

import org.springframework.http.ResponseEntity;
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
import com.mythictales.bms.taplist.keginventory.api.dto.AssignRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.KegMovementDto;
import com.mythictales.bms.taplist.keginventory.api.dto.MoveRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.ReceiveRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.ReturnRequest;
import com.mythictales.bms.taplist.keginventory.repo.KegMovementHistoryRepository;
import com.mythictales.bms.taplist.keginventory.service.KegInventoryService;
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

  public KegInventoryController(KegInventoryService service, KegMovementHistoryRepository history) {
    this.service = service;
    this.history = history;
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
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "sort", defaultValue = "movedAt,desc") String sort) {
    var pageable =
        org.springframework.data.domain.PageRequest.of(
            Math.max(0, page), Math.max(1, Math.min(200, size)), parseSort(sort));
    org.springframework.data.domain.Page<
            com.mythictales.bms.taplist.keginventory.domain.KegMovementHistory>
        data;
    if (kegId != null) data = history.findByKeg_Id(kegId, pageable);
    else if (toVenueId != null) data = history.findByToVenue_Id(toVenueId, pageable);
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
}
