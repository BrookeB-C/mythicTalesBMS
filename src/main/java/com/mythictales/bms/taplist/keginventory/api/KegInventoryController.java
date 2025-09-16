package com.mythictales.bms.taplist.keginventory.api;

import static com.mythictales.bms.taplist.api.ApiMappers.toDto;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mythictales.bms.taplist.api.dto.KegDto;
import com.mythictales.bms.taplist.keginventory.api.dto.AssignRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.MoveRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.ReceiveRequest;
import com.mythictales.bms.taplist.keginventory.api.dto.ReturnRequest;
import com.mythictales.bms.taplist.keginventory.service.KegInventoryService;
import com.mythictales.bms.taplist.security.CurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("${bms.keginventory.apiBasePath:/api/v1/keg-inventory}")
@Tag(name = "Keg Inventory")
@Validated
public class KegInventoryController {

  private final KegInventoryService service;

  public KegInventoryController(KegInventoryService service) {
    this.service = service;
  }

  @PostMapping("/assign")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(summary = "Assign a keg to a venue (status→DISTRIBUTED)")
  public ResponseEntity<KegDto> assign(
      @AuthenticationPrincipal CurrentUser user, @RequestBody @Validated AssignRequest req) {
    var keg = service.assignToVenue(user, req.kegId(), req.venueId());
    return ResponseEntity.ok(toDto(keg));
  }

  @PostMapping("/receive")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(summary = "Receive a keg at a venue (status=RECEIVED)")
  public ResponseEntity<KegDto> receive(
      @AuthenticationPrincipal CurrentUser user, @RequestBody @Validated ReceiveRequest req) {
    var keg = service.receiveAtVenue(user, req.kegId(), req.venueId());
    return ResponseEntity.ok(toDto(keg));
  }

  @PostMapping("/move")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(summary = "Move a keg between venues (status→DISTRIBUTED unless RECEIVED)")
  public ResponseEntity<KegDto> move(
      @AuthenticationPrincipal CurrentUser user, @RequestBody @Validated MoveRequest req) {
    var keg = service.move(user, req.kegId(), req.fromVenueId(), req.toVenueId());
    return ResponseEntity.ok(toDto(keg));
  }

  @PostMapping("/return")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @Operation(summary = "Return a keg to brewery (status=EMPTY, clears assignment)")
  public ResponseEntity<KegDto> returnKeg(
      @AuthenticationPrincipal CurrentUser user, @RequestBody @Validated ReturnRequest req) {
    var keg = service.returnToBrewery(user, req.kegId());
    return ResponseEntity.ok(toDto(keg));
  }
}
