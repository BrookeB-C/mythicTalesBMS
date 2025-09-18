package com.mythictales.bms.taplist.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mythictales.bms.taplist.domain.*;
import com.mythictales.bms.taplist.repo.*;
import com.mythictales.bms.taplist.kafka.DomainEventMetadata;
import com.mythictales.bms.taplist.kafka.DomainEventPublisher;

@Service
public class TapService {
  private final TapRepository taps;
  private final KegRepository kegs;
  private final KegPlacementRepository placements;
  private final KegEventRepository events;
  private final UserAccountRepository users;
  private final ApplicationEventPublisher eventsBus;
  private final Optional<DomainEventPublisher> domainEvents;

  public TapService(
      TapRepository t,
      KegRepository k,
      KegPlacementRepository p,
      KegEventRepository e,
      UserAccountRepository users,
      ApplicationEventPublisher eventsBus,
      Optional<DomainEventPublisher> domainEvents) {
    this.taps = t;
    this.kegs = k;
    this.placements = p;
    this.events = e;
    this.users = users;
    this.eventsBus = eventsBus;
    this.domainEvents = domainEvents == null ? Optional.empty() : domainEvents;
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
    UserAccount actor = loadActor(actorUserId);
    KegEvent evt = new KegEvent(placement, KegEventType.TAP, null);
    if (actor != null) {
      evt.setActor(actor);
    }
    events.save(evt);
    if (tap.getVenue() != null) {
      eventsBus.publishEvent(
          new com.mythictales.bms.taplist.events.TaproomEvents.KegTapped(
              tap.getId(), keg.getId(), tap.getVenue().getId(), actorUserId));
    }
    publishTaproomEvent("KegTapped", tap, keg, null, actor);
  }

  @Transactional
  public void blow(Long tapId, Long actorUserId) {
    Tap tap = taps.findById(tapId).orElseThrow();
    Keg keg = tap.getKeg();
    if (keg == null) return;
    keg.setRemainingOunces(0);
    keg.setStatus(KegStatus.BLOWN);
    kegs.save(keg);
    UserAccount actor = loadActor(actorUserId);
    placements
        .findFirstByTapIdAndEndedAtIsNull(tapId)
        .ifPresent(
            p -> {
              p.setEndedAt(Instant.now());
              placements.save(p);
              KegEvent evt = new KegEvent(p, KegEventType.BLOW, null);
              if (actor != null) {
                evt.setActor(actor);
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
    publishTaproomEvent("KegBlown", tap, keg, null, actor);
    publishTaproomEvent("KegUntapped", tap, keg, null, actor);
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
    UserAccount actor = loadActor(actorUserId);
    placements
        .findFirstByTapIdAndEndedAtIsNull(tapId)
        .ifPresent(
            p -> {
              KegEvent evt = new KegEvent(p, KegEventType.POUR, ounces);
              if (actor != null) {
                evt.setActor(actor);
              }
              events.save(evt);
            });
    if (tap.getVenue() != null && keg != null) {
      eventsBus.publishEvent(
          new com.mythictales.bms.taplist.events.TaproomEvents.BeerPoured(
              tap.getId(), keg.getId(), tap.getVenue().getId(), ounces, actorUserId));
    }
    publishTaproomEvent("BeerPoured", tap, keg, ounces, actor);
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
                if (actor != null) {
                  evt.setActor(actor);
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
      publishTaproomEvent("KegBlown", tap, keg, null, actor);
      publishTaproomEvent("KegUntapped", tap, keg, null, actor);
    }
  }

  private UserAccount loadActor(Long actorUserId) {
    if (actorUserId == null) {
      return null;
    }
    return users.findById(actorUserId).orElse(null);
  }

  private void publishTaproomEvent(
      String eventType, Tap tap, Keg keg, Double ounces, UserAccount actor) {
    domainEvents.ifPresent(
        publisher -> {
          if (tap == null) {
            return;
          }
          Brewery brewery = resolveBrewery(tap, keg);
          if (brewery == null || brewery.getId() == null) {
            return;
          }
          UUID breweryId = uuidFor("brewery", brewery.getId());
          Optional<UUID> facilityId = optionalUuid("taproom", tap.getTaproom());
          Optional<UUID> venueId = optionalUuid("venue", tap.getVenue());

          Map<String, Object> payload = new LinkedHashMap<>();
          payload.put("eventType", eventType);
          payload.put("tapId", tap.getId());
          payload.put("tapNumber", tap.getNumber());
          payload.put("taproomId", tap.getTaproom() != null ? tap.getTaproom().getId() : null);
          payload.put("taproomName", tap.getTaproom() != null ? tap.getTaproom().getName() : null);
          payload.put("venueId", tap.getVenue() != null ? tap.getVenue().getId() : null);
          payload.put("venueName", tap.getVenue() != null ? tap.getVenue().getName() : null);
          if (keg != null) {
            payload.put("kegId", keg.getId());
            payload.put("kegSerialNumber", keg.getSerialNumber());
            payload.put("kegStatus", keg.getStatus() != null ? keg.getStatus().name() : null);
            payload.put("kegRemainingOunces", keg.getRemainingOunces());
            payload.put("kegTotalOunces", keg.getTotalOunces());
            Brewery kegBrewery = keg.getBrewery();
            if (kegBrewery != null) {
              payload.put("kegBreweryId", kegBrewery.getId());
              payload.put("kegBreweryName", kegBrewery.getName());
            }
            Beer beer = keg.getBeer();
            if (beer != null) {
              payload.put("beerId", beer.getId());
              payload.put("beerName", beer.getName());
              payload.put("beerStyle", beer.getStyle());
            }
          }
          if (ounces != null) {
            payload.put("ounces", ounces);
          }
          if (actor != null) {
            payload.put("actorUserId", actor.getId());
            payload.put("actorUsername", actor.getUsername());
            if (actor.getRole() != null) {
              payload.put("actorRole", actor.getRole().name());
            }
          }

          DomainEventMetadata metadata =
              new DomainEventMetadata(
                  "taproom",
                  eventType,
                  breweryId,
                  facilityId,
                  venueId,
                  Instant.now(),
                  Optional.empty());
          publisher.publish(metadata, payload);
        });
  }

  private Brewery resolveBrewery(Tap tap, Keg keg) {
    if (tap.getTaproom() != null && tap.getTaproom().getBrewery() != null) {
      return tap.getTaproom().getBrewery();
    }
    if (tap.getVenue() != null && tap.getVenue().getBrewery() != null) {
      return tap.getVenue().getBrewery();
    }
    if (keg != null && keg.getBrewery() != null) {
      return keg.getBrewery();
    }
    return null;
  }

  private UUID uuidFor(String scope, Long id) {
    String value = scope + ":" + id;
    return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8));
  }

  private Optional<UUID> optionalUuid(String scope, Taproom taproom) {
    if (taproom == null || taproom.getId() == null) {
      return Optional.empty();
    }
    return Optional.of(uuidFor(scope, taproom.getId()));
  }

  private Optional<UUID> optionalUuid(String scope, Venue venue) {
    if (venue == null || venue.getId() == null) {
      return Optional.empty();
    }
    return Optional.of(uuidFor(scope, venue.getId()));
  }
}
