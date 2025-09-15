package com.mythictales.bms.taplist.api;

import com.mythictales.bms.taplist.api.dto.*;
import com.mythictales.bms.taplist.domain.*;

public final class ApiMappers {
  private ApiMappers() {}

  public static BeerDto toDto(Beer b) {
    if (b == null) return null;
    return new BeerDto(b.getId(), b.getName(), b.getStyle(), b.getAbv());
  }

  public static KegSizeSpecDto toDto(KegSizeSpec s) {
    if (s == null) return null;
    return new KegSizeSpecDto(s.getId(), s.getCode(), s.getGallons(), s.getOunces(), s.getLiters());
  }

  public static KegDto toDto(Keg k) {
    if (k == null) return null;
    return new KegDto(
        k.getId(),
        toDto(k.getBeer()),
        k.getBrewery() != null ? k.getBrewery().getId() : null,
        toDto(k.getSize()),
        k.getTotalOunces(),
        k.getRemainingOunces(),
        k.getStatus() != null ? k.getStatus().name() : null,
        k.getAssignedVenue() != null ? k.getAssignedVenue().getId() : null,
        k.getSerialNumber(),
        k.getVersion());
  }

  public static TapDto toDto(Tap t) {
    if (t == null) return null;
    return new TapDto(
        t.getId(),
        t.getNumber(),
        t.getVenue() != null ? t.getVenue().getId() : null,
        t.getTaproom() != null ? t.getTaproom().getId() : null,
        t.getBar() != null ? t.getBar().getId() : null,
        toDto(t.getKeg()),
        t.getVersion());
  }

  public static VenueDto toDto(Venue v) {
    if (v == null) return null;
    return new VenueDto(
        v.getId(),
        v.getName(),
        v.getType() != null ? v.getType().name() : null,
        v.getBrewery() != null ? v.getBrewery().getId() : null);
  }

  public static TaproomDto toDto(Taproom tr) {
    if (tr == null) return null;
    return new TaproomDto(
        tr.getId(), tr.getName(), tr.getBrewery() != null ? tr.getBrewery().getId() : null);
  }

  public static BreweryDto toDto(Brewery b) {
    if (b == null) return null;
    return new BreweryDto(b.getId(), b.getName());
  }

  public static BarDto toDto(Bar b) {
    if (b == null) return null;
    return new BarDto(
        b.getId(), b.getName(), b.getBrewery() != null ? b.getBrewery().getId() : null);
  }

  public static UserDto toDto(UserAccount u) {
    if (u == null) return null;
    return new UserDto(
        u.getId(),
        u.getUsername(),
        u.getRole() != null ? u.getRole().name() : null,
        u.getBrewery() != null ? u.getBrewery().getId() : null,
        u.getBar() != null ? u.getBar().getId() : null,
        u.getTaproom() != null ? u.getTaproom().getId() : null);
  }

  public static KegEventDto toDto(KegEvent e) {
    if (e == null) return null;
    Long tapId =
        e.getPlacement() != null && e.getPlacement().getTap() != null
            ? e.getPlacement().getTap().getId()
            : null;
    Long kegId =
        e.getPlacement() != null && e.getPlacement().getKeg() != null
            ? e.getPlacement().getKeg().getId()
            : null;
    Long venueId =
        e.getPlacement() != null
                && e.getPlacement().getTap() != null
                && e.getPlacement().getTap().getVenue() != null
            ? e.getPlacement().getTap().getVenue().getId()
            : null;
    return new KegEventDto(
        e.getId(),
        venueId,
        tapId,
        kegId,
        e.getType() != null ? e.getType().name() : null,
        e.getOunces(),
        e.getActor() != null ? e.getActor().getId() : null,
        e.getAtTime());
  }
}
