# Technical Backlog — Mythic Tales BMS

This is a prioritized, actionable backlog from a Tech Lead perspective. Items are grouped by priority and area with suggested acceptance criteria.

## Dispatch Assignments (Tracked)
- TLD-012 — Manual QA regression dry run
  - Owner: techlead (coordinate PM & QA)
  - Actions: Run `docs/testing/manual-qa.md`, capture results and triage follow-ups into appropriate genie task lists.
- TLD-013 — Review branch hygiene & feature workflow
  - Owner: techlead
  - Actions: Audit `docs/tech/reviewbranches/`, close/merge or archive stale entries; ensure each active branch links tasks/docs.

## Priority 0 — Correctness, Security, Stability

- Enforce authorization scope in REST API
  - Problem: API endpoints (kegs, taps, taprooms) do not validate that the authenticated principal belongs to the target brewery/venue/bar/taproom.
  - Actions: Add policy checks in service layer (preferred) or controller layer; centralize with helper (e.g., `AccessPolicy` component) using `CurrentUser`.
  - AC: Requests with mismatched scope return 403; happy paths remain unaffected; tests cover brewery-, taproom-, and bar-scoped users.

- Add REST exception handling and error model
  - Problem: No `@ControllerAdvice` for REST; inconsistent responses and status codes.
  - Actions: Add `RestExceptionHandler` returning consistent problem JSON with 4xx/5xx, including 404/403/409/422 mapping.
  - AC: All API errors conform to docs/api-design.md error shape; include correlation id; documented in Swagger.

- Input validation for API requests
  - Problem: Inline request records lack bean validation.
  - Actions: Introduce dedicated request DTOs with `@Valid` annotations (e.g., `@NotNull`, `@Positive`), and add `@Validated` at controller level.
  - AC: Invalid inputs yield 400 with field errors; tests included.

- Pour invariants and 422 handling
  - Problem: `TapService.pour` allows over-pour; silently clamps to 0 and triggers blow.
  - Actions: Validate ounces > 0 and <= remaining; return 422 on invalid; optionally add a flag to allow auto-blow.
  - AC: Over-pour returns 422; valid pour updates remaining accurately; tests cover edge cases.

- Optimistic concurrency for API updates
  - Problem: DTOs not using entity `version` consistently; `Tap` lacks a getter.
  - Actions: Add `getVersion()` on `Tap`; expose `version` in `TapDto`; support `If-Match` ETag or `expectedVersion` fields; handle `OptimisticLockingFailureException` as 409.
  - AC: Concurrent updates produce 409 with clear message; Swagger documents versioning.

- Lock down H2 console and Swagger in prod
  - Problem: `/h2-console` and Swagger UI are globally permitted.
  - Actions: Add Spring profiles; only enable H2 and Swagger UI on `dev`; keep `/v3/api-docs` accessible if required; gate via security or conditional config.
  - AC: In `prod` profile, H2 console and Swagger UI are disabled by default.

## Priority 1 — Architecture, Data, Observability

- Normalize Tap-Venue relationships
  - Problem: `Tap` references `Venue` and also `Taproom`/`Bar`, which can drift.
  - Actions: Choose single source of truth: keep `Venue` on `Tap`, derive taproom/bar as needed; add DB constraint or migration to ensure consistency.
  - AC: New taps cannot be saved with inconsistent venue link; migration documented.

- Keg status state machine
  - Problem: Ad-hoc transitions (EMPTY→CLEAN→FILLED→DISTRIBUTED→RECEIVED→TAPPED→BLOWN→RETURNED) without enforcement.
  - Actions: Implement small state machine or validator to guard transitions; centralize in service; log invalid attempts.
  - AC: Invalid status transitions return 422; transitions are unit tested.

- Migrations with Flyway
  - Problem: Schema relies on JPA auto DDL and `DataInitializer` for seed.
  - Actions: Add Flyway; create baseline `V1__schema.sql` and optional seed `V2__seed_demo.sql` (dev only); disable `ddl-auto` in prod.
  - AC: App boots with Flyway; CI verifies migrations; dev profile seeds demo data; prod does not.

- Database profiles and Postgres support
  - Actions: Add `application-dev.yml` (H2) and `application-prod.yml` (Postgres); Docker Compose for local Postgres + pgAdmin; Testcontainers in integration tests.
  - AC: App runs on Postgres with same features; CI integration tests run with Testcontainers.

- Observability & Ops
  - Actions: Add Spring Boot Actuator (+ Prometheus endpoint), Micrometer, basic health/info metrics; structured logging with MDC (request id, user id); add request/response logging for API (redact secrets).
  - AC: `/actuator/health` green; metrics exposed; logs include correlation id.

## Priority 2 — API Maturity, UX, Tooling

- Pagination and sorting on list endpoints
  - Actions: Replace list returns with `Page<T>` using `Pageable`; add indices on common filters (keg status, venue id, brewery id).
  - AC: Endpoints accept `page,size,sort` and return page metadata; N+1 queries avoided.

- Complete OpenAPI documentation
  - Actions: Add `@Tag`, `@Operation`, `@ApiResponses`, and schema examples; enable `springdoc` bean customizers; include error model.
  - AC: `/v3/api-docs` fully depicts endpoints with examples; Swagger UI groups are clear.

- REST error consistency for MVC endpoints
  - Actions: If any JSON endpoints remain under MVC controllers, align to same error model or migrate to API controllers.

- CORS configuration for API
  - Actions: Allow configurable origins for SPA/mobile clients; restrict methods/headers appropriately.
  - AC: CORS works in dev; locked down in prod via config.

- DTO refinements
  - Actions: Include derived fields (e.g., `fillPercent`) in `KegDto`; add thin request DTOs vs inline records; map nulls consistently.

- Test coverage
  - Actions: Unit tests for `TapService`; MockMvc tests for API controllers (auth + scope); repository slice tests; Testcontainers ITs with Postgres.
  - AC: Coverage baseline ≥70% on service+controller packages; critical flows covered.

- CI/CD enhancements
  - Actions: Add SonarCloud (if available); cache Docker layers; publish Swagger JSON as artifact; guard release workflow with conventional tags.

## Priority 3 — Cleanup, Developer Experience

- Replace `DataInitializer` with profile-based seeders
  - Actions: Move demo data into `dev` profile bean or Flyway seed; keep prod clean.

- Pre-commit hooks
  - Actions: Add `.husky`/`pre-commit` or simple script for `spotless:apply` + `spotbugs:spotbugs` high-level check.

- Ownership & docs
  - Actions: Add CODEOWNERS; CONTRIBUTING.md; local environment bootstrap docs; architecture diagrams (C4).

- Performance notes
  - Actions: Review eager/lazy loading; ensure event queries use indexes; consider projection DTOs to reduce payloads.

---

Last updated: YYYY-MM-DD
