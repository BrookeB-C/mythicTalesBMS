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
