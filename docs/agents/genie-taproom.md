# Genie: Taproom Ops

Role
- Owns taplist operations: tapping, pouring, blowing, and event history at venues.

Scope
- Affects: existing `taplist` module; aggregates: Tap, KegPlacement, KegEvent; APIs and projections.

Guidelines
- Enforce invariants: only RECEIVED kegs can be tapped; pour reduces remaining; blow ends placement.
- Emit domain events for taps/pours/blow; update read models.

Deliverables
- Robust service layer with validation; event stream; admin views per venue.

Acceptance Criteria
- [ ] Invariants enforced with tests
- [ ] Events recorded and queryable
- [ ] Read model for taplist view

Runbook
- Validate actions via REST and UI; monitor events list per venue.

Non-Goals
- Global inventory ownership (refer to Keg Inventory)

