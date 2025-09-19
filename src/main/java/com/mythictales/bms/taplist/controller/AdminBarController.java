package com.mythictales.bms.taplist.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.security.CurrentUser;

@Controller
@RequestMapping("/admin/bar")
public class AdminBarController {
  private final TapRepository taps;

  public AdminBarController(TapRepository taps) {
    this.taps = taps;
  }

  @GetMapping
  public String page(@AuthenticationPrincipal CurrentUser user, Model model) {
    List<Tap> tapList = List.of();
    if (user != null && user.getBarId() != null) {
      tapList = taps.findByBarId(user.getBarId());
    }

    long activeTaps =
        tapList.stream().filter(t -> t.getKeg() != null).count();
    long lowAlerts =
        tapList.stream()
            .filter(t -> t.getKeg() != null)
            .filter(t -> t.getKeg().getFillPercent() <= 10)
            .count();

    model.addAttribute("taps", tapList);
    model.addAttribute("tapCount", tapList.size());
    model.addAttribute("activeTapCount", activeTaps);
    model.addAttribute("lowTapAlertCount", lowAlerts);
    return "admin/bar";
  }
}
