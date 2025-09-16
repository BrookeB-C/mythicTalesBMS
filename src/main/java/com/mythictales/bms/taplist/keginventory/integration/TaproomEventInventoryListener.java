package com.mythictales.bms.taplist.keginventory.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.events.TaproomEvents;
import com.mythictales.bms.taplist.repo.KegRepository;

@Component
public class TaproomEventInventoryListener {
  private static final Logger log = LoggerFactory.getLogger(TaproomEventInventoryListener.class);

  private final KegRepository kegs;

  public TaproomEventInventoryListener(KegRepository kegs) {
    this.kegs = kegs;
  }

  @EventListener
  @Transactional
  public void onTapped(TaproomEvents.KegTapped e) {
    kegs.findById(e.kegId())
        .ifPresent(
            keg -> {
              log.info("[Inventory] KegTapped kegId={} venueId={}", e.kegId(), e.venueId());
              keg.setStatus(KegStatus.TAPPED);
              kegs.save(keg);
            });
  }

  @EventListener
  @Transactional
  public void onPoured(TaproomEvents.BeerPoured e) {
    kegs.findById(e.kegId())
        .ifPresent(
            keg -> {
              double newRemaining =
                  Math.max(0.0, keg.getRemainingOunces() - (e.ounces() != null ? e.ounces() : 0.0));
              keg.setRemainingOunces(newRemaining);
              log.info(
                  "[Inventory] BeerPoured kegId={} venueId={} ounces={} remaining={}",
                  e.kegId(),
                  e.venueId(),
                  e.ounces(),
                  newRemaining);
              kegs.save(keg);
            });
  }

  @EventListener
  @Transactional
  public void onBlown(TaproomEvents.KegBlown e) {
    kegs.findById(e.kegId())
        .ifPresent(
            keg -> {
              log.info("[Inventory] KegBlown kegId={} venueId={}", e.kegId(), e.venueId());
              keg.setStatus(KegStatus.BLOWN);
              kegs.save(keg);
            });
  }

  @EventListener
  @Transactional
  public void onUntapped(TaproomEvents.KegUntapped e) {
    kegs.findById(e.kegId())
        .ifPresent(
            keg -> {
              log.info("[Inventory] KegUntapped kegId={} venueId={}", e.kegId(), e.venueId());
              // Keep assignedVenue; mark as RECEIVED for simplicity.
              keg.setStatus(KegStatus.RECEIVED);
              kegs.save(keg);
            });
  }
}
