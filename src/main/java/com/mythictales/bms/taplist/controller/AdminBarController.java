package com.mythictales.bms.taplist.controller;
import com.mythictales.bms.taplist.repo.TapRepository; import com.mythictales.bms.taplist.security.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/admin/bar")
public class AdminBarController {
    private final TapRepository taps; public AdminBarController(TapRepository taps){ this.taps=taps; }
    @GetMapping public String page(@AuthenticationPrincipal CurrentUser user, Model model){
        if (user!=null && user.getBarId()!=null) model.addAttribute("taps", taps.findByBarId(user.getBarId()));
        else model.addAttribute("taps", java.util.List.of());
        return "admin/bar";
    }
}
