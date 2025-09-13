package com.mythictales.bms.taplist.controller;

import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.domain.VenueType;
import com.mythictales.bms.taplist.domain.KegEvent;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.KegEventRepository;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.repo.TaproomRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;
import com.mythictales.bms.taplist.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequestMapping("/admin/taproom")
@PreAuthorize("hasRole('TAPROOM_ADMIN')")
public class AdminTaproomController {

    private final TapRepository taps;
    private final KegRepository kegs;
    private final TaproomRepository taprooms;
    private final VenueRepository venues;
    private final KegEventRepository events;

    public AdminTaproomController(TapRepository taps, KegRepository kegs, TaproomRepository taprooms, VenueRepository venues, KegEventRepository events) {
        this.taps = taps;
        this.kegs = kegs;
        this.taprooms = taprooms;
        this.venues = venues;
        this.events = events;
    }

    @GetMapping
    public String page(@AuthenticationPrincipal CurrentUser user, Model model) {
        if (user == null || user.getTaproomId() == null) {
            model.addAttribute("taps", List.of());
            model.addAttribute("kegs", List.of());
            model.addAttribute("venue", null);
            return "admin/taproom";
        }

        List<Tap> userTaps = taps.findByTaproomId(user.getTaproomId());
        model.addAttribute("taps", userTaps);

        // Determine venue
        Venue venue = null;
        if (!userTaps.isEmpty() && userTaps.get(0).getVenue() != null) {
            venue = userTaps.get(0).getVenue();
        } else {
            // Fallback: resolve a Venue for this taproom via its brewery and type
            taprooms.findById(user.getTaproomId()).ifPresent(tr ->
                venues.findFirstByBreweryIdAndType(tr.getBrewery().getId(), VenueType.TAPROOM)
                      .ifPresent(v -> model.addAttribute("venue", v))
            );
        }
        if (venue != null) {
            model.addAttribute("venue", venue);
            model.addAttribute("kegs", kegs.findByAssignedVenueIdAndStatus(venue.getId(), KegStatus.RECEIVED));
            model.addAttribute("inboundKegs", kegs.findByAssignedVenueIdAndStatus(venue.getId(), KegStatus.DISTRIBUTED));
        } else {
            model.addAttribute("kegs", List.of());
            model.addAttribute("inboundKegs", List.of());
        }
        // Add the most recent event for a quick "last activity" badge
        if (venue != null) {
            try {
                List<KegEvent> evs = events.findVenueEvents(venue.getId());
                if (!evs.isEmpty()) {
                    model.addAttribute("lastEvent", evs.get(0));
                }
            } catch (Exception ignored) { }
        }

        return "admin/taproom";
    }

    @PostMapping("/kegs/{id}/receive")
    @PreAuthorize("hasAnyRole('SITE_ADMIN','BREWERY_ADMIN','TAPROOM_ADMIN','BAR_ADMIN')")
    public String receiveKeg(@PathVariable Long id){
        var keg = kegs.findById(id).orElseThrow();
        keg.setStatus(KegStatus.RECEIVED);
        kegs.save(keg);
        return "redirect:/admin/taproom";
    }
}
