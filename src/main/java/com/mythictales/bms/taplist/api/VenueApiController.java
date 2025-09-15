package com.mythictales.bms.taplist.api;

import java.util.List;
import java.util.stream.Collectors;

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
  public List<VenueDto> list(@RequestParam(value = "breweryId", required = false) Long breweryId) {
    var list = (breweryId != null) ? venues.findByBreweryId(breweryId) : venues.findAll();
    return list.stream().map(ApiMappers::toDto).collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<VenueDto> get(@PathVariable Long id) {
    return venues
        .findById(id)
        .map(v -> ResponseEntity.ok(ApiMappers.toDto(v)))
        .orElse(ResponseEntity.notFound().build());
  }
}
