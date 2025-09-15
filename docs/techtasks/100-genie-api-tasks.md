# Genie API — Initial Task List

Scope: `/api/v1/**` controllers, DTOs/mappers, security enforcement, validation, error handling, OpenAPI docs.

## Sprint 1 — Correctness & Safety
- Add `RestExceptionHandler` producing Problem JSON
  - Map: 400 (validation), 403 (scope), 404 (not found), 409 (optimistic lock), 422 (business validation)
  - Include `traceId` and timestamp
- Request validation
  - Replace inline request records with DTOs annotated with `@Valid` constraints
  - Add `@Validated` at controller level
- Authorization scope checks
  - Centralize checks in a small `AccessPolicy` component using `CurrentUser`
  - Enforce brewery/taproom/bar scoping for read and write endpoints
- Pour invariants (422)
  - Reject negative/zero ounces
  - Enforce preset sizes (4/8/12/16/20); allow override only for TAPROOM_ADMIN (config: `bms.taproom.pour.overrideRole`)
  - Reject over‑pour (> remaining) with 422 unless an explicit flag allows auto‑blow

## Sprint 2 — Concurrency & Pagination
- Expose and enforce optimistic concurrency
  - Add `getVersion()` to `Tap`; include `version` in `TapDto`
  - Support `If-Match` ETag or `expectedVersion` request fields
  - Translate `OptimisticLockingFailureException` to 409
- Pagination & sorting
  - Convert list endpoints to `Page<T>` with `Pageable`
  - Ensure MVC binder resolves `Pageable` (no `@ModelAttribute` misuse); add default page/size if missing
  - Add default sorts (e.g., taps by `number`, kegs by `serialNumber`)

## Sprint 3 — Documentation & Tests
- OpenAPI annotations
  - Add `@Operation`, `@ApiResponses`, request/response schemas, and examples
  - Document error model schema and pour preset override (role‑gated)
- Tests
  - MockMvc tests for all endpoints (happy paths, 400/403/404/409/422)
  - Unit tests for `AccessPolicy` and pour validation logic
  - Pagination tests with `Pageable` arguments across taps/venues/kegs

## KegInventory API (Path Update)
- Change base path to `/api/v1/keg-inventory` for inventory operations
  - Endpoints: `POST /receive`, `POST /{kegId}/move`, `POST /{kegId}/assign`
- Optionally document a future redirect from `/api/v1/inventory/*`
- Update OpenAPI docs and any client references

Artifacts to touch
- `src/main/java/com/mythictales/bms/taplist/api/**`
- `src/main/java/com/mythictales/bms/taplist/security/**`
- `src/main/java/com/mythictales/bms/taplist/service/**` (only to add validation/authz surface as needed)
- `src/main/java/com/mythictales/bms/taplist/config/**` (exception handler, ETag filters)
 - `docs/api-design.md` (KegInventory path, pour rules)

Done criteria
- All endpoints return consistent Problem JSON on errors
- Unauthorized scope access returns 403 reliably
- Pagination present for collections; Swagger shows parameters and page model
- CI green; SpotBugs high‑severity clean; CodeQL no new alerts
 - Pour presets enforced; TAPROOM_ADMIN override verified in tests
 - KegInventory endpoints available under `/api/v1/keg-inventory`
