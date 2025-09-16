package com.mythictales.bms.taplist.api;

import static com.mythictales.bms.taplist.api.ApiMappers.toDto;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.KegDto;
import com.mythictales.bms.taplist.domain.*;
import com.mythictales.bms.taplist.repo.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/kegs")
@Tag(name = "Kegs")
public class KegApiController {
  private final KegRepository kegs;
  private final VenueRepository venues;
  private final BreweryRepository breweries;
  private final BeerRepository beers;
  private final KegSizeSpecRepository sizes;

  public KegApiController(
      KegRepository kegs,
      VenueRepository venues,
      BreweryRepository breweries,
      BeerRepository beers,
      KegSizeSpecRepository sizes) {
    this.kegs = kegs;
    this.venues = venues;
    this.breweries = breweries;
    this.beers = beers;
    this.sizes = sizes;
  }

  @GetMapping
  @Operation(summary = "List kegs with optional filters")
  public Page<KegDto> list(
      @RequestParam(value = "breweryId", required = false) Long breweryId,
      @RequestParam(value = "status", required = false) KegStatus status,
      @RequestParam(value = "assignedVenueId", required = false) Long assignedVenueId,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "sort", defaultValue = "serialNumber,asc") String sort) {
    Pageable pageable =
        org.springframework.data.domain.PageRequest.of(
            Math.max(0, page), Math.max(1, Math.min(200, size)), parseSort(sort));
    Page<Keg> pageData;
    if (assignedVenueId != null && status != null)
      pageData = kegs.findByAssignedVenueIdAndStatus(assignedVenueId, status, pageable);
    else if (assignedVenueId != null)
      pageData = kegs.findByAssignedVenueId(assignedVenueId, pageable);
    else if (breweryId != null && status != null)
      pageData = kegs.findByBreweryIdAndStatus(breweryId, status, pageable);
    else if (breweryId != null) pageData = kegs.findByBreweryId(breweryId, pageable);
    else if (status != null) pageData = kegs.findByStatus(status, pageable);
    else pageData = kegs.findAll(pageable);
    return pageData.map(ApiMappers::toDto);
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
      return org.springframework.data.domain.Sort.by("serialNumber").ascending();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<KegDto> get(@PathVariable Long id) {
    return kegs.findById(id)
        .map(k -> ResponseEntity.ok(toDto(k)))
        .orElse(ResponseEntity.notFound().build());
  }

  public record CreateKegRequest(
      Long beerId, Long breweryId, Long sizeSpecId, String serialNumber) {}

  @PostMapping
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  @Operation(summary = "Create a keg (status=EMPTY)")
  public ResponseEntity<KegDto> create(@RequestBody CreateKegRequest req) {
    Beer beer = beers.findById(Objects.requireNonNull(req.beerId())).orElseThrow();
    Brewery brewery = breweries.findById(Objects.requireNonNull(req.breweryId())).orElseThrow();
    KegSizeSpec size = sizes.findById(Objects.requireNonNull(req.sizeSpecId())).orElseThrow();
    Keg k = new Keg(beer, size);
    k.setBrewery(brewery);
    k.setSerialNumber(req.serialNumber());
    k.setStatus(KegStatus.EMPTY);
    Keg saved = kegs.save(k);
    return ResponseEntity.ok(toDto(saved));
  }

  public record DistributeRequest(Long venueId) {}

  @PostMapping("/{id}/distribute")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  public ResponseEntity<KegDto> distribute(
      @PathVariable Long id, @RequestBody DistributeRequest req) {
    Keg keg = kegs.findById(id).orElseThrow();
    Venue v = venues.findById(Objects.requireNonNull(req.venueId())).orElseThrow();
    keg.setAssignedVenue(v);
    // canonicalize status chain to DISTRIBUTED
    if (keg.getStatus() == KegStatus.EMPTY) keg.setStatus(KegStatus.CLEAN);
    if (keg.getStatus() == KegStatus.CLEAN) keg.setStatus(KegStatus.FILLED);
    keg.setStatus(KegStatus.DISTRIBUTED);
    return ResponseEntity.ok(toDto(kegs.save(keg)));
  }

  @PostMapping("/{id}/receive")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  public ResponseEntity<KegDto> receive(@PathVariable Long id) {
    Keg keg = kegs.findById(id).orElseThrow();
    keg.setStatus(KegStatus.RECEIVED);
    return ResponseEntity.ok(toDto(kegs.save(keg)));
  }

  @PostMapping("/{id}/return")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  public ResponseEntity<KegDto> returnKeg(@PathVariable Long id) {
    Keg keg = kegs.findById(id).orElseThrow();
    // Behavior depends on perspective; we adopt brewery-style reset here
    keg.setAssignedVenue(null);
    keg.setStatus(KegStatus.EMPTY);
    if (keg.getSize() != null) keg.setRemainingOunces(keg.getSize().getOunces());
    return ResponseEntity.ok(toDto(kegs.save(keg)));
  }

  @PostMapping("/{id}/clean")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  public ResponseEntity<KegDto> markClean(@PathVariable Long id) {
    Keg keg = kegs.findById(id).orElseThrow();
    keg.setStatus(KegStatus.CLEAN);
    return ResponseEntity.ok(toDto(kegs.save(keg)));
  }
}
