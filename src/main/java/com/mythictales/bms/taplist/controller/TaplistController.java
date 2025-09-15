package com.mythictales.bms.taplist.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mythictales.bms.taplist.domain.Tap;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.security.CurrentUser;
import com.mythictales.bms.taplist.service.TapService;

@Controller
public class TaplistController {
  private final TapRepository taps;
  private final TapService tapService;

  public TaplistController(TapRepository taps, TapService tapService) {
    this.taps = taps;
    this.tapService = tapService;
  }

  @GetMapping("/taplist")
  public String taplist(@AuthenticationPrincipal CurrentUser user, Model model) {
    List<Tap> tapList;
    if (user != null && user.getTaproomId() != null) {
      tapList = taps.findByTaproomId(user.getTaproomId());
    } else if (user != null && user.getBarId() != null) {
      tapList = taps.findByBarId(user.getBarId());
    } else {
      tapList = taps.findAll();
    }
    model.addAttribute("taps", tapList);
    return "taplist/list";
  }

  @PostMapping("/taps/{id}/tapKeg")
  public String tapKeg(
      @AuthenticationPrincipal CurrentUser currentUser,
      @PathVariable Long id,
      @RequestParam("kegId") Long kegId,
      @RequestParam(value = "redirect", required = false) String redirect) {
    tapService.tapKeg(id, kegId, currentUser != null ? currentUser.getId() : null);
    if (redirect != null && redirect.startsWith("/admin/taproom")) return "redirect:" + redirect;
    return "redirect:/taplist";
  }

  @PostMapping("/taps/{id}/pour")
  public String pour(
      @AuthenticationPrincipal CurrentUser currentUser,
      @PathVariable Long id,
      @RequestParam("ounces") double ounces,
      @RequestParam(value = "redirect", required = false) String redirect) {
    // Allow overpour to auto-blow in MVC flow for smoother UX
    tapService.pour(id, ounces, currentUser != null ? currentUser.getId() : null, true);
    if (redirect != null && redirect.startsWith("/admin/taproom")) return "redirect:" + redirect;
    return "redirect:/taplist";
  }

  @PostMapping("/taps/{id}/blow")
  public String blow(
      @AuthenticationPrincipal CurrentUser currentUser,
      @PathVariable Long id,
      @RequestParam(value = "redirect", required = false) String redirect) {
    tapService.blow(id, currentUser != null ? currentUser.getId() : null);
    if (redirect != null && redirect.startsWith("/admin/taproom")) return "redirect:" + redirect;
    return "redirect:/taplist";
  }
}
