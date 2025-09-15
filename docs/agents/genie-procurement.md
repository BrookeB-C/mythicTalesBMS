# Genie: Procurement

Role
- Owns suppliers, purchase orders, and receiving for non-finished goods.

Scope
- Affects: `procurement/**` module; aggregates: Supplier, PurchaseOrder, Receipt; integration with Production Inventory.

Guidelines
- PO lifecycle with approvals; receiving creates material receipts and lots; three-way match optional.

Deliverables
- PlacePO, ApprovePO, ReceiveGoods commands; supplier master data.

Acceptance Criteria
- [ ] PO states enforced; receipts create material lots
- [ ] Tests for command handlers

Runbook
- Dev: create suppliers; place/approve POs; receive into materials.

Non-Goals
- Finished goods sales/billing

