# feature/platform/kafka-streaming

- Area: platform
- Owner: genie-platform-data
- Task: docs/techtasks/platform-kafka-streaming-plan.md
- Summary: Wire Kafka producer baseline (topics, publisher, dev stack)
- Scope: pom.xml, src/main/java/com/mythictales/bms/taplist/config/kafka/**, src/main/java/com/mythictales/bms/taplist/kafka/**, src/main/resources/application*.yml, docker/compose/kafka-dev.yml, Makefile, README.md, docs/ops/kafka-domain-events.md, tests
- Risk: low (dev-only broker, no consumer side-effects)
- Test Plan:
  - `docker compose -f docker/compose/kafka-dev.yml up -d`
  - `BMS_KAFKA_SAMPLE_ENABLED=true mvn spring-boot:run` (verify sample event)
  - `mvn -q -DskipTests compile` and targeted Testcontainers IT
- Status: in-progress
- PR: <tbd>

## Notes
- Topics provisioned via Spring `NewTopic` definitions; compose init container double-checks availability.
- Headers follow plan (`breweryId`, `facilityId`, `venueId`, `eventType`, `occurredAt`, `traceId`).
- Sample publisher gated by `bms.kafka.sample.enabled` for smoke testing.
