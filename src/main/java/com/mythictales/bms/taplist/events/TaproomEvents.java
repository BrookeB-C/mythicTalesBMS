package com.mythictales.bms.taplist.events;

public class TaproomEvents {
  public record KegTapped(Long tapId, Long kegId, Long venueId, Long actorUserId) {}

  public record BeerPoured(Long tapId, Long kegId, Long venueId, Double ounces, Long actorUserId) {}

  public record KegBlown(Long tapId, Long kegId, Long venueId, Long actorUserId) {}

  public record KegUntapped(Long tapId, Long kegId, Long venueId, Long actorUserId) {}
}
