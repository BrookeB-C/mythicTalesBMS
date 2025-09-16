package com.mythictales.bms.taplist.api;

import org.springframework.data.domain.Page;
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
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size,
      @RequestParam(value = "sort", defaultValue = "name,asc") String sort) {
    var pageable =
        org.springframework.data.domain.PageRequest.of(
            Math.max(0, page), Math.max(1, Math.min(200, size)), parseSort(sort));
    return (breweryId != null)
        ? venues.findByBreweryId(breweryId, pageable).map(ApiMappers::toDto)
        : venues.findAll(pageable).map(ApiMappers::toDto);
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
      return org.springframework.data.domain.Sort.by("name").ascending();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<VenueDto> get(@PathVariable Long id) {
    return venues
        .findById(id)
        .map(v -> ResponseEntity.ok(ApiMappers.toDto(v)))
        .orElse(ResponseEntity.notFound().build());
  }
}
