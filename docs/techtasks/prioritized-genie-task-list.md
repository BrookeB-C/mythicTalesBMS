# Prioritized Genie Task List (Taproom Ops First)

This queue is ready-to-assign work for an executor (“genie”). Tasks are grouped by priority with clear outcomes and acceptance criteria.

Legend
- Priority: P0 (now), P1 (next), P2 (later)
- Est: rough time (focused hours)
- AC: acceptance criteria

## P0 — Immediate (Unblocks core flows)

1) Command Handlers for Taproom Ops (replace direct service calls)
- Est: 6–10h
- Summary: Introduce command objects and handlers for TapKeg, Pour, Blow, Untap.
- Steps:
  - Create package `taplist.app.commands` with `TapKegCommand`, `PourCommand`, `BlowCommand`, `UntapCommand`.
  - Implement handlers that orchestrate domain logic currently in `TapService` (preserve behavior).
  - Validate inputs and actor permissions at boundary.
- Deliverables:
  - New command classes + handlers, refactored `TapService` to call handlers.
  - Unit tests covering happy paths and edge cases (over‑pour, null keg, etc.).
- AC:
  - Tests green; REST and MVC flows behave identical to current implementation.

2) Domain Events (in‑process) for key actions
- Est: 3–5h
- Summary: Emit `KegTapped`, `BeerPoured`, `KegBlown`, `KegUntapped` via Spring events.
- Steps:
  - Define event classes in `taplist.domain.events`.
  - Publish events from command handlers transactionally.
- Deliverables: Event classes, publishers in handlers, simple logging listener.
- AC: Events emitted on each action; visible in logs during tests.

3) Projections for Taplist and History
- Est: 8–12h
- Summary: Add read models to speed UI and API queries.
- Steps:
  - Flyway V2: tables `taplist_view` (per venue tap state) and `keg_history_view`.
  - Projection updaters (event listeners) to maintain these tables.
  - Repos + read services; optional API endpoints to read projections.
- Deliverables: Migration, listeners, repositories, simple DTOs.
- AC: Taplist and event history can be served from projections with parity to current queries.

4) REST Error Model + Validation (problem+json)
- Est: 4–6h
- Summary: Standardize API errors and input validation.
- Steps:
  - Add `@ControllerAdvice` to return RFC‑7807‑style responses.
  - Bean validation on request DTOs; map validation exceptions.
- Deliverables: Error handler, validated DTOs for Taproom Ops APIs.
- AC: Known error cases return structured `application/problem+json` with codes.

5) Tests: Command + Projection Integration
- Est: 6–8h
- Summary: Integration tests to ensure commands emit events and projections update.
- Steps:
  - Tests that execute commands, then assert projection state.
- Deliverables: New integration test class; data fixtures.
- AC: Green tests proving end‑to‑end flow.

## P1 — Next Up (Quality, Ops, and Guardrails)

6) Rate Limiting for Mutation Endpoints
- Est: 3–5h
- Summary: Add lightweight in‑memory throttling for pour/tap/blow.
- Steps: Integrate Bucket4j (or simple Guava rate limiter) at controller layer.
- AC: Exceeding threshold yields 429 with backoff headers.

7) Observability: Structured Logs + Metrics
- Est: 4–6h
- Summary: Add logs with tapId, venueId, kegId, actor; counters for pours/taps/blows.
- Steps: Mapped diagnostic context (MDC), Micrometer counters.
- AC: Logs include IDs; metrics visible on actuator `/metrics`.

8) Projections Cutover in UI
- Est: 4–6h
- Summary: Switch Thymeleaf pages to read from projections service.
- AC: Same UI, faster queries, no behavioral changes.

9) Additional Indexes and Query Tuning
- Est: 2–4h
- Summary: Flyway V3 with targeted indexes for hot paths; adjust repos to use projections.
- AC: Noticeable reduction in query count/latency on large taprooms.

## P2 — Later (Scalable Foundations)

10) Outbox Pattern (skeleton)
- Est: 8–12h
- Summary: Add outbox table + dispatcher to publish integration events (no broker yet).
- AC: Events persisted in outbox; periodic dispatcher logs “would publish” entries.

11) Inventory Integration Hooks
- Est: 4–8h
- Summary: Translate Taproom Ops events into inventory status changes (stubs).
- AC: Listener writes stub inventory state transitions (to be replaced by Inventory context).

12) API Auth Tokens (JWT)
- Est: 6–10h
- Summary: Add optional Bearer token auth for `/api/**`, behind feature flag.
- AC: Tokens accepted when enabled; session auth remains for MVC.

## Handover & Conventions
- Code style: Spotless + .editorconfig (run `make format`).
- DB migrations: Flyway (create V2, V3... in `src/main/resources/db/migration`).
- Tests: Put new tests under `src/test/java/.../taplist` with clear naming.
- Docs: Update `docs/TaproomOps/tasks.md` and add ADRs when introducing architecture mechanisms (events, outbox, projections).

