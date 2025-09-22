package com.mythictales.bms.taplist.api.dto;

import java.util.List;

public record KegInventorySummaryDto(
    Hero hero,
    List<QueueItem> queue,
    List<ActivityItem> activity,
    List<QuickAction> quickActions) {

  public record Hero(int availableKegs, int distributedKegs, int returnedKegs) {}

  public record QueueItem(
      Long kegId,
      String serialNumber,
      String beerName,
      String status,
      String severity,
      String location) {}

  public record ActivityItem(
      Long eventId, String type, String summary, String actor, String occurredAtIso) {}

  public record QuickAction(String label, String href, Command command) {
    public record Command(String type, Long kegId, Boolean requiresVenue) {}
  }
}
