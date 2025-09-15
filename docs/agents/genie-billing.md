# Genie: Billing & Invoicing

Role
- Owns invoices, payments, statements, and reconciliation.

Scope
- Affects: `billing/**` module; aggregates: Invoice, InvoiceLine, Payment; integration with Sales.

Guidelines
- Invoices derived from approved orders; payment application rules clear; audit trail for adjustments.

Deliverables
- IssueInvoice, RecordPayment, Reconcile commands; AR aging reports.

Acceptance Criteria
- [ ] Invoice/Payment lifecycle covered with tests
- [ ] Idempotent payment recording
- [ ] Reports consistent with orders

Runbook
- Dev: issue invoices for demo orders; record payments; verify balances.

Non-Goals
- Inventory and shipment handling

