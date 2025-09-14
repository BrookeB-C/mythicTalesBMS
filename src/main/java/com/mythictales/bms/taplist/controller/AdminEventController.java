package com.mythictales.bms.taplist.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mythictales.bms.taplist.repo.KegEventRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;

@Controller
public class AdminEventController {

  private final KegEventRepository events;
  private final VenueRepository venues;

  public AdminEventController(KegEventRepository events, VenueRepository venues) {
    this.events = events;
    this.venues = venues;
  }

  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @GetMapping("/admin/venue/{venueId}/events")
  public String eventsForVenue(@PathVariable Long venueId, Model model) {
    model.addAttribute("venue", venues.findById(venueId).orElse(null));
    model.addAttribute("events", events.findVenueEvents(venueId));
    return "admin/events";
  }
}
