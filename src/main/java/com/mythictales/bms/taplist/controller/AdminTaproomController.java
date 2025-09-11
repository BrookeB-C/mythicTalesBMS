package com.mythictales.bms.taplist.controller;
import com.mythictales.bms.taplist.repo.*; import com.mythictales.bms.taplist.security.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/admin/taproom")
public class AdminTaproomController {
    private final TapRepository taps; private final KegRepository kegs;
    public AdminTaproomController(TapRepository taps, KegRepository kegs){ this.taps=taps; this.kegs=kegs; }
    @GetMapping public String page(@AuthenticationPrincipal CurrentUser user, Model model){
        if (user!=null && user.getTaproomId()!=null) model.addAttribute("taps", taps.findByTaproomId(user.getTaproomId()));
        else model.addAttribute("taps", java.util.List.of());
        model.addAttribute("kegs", kegs.findAll()); return "admin/taproom";
    }
}
