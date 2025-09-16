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
