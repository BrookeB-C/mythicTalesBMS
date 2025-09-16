# Tech Lead Dispatch Queue — Assignable Tasks

Curated list of near-term tasks the Tech Lead can assign to domain genies or platform leads. Each item references backlog priorities, suggests likely owner, and captures acceptance criteria highlights.

## Priority 0 — Correctness & Security

### TLD-001 — Enforce authorization scope on REST endpoints
- **Owner**: genie-api (coordination with service layer as needed)
- **Context**: `docs/techtasks/000-technical-backlog.md` (Priority 0)
- **Scope**: Audit `/api/v1/**` controllers and associated services to ensure `CurrentUser` affiliations gate access (brewery, taproom, bar).
- **AC**:
  - Cross-scope requests respond `403` with Problem JSON body.
  - Happy paths for matching affiliations remain green (MockMvc tests per role).
  - Shared helper (e.g., `AccessPolicy`) lives in API layer but can invoke service checks.

### TLD-002 — REST exception handling with Problem JSON
- **Owner**: genie-api
- **Context**: Priority 0 backlog item "Add REST exception handling and error model"
- **Scope**: Introduce `@ControllerAdvice` for `/api/v1/**` that emits `{status,error,message,details,timestamp,traceId}`.
- **AC**:
  - 404, 403, 409, 422 map to documented error codes.
  - Trace/correlation id extracted from MDC or generated per request.
  - Tests cover representative exceptions + validation failures.

### TLD-003 — Validate API input payloads
- **Owner**: genie-api
- **Scope**: Replace inline request records with DTOs carrying Bean Validation annotations.
- **AC**:
  - Invalid payloads return 400 Problem responses with field-level details.
  - Controller methods annotated with `@Validated`; tests assert rejection cases.

### TLD-004 — Pour invariants and 422 handling
- **Owner**: genie-api (coordination with tap service)
- **Scope**: Update `TapService.pour` to enforce `0 < ounces <= remaining` (unless explicit flag). Surface violations as 422 from API.
- **AC**:
  - Over-pour attempt returns 422; message explains remaining volume.
  - MVC flow retains existing UX (autoblow flag) via explicit opt-in.
  - Unit tests for service + MockMvc regression.

### TLD-005 — Optimistic concurrency on taps/kegs endpoints
- **Owner**: genie-api
- **Scope**: Expose/version taps & kegs (`version` field); support `If-Match` or explicit `expectedVersion`. Handle conflicts as 409 Problem JSON.
- **AC**:
  - Entities expose version in DTO + mappers.
  - Concurrent update test demonstrates 409.
  - Swagger docs updated with concurrency guidance.

### TLD-006 — Lock down H2 console & Swagger UI in prod
- **Owner**: genie-platform-data
- **Scope**: Profile-guard `/h2-console` and `/swagger-ui.html`; prod profile disables by default while keeping `/v3/api-docs` accessible if required.
- **AC**:
  - `application-prod.yml` excludes dev-only endpoints.
  - Regression tests (Spring profile tests) verify restrictions.
  - Docs updated with access instructions per profile.

## Priority 1 — Platform & Data

### TLD-007 — Normalize Tap ↔ Venue assignments
- **Owner**: genie-platform-data (domain support from production genie if needed)
- **Scope**: Ensure `Tap` uses `Venue` as source of truth; eliminate divergence between taproom/bar references.
- **AC**:
  - Migration enforces non-null venue and consistent taproom/bar linkage.
  - Repositories updated; tests cover creation/lookup.

### TLD-008 — Introduce Flyway baseline + seed scripts
- **Owner**: genie-platform-data
- **Scope**: Create `V1__schema.sql` matching current JPA schema and optional dev seed `V2__seed_demo.sql`. Disable `ddl-auto` in prod.
- **AC**:
  - App boots via Flyway in dev/test; CI uses migrations.
  - README docs how to refresh schemas.

### TLD-009 — Observability baseline (Actuator + metrics)
- **Owner**: genie-platform-data
- **Scope**: Enable Actuator endpoints, Micrometer, MDC-enhanced logging.
- **AC**:
  - `/actuator/health` & `/actuator/metrics` respond appropriately.
  - Logs include request/trace ids; sensitive data excluded.
  - Config toggles for prod vs dev documented.

## Priority 2 — API Maturity & UX

### TLD-010 — Pageable collection endpoints
- **Owner**: genie-api
- **Scope**: Replace ad-hoc lists with Spring `Pageable` for taps, kegs, venues, etc. Ensure API responses include page metadata.
- **AC**:
  - Controllers accept `page,size,sort`; tests cover pagination.
  - Repos optimized to avoid N+1 queries.

### TLD-011 — Complete OpenAPI annotations
- **Owner**: genie-api (with PM support for examples)
- **Scope**: Add `@Operation`, `@ApiResponses`, schema examples, and error model docs across `/api/v1/**`.
- **AC**:
  - `/v3/api-docs` reflects enriched metadata; Swagger UI shows descriptions/examples.
  - Error schemas documented; manual QA doc references swagger location.

## Cross-Cutting Support

### TLD-012 — Manual QA regression dry run
- **Owner**: tech lead to coordinate with PM & QA
- **Scope**: Execute `docs/testing/manual-qa.md` against current build, log failures, triage follow-ups.
- **AC**:
  - Results captured (pass/fail + notes) in release checklist or new doc.
  - Blocking issues promoted to backlog with owners.

### TLD-013 — Review branch hygiene & feature workflow
- **Owner**: tech lead
- **Scope**: Audit `docs/tech/reviewbranches/` for stale entries, ensure each active branch references tasks/docs.
- **AC**:
  - Up-to-date review entries; completed branches archived or merged.
  - Identified gaps communicated to responsible genies.

---
Update this dispatch list after each planning cycle; link assigned items back into `docs/techtasks/000-technical-backlog.md` and the relevant review entries.
