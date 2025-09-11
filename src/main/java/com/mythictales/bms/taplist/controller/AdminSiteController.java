package com.mythictales.bms.taplist.controller;
import com.mythictales.bms.taplist.repo.*; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/admin/site")
public class AdminSiteController {
    private final BreweryRepository breweries; private final BarRepository bars;
    public AdminSiteController(BreweryRepository breweries, BarRepository bars){ this.breweries=breweries; this.bars=bars; }
    @GetMapping public String page(Model model){ model.addAttribute("breweries", breweries.findAll()); model.addAttribute("bars", bars.findAll()); return "admin/site"; }
}
