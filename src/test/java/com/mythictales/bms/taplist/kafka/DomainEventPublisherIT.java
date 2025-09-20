package com.mythictales.bms.taplist.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.mythictales.bms.taplist.TaplistApplication;

@SpringBootTest(classes = TaplistApplication.class)
@Testcontainers
class DomainEventPublisherIT {

  private static final DockerImageName KAFKA_IMAGE =
      DockerImageName.parse("confluentinc/cp-kafka:7.6.1");

  @Container static final KafkaContainer KAFKA = new KafkaContainer(KAFKA_IMAGE);

  private Consumer<String, Map<String, Object>> consumer;

  @DynamicPropertySource
  static void kafkaProps(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    registry.add("bms.kafka.enabled", () -> "true");
    registry.add("bms.kafka.sample.enabled", () -> "false");
  }

  @Autowired private DomainEventPublisher publisher;

  @AfterEach
  void tearDown() {
    if (consumer != null) {
      consumer.close();
    }
  }

  @Test
  void publishesDomainEventWithRequiredHeaders() {
    Instant occurredAt = Instant.parse("2024-09-17T20:17:45Z");
    UUID breweryId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    UUID facilityId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    UUID venueId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    DomainEventMetadata metadata =
        new DomainEventMetadata(
            "prodinventory",
            "InventoryAdjusted",
            breweryId,
            Optional.of(facilityId),
            Optional.of(venueId),
            occurredAt,
            Optional.of("trace-from-test"));

    Map<String, Object> payload = Map.of("delta", 5, "sku", "MYSTIC-IPA-16OZ");

    publisher.publish(metadata, payload);

    ConsumerRecord<String, Map<String, Object>> record =
        consumeSingleRecord("prodinventory.events.v1");

    assertThat(record.key()).isEqualTo(breweryId + ":" + facilityId);
    assertThat(record.value()).containsEntry("delta", 5).containsEntry("sku", "MYSTIC-IPA-16OZ");
    assertThat(header(record, "breweryId")).isEqualTo(breweryId.toString());
    assertThat(header(record, "facilityId")).isEqualTo(facilityId.toString());
    assertThat(header(record, "venueId")).isEqualTo(venueId.toString());
    assertThat(header(record, "eventType")).isEqualTo("InventoryAdjusted");
    assertThat(header(record, "occurredAt")).isEqualTo(occurredAt.toString());
    assertThat(header(record, "traceId")).isEqualTo("trace-from-test");
    assertThat(record.timestamp()).isEqualTo(occurredAt.toEpochMilli());
  }

  private ConsumerRecord<String, Map<String, Object>> consumeSingleRecord(String topic) {
    consumer = buildConsumer();
    consumer.subscribe(List.of(topic));
    return KafkaTestUtils.getSingleRecord(consumer, topic);
  }

  @Test
  void defaultsHeadersWhenOptionalMetadataMissing() {
    Instant occurredAt = Instant.parse("2024-09-18T09:00:00Z");
    UUID breweryId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    DomainEventMetadata metadata =
        new DomainEventMetadata(
            "taproom",
            "TaproomSynced",
            breweryId,
            Optional.empty(),
            Optional.empty(),
            occurredAt,
            Optional.empty());

    publisher.publish(metadata, Map.of("status", "OK"));

    ConsumerRecord<String, Map<String, Object>> record = consumeSingleRecord("taproom.events.v1");

    assertThat(record.key()).isEqualTo(breweryId + ":global");
    assertThat(headerOptional(record, "facilityId")).isEmpty();
    assertThat(headerOptional(record, "venueId")).isEmpty();
    assertThat(header(record, "traceId")).isNotBlank();
    assertThat(record.timestamp()).isEqualTo(occurredAt.toEpochMilli());
  }

  private Consumer<String, Map<String, Object>> buildConsumer() {
    Map<String, Object> props =
        KafkaTestUtils.consumerProps("domain-event-it", "true", KAFKA.getBootstrapServers());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

    JsonDeserializer<Map<String, Object>> jsonDeserializer = new JsonDeserializer<>();
    jsonDeserializer.addTrustedPackages("*");
    jsonDeserializer.setRemoveTypeHeaders(false);
    jsonDeserializer.setUseTypeMapperForKey(false);

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer)
        .createConsumer();
  }

  private String header(ConsumerRecord<String, Map<String, Object>> record, String key) {
    return headerOptional(record, key).orElseThrow();
  }

  private Optional<String> headerOptional(
      ConsumerRecord<String, Map<String, Object>> record, String key) {
    if (record.headers().lastHeader(key) == null) {
      return Optional.empty();
    }
    return Optional.of(
        new String(record.headers().lastHeader(key).value(), StandardCharsets.UTF_8));
  }
}
