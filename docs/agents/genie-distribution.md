# Genie: Distribution & Logistics

Role
- Owns shipments, delivery routes, returns processing for kegs.

Scope
- Affects: `distribution/**` module; aggregates: Shipment, ShipmentItem, ReturnAuthorization; integration with KegInventory.

Guidelines
- Shipments immutable post-dispatch; proof-of-delivery required for completion; returns reconcile inventory.

Deliverables
- CreateShipment, Dispatch, ConfirmDelivery commands; Return flows; read models for in-transit.

Acceptance Criteria
- [ ] Shipment lifecycle enforced; events emitted
- [ ] Inventory reconciled on delivery/return
- [ ] Tests for command handlers

Runbook
- Dev: simulate shipments; verify in-transit views and reconciliations.

Non-Goals
- Taproom pouring and placements

