package com.mythictales.bms.taplist.api;

import static com.mythictales.bms.taplist.api.ApiMappers.toDto;

import java.util.List;
import java.util.stream.Collectors;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.BlowRequestDto;
import com.mythictales.bms.taplist.api.dto.KegDto;
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
@Validated
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
      @AuthenticationPrincipal CurrentUser user,
      @Parameter(description = "Filter by venue id")
          @RequestParam(value = "venueId", required = false)
          Long venueId,
      @Parameter(description = "Filter by taproom id")
          @RequestParam(value = "taproomId", required = false)
          Long taproomId,
      @Parameter(description = "Filter by bar id") @RequestParam(value = "barId", required = false)
          Long barId,
      @org.springframework.data.web.PageableDefault(sort = "number") @ParameterObject
          org.springframework.data.domain.Pageable pageable) {
    org.springframework.data.domain.Page<Tap> page;
    if (venueId != null) page = taps.findByVenueId(venueId, pageable);
    else if (taproomId != null) page = taps.findByTaproomId(taproomId, pageable);
    else if (barId != null) page = taps.findByBarId(barId, pageable);
    else if (user != null && user.getTaproomId() != null)
      page = taps.findByTaproomId(user.getTaproomId(), pageable);
    else if (user != null && user.getBarId() != null)
      page = taps.findByBarId(user.getBarId(), pageable);
    else if (user != null && user.getBreweryId() != null)
      page = taps.findByVenueBreweryId(user.getBreweryId(), pageable);
    else page = taps.findAll(pageable);

    // Enforce read scope filtering post-query (best effort)
    List<Tap> filtered =
        page.getContent().stream()
            .filter(t -> user == null || isAllowedRead(user, t))
            .collect(Collectors.toList());
    return new org.springframework.data.domain.PageImpl<>(
        filtered, pageable, page.getTotalElements());
  }

  private boolean isAllowedRead(CurrentUser user, Tap tap) {
    try {
      accessPolicy.ensureCanReadTap(user, tap);
      return true;
    } catch (org.springframework.security.access.AccessDeniedException ex) {
      return false;
    }
  }

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
  public ResponseEntity<TapDto> tapKeg(
      @PathVariable("id") Long tapId,
      @Valid @RequestBody TapKegRequestDto body,
      @AuthenticationPrincipal CurrentUser currentUser) {
    Tap tap = taps.findById(tapId).orElseThrow();
    accessPolicy.ensureCanWriteTap(currentUser, tap);
    Long actor =
        body.actorUserId() != null
            ? body.actorUserId()
            : (currentUser != null ? currentUser.getId() : null);
    if (body.expectedVersion() != null && !body.expectedVersion().equals(tap.getVersion())) {
      throw new OptimisticLockingFailureException("Tap version conflict");
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
  public ResponseEntity<KegDto> pour(
      @PathVariable("id") Long tapId,
      @Valid @RequestBody PourRequestDto req,
      @AuthenticationPrincipal CurrentUser currentUser) {
    Tap tap = taps.findById(tapId).orElseThrow();
    accessPolicy.ensureCanWriteTap(currentUser, tap);
    Long actor =
        req.actorUserId() != null
            ? req.actorUserId()
            : (currentUser != null ? currentUser.getId() : null);
    if (req.expectedVersion() != null && !req.expectedVersion().equals(tap.getVersion())) {
      throw new OptimisticLockingFailureException("Tap version conflict");
    }
    tapService.pour(tapId, req.ounces(), actor, req.allowOverpourToBlow());
    return taps.findById(tapId)
        .map(Tap::getKeg)
        .map(k -> ResponseEntity.ok(toDto(k)))
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
  public ResponseEntity<TapDto> blow(
      @PathVariable("id") Long tapId,
      @RequestBody(required = false) BlowRequestDto req,
      @AuthenticationPrincipal CurrentUser currentUser) {
    Tap tap = taps.findById(tapId).orElseThrow();
    accessPolicy.ensureCanWriteTap(currentUser, tap);
    Long actor =
        req != null && req.actorUserId() != null
            ? req.actorUserId()
            : (currentUser != null ? currentUser.getId() : null);
    if (req != null
        && req.expectedVersion() != null
        && !req.expectedVersion().equals(tap.getVersion())) {
      throw new OptimisticLockingFailureException("Tap version conflict");
    }
    tapService.blow(tapId, actor);
    return taps.findById(tapId)
        .map(t -> ResponseEntity.ok(toDto(t)))
        .orElse(ResponseEntity.notFound().build());
  }
}
