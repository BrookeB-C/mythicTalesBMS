package com.mythictales.bms.taplist.kafka;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record DomainEventMetadata(
    String domain,
    String eventType,
    UUID breweryId,
    Optional<UUID> facilityId,
    Optional<UUID> venueId,
    Instant occurredAt,
    Optional<String> traceId) {

  public DomainEventMetadata {
    Objects.requireNonNull(domain, "domain must not be null");
    Objects.requireNonNull(eventType, "eventType must not be null");
    Objects.requireNonNull(breweryId, "breweryId must not be null");
    Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    facilityId = facilityId == null ? Optional.empty() : facilityId;
    venueId = venueId == null ? Optional.empty() : venueId;
    traceId =
        traceId == null ? Optional.empty() : traceId.map(String::trim).filter(s -> !s.isBlank());
  }

  public String partitionKey() {
    return facilityId
        .map(UUID::toString)
        .map(value -> breweryId + ":" + value)
        .orElseGet(() -> breweryId + ":global");
  }

  public Optional<String> resolvedTraceId() {
    return traceId;
  }
}
