package com.mythictales.bms.taplist.kafka;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.mythictales.bms.taplist.config.kafka.KafkaEventsProperties;

@Component
@ConditionalOnBean(name = "domainEventKafkaTemplate")
public class KafkaDomainEventPublisher implements DomainEventPublisher {

  private static final String TRACE_ID_KEY = "traceId";

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final KafkaEventsProperties properties;

  public KafkaDomainEventPublisher(
      KafkaTemplate<String, Object> kafkaTemplate, KafkaEventsProperties properties) {
    this.kafkaTemplate = kafkaTemplate;
    this.properties = properties;
  }

  @Override
  public void publish(DomainEventMetadata metadata, Object payload) {
    Objects.requireNonNull(metadata, "metadata must not be null");
    Objects.requireNonNull(payload, "payload must not be null");

    KafkaEventsProperties.TopicDescriptor descriptor = properties.descriptorFor(metadata.domain());
    String partitionKey = metadata.partitionKey();
    String traceId = resolveTraceId(metadata);

    MessageBuilder<Object> builder =
        MessageBuilder.withPayload(payload)
            .setHeader(KafkaHeaders.TOPIC, descriptor.name())
            .setHeader(KafkaHeaders.KEY, partitionKey)
            .setHeader(KafkaHeaders.TIMESTAMP, metadata.occurredAt().toEpochMilli())
            .setHeader("eventType", metadata.eventType())
            .setHeader("breweryId", metadata.breweryId().toString())
            .setHeader("occurredAt", metadata.occurredAt().toString())
            .setHeader(TRACE_ID_KEY, traceId);

    metadata
        .facilityId()
        .map(UUID::toString)
        .ifPresent(value -> builder.setHeader("facilityId", value));
    metadata.venueId().map(UUID::toString).ifPresent(value -> builder.setHeader("venueId", value));

    Message<Object> message = builder.build();
    kafkaTemplate.send(message);
  }

  private String resolveTraceId(DomainEventMetadata metadata) {
    Optional<String> traceId = metadata.resolvedTraceId();
    if (traceId.isPresent()) {
      return traceId.get();
    }
    String mdcTrace = MDC.get(TRACE_ID_KEY);
    if (mdcTrace != null && !mdcTrace.isBlank()) {
      return mdcTrace;
    }
    return UUID.randomUUID().toString();
  }
}
