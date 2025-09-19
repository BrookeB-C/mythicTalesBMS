package com.mythictales.bms.taplist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.repo.BarRepository;
import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.TaproomRepository;

@Controller
@RequestMapping("/admin/site")
public class AdminSiteController {
  private final BreweryRepository breweries;
  private final BarRepository bars;
  private final TaproomRepository taprooms;
  private final KegRepository kegs;

  public AdminSiteController(
      BreweryRepository breweries,
      BarRepository bars,
      TaproomRepository taprooms,
      KegRepository kegs) {
    this.breweries = breweries;
    this.bars = bars;
    this.taprooms = taprooms;
    this.kegs = kegs;
  }

  @GetMapping
  public String page(Model model) {
    model.addAttribute("breweries", breweries.findAll());
    model.addAttribute("bars", bars.findAll());
    model.addAttribute("breweryCount", breweries.count());
    model.addAttribute("barCount", bars.count());
    model.addAttribute("taproomCount", taprooms.count());
    model.addAttribute(
        "tappedKegCount", kegs.findByStatus(KegStatus.TAPPED).size());
    model.addAttribute(
        "distributedKegCount", kegs.findByStatus(KegStatus.DISTRIBUTED).size());
    return "admin/site";
  }
}
