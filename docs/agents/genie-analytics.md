# Genie: Analytics

Role
- Owns cross-context dashboards and projections for insights.

Scope
- Affects: `analytics/**` read models; consumes domain events; no authoritative writes.

Guidelines
- Build projections optimized for queries; avoid coupling to write models; document metrics.

Deliverables
- SalesDashboard, ProductionDashboard, InventoryKPIs, etc.

Acceptance Criteria
- [ ] Projections fed by events; updated in near-real-time
- [ ] Queries performant and paginated

Runbook
- Dev: seed events; verify dashboards render expected figures.

Non-Goals
- Command handling or authoritative data ownership

