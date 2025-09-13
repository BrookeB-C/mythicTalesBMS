package com.mythictales.bms.taplist.controller;

import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.TaproomRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;
import com.mythictales.bms.taplist.repo.BreweryRepository;
import com.mythictales.bms.taplist.repo.UserAccountRepository;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.security.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/brewery")
public class AdminBreweryController {
    private final TaproomRepository taprooms;
    private final KegRepository kegs;
    private final VenueRepository venues;
    private final BreweryRepository breweries;
    private final UserAccountRepository users;
    private final TapRepository taps;

    public AdminBreweryController(TaproomRepository taprooms, KegRepository kegs, VenueRepository venues,
                                  BreweryRepository breweries, UserAccountRepository users, TapRepository taps){
        this.taprooms=taprooms; this.kegs=kegs; this.venues=venues; this.breweries=breweries; this.users=users; this.taps=taps;
    }

    @GetMapping
    public String page(@AuthenticationPrincipal CurrentUser user,
                       @RequestParam(value = "status", required = false) KegStatus status,
                       @RequestParam(value = "taproomFilterId", required = false) Long taproomFilterId,
                       @RequestParam(value = "barFilterId", required = false) Long barFilterId,
                       @RequestParam(value = "assignedVenueId", required = false) Long assignedVenueFilterId,
                       @RequestParam(value = "tab", required = false) String tab,
                       @RequestParam(value = "q", required = false) String q,
                       Model model){
        if (user!=null && user.getBreweryId()!=null){
            var brewery = breweries.findById(user.getBreweryId()).orElse(null);
            model.addAttribute("brewery", brewery);
            var trList = taprooms.findByBreweryId(user.getBreweryId());
            // Optional search filter for taprooms
            if (q != null && !q.isBlank()) {
                String qlc = q.toLowerCase();
                trList.removeIf(t -> t.getName() == null || !t.getName().toLowerCase().contains(qlc));
            }
            model.addAttribute("taprooms", trList);
            // Map taproomId -> venueId for correct Admin link
            java.util.Map<Long, Long> taproomVenueIds = new java.util.HashMap<>();
            for (var trm : trList) {
                Long venueId = null;
                var trTaps = taps.findByTaproomId(trm.getId());
                if (!trTaps.isEmpty() && trTaps.get(0).getVenue() != null) {
                    venueId = trTaps.get(0).getVenue().getId();
                } else {
                    var vOpt = venues.findFirstByName(trm.getName());
                    if (vOpt.isPresent()) venueId = vOpt.get().getId();
                }
                if (venueId != null) taproomVenueIds.put(trm.getId(), venueId);
            }
            model.addAttribute("taproomVenueIds", taproomVenueIds);
            // Quick metrics maps (optional usage in UI)
            java.util.Map<Long, java.util.List<com.mythictales.bms.taplist.domain.Tap>> tapsByTaproom = new java.util.HashMap<>();
            java.util.Map<Long, Integer> activeKegsByTaproom = new java.util.HashMap<>();
            for (var trm : trList) {
                var tlist = taps.findByTaproomId(trm.getId());
                tapsByTaproom.put(trm.getId(), tlist);
                int active = 0; for (var t : tlist) { if (t.getKeg()!=null) active++; }
                activeKegsByTaproom.put(trm.getId(), active);
            }
            model.addAttribute("tapsByTaproom", tapsByTaproom);
            model.addAttribute("activeKegsByTaproom", activeKegsByTaproom);
            var vList = venues.findByBreweryId(user.getBreweryId());
            model.addAttribute("venues", vList);
            model.addAttribute("selectedStatus", status);

            // Unassigned (in-brewery) kegs
            List<Keg> unassigned = (status != null)
                    ? kegs.findByBreweryIdAndAssignedVenueIsNullAndStatus(user.getBreweryId(), status)
                    : kegs.findByBreweryIdAndAssignedVenueIsNull(user.getBreweryId());
            model.addAttribute("kegs", unassigned);

            // Assigned kegs (sent to venues), with optional status & venue filters
            List<Keg> assigned = (status != null)
                    ? kegs.findByBreweryIdAndAssignedVenueIsNotNullAndStatus(user.getBreweryId(), status)
                    : kegs.findByBreweryIdAndAssignedVenueIsNotNull(user.getBreweryId());
            if (assignedVenueFilterId != null) {
                assigned.removeIf(k -> k.getAssignedVenue() == null || !assignedVenueFilterId.equals(k.getAssignedVenue().getId()));
            }
            model.addAttribute("assignedKegs", assigned);
            model.addAttribute("assignedVenueId", assignedVenueFilterId);

            // Users for this brewery (direct brewery users + those on taps/bars belonging to the brewery)
            java.util.Set<com.mythictales.bms.taplist.domain.UserAccount> userSet = new java.util.LinkedHashSet<>();
            userSet.addAll(users.findByBreweryId(user.getBreweryId()));
            userSet.addAll(users.findByTaproom_Brewery_Id(user.getBreweryId()));
            userSet.addAll(users.findByBar_Brewery_Id(user.getBreweryId()));
            java.util.List<com.mythictales.bms.taplist.domain.UserAccount> userList = new java.util.ArrayList<>(userSet);
            // Filter by taproom/bar if provided
            if (taproomFilterId != null) {
                userList.removeIf(u -> u.getTaproom() == null || !taproomFilterId.equals(u.getTaproom().getId()));
            }
            if (barFilterId != null) {
                userList.removeIf(u -> u.getBar() == null || !barFilterId.equals(u.getBar().getId()));
            }
            model.addAttribute("breweryUsers", userList);
            model.addAttribute("taproomFilterId", taproomFilterId);
            model.addAttribute("barFilterId", barFilterId);
        } else {
            model.addAttribute("taprooms", java.util.List.of());
            model.addAttribute("kegs", java.util.List.of());
            model.addAttribute("venues", java.util.List.of());
            model.addAttribute("breweryUsers", java.util.List.of());
        }
        model.addAttribute("allStatuses", KegStatus.values());
        model.addAttribute("tab", tab);
        model.addAttribute("q", q);
        return "admin/brewery";
    }

    @PostMapping("/kegs/{id}/distribute")
    public String distribute(@PathVariable Long id,
                             @RequestParam("venueId") Long venueId,
                             @AuthenticationPrincipal CurrentUser user){
        Keg keg = kegs.findById(id).orElseThrow();
        if (user!=null && user.getBreweryId()!=null && keg.getBrewery()!=null && user.getBreweryId().equals(keg.getBrewery().getId())){
            keg.setAssignedVenue(venues.findById(venueId).orElse(null));
            // Only allow distribution from FILLED or EMPTY→CLEAN→FILLED; simplify: set FILLED if not already
            if (keg.getStatus() == KegStatus.EMPTY) keg.setStatus(KegStatus.CLEAN);
            if (keg.getStatus() == KegStatus.CLEAN) keg.setStatus(KegStatus.FILLED);
            keg.setStatus(KegStatus.DISTRIBUTED);
            kegs.save(keg);
        }
        return "redirect:/admin/brewery";
    }

    @PostMapping("/updateInfo")
    public String updateInfo(@AuthenticationPrincipal CurrentUser user,
                             @RequestParam("name") String name){
        if (user!=null && user.getBreweryId()!=null && name!=null && !name.isBlank()){
            breweries.findById(user.getBreweryId()).ifPresent(b -> { b.setName(name.trim()); breweries.save(b); });
        }
        return "redirect:/admin/brewery";
    }

    @PostMapping("/kegs/{id}/return")
    public String returnKeg(@PathVariable Long id, @AuthenticationPrincipal CurrentUser user){
        Keg keg = kegs.findById(id).orElseThrow();
        if (user!=null && user.getBreweryId()!=null && keg.getBrewery()!=null && user.getBreweryId().equals(keg.getBrewery().getId())){
            keg.setAssignedVenue(null);
            keg.setStatus(KegStatus.EMPTY);
            // reset contents
            if (keg.getSize()!=null) { keg.setRemainingOunces(keg.getSize().getOunces()); }
            kegs.save(keg);
        }
        return "redirect:/admin/brewery";
    }

    @PostMapping("/taprooms/add")
    public String addTaproom(@RequestParam("name") String name, @AuthenticationPrincipal CurrentUser user){
        if (user!=null && user.getBreweryId()!=null && name!=null && !name.isBlank()){
            var br = breweries.findById(user.getBreweryId()).orElse(null);
            if (br != null){
                var tr = taprooms.save(new com.mythictales.bms.taplist.domain.Taproom(name.trim(), br));
                venues.save(new com.mythictales.bms.taplist.domain.Venue(name.trim(), com.mythictales.bms.taplist.domain.VenueType.TAPROOM, br));
            }
        }
        return "redirect:/admin/brewery";
    }

    @PostMapping("/taprooms/{id}/delete")
    public String deleteTaproom(@PathVariable Long id){
        // only delete if no taps are linked (safety)
        // If taps exist, skip deletion silently
        // A robust version would check and show a message
        taprooms.findById(id).ifPresent(tr -> {
            // no-op check; relying on DB constraints; try delete
            try { taprooms.delete(tr); } catch (Exception ignored) {}
        });
        return "redirect:/admin/brewery";
    }
}
