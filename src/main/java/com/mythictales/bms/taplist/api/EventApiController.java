package com.mythictales.bms.taplist.api;

import java.util.List;
import java.util.stream.Collectors;

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
  public ResponseEntity<List<KegEventDto>> list(@PathVariable Long venueId) {
    if (!venues.existsById(venueId)) return ResponseEntity.notFound().build();
    var list =
        events.findVenueEvents(venueId).stream()
            .map(ApiMappers::toDto)
            .collect(Collectors.toList());
    return ResponseEntity.ok(list);
  }
}
