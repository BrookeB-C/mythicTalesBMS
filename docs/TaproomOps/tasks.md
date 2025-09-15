# Taproom Ops — Task List

This is a scoped backlog for the Taproom Operations context (taps, keg lifecycle, taplist UI/API). Tasks are grouped by area and ordered roughly by priority. Use this as a working checklist.

## Foundation & Domain
- [ ] Review and finalize domain model: `Tap`, `KegPlacement`, `KegEvent`, `Venue`.
- [ ] Enforce invariants in domain layer (single active placement per tap; remaining ounces ≥ 0; valid status transitions).
- [ ] Add optimistic locking (`@Version`) to aggregates that mutate frequently (`Keg`, `Tap`, `KegPlacement`).
- [ ] Add DB indexes for common lookups: `tap(venue_id, number)`, `keg_event(placement_id, at_time)`, `keg_placement(tap_id, ended_at)`.
- [x] Add Flyway migrations for schema, constraints, and indexes.

## Commands & Application Services
- [ ] Introduce command objects: `TapKegCommand`, `PourCommand`, `BlowCommand`, `UntapCommand`.
- [ ] Refactor `TapService` to command handlers with clear input validation and domain errors.
- [ ] Add idempotency key support for commands that may be retried (e.g., pour from POS).
- [ ] Validate actor permissions/venue ownership at command boundary.

## Domain Events & Projections (CQRS)
- [ ] Emit events: `KegTapped`, `BeerPoured`, `KegBlown`, `KegUntapped` from command handlers.
- [ ] Build projections: `TaplistView` (per venue), `KegHistoryView` (events/placements), `RecentActivityView`.
- [ ] Add event listeners to update projections transactionally.
- [ ] Add outbox table and publisher (in‑process), gated off by feature flag.

## REST API
- [ ] Finalize endpoints and request/response DTOs:
  - [ ] `POST /api/v1/taps/{tapId}/tap-keg {kegId}`
  - [ ] `POST /api/v1/taps/{tapId}/pour {ounces}`
  - [ ] `POST /api/v1/taps/{tapId}/blow`
  - [ ] `GET  /api/v1/taps?venueId=...` (taplist view)
  - [ ] `GET  /api/v1/taps/{tapId}/events` (history)
- [ ] Add validation and error responses (problem+json) with stable error codes.
- [ ] Add rate limiting on mutation endpoints (basic in‑memory for now).

## MVC & UI (Thymeleaf)
- [ ] Align Taplist page with projection model (remove direct entity coupling).
- [ ] Add AJAX flows for pour/tap/blow with optimistic UI updates.
- [ ] Add visual warnings for low volume (15% threshold) and blown kegs; ensure the threshold is configurable with default 15%.
- [ ] Improve accessibility (ARIA labels for SVG pint, focus states, error summaries).
- [ ] Replace deprecated Thymeleaf fragment syntax (done for footer) sweep full templates.
 - [ ] Big‑board display mode: dedicated MVC route `GET /taplist/board?venueId=...`; auto‑refresh every 15s; show beer name, style, ABV, fill %; responsive for TV displays.
 - [ ] QR scanning (camera/PWA): parse JSON payload with `serial` and `breweryId`; graceful fallback to manual entry.

## Security & Roles
- [ ] Confirm route guards for `TAPROOM_ADMIN`, `TAPROOM_USER`, and brewery/bar admins.
- [ ] Add method‑level checks on command handlers.
- [ ] Ensure cross‑venue access is blocked (tenant and venue scoping at query layer).

## Integrations (Internal)
- [ ] Prepare integration with `Inventory` context (future): translate taproom events → inventory status changes.
- [ ] Publish lightweight integration events via outbox once Inventory exists.

## Observability & Ops
- [ ] Structured logs for commands and events (tapId, venueId, kegId, actorUserId).
- [ ] Counters/metrics: pours by size, taps/blows per day, active taps by venue.
- [ ] Audit trail export for keg placements and events.

## Testing
- [ ] Unit tests: domain invariants and command handlers (happy paths + edge cases).
- [ ] Integration tests: repository queries, projections updated by events.
- [ ] Web tests: REST endpoints and MVC flows (security + CSRF).
- [ ] Performance check: large taprooms (50–100 taps) render quickly.

## Data & Seeding
- [ ] Expand `DataInitializer` to generate realistic taproom scenarios (varying sizes, statuses).
- [ ] Fixture builders for taps/kegs/placements for fast test setup.

## Migration Plan
- [ ] Introduce new tables/columns with backward‑compatible migrations.
- [ ] Backfill projections from existing `KegEvent` and `KegPlacement` history.
- [ ] Toggle cutover: UI reads from projections instead of live entities.

## Documentation
- [ ] API reference (OpenAPI groups for Taproom Ops).
- [ ] ADR: choose modular monolith with event outbox for now.
- [ ] Operational runbook: common issues (stuck placements, inconsistent statuses) and fixes.

---

Notes
- Keep Taproom Ops as the primary driver for eventing patterns; other contexts will subscribe as they are implemented.
- Prefer small, transactional projections over complex joins on hot paths.
