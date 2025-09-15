package com.mythictales.bms.taplist.api;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.VenueDto;
import com.mythictales.bms.taplist.repo.VenueRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/venues")
@Tag(name = "Venues")
public class VenueApiController {
  private final VenueRepository venues;

  public VenueApiController(VenueRepository venues) {
    this.venues = venues;
  }

  @GetMapping
  public Page<VenueDto> list(
      @RequestParam(value = "breweryId", required = false) Long breweryId,
      @ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
    return (breweryId != null)
        ? venues.findByBreweryId(breweryId, pageable).map(ApiMappers::toDto)
        : venues.findAll(pageable).map(ApiMappers::toDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<VenueDto> get(@PathVariable Long id) {
    return venues
        .findById(id)
        .map(v -> ResponseEntity.ok(ApiMappers.toDto(v)))
        .orElse(ResponseEntity.notFound().build());
  }
}
