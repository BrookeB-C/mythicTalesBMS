package com.mythictales.bms.taplist.api;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.api.dto.KegEventDto;
import com.mythictales.bms.taplist.repo.KegEventRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Events")
public class EventApiController {
  private final KegEventRepository events;
  private final VenueRepository venues;

  public EventApiController(KegEventRepository events, VenueRepository venues) {
    this.events = events;
    this.venues = venues;
  }

  @GetMapping("/venues/{venueId}/events")
  public ResponseEntity<Page<KegEventDto>> list(
      @PathVariable Long venueId,
      @ParameterObject
          @PageableDefault(
              sort = "atTime",
              direction = org.springframework.data.domain.Sort.Direction.DESC)
          Pageable pageable) {
    if (!venues.existsById(venueId)) return ResponseEntity.notFound().build();
    var page = events.findVenueEvents(venueId, pageable).map(ApiMappers::toDto);
    return ResponseEntity.ok(page);
  }
}
