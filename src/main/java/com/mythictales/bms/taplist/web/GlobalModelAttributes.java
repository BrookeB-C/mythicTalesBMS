package com.mythictales.bms.taplist.web;
import com.mythictales.bms.taplist.domain.*; import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.security.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component; import org.springframework.web.bind.annotation.*;
import java.util.List;
@ControllerAdvice @Component
public class GlobalModelAttributes {
    private final KegRepository kegs;
    public GlobalModelAttributes(KegRepository kegs){ this.kegs=kegs; }
    @ModelAttribute("currentUser")
    public CurrentUser currentUser(@AuthenticationPrincipal CurrentUser user){ return user; }
    @ModelAttribute("availableKegs")
    public List<Keg> availableKegs(){ try { return kegs.findByStatus(KegStatus.UNTAPPED);} catch(Exception e){ return java.util.List.of(); } }
    @ModelAttribute("untappedKegs")
    public List<Keg> untappedKegs(){ return availableKegs(); }
}
