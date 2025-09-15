# Genie: Compliance & Reporting

Role
- Owns regulated reports (alcohol production/sales), excise filings, traceability.

Scope
- Affects: `compliance/**` module; aggregates: ReportDefinition, Filing; consumes events from other contexts.

Guidelines
- Deterministic report generation; retain snapshots for audit; parameterize by period/tenant.

Deliverables
- GenerateReports, SubmitFiling commands; export formats; audit logs.

Acceptance Criteria
- [ ] Reports reconcile with source contexts
- [ ] Filings immutable once submitted

Runbook
- Dev: mock filings; validate figures vs fixtures.

Non-Goals
- Operational workflows (production, sales, logistics)

