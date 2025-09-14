package com.mythictales.bms.taplist.service;

import java.time.Instant;

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

  public TapService(
      TapRepository t,
      KegRepository k,
      KegPlacementRepository p,
      KegEventRepository e,
      UserAccountRepository users) {
    this.taps = t;
    this.kegs = k;
    this.placements = p;
    this.events = e;
    this.users = users;
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
  }

  @Transactional
  public void pour(Long tapId, double ounces, Long actorUserId) {
    Tap tap = taps.findById(tapId).orElseThrow();
    Keg keg = tap.getKeg();
    if (keg == null) return;
    double remain = keg.getRemainingOunces() - ounces;
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
    }
  }
}
