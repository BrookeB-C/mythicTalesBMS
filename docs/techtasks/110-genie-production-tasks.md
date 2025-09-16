# Genie Production — Initial Task List

Scope: Production planning and execution — facilities, brew systems, fermentors, runs, packaging.

## P0 — Run Planning MVP
- Endpoints: list facilities, brew systems, fermentors; create/update ProductionRun; shopping list.
- AC: Validations per api-design; warning semantics via Problem JSON; tests in API + repos.

## P1 — Scheduling & Conflicts
- Enforce non-overlapping reservations on brew systems and fermentors; list schedule.

## P2 — Packaging & Metrics
- Packaging runs, volume conservation, and summary metrics.
