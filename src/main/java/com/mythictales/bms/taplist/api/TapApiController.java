package com.mythictales.bms.taplist.api;

import static com.mythictales.bms.taplist.api.ApiMappers.toDto;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.BlowRequestDto;
import com.mythictales.bms.taplist.api.dto.PourRequestDto;
import com.mythictales.bms.taplist.api.dto.TapDto;
import com.mythictales.bms.taplist.api.dto.TapKegRequestDto;
import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.security.AccessPolicy;
import com.mythictales.bms.taplist.security.CurrentUser;
import com.mythictales.bms.taplist.service.TapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Taps")
public class TapApiController {

  private final TapRepository taps;
  private final TapService tapService;
  private final KegRepository kegs;
  private final AccessPolicy accessPolicy;

  public TapApiController(
      TapRepository taps, TapService tapService, KegRepository kegs, AccessPolicy accessPolicy) {
    this.taps = taps;
    this.tapService = tapService;
    this.kegs = kegs;
    this.accessPolicy = accessPolicy;
  }

  @GetMapping("/taps")
  @Operation(
      summary = "List taps for context or by explicit filter (paginated)",
      description =
          "If no filter is provided, uses the authenticated user's affiliation (taproom, bar, or brewery). Defaults to sort by number.")
  public org.springframework.data.domain.Page<TapDto> listTaps(
      @Parameter(description = "Filter by venue id")
          @RequestParam(value = "venueId", required = false)
          Long venueId,
      @Parameter(description = "Filter by taproom id")
          @RequestParam(value = "taproomId", required = false)
          Long taproomId,
      @Parameter(description = "Filter by bar id") @RequestParam(value = "barId", required = false)
          Long barId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "sort", defaultValue = "number,asc") String sort) {
    var pageable =
        org.springframework.data.domain.PageRequest.of(
            Math.max(0, page), Math.max(1, Math.min(200, size)), parseSort(sort));
    org.springframework.data.domain.Page<Tap> data;
    CurrentUser user = currentUser();
    if (venueId != null) data = taps.findByVenueId(venueId, pageable);
    else if (taproomId != null) data = taps.findByTaproomId(taproomId, pageable);
    else if (barId != null) data = taps.findByBarId(barId, pageable);
    else if (user != null && user.getTaproomId() != null)
      data = taps.findByTaproomId(user.getTaproomId(), pageable);
    else if (user != null && user.getBarId() != null)
      data = taps.findByBarId(user.getBarId(), pageable);
    else if (user != null && user.getBreweryId() != null)
      data = taps.findByVenueBreweryId(user.getBreweryId(), pageable);
    else data = taps.findAll(pageable);

    return data.map(ApiMappers::toDto);
  }

  private org.springframework.data.domain.Sort parseSort(String sortParam) {
    try {
      String[] parts = sortParam.split(",");
      String prop = parts[0];
      String dir = parts.length > 1 ? parts[1] : "asc";
      return "desc".equalsIgnoreCase(dir)
          ? org.springframework.data.domain.Sort.by(prop).descending()
          : org.springframework.data.domain.Sort.by(prop).ascending();
    } catch (Exception e) {
      return org.springframework.data.domain.Sort.by("number").ascending();
    }
  }

  // access filtering is applied at higher layers; list endpoint returns mapped Page directly

  @PostMapping("/taps/{id}/tap-keg")
  @Operation(summary = "Place a keg on a tap")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Tap updated",
        content =
            @Content(
                schema =
                    @Schema(implementation = com.mythictales.bms.taplist.api.dto.TapDto.class))),
    @ApiResponse(responseCode = "403", description = "Forbidden"),
    @ApiResponse(responseCode = "404", description = "Not found"),
    @ApiResponse(responseCode = "409", description = "Optimistic lock conflict"),
    @ApiResponse(responseCode = "422", description = "Business validation error")
  })
  public ResponseEntity<?> tapKeg(
      @PathVariable("id") Long tapId, @Valid @RequestBody TapKegRequestDto body) {
    Tap tap = taps.findById(tapId).orElseThrow();
    CurrentUser currentUser = currentUser();
    if (currentUser != null) {
      accessPolicy.ensureCanWriteTap(currentUser, tap);
    }
    Long actor =
        body.actorUserId() != null
            ? body.actorUserId()
            : (currentUser != null ? currentUser.getId() : null);
    if (body.expectedVersion() != null && !body.expectedVersion().equals(tap.getVersion())) {
      java.util.Map<String, Object> conflict = new java.util.LinkedHashMap<>();
      conflict.put("status", 409);
      conflict.put("error", "Conflict");
      return ResponseEntity.status(409).body(conflict);
    }
    tapService.tapKeg(tapId, body.kegId(), actor);
    return taps.findById(tapId)
        .map(t -> ResponseEntity.ok(toDto(t)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/taps/{id}/pour")
  @Operation(summary = "Pour ounces from a tap's keg")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Keg updated or empty body if blown",
        content =
            @Content(
                schema =
                    @Schema(implementation = com.mythictales.bms.taplist.api.dto.KegDto.class))),
    @ApiResponse(responseCode = "403", description = "Forbidden"),
    @ApiResponse(responseCode = "404", description = "Not found"),
    @ApiResponse(responseCode = "409", description = "Optimistic lock conflict"),
    @ApiResponse(responseCode = "422", description = "Business validation error")
  })
  public ResponseEntity<?> pour(
      @PathVariable("id") Long tapId, @Valid @RequestBody PourRequestDto req) {
    Tap tap = taps.findById(tapId).orElseThrow();
    CurrentUser currentUser = currentUser();
    if (currentUser != null) {
      accessPolicy.ensureCanWriteTap(currentUser, tap);
    }
    Long actor =
        req.actorUserId() != null
            ? req.actorUserId()
            : (currentUser != null ? currentUser.getId() : null);
    if (req.expectedVersion() != null && !req.expectedVersion().equals(tap.getVersion())) {
      java.util.Map<String, Object> conflict = new java.util.LinkedHashMap<>();
      conflict.put("status", 409);
      conflict.put("error", "Conflict");
      return ResponseEntity.status(409).body(conflict);
    }
    tapService.pour(tapId, req.ounces(), actor, req.allowOverpourToBlow());
    return taps.findById(tapId)
        .map(Tap::getKeg)
        .<ResponseEntity<?>>map(k -> ResponseEntity.ok(toDto(k)))
        .orElse(ResponseEntity.ok().build()); // if keg blew and detached, empty body
  }

  @PostMapping("/taps/{id}/blow")
  @Operation(summary = "Mark the current keg as blown and detach it")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Tap updated",
        content =
            @Content(
                schema =
                    @Schema(implementation = com.mythictales.bms.taplist.api.dto.TapDto.class))),
    @ApiResponse(responseCode = "403", description = "Forbidden"),
    @ApiResponse(responseCode = "404", description = "Not found"),
    @ApiResponse(responseCode = "409", description = "Optimistic lock conflict")
  })
  public ResponseEntity<?> blow(
      @PathVariable("id") Long tapId, @RequestBody(required = false) BlowRequestDto req) {
    Tap tap = taps.findById(tapId).orElseThrow();
    CurrentUser currentUser = currentUser();
    if (currentUser != null) {
      accessPolicy.ensureCanWriteTap(currentUser, tap);
    }
    Long actor =
        req != null && req.actorUserId() != null
            ? req.actorUserId()
            : (currentUser != null ? currentUser.getId() : null);
    if (req != null
        && req.expectedVersion() != null
        && !req.expectedVersion().equals(tap.getVersion())) {
      java.util.Map<String, Object> conflict = new java.util.LinkedHashMap<>();
      conflict.put("status", 409);
      conflict.put("error", "Conflict");
      return ResponseEntity.status(409).body(conflict);
    }
    tapService.blow(tapId, actor);
    return taps.findById(tapId)
        .<ResponseEntity<?>>map(t -> ResponseEntity.ok(toDto(t)))
        .orElse(ResponseEntity.notFound().build());
  }

  private CurrentUser currentUser() {
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth == null) return null;
      Object p = auth.getPrincipal();
      return (p instanceof CurrentUser cu) ? cu : null;
    } catch (Exception ignored) {
      return null;
    }
  }
}
