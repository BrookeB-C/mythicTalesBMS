# Genie Platform/Data — Initial Task List

Scope: DB schema/migrations, profiles, observability, CI/CD, infrastructure.

## Sprint 1 — Profiles & Migrations
- Introduce profiles
  - `application-dev.yml`: H2, Swagger UI enabled, relaxed CORS, demo seed enabled
  - `application-prod.yml`: Postgres, Swagger UI disabled by default, strict CORS
- Flyway baseline
  - Add `flyway` dependency and `V1__schema.sql` matching current JPA schema
  - Configure `spring.jpa.hibernate.ddl-auto=none` in prod
- Seed data (dev only)
  - Move demo data from `DataInitializer` behind `@Profile("dev")` or into `V2__seed_demo.sql` marked dev-only
 - Config properties
  - Bind feature flags with `@ConfigurationProperties(prefix = "bms")`
  - Defaults:
    - `taproom.lowVolume.defaultPercent=15`
    - `taproom.lowVolume.scope=venue`
    - `taproom.lowVolume.notifyRoles=[TAPROOM_ADMIN,BAR_ADMIN]`
    - `taproom.pour.overrideRole=TAPROOM_ADMIN`
    - `ui.bigboard.refreshSeconds=15`
    - `scan.input=camera`, `scan.qr.payload=json`
    - `keginventory.apiBasePath=/api/v1/keg-inventory`

## Sprint 2 — Observability & Logging
- Add Actuator + Micrometer Prometheus registry
- Structured logging with MDC
  - Include `traceId` and `userId` in logs (via filter/interceptor)
- Health and readiness probes
  - Enable `/actuator/health` and `/actuator/info` endpoints
 - Release operations
  - Document weekly Friday release window and rollback steps (prior image + DB rollback checklist)

## Sprint 3 — DB & CI Enhancements
- Postgres enablement
  - Provide `compose.yaml` for local Postgres + pgAdmin
  - Verify app boots against Postgres
- Testcontainers integration tests
  - Add ITs for repositories and a couple of service flows
- CI/CD
  - Cache Docker layers in docker-build job
  - Optionally publish Docker images to GHCR on tags (already configured)

## Sprint 4 — Production & Partners Schema
- Production domain tables (Flyway V4)
  - `production_facility` (id, brewery_id, name, address fields optional)
  - `brew_system` (id, facility_id, name, capacity, unit: `GALLONS|LITERS|BBL`)
  - `fermentor` (id, facility_id, name, capacity, unit)
  - `production_run` (id, brewery_id, facility_id, brew_system_id, fermentor_id?, recipe_id?, scaled_target_volume, unit, start_at, notes)
  - `packaging_run` (id, production_run_id, packaged_volume, unit, packaged_at)
  - Indexes: foreign keys, lookup by facility/brewery; schedule queries by `start_at`
- Partners directory (Flyway V5)
  - `external_venue` (id, brewery_id, name, contact?, address?, unique(brewery_id,name))
  - `distributor` (id, brewery_id, name, license_number?, contact?, unique(brewery_id,name))
- Keg distribution reference (later)
  - Plan optional reference to external destination for distribution events if persisted outside `keg_event` notes
- Testcontainers ITs
  - Add repository integration tests for new tables (basic CRUD + constraints)
- Acceptance criteria
  - Fresh DB migrates V1..V5 cleanly; existing dev DB upgrades without data loss
  - Repositories for Production and Partners operate against H2 and Postgres (Testcontainers)
  - Indexes present for schedule queries and common lookups

Artifacts to touch
- `src/main/resources/application-*.yml`
- `src/main/java/**/config/**` (logging filters, actuator configuration)
- `src/test/java/**` (Testcontainers ITs)
- `db/migration/**` (Flyway)
- `compose.yaml`, `Dockerfile`, `.github/workflows/**`

Done criteria
- Dev and prod profiles behave as specified
- Database created and migrated via Flyway
- Health and metrics endpoints available; logs include correlation ids
- CI green; Docker image builds in CI; manual smoke test passes with Postgres

Dispatch assignments (from 130-techlead-dispatch.md)
- TLD-006 Lock down H2 console & Swagger UI in prod
- TLD-007 Normalize Tap ↔ Venue assignments
- TLD-008 Introduce Flyway baseline + seed scripts
- TLD-009 Observability baseline (Actuator + metrics)

## Sprint 5 — Observability Maturity (Tracing, Logs, Metrics)
- Tracing (OpenTelemetry)
  - Add micrometer-tracing-bridge-otel; propagate `traceId`/`spanId` via W3C and/or B3.
  - AC: `traceId` present in logs and Problem JSON; spans around MVC/API handlers and DB calls.
- Structured JSON logging
  - Logback JSON encoder profile: `dev` human‑readable; `prod` JSON lines. MDC fields: `traceId`, `userId`, optional `venueId`/`kegId` when known.
  - AC: Logs parse as JSON; sensitive fields redacted; correlation works across async threads.
- Metrics
  - Micrometer timers for controllers/HTTP server; counters for tap/pour/blow/untap; gauges for keg status counts.
  - AC: `/actuator/metrics` exposes custom meters; sample dashboards described in docs.

Artifacts
- `pom.xml`, `src/main/resources/logback-*.xml`, `src/main/java/**/config/TracingConfig.java`
- `docs/ops/observability.md` (how-to, field mapping)

## Sprint 6 — Runtime Profiles Hardening
- Management endpoints
  - Dev: expose `health,info,prometheus,metrics,loggers`. Prod: `health,info` only; `/h2-console` and Swagger UI disabled.
  - AC: Profiles enforce exposure; security returns 403 for blocked endpoints in `prod`.
- Connection pool tuning
  - Hikari defaults (timeouts, max size, leak detection in `dev`). Expose pool metrics via Micrometer.
  - AC: Pool metrics visible; timeouts prevent thread starvation under load.
- 12‑factor configuration
  - Externalize secrets via env; document precedence; fail fast on missing required envs.
  - AC: No secrets logged; startup fails with clear message when config missing.

Artifacts
- `src/main/resources/application-dev.yml`, `application-prod.yml`
- Security/OpenAPI conditional config; `docs/ops/runtime-profiles.md`

## Sprint 7 — DB Performance & Housekeeping
- Repeatable migrations and indexes
  - Add Flyway `R__indexes.sql` for hot‑path queries; validate parity H2 vs Postgres.
  - AC: Query plans improved for tap/keg list endpoints; docs include sample EXPLAIN plans.
- Maintenance guidance (docs)
  - Vacuum/Autovacuum and basic Postgres maintenance notes.
  - AC: `docs/db/postgres-maintenance.md` covers defaults and recommended overrides.
- Backups & restore runbook (docs)
  - Steps for `pg_dump/pg_restore` and local restore.
  - AC: Runbook validated locally.

Artifacts
- `src/main/resources/db/migration/R__indexes.sql`, `docs/db/*.md`

## Sprint 8 — Testcontainers & CI Enhancements
- Testcontainers base
  - Base IT class for repositories/services using Postgres container; profile‑guarded.
  - AC: At least two ITs (repo + simple service) run green locally and in CI.
- CI performance
  - Cache Maven repo; cache Docker/Testcontainers layers; split unit vs IT stages.
  - AC: CI time reduced; no flaky retries needed.

Artifacts
- `src/test/java/**/it/*`, `.github/workflows/ci.yml`, `Makefile`

## Sprint 9 — Local Dev UX
- Compose stack
  - `compose.yaml` for Postgres + pgAdmin; seeded bootstrap user.
  - AC: `docker compose up` + `SPRING_PROFILES_ACTIVE=prod` boots app against Postgres locally.
- Developer bootstrap scripts
  - `bin/dev-up.sh` to start stack; `bin/dev-down.sh` to clean.
  - AC: One‑command spin‑up documented in README.

Artifacts
- `compose.yaml`, `bin/dev-*.sh`, `README.md` additions
