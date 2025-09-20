package com.mythictales.bms.taplist.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mythictales.bms.taplist.domain.KegEvent;
import com.mythictales.bms.taplist.domain.Taproom;
import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.KegEventRepository;
import com.mythictales.bms.taplist.repo.TaproomRepository;

@Controller
@RequestMapping("/admin/site/events")
public class AdminSiteEventsController {

  private static final int PAGE_SIZE = 25;

  private final KegEventRepository events;
  private final BreweryRepository breweries;
  private final TaproomRepository taprooms;

  public AdminSiteEventsController(
      KegEventRepository events, BreweryRepository breweries, TaproomRepository taprooms) {
    this.events = events;
    this.breweries = breweries;
    this.taprooms = taprooms;
  }

  @GetMapping
  public String list(
      @RequestParam(name = "breweryId", required = false) Long breweryId,
      @RequestParam(name = "taproomId", required = false) Long taproomId,
      @RequestParam(name = "page", defaultValue = "0") int page,
      Model model) {

    Long normalizedBreweryId = normalizeId(breweryId);
    Long normalizedTaproomId = normalizeId(taproomId);

    Pageable pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE);
    Page<KegEvent> eventsPage =
        events.findEventsFiltered(normalizedBreweryId, normalizedTaproomId, pageable);

    List<Taproom> taproomChoices =
        normalizedBreweryId != null
            ? taprooms.findByBreweryId(normalizedBreweryId)
            : taprooms.findAll(Sort.by("name"));

    Integer previousPage =
        eventsPage.hasPrevious() ? eventsPage.previousPageable().getPageNumber() : null;
    Integer nextPage = eventsPage.hasNext() ? eventsPage.nextPageable().getPageNumber() : null;

    model.addAttribute("events", eventsPage);
    model.addAttribute("breweries", breweries.findAll(Sort.by("name")));
    model.addAttribute("taprooms", taproomChoices);
    model.addAttribute("selectedBreweryId", normalizedBreweryId);
    model.addAttribute("selectedTaproomId", normalizedTaproomId);
    model.addAttribute("pageSize", PAGE_SIZE);
    model.addAttribute("previousPage", previousPage);
    model.addAttribute("nextPage", nextPage);

    return "admin/site-events";
  }

  private Long normalizeId(Long id) {
    return Optional.ofNullable(id).filter(value -> value > 0).orElse(null);
  }
}
