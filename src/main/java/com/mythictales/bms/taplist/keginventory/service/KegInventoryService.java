package com.mythictales.bms.taplist.keginventory.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegStatus;
import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.repo.KegRepository;
import com.mythictales.bms.taplist.repo.VenueRepository;
import com.mythictales.bms.taplist.security.CurrentUser;
import com.mythictales.bms.taplist.service.BusinessValidationException;

@Service
public class KegInventoryService {
  private final KegRepository kegs;
  private final VenueRepository venues;
  private final KegMovementRecorder movement;

  public KegInventoryService(
      KegRepository kegs, VenueRepository venues, KegMovementRecorder movement) {
    this.kegs = kegs;
    this.venues = venues;
    this.movement = movement;
  }

  private void ensureScope(CurrentUser user, Keg keg, Venue venue) {
    if (user == null) throw new AccessDeniedException("Unauthenticated");
    // SITE_ADMIN bypasses scope checks
    if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SITE_ADMIN")))
      return;

    Long userBreweryId = user.getBreweryId();
    Long kegBreweryId = keg.getBrewery() != null ? keg.getBrewery().getId() : null;
    Long venueBreweryId =
        venue != null && venue.getBrewery() != null ? venue.getBrewery().getId() : null;

    if (userBreweryId == null
        || (kegBreweryId != null && !userBreweryId.equals(kegBreweryId))
        || (venueBreweryId != null && !userBreweryId.equals(venueBreweryId))) {
      throw new AccessDeniedException("Forbidden");
    }
  }

  @Transactional
  public Keg assignToVenue(CurrentUser user, Long kegId, Long venueId) {
    Keg keg = kegs.findById(kegId).orElseThrow();
    Venue venue = venues.findById(venueId).orElseThrow();
    ensureScope(user, keg, venue);
    Venue from = keg.getAssignedVenue();
    // Canonicalize to DISTRIBUTED
    if (keg.getStatus() == KegStatus.TAPPED)
      throw new BusinessValidationException("Cannot assign a tapped keg");
    if (keg.getStatus() == KegStatus.BLOWN)
      throw new BusinessValidationException("Cannot assign a blown keg");
    if (keg.getStatus() == KegStatus.RETURNED) keg.setStatus(KegStatus.EMPTY);
    if (keg.getStatus() == KegStatus.EMPTY) keg.setStatus(KegStatus.CLEAN);
    if (keg.getStatus() == KegStatus.CLEAN) keg.setStatus(KegStatus.FILLED);
    keg.setAssignedVenue(venue);
    keg.setStatus(KegStatus.DISTRIBUTED);
    Keg saved = kegs.save(keg);
    movement.record(saved, from, venue, null, user);
    return saved;
  }

  @Transactional
  public Keg receiveAtVenue(CurrentUser user, Long kegId, Long venueId) {
    Keg keg = kegs.findById(kegId).orElseThrow();
    Venue venue = venues.findById(venueId).orElseThrow();
    ensureScope(user, keg, venue);
    Venue from = keg.getAssignedVenue();
    keg.setAssignedVenue(venue);
    keg.setStatus(KegStatus.RECEIVED);
    Keg saved = kegs.save(keg);
    movement.record(saved, from, venue, null, user);
    return saved;
  }

  @Transactional
  public Keg move(CurrentUser user, Long kegId, Long fromVenueId, Long toVenueId) {
    if (fromVenueId.equals(toVenueId))
      throw new BusinessValidationException("fromVenueId and toVenueId must differ");
    Keg keg = kegs.findById(kegId).orElseThrow();
    Venue from = venues.findById(fromVenueId).orElseThrow();
    Venue to = venues.findById(toVenueId).orElseThrow();
    ensureScope(user, keg, to);
    if (keg.getAssignedVenue() == null || !from.getId().equals(keg.getAssignedVenue().getId()))
      throw new BusinessValidationException("Keg not assigned to fromVenueId");
    if (keg.getStatus() == KegStatus.TAPPED)
      throw new BusinessValidationException("Cannot move a tapped keg");
    keg.setAssignedVenue(to);
    if (keg.getStatus() != KegStatus.RECEIVED) keg.setStatus(KegStatus.DISTRIBUTED);
    Keg saved = kegs.save(keg);
    movement.record(saved, from, to, null, user);
    return saved;
  }

  @Transactional
  public Keg returnToBrewery(CurrentUser user, Long kegId) {
    Keg keg = kegs.findById(kegId).orElseThrow();
    ensureScope(user, keg, null);
    Venue from = keg.getAssignedVenue();
    keg.setAssignedVenue(null);
    keg.setStatus(KegStatus.EMPTY);
    if (keg.getSize() != null) keg.setRemainingOunces(keg.getSize().getOunces());
    Keg saved = kegs.save(keg);
    movement.record(saved, from, null, null, user);
    return saved;
  }

  @Transactional
  public Keg assignToExternal(CurrentUser user, Long kegId, String externalPartner) {
    Keg keg = kegs.findById(kegId).orElseThrow();
    ensureScope(user, keg, null);
    if (keg.getStatus() == KegStatus.TAPPED)
      throw new BusinessValidationException("Cannot assign a tapped keg");
    if (keg.getStatus() == KegStatus.BLOWN)
      throw new BusinessValidationException("Cannot assign a blown keg");
    if (keg.getStatus() == KegStatus.RETURNED) keg.setStatus(KegStatus.EMPTY);
    if (keg.getStatus() == KegStatus.EMPTY) keg.setStatus(KegStatus.CLEAN);
    if (keg.getStatus() == KegStatus.CLEAN) keg.setStatus(KegStatus.FILLED);
    Venue from = keg.getAssignedVenue();
    keg.setAssignedVenue(null);
    keg.setStatus(KegStatus.DISTRIBUTED);
    Keg saved = kegs.save(keg);
    movement.record(saved, from, null, externalPartner, user);
    return saved;
  }
}
