# Genie: Sales & Orders

Role
- Owns wholesale and internal orders; tracks approval and fulfillment states.

Scope
- Affects: `sales/**` module; aggregates: Order, OrderLine, CustomerAccount; integration with Distribution and Billing.

Guidelines
- Validate availability before approval; immutable order lines post-approval except via amendments.

Deliverables
- PlaceOrder, Approve, Fulfill, Cancel commands; read models for sales pipeline.

Acceptance Criteria
- [ ] State transitions valid; allocations checked
- [ ] Events emitted for downstream flows
- [ ] Tests for command handlers

Runbook
- Dev: create sample customers; simulate order lifecycle.

Non-Goals
- Invoicing and payments (Billing)

