package com.mythictales.bms.taplist.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.mythictales.bms.taplist.catalog.domain.BjcpStyle;
import com.mythictales.bms.taplist.catalog.repo.BjcpStyleRepository;
import com.mythictales.bms.taplist.domain.Beer;
import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.repo.BeerRepository;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.security.CurrentUser;

@Controller
@RequestMapping("/admin/beer")
public class AdminBeerController {
  private final BeerRepository beers;
  private final KegRepository kegs;
  private final BjcpStyleRepository styles;

  public AdminBeerController(BeerRepository beers, KegRepository kegs, BjcpStyleRepository styles) {
    this.beers = beers;
    this.kegs = kegs;
    this.styles = styles;
  }

  @GetMapping
  public String page(
      @AuthenticationPrincipal CurrentUser user,
      @RequestParam(value = "year", required = false, defaultValue = "2015") Integer year,
      Model model) {
    List<Beer> beerList = new ArrayList<>();
    if (user != null && user.getBreweryId() != null) {
      List<Keg> breweryKegs = kegs.findByBreweryId(user.getBreweryId());
      Set<Long> seen = new LinkedHashSet<>();
      for (Keg k : breweryKegs) {
        if (k.getBeer() != null && k.getBeer().getId() != null && seen.add(k.getBeer().getId())) {
          beerList.add(k.getBeer());
        }
      }
    } else {
      beerList = beers.findAll();
    }
    // Styles filtered by year (client can change year via query param)
    List<BjcpStyle> styleList = styles.findAll();
    if (year != null) {
      styleList.removeIf(s -> s.getGuidelineYear() == null || !year.equals(s.getGuidelineYear()));
    }
    // Group styles by category for nicer dropdowns
    Map<String, List<BjcpStyle>> stylesByCategory = new LinkedHashMap<>();
    for (BjcpStyle s : styleList) {
      stylesByCategory
          .computeIfAbsent(
              s.getCategory() != null ? s.getCategory() : "Other", k -> new ArrayList<>())
          .add(s);
    }
    model.addAttribute("beers", beerList);
    model.addAttribute("stylesByCategory", stylesByCategory);
    model.addAttribute("year", year);
    return "admin/beer";
  }

  @PostMapping("/{beerId}/style")
  public String setStyle(
      @PathVariable Long beerId,
      @RequestParam("styleId") Long styleId,
      @RequestParam(value = "year", required = false, defaultValue = "2015") Integer year) {
    Beer b = beers.findById(beerId).orElseThrow();
    BjcpStyle style = styles.findById(styleId).orElseThrow();
    b.setStyleRef(style);
    beers.save(b);
    return "redirect:/admin/beer?year=" + year;
  }
}
