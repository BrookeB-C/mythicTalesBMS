package com.mythictales.bms.taplist.controller;
import com.mythictales.bms.taplist.repo.TaproomRepository; import com.mythictales.bms.taplist.security.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/admin/brewery")
public class AdminBreweryController {
    private final TaproomRepository taprooms; public AdminBreweryController(TaproomRepository taprooms){ this.taprooms=taprooms; }
    @GetMapping public String page(@AuthenticationPrincipal CurrentUser user, Model model){
        if (user!=null && user.getBreweryId()!=null) model.addAttribute("taprooms", taprooms.findByBreweryId(user.getBreweryId()));
        else model.addAttribute("taprooms", java.util.List.of());
        return "admin/brewery";
    }
}
