# Acceptance Checklist (cross-cutting)

Use this checklist to verify completion of high-priority tasks across environments.

- Security & AuthZ
  - [ ] API scope checks block cross-brewery/venue access (403)
  - [ ] H2 and Swagger UI disabled in prod profile
  - [ ] CORS configured per environment

- API Quality
  - [ ] Consistent error model with correlation id
  - [ ] Validation annotations on request DTOs
  - [ ] Pagination implemented on all collection endpoints
  - [ ] 409 on optimistic lock conflicts; 422 on validation errors

- Data & Migrations
  - [ ] Flyway baseline applied; no auto DDL in prod
  - [ ] Dev seed isolated to dev profile or Flyway dev scripts

- Observability
  - [ ] Actuator health, info, metrics enabled
  - [ ] Structured logs with request id + user id

- Testing
  - [ ] Unit tests for `TapService`
  - [ ] MockMvc tests for API with authZ scope cases
  - [ ] Testcontainers IT against Postgres

- CI/CD
  - [ ] CodeQL green, SpotBugs high-severity clean
  - [ ] Docker image builds on tags; versioned + latest
  - [ ] Swagger JSON published as artifact

Last updated: YYYY-MM-DD

