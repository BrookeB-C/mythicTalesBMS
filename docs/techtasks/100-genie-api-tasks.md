# Genie API — Initial Task List

Scope: `/api/v1/**` controllers, DTOs/mappers, security enforcement, validation, error handling, OpenAPI docs.

Dispatch assignments (from 130-techlead-dispatch.md)
- TLD-001 Enforce authorization scope on REST endpoints
- TLD-002 REST exception handling with Problem JSON
- TLD-003 Validate API input payloads
- TLD-004 Pour invariants and 422 handling
- TLD-005 Optimistic concurrency on taps/kegs endpoints
- TLD-010 Pageable collection endpoints
- TLD-011 Complete OpenAPI annotations

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

## Catalog — Recipe Import (BeerXML/BeerSmith)
- Expand importer mapping
  - Parse and persist `RecipeFermentable`, `RecipeHop`, `RecipeYeast`, `RecipeMisc`, `MashStep`
  - Unit conversions (L, kg/g, °C) and safe defaults
- Read endpoints
  - `GET /api/v1/catalog/recipes/{id}` → Recipe with child components
  - `GET /api/v1/catalog/recipes?breweryId=...&q=...&page=...` → paginated list
  - Enforce brewery tenant scoping in repos and controllers
- Dedup/force semantics
  - Current: 409 on duplicate `source_hash` unless `?force=true`
  - Optional (later): if `force=true`, update existing recipe in-place rather than creating a new row
- OpenAPI + Problem JSON
  - Document import endpoint (`multipart/form-data`), error codes: `DUPLICATE_RECIPE`, `IMPORT_FAILED`
- Validation & limits
  - Validate XML mimetypes/size; reject files > configurable size; return 422 on invalid structure
- Tests
  - Import tests (single/multiple recipes), dedup 409 vs `force=true`, ingredient counts match
  - Read API tests for single + paginated list with tenant scoping

Artifacts
- `catalog.service.RecipeImportService` (expand mapping)
- `catalog.api.RecipeImportController` (error model + docs)
- Repos for Recipe + list endpoints
- `docs/catalog/recipes-import.md` (keep in sync)

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
- BJCP styles API
  - `GET /api/v1/catalog/styles` and `GET /api/v1/catalog/styles/{id}` for lookup
  - `POST /api/v1/catalog/styles/import` (CSV, SITE_ADMIN)
  - CSV columns: `code,name,category,subcategory,year,og_min,og_max,fg_min,fg_max,ibu_min,ibu_max,abv_min,abv_max,srm_min,srm_max,notes`
  - Tenant linking: Beers can link to `BjcpStyle` via `beer.styleRef`
  - Server-side filters: `year` and `q` (matches `code` or `name`), plus `page/size/sort`

## Production — Run Planning MVP
- Entities & lists
  - Add `GET /api/v1/production/facilities`, `/brew-systems`, `/fermentors` with capacity + unit fields.
  - Add `GET /api/v1/production/fermentors/{id}/schedule` (calendar view).
- Plan runs
  - `POST /api/v1/production/runs` with `brewSystemId`, optional `fermentorId`, `recipeId` or on‑run recipe, `startAt`, scaled target volume.
  - Validate brew system availability; warn if fermentor not available for expected finish; allow optional assignment.
  - `PATCH /api/v1/production/runs/{id}` to edit recipe on the run; optional save to catalog as NEW or REPLACE.
- Shopping list
  - `GET /api/v1/production/runs/{id}/shopping-list` computed from scaled recipe against Production Inventory.
- OpenAPI + tests
  - Document fields, warning semantics, and scaling; add MockMvc tests for happy path and warnings.
- Acceptance criteria
  - Endpoints return 200/201 on valid requests; 400 on validation errors; 403 on scope violations; 409 on optimistic conflicts; 422 on business rule violations (e.g., unavailable system).
  - Brew system availability validated; optional fermentor assignment warning surfaced via Problem JSON `details`.
  - Shopping list endpoint returns computed materials with quantities and units derived from scaled recipe.
  - OpenAPI docs include schemas and examples; tests cover happy/warning/error paths.

## Partners — External Recipients
- Directory endpoints
  - `GET/POST /api/v1/partners/external-venues`, `GET/POST /api/v1/partners/distributors` (tenant‑scoped).
- Keg distribution (external)
  - Extend `POST /api/v1/kegs/{id}/distribute` to accept either `venueId` or an external partner id; validate mutual exclusivity.
  - Kegs sent to external partners remain `DISTRIBUTED` until `POST /return`.
- OpenAPI + tests
  - Add schemas for partner entities; tests for distribute→return flows (internal vs external).
- Acceptance criteria
  - Partners directory endpoints support create/list with tenant scoping and validation; duplicates rejected with 409.
  - Distribute accepts exactly one destination (internal venue OR external partner) and rejects ambiguous requests with 400.
  - Kegs distributed externally remain DISTRIBUTED until returned; return flow updates status correctly.
  - OpenAPI examples reflect internal vs external flows; MockMvc tests cover both.

## IAM — Roles Alignment
- Define additional roles aligned to requirements: `BREWER`, `HEAD_BREWER`, `TAPROOM_MANAGER` (map to contexts and permissions).
- Update access policy checks for new roles where applicable (production planning endpoints).
- Acceptance criteria
  - New roles recognized by access policy; endpoints gated appropriately (e.g., production planning requires BREWER/HEAD_BREWER).
  - Swagger documents authorization requirements; unauthorized returns 403 with Problem JSON.
