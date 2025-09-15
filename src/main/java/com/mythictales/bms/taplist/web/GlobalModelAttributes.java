package com.mythictales.bms.taplist.web;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.domain.*;
import com.mythictales.bms.taplist.repo.*;
import com.mythictales.bms.taplist.security.CurrentUser;

@ControllerAdvice
@Component
public class GlobalModelAttributes {
  private final KegRepository kegs;
  private final TaproomRepository taprooms;
  private final BarRepository bars;
  private final VenueRepository venues;

  public GlobalModelAttributes(
      KegRepository kegs, TaproomRepository taprooms, BarRepository bars, VenueRepository venues) {
    this.kegs = kegs;
    this.taprooms = taprooms;
    this.bars = bars;
    this.venues = venues;
  }

  @ModelAttribute("currentUser")
  public CurrentUser currentUser(@AuthenticationPrincipal CurrentUser user) {
    return user;
  }

  @ModelAttribute("availableKegs")
  public List<Keg> availableKegs(@AuthenticationPrincipal CurrentUser user) {
    try {
      if (user == null) return java.util.List.of();
      // Resolve current venue id based on user's association
      Long venueId = null;
      if (user.getTaproomId() != null) {
        var tr = taprooms.findById(user.getTaproomId());
        if (tr.isPresent()) {
          var v =
              venues.findFirstByBreweryIdAndType(tr.get().getBrewery().getId(), VenueType.TAPROOM);
          if (v.isPresent()) venueId = v.get().getId();
        }
      } else if (user.getBarId() != null) {
        var barOpt = bars.findById(user.getBarId());
        if (barOpt.isPresent()) {
          var vOpt = venues.findFirstByName(barOpt.get().getName());
          if (vOpt.isPresent()) venueId = vOpt.get().getId();
        }
      }
      if (venueId == null) return java.util.List.of();
      return kegs.findByAssignedVenueIdAndStatus(venueId, KegStatus.RECEIVED);
    } catch (Exception e) {
      return java.util.List.of();
    }
  }

  @ModelAttribute("untappedKegs")
  public List<Keg> untappedKegs(@AuthenticationPrincipal CurrentUser user) {
    return availableKegs(user);
  }
}
