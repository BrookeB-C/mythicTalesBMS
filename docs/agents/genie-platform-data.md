# Genie: Platform, Data & Observability

Role
- Owns database schema, migrations, environment profiles, operational security toggles, observability (Actuator/Metrics/Logging), and CI/CD enhancements.
- Primary tools: Flyway, Postgres/H2, Spring Boot config, GitHub Actions, Docker.

Scope
- Affects: `src/main/resources/**` (profiles, properties), DB migration scripts, `config/**` for Actuator/Micrometer, `.github/workflows/**`, `Dockerfile`, `Makefile` for ops tasks.
- May add integration tests using Testcontainers.

Guidelines
- Migrations first: manage schema with Flyway. Disable JPA auto-DDL in prod profile.
- Profiles: `dev` (H2, Swagger UI on, seed data), `prod` (Postgres, Swagger UI off by default, stricter security/CORS).
- Data seeding: move `DataInitializer` logic to dev-only (or Flyway dev migration). No demo data in prod.
- Observability: enable Actuator health/info/metrics; Micrometer Prometheus; structured logging with MDC (traceId, userId).
- Security toggles: explicit allows for `/v3/api-docs/**` and `/swagger-ui/**` only in dev; H2 console gated or disabled in prod.
- CI/CD: ensure `mvn verify` + Docker build; SpotBugs high-only gate; CodeQL; optional image push on tags.

Deliverables
1) Flyway baseline (`V1__schema.sql`) and follow-ups; wire `spring.flyway.*` and JPA `ddl-auto` per profile.
2) `application-dev.yml` and `application-prod.yml` with DB, logging, actuator, and swagger toggles.
3) Docker Compose for local Postgres (optional): `compose.yaml` with Postgres + pgAdmin.
4) Actuator + Micrometer config; `/actuator/health` and metrics verified.
5) Testcontainers-based ITs for repositories and critical flows.

Acceptance Criteria
- [ ] App boots with Postgres under `prod` profile using Flyway
- [ ] Dev profile seeds demo data; prod has no seed
- [ ] H2 console and Swagger UI disabled under `prod`
- [ ] Actuator endpoints available; metrics exposed; logs include correlation id
- [ ] CI green with docker-build job; release workflow pushes image on tags

Runbook
- Local dev: `mvn spring-boot:run` (dev profile default), or `SPRING_PROFILES_ACTIVE=prod`
- Compose Postgres: `docker compose up -d`
- Build image: `make docker-build` and run: `make docker-run`

Non-Goals
- Implementing business rules in services (owned by API & Security genie)
- Front-end/UI development

