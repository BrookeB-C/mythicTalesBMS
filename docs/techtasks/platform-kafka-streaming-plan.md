# Platform Plan — Domain Events to Kafka Streams

## Goals
- Publish domain events to Kafka with one topic per top‑level domain (e.g., `prodinventory.events.v1`).
- Ensure events are filterable by Brewery, Venue, and Production Facility via headers and partition keying.
- Provide local dev Kafka (KRaft) via Docker Compose and bootstrap topics.
- Adopt Spring for Apache Kafka with robust serialization, retries, and observability.

## Topic Design
- Naming: `<domain>.events.v1` (e.g., `prodinventory.events.v1`, `taproom.events.v1`).
- Partitions: default 6 (dev), replication factor 1 (dev). Prod to be sized later.
- Keying: message key = `breweryId:facilityId` (string), to colocate facility‑scoped traffic.
- Headers (required for filtering): `breweryId`, `venueId` (optional for non‑venue events), `facilityId`, `traceId`, `eventType`, `occurredAt`.
- Retention: 7d for dev; revisit per domain (events, not compaction). Snapshot/compacted topics may be added separately.

## Serialization
- JSON via Jackson for dev. Option to adopt JSON Schema + Schema Registry in prod later.
- Conventions: flat DTOs, ISO‑8601 timestamps, no secrets, PII minimized.

## Spring Integration (Publish)
- Library: `spring-kafka`.
- Producer config:
  - `key.serializer=org.apache.kafka.common.serialization.StringSerializer`
  - `value.serializer=org.springframework.kafka.support.serializer.JsonSerializer`
  - `acks=all`, `retries=5`, `delivery.timeout.ms=120000`, `enable.idempotence=true`
  - Observability: `MicrometerTracingKafkaPropagation` for trace headers if available.
- Producer factory and `KafkaTemplate<String, Object>` per domain or a routed publisher by header `eventType`.
- Header injection: decorate events with `breweryId`, `venueId`, `facilityId`, `traceId`.

## Spring Integration (Consume/Filter)
- Consumers downstream filter by headers and/or by key prefix (`breweryId:facilityId`).
- Suggest Kafka Streams or Spring Cloud Stream for routing to per‑tenant sub‑topics if needed later.

## Topics & Bootstrap
- Manage topics via admin client (`KafkaAdmin` beans) or one‑off init container in dev.
- Default dev topics:
  - `prodinventory.events.v1`
  - `keginventory.events.v1`
  - `taproom.events.v1`
  - `catalog.events.v1`
  - `sales.events.v1`
  - `distribution.events.v1`
  - `procurement.events.v1`
  - `maintenance.events.v1`
  - `analytics.events.v1`
  - `billing.events.v1`
  - `compliance.events.v1`
  - `iam.events.v1`

## Docker Compose (Dev, KRaft)
- Add `docker/compose/kafka-dev.yml` (single broker, KRaft). Include Kafka UI for local inspection.
- Bootstrap topics via an init service using `kafka-topics` after broker is healthy.

## Security & Profiles
- Dev only: no auth on broker. Do not expose outside localhost.
- Prod plan (follow‑up): TLS/SASL, network policies, Terraform/Helm, Kafka ACLs per app.

## Observability
- Enable client metrics (Micrometer) and logging with correlation ids.
- Add counters/timers for publish success/failure; DLQ if needed later.

## Testing
- Unit tests: publisher header enrichment and keying.
- Integration tests: Testcontainers Kafka; produce/consume round‑trip; header‑based filtering.

## Acceptance Criteria
- Docker Compose starts Kafka; topics exist and are listable.
- Spring app publishes sample domain events with required headers and keying.
- Consumers can filter by Brewery/Venue/Facility using headers and/or key prefix.
- Metrics visible; basic retries/idempotence enabled.

## Tasks (Platform Genie)
- Add Spring Kafka dependency and baseline config under `.../config/kafka/**`.
- Implement a `DomainEventPublisher` with header enrichment and routing by domain.
- Register `KafkaAdmin` to ensure dev topics exist; keep init container as fallback.
- Add `docker/compose/kafka-dev.yml` and `README.md` with run instructions.
- Wire sample publisher in one module (e.g., prodinventory events) behind a profile.
- Add Testcontainers IT for publish/consume.
- Document topic contracts and header schema under `docs/`.

