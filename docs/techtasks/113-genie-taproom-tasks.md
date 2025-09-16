# Genie Taproom — Initial Task List

Scope: Tap operations (Tap, Pour, Blow, Untap), projections, low-volume alerts.

## P0 — Command Handlers & Events
- Replace direct service calls; emit domain events; low-volume threshold check.

## P1 — Projections
- Maintain `taplist_view` and `keg_history_view`; serve UI/API from projections.

## P2 — Big-Board
- Read-only board with auto-refresh; markers for UI tests.
