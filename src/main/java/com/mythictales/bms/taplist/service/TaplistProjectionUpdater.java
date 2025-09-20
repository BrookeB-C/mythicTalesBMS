package com.mythictales.bms.taplist.service;

import java.time.Instant;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mythictales.bms.taplist.domain.*;
import com.mythictales.bms.taplist.events.TaproomEvents.*;
import com.mythictales.bms.taplist.repo.TapRepository;
import com.mythictales.bms.taplist.repo.TaplistViewRepository;

@Component
public class TaplistProjectionUpdater {
  private final TapRepository taps;
  private final TaplistViewRepository viewRepo;

  public TaplistProjectionUpdater(TapRepository taps, TaplistViewRepository viewRepo) {
    this.taps = taps;
    this.viewRepo = viewRepo;
  }

  @EventListener
  @Transactional
  public void onTapped(KegTapped evt) {
    refreshTap(evt.tapId());
  }

  @EventListener
  @Transactional
  public void onPoured(BeerPoured evt) {
    refreshTap(evt.tapId());
  }

  @EventListener
  @Transactional
  public void onBlown(KegBlown evt) {
    refreshTap(evt.tapId());
  }

  @EventListener
  @Transactional
  public void onUntapped(KegUntapped evt) {
    refreshTap(evt.tapId());
  }

  private void refreshTap(Long tapId) {
    Tap tap = taps.findById(tapId).orElse(null);
    if (tap == null) return;
    TaplistView v = viewRepo.findById(tapId).orElse(new TaplistView(tapId));
    v.setVenueId(tap.getVenue() != null ? tap.getVenue().getId() : null);
    if (tap.getKeg() != null && tap.getKeg().getBeer() != null) {
      Beer beer = tap.getKeg().getBeer();
      v.setBeerName(beer.getName());
      v.setStyle(beer.getStyle());
      v.setAbv(beer.getAbv());
      Double total = tap.getKeg().getTotalOunces();
      Double remain = tap.getKeg().getRemainingOunces();
      v.setTotalOunces(total);
      v.setRemainingOunces(remain);
      int pct = 0;
      if (total != null && total > 0 && remain != null && remain >= 0) {
        pct = (int) Math.round(100.0 * remain / total);
      }
      v.setFillPercent(pct);
    } else {
      v.setBeerName(null);
      v.setStyle(null);
      v.setAbv(null);
      v.setRemainingOunces(null);
      v.setTotalOunces(null);
      v.setFillPercent(null);
    }
    v.setUpdatedAt(Instant.now());
    viewRepo.save(v);
  }
}
