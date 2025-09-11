package com.mythictales.bms.taplist.service;
import com.mythictales.bms.taplist.domain.*; import com.mythictales.bms.taplist.repo.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service
public class TapService {
    private final TapRepository taps; private final KegRepository kegs;
    public TapService(TapRepository t, KegRepository k){ this.taps=t; this.kegs=k; }
    @Transactional public void tapKeg(Long tapId, Long kegId){
        Tap tap = taps.findById(tapId).orElseThrow(); Keg keg = kegs.findById(kegId).orElseThrow();
        keg.setStatus(KegStatus.TAPPED); tap.setKeg(keg); taps.save(tap); kegs.save(keg);
    }
    @Transactional public void blow(Long tapId){
        Tap tap = taps.findById(tapId).orElseThrow(); Keg keg = tap.getKeg(); if (keg==null) return;
        keg.setRemainingOunces(0); keg.setStatus(KegStatus.EMPTY); kegs.save(keg); tap.setKeg(null); taps.save(tap);
    }
    @Transactional public void pour(Long tapId, double ounces){
        Tap tap = taps.findById(tapId).orElseThrow(); Keg keg = tap.getKeg(); if (keg==null) return;
        double remain = keg.getRemainingOunces() - ounces; keg.setRemainingOunces(Math.max(0, remain));
        if (keg.getRemainingOunces() <= 0){ keg.setStatus(KegStatus.EMPTY); tap.setKeg(null); taps.save(tap); }
        kegs.save(keg);
    }
}
