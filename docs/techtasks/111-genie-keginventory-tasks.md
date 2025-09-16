# Genie KegInventory — Initial Task List

Scope: System of record for keg locations and statuses; integration with Taproom events.

## P0 — API Base Path & Hooks
- Confirm base path `/api/v1/keg-inventory`; add stubs to react to Taproom events (receive, tap, blow, return).

## P1 — Location History
- Persist movement history and ensure immutable audit.

## P2 — Reconciliation
- Add periodic checks and admin tools to reconcile inventory mismatches.
