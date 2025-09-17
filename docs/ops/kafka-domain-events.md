# Kafka Domain Events — Platform Baseline

## Topics

- Naming pattern: `<domain>.events.v1`
- Provisioned domains (dev default): `prodinventory`, `keginventory`, `taproom`, `catalog`, `sales`, `distribution`, `procurement`, `maintenance`, `analytics`, `billing`, `compliance`, `iam`
- Partitions: 6
- Replication factor: 1 (dev KRaft broker)
- Retention: 7 days (override via `bms.kafka.retention`)

## Message Contract

- Key: `"<breweryId>:<facilityId>"` — `facilityId` defaults to `global` when missing
- Headers (string values unless noted):
  - `breweryId` (required)
  - `facilityId` (optional)
  - `venueId` (optional)
  - `eventType` (required)
  - `occurredAt` (ISO-8601 timestamp, required)
  - `traceId` (generated when absent)
- Kafka timestamp: `occurredAt` as epoch millis
- Payload: JSON encoded via Jackson, flat DTOs per event type. Type headers are disabled to allow polyglot consumers.

## Spring Integration

- Producer configuration lives under `com.mythictales.bms.taplist.config.kafka`
- Enable publisher via `bms.kafka.enabled=true` (default in dev profile)
- Sample publisher (disabled by default) toggled with `bms.kafka.sample.enabled=true`. Customize identifiers with:
  - `bms.kafka.sample.brewery-id`
  - `bms.kafka.sample.facility-id`
  - `bms.kafka.sample.venue-id`

## Local Development

1. Start Kafka stack: `docker compose -f docker/compose/kafka-dev.yml up -d`
2. Run the app (dev profile default) and verify sample event: `BMS_KAFKA_SAMPLE_ENABLED=true mvn spring-boot:run`
3. Inspect topic via Kafka UI at http://localhost:8085 or CLI:
   ```bash
   docker compose -f docker/compose/kafka-dev.yml exec kafka kafka-console-consumer.sh \
     --bootstrap-server localhost:9092 \
     --topic prodinventory.events.v1 \
     --from-beginning \
     --timeout-ms 5000
   ```

## Observability

- `KafkaTemplate` publishes with Micrometer observations when `ObservationRegistry` is present
- Trace IDs propagate from MDC when available, or fall back to generated UUIDs
- Retry configuration: `acks=all`, `retries=5`, delivery timeout 120s, idempotence enabled

## Test Strategy

- `DomainEventPublisherIT` (Testcontainers Kafka) verifies publish headers and payload contracts
- Unit coverage should validate metadata helpers (`partitionKey`, trace ID resolution) as additional publishers emerge

