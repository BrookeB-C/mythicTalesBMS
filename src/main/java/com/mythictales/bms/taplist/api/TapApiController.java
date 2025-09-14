package com.mythictales.bms.taplist.api;

import static com.mythictales.bms.taplist.api.ApiMappers.toDto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.KegDto;
import com.mythictales.bms.taplist.api.dto.TapDto;
import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.security.CurrentUser;
import com.mythictales.bms.taplist.service.TapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Taps")
public class TapApiController {

  private final TapRepository taps;
  private final TapService tapService;
  private final KegRepository kegs;

  public TapApiController(TapRepository taps, TapService tapService, KegRepository kegs) {
    this.taps = taps;
    this.tapService = tapService;
    this.kegs = kegs;
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
    else if (user != null && user.getTaproomId() != null)
      list = taps.findByTaproomId(user.getTaproomId());
    else if (user != null && user.getBarId() != null) list = taps.findByBarId(user.getBarId());
    else list = taps.findAll();
    return list.stream().map(ApiMappers::toDto).collect(Collectors.toList());
  }

  public record TapKegRequest(Long kegId, Long actorUserId) {}

  @PostMapping("/taps/{id}/tap-keg")
  @Operation(summary = "Place a keg on a tap")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  public ResponseEntity<TapDto> tapKeg(
      @PathVariable("id") Long tapId,
      @RequestBody TapKegRequest body,
      @AuthenticationPrincipal CurrentUser currentUser) {
    Long actor =
        body != null && body.actorUserId != null
            ? body.actorUserId
            : (currentUser != null ? currentUser.getId() : null);
    tapService.tapKeg(tapId, body.kegId, actor);
    return taps.findById(tapId)
        .map(t -> ResponseEntity.ok(toDto(t)))
        .orElse(ResponseEntity.notFound().build());
  }

  public record PourRequest(double ounces, Long actorUserId) {}

  @PostMapping("/taps/{id}/pour")
  @Operation(summary = "Pour ounces from a tap's keg")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  public ResponseEntity<KegDto> pour(
      @PathVariable("id") Long tapId,
      @RequestBody PourRequest req,
      @AuthenticationPrincipal CurrentUser currentUser) {
    Long actor =
        req != null && req.actorUserId != null
            ? req.actorUserId
            : (currentUser != null ? currentUser.getId() : null);
    tapService.pour(tapId, req.ounces, actor);
    return taps.findById(tapId)
        .map(Tap::getKeg)
        .map(k -> ResponseEntity.ok(toDto(k)))
        .orElse(ResponseEntity.ok().build()); // if keg blew and detached, empty body
  }

  public record BlowRequest(Long actorUserId) {}

  @PostMapping("/taps/{id}/blow")
  @Operation(summary = "Mark the current keg as blown and detach it")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  public ResponseEntity<TapDto> blow(
      @PathVariable("id") Long tapId,
      @RequestBody(required = false) BlowRequest req,
      @AuthenticationPrincipal CurrentUser currentUser) {
    Long actor =
        req != null && req.actorUserId != null
            ? req.actorUserId
            : (currentUser != null ? currentUser.getId() : null);
    tapService.blow(tapId, actor);
    return taps.findById(tapId)
        .map(t -> ResponseEntity.ok(toDto(t)))
        .orElse(ResponseEntity.notFound().build());
  }
}
