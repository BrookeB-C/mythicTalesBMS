package com.mythictales.bms.taplist.api;

import java.util.Objects;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.TaproomDto;
import com.mythictales.bms.taplist.domain.Brewery;
import com.mythictales.bms.taplist.domain.Taproom;
import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.domain.VenueType;
import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.TaproomRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/taprooms")
@Tag(name = "Taprooms")
public class TaproomApiController {
  private final TaproomRepository taprooms;
  private final BreweryRepository breweries;
  private final VenueRepository venues;

  public TaproomApiController(
      TaproomRepository taprooms, BreweryRepository breweries, VenueRepository venues) {
    this.taprooms = taprooms;
    this.breweries = breweries;
    this.venues = venues;
  }

  @GetMapping
  public Page<TaproomDto> list(
      @RequestParam(value = "breweryId", required = false) Long breweryId,
      @ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
    return (breweryId != null)
        ? taprooms.findByBreweryId(breweryId, pageable).map(ApiMappers::toDto)
        : taprooms.findAll(pageable).map(ApiMappers::toDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaproomDto> get(@PathVariable Long id) {
    return taprooms
        .findById(id)
        .map(tr -> ResponseEntity.ok(ApiMappers.toDto(tr)))
        .orElse(ResponseEntity.notFound().build());
  }

  public record CreateTaproomRequest(String name, Long breweryId) {}

  @PostMapping
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  public ResponseEntity<TaproomDto> create(@RequestBody CreateTaproomRequest req) {
    Brewery b = breweries.findById(Objects.requireNonNull(req.breweryId())).orElseThrow();
    Taproom tr = new Taproom(Objects.requireNonNull(req.name()).trim(), b);
    Taproom saved = taprooms.save(tr);
    venues.save(new Venue(saved.getName(), VenueType.TAPROOM, b));
    return ResponseEntity.ok(ApiMappers.toDto(saved));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!taprooms.existsById(id)) return ResponseEntity.notFound().build();
    try {
      taprooms.deleteById(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(409).build();
    }
  }
}
