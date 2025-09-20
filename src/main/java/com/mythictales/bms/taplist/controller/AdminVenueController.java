package com.mythictales.bms.taplist.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;

@Controller
public class AdminVenueController {

  private final VenueRepository venues;
  private final TapRepository taps;

  public AdminVenueController(VenueRepository venues, TapRepository taps) {
    this.venues = venues;
    this.taps = taps;
  }

  @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
  @GetMapping("/admin/venue/{venueId}")
  public String venueAdmin(@PathVariable Long venueId, Model model) {
    model.addAttribute("venue", venues.findById(venueId).orElse(null));
    List<Tap> tapList = taps.findByVenueId(venueId);
    long activeTaps = tapList.stream().filter(t -> t.getKeg() != null).count();
    long lowAlerts =
        tapList.stream()
            .filter(t -> t.getKeg() != null)
            .filter(t -> t.getKeg().getFillPercent() <= 10)
            .count();

    model.addAttribute("taps", tapList);
    model.addAttribute("tapCount", tapList.size());
    model.addAttribute("activeTapCount", activeTaps);
    model.addAttribute("lowTapAlertCount", lowAlerts);
    return "admin/venue";
  }
}
