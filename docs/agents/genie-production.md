# Genie: Production

Role
- Owns batches, tanks, packaging runs; tracks state from planned → completed.

Scope
- Affects: `production/**` module, aggregates: Batch, Tank, PackagingRun; commands and events.

Guidelines
- Enforce state transitions; conserve volumes across packaging; record timestamps and operators.

Deliverables
- Commands: PlanBatch, StartBatch, PackageBatch, CompleteBatch; read models for schedule.

Acceptance Criteria
- [ ] Valid transitions only; events emitted
- [ ] Volume conservation checks
- [ ] Tests for command handlers

Runbook
- Dev: stub tanks; simulate runs; verify projections.

Non-Goals
- Retail/taproom operations and sales

