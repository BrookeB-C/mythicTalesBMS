# Genie KegInventory — Task List (aligned with PM plan)

Scope: System of record for keg locations and statuses; integration with Taproom events; user‑facing docs and demo assets.

## P0 — Receive/Move/Assign/Return (MVP) + Docs
- API base path alignment
  - Ensure endpoints live under `/api/v1/keg-inventory` (Receive, Move, Assign, Return).
  - AC: Requests succeed with 200/201; invalid inputs → 400; scope violations → 403; unsupported transitions → 422; Problem JSON used.
- Acceptance criteria (per AC template)
  - Draft AC for each operation (Given/When/Then) referencing states: FILLED, RECEIVED, TAPPED, BLOWN, EMPTY, RETURNED.
  - Include tenant scoping rules (brewery → venues) and conflict handling.
- User docs (PM deliverable)
  - Add “Keg Inventory Basics” to `docs/features/` describing Receive, Move, Assign, Return with examples.
  - Add curl examples and expected Problem JSON snippets.
- Demo assets
  - Place example payloads under `src/main/resources/samples/keg-inventory/` (receive.json, move.json, assign.json, return.json) with a README.
- Event hooks (integration)
  - Add stubs/listeners to react to Taproom events (tap, pour/blow, untap) to keep inventory consistent.

## P1 — Location History + External Partners
- Location history (audit)
  - Persist movement history (from → to); immutable audit; indexes for kegId/time.
  - AC: History endpoint returns paginated moves; ordering by time; filters by keg/venue.
- External partners alignment (from PM design)
  - Support DISTRIBUTED flows to/from external partners; remain DISTRIBUTED until return.
  - AC: Distribute records external destination reference; return transitions status back to EMPTY/RECEIVED as appropriate.
- Docs
  - Update features doc with external partner scenarios; add examples.

## P2 — Reconciliation & Reporting
- Reconciliation
  - Add checks for mismatch between Taproom placements/events and Inventory state; admin endpoint to list anomalies.
  - AC: Reconciliation report lists stale placements, missing returns, or double‑assigned kegs.
- Reporting
  - Simple counts by status and by venue/brewery; expose as read endpoints for dashboards.

Artifacts
- API: `src/main/java/.../api/**` (if endpoints live here) or maintain in domain specific controllers.
- Event hooks: listeners that translate Taproom events → Inventory updates.
- Docs: `docs/features/keg-inventory-basics.md`; Problem JSON examples.
- Samples: `src/main/resources/samples/keg-inventory/*` with README.

References
- PM tasks: `docs/techtasks/102-genie-product-manager-tasks.md`
- API plan: `docs/techtasks/100-genie-api-tasks.md` (KegInventory base path)
- Design notes: `docs/api-design.md` (distribution and return semantics)
