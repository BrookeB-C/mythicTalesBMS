package com.mythictales.bms.taplist.api;

import static com.mythictales.bms.taplist.api.ApiMappers.toDto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import com.mythictales.bms.taplist.api.dto.KegDto;
import com.mythictales.bms.taplist.api.dto.TapDto;
import com.mythictales.bms.taplist.api.dto.TapKegRequestDto;
import com.mythictales.bms.taplist.api.dto.PourRequestDto;
import com.mythictales.bms.taplist.api.dto.BlowRequestDto;
import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.security.CurrentUser;
import com.mythictales.bms.taplist.service.TapService;
import com.mythictales.bms.taplist.security.AccessPolicy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Taps")
@Validated
public class TapApiController {

  private final TapRepository taps;
  private final TapService tapService;
  private final KegRepository kegs;
  private final AccessPolicy accessPolicy;

  public TapApiController(TapRepository taps, TapService tapService, KegRepository kegs, AccessPolicy accessPolicy) {
    this.taps = taps;
    this.tapService = tapService;
    this.kegs = kegs;
    this.accessPolicy = accessPolicy;
  }

  @GetMapping("/taps")
  @Operation(summary = "List taps for context or by explicit filter")
  public List<TapDto> listTaps(
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam(value = "venueId", required = false) Long venueId,
      @RequestParam(value = "taproomId", required = false) Long taproomId,
      @RequestParam(value = "barId", required = false) Long barId) {
    List<Tap> list;
    if (venueId != null) list = taps.findByVenueId(venueId);
    else if (taproomId != null) list = taps.findByTaproomId(taproomId);
    else if (barId != null) list = taps.findByBarId(barId);
    else if (user != null && user.getTaproomId() != null) list = taps.findByTaproomId(user.getTaproomId());
    else if (user != null && user.getBarId() != null) list = taps.findByBarId(user.getBarId());
    else if (user != null && user.getBreweryId() != null) list = taps.findByVenueBreweryId(user.getBreweryId());
    else list = taps.findAll();
    // Enforce read scope filtering
    if (user != null) {
      list = list.stream()
          .filter(t -> isAllowedRead(user, t))
          .collect(Collectors.toList());
    }
    return list.stream().map(ApiMappers::toDto).collect(Collectors.toList());
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
  public ResponseEntity<TapDto> tapKeg(
      @PathVariable("id") Long tapId,
      @Valid @RequestBody TapKegRequestDto body,
      @AuthenticationPrincipal CurrentUser currentUser) {
    Tap tap = taps.findById(tapId).orElseThrow();
    accessPolicy.ensureCanWriteTap(currentUser, tap);
    Long actor = body.actorUserId() != null ? body.actorUserId() : (currentUser != null ? currentUser.getId() : null);
    tapService.tapKeg(tapId, body.kegId(), actor);
    return taps.findById(tapId)
        .map(t -> ResponseEntity.ok(toDto(t)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/taps/{id}/pour")
  @Operation(summary = "Pour ounces from a tap's keg")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  public ResponseEntity<KegDto> pour(
      @PathVariable("id") Long tapId,
      @Valid @RequestBody PourRequestDto req,
      @AuthenticationPrincipal CurrentUser currentUser) {
    Tap tap = taps.findById(tapId).orElseThrow();
    accessPolicy.ensureCanWriteTap(currentUser, tap);
    Long actor = req.actorUserId() != null ? req.actorUserId() : (currentUser != null ? currentUser.getId() : null);
    tapService.pour(tapId, req.ounces(), actor, req.allowOverpourToBlow());
    return taps.findById(tapId)
        .map(Tap::getKeg)
        .map(k -> ResponseEntity.ok(toDto(k)))
        .orElse(ResponseEntity.ok().build()); // if keg blew and detached, empty body
  }

  @PostMapping("/taps/{id}/blow")
  @Operation(summary = "Mark the current keg as blown and detach it")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
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
    tapService.blow(tapId, actor);
    return taps.findById(tapId)
        .map(t -> ResponseEntity.ok(toDto(t)))
        .orElse(ResponseEntity.notFound().build());
  }
}
