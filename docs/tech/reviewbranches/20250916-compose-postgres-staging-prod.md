# feature/platform/compose-postgres-staging-prod

- Area: platform
- Owner: genie-platform-data
- Task: docs/techtasks/101-genie-platform-data-tasks.md (Sprint 3 — Postgres enablement)
- Summary: Add Docker Compose for Postgres (staging & prod) and wire Spring profiles
- Scope: compose.yaml, .env.example, Makefile, README, application-staging.yml, application-prod.yml, pom.xml (Postgres driver)
- Risk: low (dev-only infra; isolated volumes; no schema change)
- Test Plan:
  - Copy `.env.example` to `.env` and set strong passwords
  - `make db-up-staging` and `make db-up-prod` bring containers up healthy
  - `SPRING_PROFILES_ACTIVE=staging mvn spring-boot:run` connects to staging DB
  - `SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run` connects to prod DB
  - Verify Flyway runs and app boots
- Status: in-progress
- PR: <tbd>

## Notes
- Uses Compose profiles to isolate staging (`5433`) and prod (`5432`).
- Healthchecks via `pg_isready`; data persisted in separate volumes.
- Swagger UI enabled in staging, disabled in prod; H2 console disabled for both.
