package com.mythictales.bms.taplist.service;
import com.mythictales.bms.taplist.domain.*; 
import com.mythictales.bms.taplist.repo.*;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class TapService {
    private final TapRepository taps; 
    private final KegRepository kegs;
    private final KegPlacementRepository placements;
    private final KegEventRepository events;

    public TapService(TapRepository t, KegRepository k, KegPlacementRepository p, KegEventRepository e){ 
        this.taps=t; this.kegs=k; this.placements=p; this.events=e; 
    }

    @Transactional 
    public void tapKeg(Long tapId, Long kegId){
        Tap tap = taps.findById(tapId).orElseThrow(); 
        Keg keg = kegs.findById(kegId).orElseThrow();
        placements.findFirstByTapIdAndEndedAtIsNull(tapId).ifPresent(p -> { 
            p.setEndedAt(Instant.now()); placements.save(p);
        });
        keg.setStatus(KegStatus.TAPPED);
        // ensure keg is assigned to the venue of the tap
        if (tap.getVenue() != null) { keg.setAssignedVenue(tap.getVenue()); }
        tap.setKeg(keg); 
        taps.save(tap); 
        kegs.save(keg);
        KegPlacement placement = placements.save(new KegPlacement(tap, keg));
        events.save(new KegEvent(placement, KegEventType.TAP, null));
    }

    @Transactional 
    public void blow(Long tapId){
        Tap tap = taps.findById(tapId).orElseThrow(); 
        Keg keg = tap.getKeg(); 
        if (keg==null) return;
        keg.setRemainingOunces(0);
        keg.setStatus(KegStatus.BLOWN);
        kegs.save(keg);
        placements.findFirstByTapIdAndEndedAtIsNull(tapId).ifPresent(p -> { 
            p.setEndedAt(Instant.now()); placements.save(p);
            events.save(new KegEvent(p, KegEventType.BLOW, null));
        });
        tap.setKeg(null); 
        taps.save(tap);
    }

    @Transactional 
    public void pour(Long tapId, double ounces){
        Tap tap = taps.findById(tapId).orElseThrow(); 
        Keg keg = tap.getKeg(); 
        if (keg==null) return;
        double remain = keg.getRemainingOunces() - ounces; 
        keg.setRemainingOunces(Math.max(0, remain));
        kegs.save(keg);
        placements.findFirstByTapIdAndEndedAtIsNull(tapId).ifPresent(p -> { 
            events.save(new KegEvent(p, KegEventType.POUR, ounces));
        });
        if (keg.getRemainingOunces() <= 0){ 
            keg.setStatus(KegStatus.BLOWN);
            kegs.save(keg);
            placements.findFirstByTapIdAndEndedAtIsNull(tapId).ifPresent(p -> { 
                p.setEndedAt(Instant.now()); placements.save(p);
                events.save(new KegEvent(p, KegEventType.UNTAP, null));
            });
            tap.setKeg(null); 
            taps.save(tap); 
        }
    }
}
