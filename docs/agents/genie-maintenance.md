# Genie: Maintenance

Role
- Owns equipment registry and work orders (scheduled/unscheduled), downtime tracking.

Scope
- Affects: `maintenance/**` module; aggregates: Equipment, WorkOrder.

Guidelines
- Track states and metrics (MTTR/MTBF); tie work orders to equipment and optional batches.

Deliverables
- OpenWorkOrder, CompleteWorkOrder commands; equipment registry; downtime reports.

Acceptance Criteria
- [ ] Work order lifecycle with tests
- [ ] Basic KPIs exposed via read models

Runbook
- Dev: seed equipment; simulate work orders.

Non-Goals
- Production planning and execution

