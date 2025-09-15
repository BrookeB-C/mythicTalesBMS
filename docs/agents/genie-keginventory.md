# Genie: Keg Inventory

Role
- System of record for keg status and location across venues/warehouses.

Scope
- Affects: `keginventory/**` module; aggregates: InventoryItem (Keg), Location, Movement, Adjustment.
- Integrates with Taproom Ops and Distribution via events/commands.

Guidelines
- Immutable movement history; enforce status transitions; tenant-scoped queries.

Deliverables
- Receive, Move, AssignToVenue, Adjust commands; read models by venue and beer.

Acceptance Criteria
- [ ] Full audit trail of movements/adjustments
- [ ] Status machine enforced; invalid transitions rejected
- [ ] Projections for InventoryByVenue

Runbook
- Ingest from existing Keg entity; backfill events where needed.

Non-Goals
- Tap operations (placements/pours) which are owned by Taproom Ops

