package com.mythictales.bms.taplist.kafka;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.mythictales.bms.taplist.config.kafka.KafkaSampleProperties;

@Component
@ConditionalOnBean(DomainEventPublisher.class)
@ConditionalOnProperty(prefix = "bms.kafka.sample", name = "enabled", havingValue = "true")
public class KafkaSamplePublisher implements ApplicationRunner {

  private static final UUID DEFAULT_BREWERY_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000001");

  private final DomainEventPublisher publisher;
  private final KafkaSampleProperties properties;

  public KafkaSamplePublisher(DomainEventPublisher publisher, KafkaSampleProperties properties) {
    this.publisher = publisher;
    this.properties = properties;
  }

  @Override
  public void run(ApplicationArguments args) {
    DomainEventMetadata metadata =
        new DomainEventMetadata(
            "prodinventory",
            "SampleInventoryEvent",
            parseUuid(properties.getBreweryId(), DEFAULT_BREWERY_ID),
            properties.getFacilityId().map(UUID::fromString),
            properties.getVenueId().map(UUID::fromString),
            Instant.now(),
            Optional.empty());

    Map<String, Object> payload = new HashMap<>();
    payload.put("kind", "SAMPLE");
    payload.put("note", "Sample domain event emitted for connectivity verification");

    publisher.publish(metadata, payload);
  }

  private UUID parseUuid(String value, UUID fallback) {
    if (value == null || value.isBlank()) {
      return fallback;
    }
    try {
      return UUID.fromString(value.trim());
    } catch (IllegalArgumentException ex) {
      return fallback;
    }
  }
}
