package com.mythictales.bms.taplist.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mythictales.bms.taplist.domain.*;
import com.mythictales.bms.taplist.repo.*;

@Service
public class TapService {
  private final TapRepository taps;
  private final KegRepository kegs;
  private final KegPlacementRepository placements;
  private final KegEventRepository events;
  private final UserAccountRepository users;
  private final ApplicationEventPublisher eventsBus;

  public TapService(
      TapRepository t,
      KegRepository k,
      KegPlacementRepository p,
      KegEventRepository e,
      UserAccountRepository users,
      ApplicationEventPublisher eventsBus) {
    this.taps = t;
    this.kegs = k;
    this.placements = p;
    this.events = e;
    this.users = users;
    this.eventsBus = eventsBus;
  }

  // Overloads without actor for system/data initialization
  @Transactional
  public void tapKeg(Long tapId, Long kegId) {
    tapKeg(tapId, kegId, null);
  }

  @Transactional
  public void blow(Long tapId) {
    blow(tapId, null);
  }

  @Transactional
  public void pour(Long tapId, double ounces) {
    pour(tapId, ounces, null);
  }

  // Actor-aware variants
  @Transactional
  public void tapKeg(Long tapId, Long kegId, Long actorUserId) {
    Tap tap = taps.findById(tapId).orElseThrow();
    Keg keg = kegs.findById(kegId).orElseThrow();
    placements
        .findFirstByTapIdAndEndedAtIsNull(tapId)
        .ifPresent(
            p -> {
              p.setEndedAt(Instant.now());
              placements.save(p);
            });
    keg.setStatus(KegStatus.TAPPED);
    // ensure keg is assigned to the venue of the tap
    if (tap.getVenue() != null) {
      keg.setAssignedVenue(tap.getVenue());
    }
    tap.setKeg(keg);
    taps.save(tap);
    kegs.save(keg);
    KegPlacement placement = placements.save(new KegPlacement(tap, keg));
    KegEvent evt = new KegEvent(placement, KegEventType.TAP, null);
    if (actorUserId != null) {
      users.findById(actorUserId).ifPresent(evt::setActor);
    }
    events.save(evt);
    if (tap.getVenue() != null) {
      eventsBus.publishEvent(
          new com.mythictales.bms.taplist.events.TaproomEvents.KegTapped(
              tap.getId(), keg.getId(), tap.getVenue().getId(), actorUserId));
    }
  }

  @Transactional
  public void blow(Long tapId, Long actorUserId) {
    Tap tap = taps.findById(tapId).orElseThrow();
    Keg keg = tap.getKeg();
    if (keg == null) return;
    keg.setRemainingOunces(0);
    keg.setStatus(KegStatus.BLOWN);
    kegs.save(keg);
    placements
        .findFirstByTapIdAndEndedAtIsNull(tapId)
        .ifPresent(
            p -> {
              p.setEndedAt(Instant.now());
              placements.save(p);
              KegEvent evt = new KegEvent(p, KegEventType.BLOW, null);
              if (actorUserId != null) {
                users.findById(actorUserId).ifPresent(evt::setActor);
              }
              events.save(evt);
            });
    tap.setKeg(null);
    taps.save(tap);
    if (tap.getVenue() != null && keg != null) {
      eventsBus.publishEvent(
          new com.mythictales.bms.taplist.events.TaproomEvents.KegBlown(
              tap.getId(), keg.getId(), tap.getVenue().getId(), actorUserId));
      eventsBus.publishEvent(
          new com.mythictales.bms.taplist.events.TaproomEvents.KegUntapped(
              tap.getId(), keg.getId(), tap.getVenue().getId(), actorUserId));
    }
  }

  @Transactional
  public void pour(Long tapId, double ounces, Long actorUserId) {
    pour(tapId, ounces, actorUserId, false);
  }

  @Transactional
  public void pour(Long tapId, double ounces, Long actorUserId, boolean allowOverpourToBlow) {
    Tap tap = taps.findById(tapId).orElseThrow();
    Keg keg = tap.getKeg();
    if (keg == null) return;
    if (ounces <= 0) {
      throw new BusinessValidationException("Pour amount must be positive");
    }
    double remain = keg.getRemainingOunces() - ounces;
    if (remain < 0 && !allowOverpourToBlow) {
      throw new BusinessValidationException(
          "Overpour exceeds remaining ounces",
          Map.of("remainingOunces", keg.getRemainingOunces(), "requestedOunces", ounces));
    }
    keg.setRemainingOunces(Math.max(0, remain));
    kegs.save(keg);
    placements
        .findFirstByTapIdAndEndedAtIsNull(tapId)
        .ifPresent(
            p -> {
              KegEvent evt = new KegEvent(p, KegEventType.POUR, ounces);
              if (actorUserId != null) {
                users.findById(actorUserId).ifPresent(evt::setActor);
              }
              events.save(evt);
            });
    if (tap.getVenue() != null && keg != null) {
      eventsBus.publishEvent(
          new com.mythictales.bms.taplist.events.TaproomEvents.BeerPoured(
              tap.getId(), keg.getId(), tap.getVenue().getId(), ounces, actorUserId));
    }
    if (keg.getRemainingOunces() <= 0) {
      keg.setStatus(KegStatus.BLOWN);
      kegs.save(keg);
      placements
          .findFirstByTapIdAndEndedAtIsNull(tapId)
          .ifPresent(
              p -> {
                p.setEndedAt(Instant.now());
                placements.save(p);
                KegEvent evt = new KegEvent(p, KegEventType.UNTAP, null);
                if (actorUserId != null) {
                  users.findById(actorUserId).ifPresent(evt::setActor);
                }
                events.save(evt);
              });
      tap.setKeg(null);
      taps.save(tap);
      if (tap.getVenue() != null) {
        eventsBus.publishEvent(
            new com.mythictales.bms.taplist.events.TaproomEvents.KegBlown(
                tap.getId(), keg.getId(), tap.getVenue().getId(), actorUserId));
        eventsBus.publishEvent(
            new com.mythictales.bms.taplist.events.TaproomEvents.KegUntapped(
                tap.getId(), keg.getId(), tap.getVenue().getId(), actorUserId));
      }
    }
  }
}
