package com.mythictales.bms.taplist.controller;

import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        model.addAttribute("taps", taps.findByVenueId(venueId));
        return "admin/venue";
    }
}

