package com.mythictales.bms.taplist.controller;
import com.mythictales.bms.taplist.domain.*; import com.mythictales.bms.taplist.repo.*; import com.mythictales.bms.taplist.security.CurrentUser;
import com.mythictales.bms.taplist.service.TapService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
public class TaplistController {
    private final TapRepository taps; private final TaproomRepository taprooms; private final BarRepository bars; private final KegRepository kegs; private final TapService tapService;
    public TaplistController(TapRepository taps, TaproomRepository taprooms, BarRepository bars, KegRepository kegs, TapService tapService){
        this.taps=taps; this.taprooms=taprooms; this.bars=bars; this.kegs=kegs; this.tapService=tapService; }
    @GetMapping("/taplist")
    public String taplist(@AuthenticationPrincipal CurrentUser user, Model model){
        List<Tap> tapList;
        if (user != null && user.getTaproomId()!=null){ tapList = taps.findByTaproomId(user.getTaproomId()); }
        else if (user != null && user.getBarId()!=null){ tapList = taps.findByBarId(user.getBarId()); }
        else { tapList = taps.findAll(); }
        model.addAttribute("taps", tapList);
        return "taplist/list";
    }
    @PostMapping("/taps/{id}/tapKeg") public String tapKeg(@PathVariable Long id, @RequestParam("kegId") Long kegId){ tapService.tapKeg(id, kegId); return "redirect:/taplist"; }
    @PostMapping("/taps/{id}/pour")  public String pour(@PathVariable Long id, @RequestParam("ounces") double ounces){ tapService.pour(id, ounces); return "redirect:/taplist"; }
    @PostMapping("/taps/{id}/blow")  public String blow(@PathVariable Long id){ tapService.blow(id); return "redirect:/taplist"; }
}
