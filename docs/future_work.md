# Future Work — US Market Gap Analysis

This document summarizes gaps between our current Mythic Tales BMS design docs and the capabilities commonly delivered by US‑focused brewery management competitors. It also recommends concrete additions to our docs and APIs to close near‑term parity gaps.

## Scope & Method
- Sources: `docs/api-design.md`, `docs/ddd-design.md`, `docs/TaproomOps/tasks.md`, `docs/techtasks/100-genie-api-tasks.md`, `docs/catalog/recipes-import.md`.
- Benchmarked vendors (US focus): Ekos, Orchestrated (Encompass), Ollie (Next Glass), Beer30, Ohanafy, VicinityBrew, Crafted ERP.

## Current Capabilities (in docs)
- Taproom Ops: taps, tap‑keg/pour/blow, keg lifecycle; pagination, optimistic concurrency, consistent error model.
- Keg Inventory: basic movement/assignment and status changes; venue/taproom/bar concepts; events outline.
- Catalog: beer catalog foundations and BeerXML/BeerSmith recipe import (doc and tasks present).
- Ops niceties: low‑volume alerts, big‑board display, QR scanning intake.
- DDD outlines: Production, Sales, Distribution, Billing, Compliance, Analytics, Production Inventory (high‑level only; few concrete APIs).

## Gaps vs. US Direct Competitors

1) Production & Materials
- Missing: tank/fermenter scheduling, batch/packaging workflows, yields, batch costing, WIP states with timings.
- Materials inventory with lot/COA tracking, consumption against recipes, substitutions, yield variance (only sketched as “Production Inventory”).
- Packaging runs for case goods/pallets and finished goods staging not defined in API.

2) QA/QC & Compliance Logs
- Missing: lab test templates and captures (gravity, pH, DO, ABV), COAs, SOP/CIP checklists, QA holds and release gates.
- End‑to‑end audit trails tied to batches and ingredient lots beyond `KegEvent` history.

3) Sales, CRM, B2B Ordering
- Missing: wholesale/B2B portal for customer ordering, price lists/tiers, promotions, credit limits, approvals.
- CRM: account management, visit logs, samples, sales pipeline tracking.
- Inventory allocation to orders and backorder handling.

4) Billing, Payments, Accounting
- Missing: invoicing, taxes/fees, payments (ACH/CC), refunds/credits, dunning.
- Accounting integrations (QuickBooks/Xero) for items, customers, invoices, payments.

5) US Compliance & Traceability
- Missing: TTB/excise reporting, state compliance (by package/ABV), distributor compliance exports.
- Recall/traceability from finished goods lot → customer; retention policies with exports.

6) Distribution & Logistics
- Missing: shipments/pick‑pack‑ship, route planning, proof of delivery (signature/photos), returns (empties/RMAs), delivery fees/surcharges.
- Distributor/EDI integrations (e.g., Encompass/VIP) and marketplace ordering (Provi) unspecified.

7) Keg Asset Management
- Missing: keg fleet ownership/leasing, deposits/fees, maintenance/refurb cycles; statuses like LOST/DAMAGED/QA_HOLD.
- Native barcode/RFID flows and consolidated asset reporting.

8) POS, Taproom, eCommerce Integrations
- Missing: POS integrations (Arryved, Toast, Square) for sales reconciliation or pour events; retail SKU catalog sync.
- DTC eCommerce hooks (Shopify/Bouncer) and age verification.

9) Forecasting, MRP, Demand Planning
- Missing: demand forecasting, brew schedule recommendations, MRP for materials (mins/max, lead times), vendor POs and receiving.

10) Reporting & Analytics
- Missing: packaged dashboards/KPIs (COGS by beer, draft loss, sell‑through, cellar load, OTIF delivery, aging inventory), scheduled exports, data‑warehouse connectors.

11) Security & Enterprise Admin
- Missing: SSO (OIDC/SAML), granular permissions and row‑level policies per context, audit log exports with retention.
- Multi‑entity (holdings) and intercompany flows; consolidated reporting.

12) Mobile & Field Enablement
- Missing: native/PWA mobile for field sales, drivers (POD), and warehouse scanning; offline‑first behaviors.

13) Integrations & Extensibility
- Missing: explicit webhook/outbox schemas per context; connectors for tax (Sovos/Avalara Beverage Alcohol), accounting, POS, label/printing, carriers.

## Where These Gaps Sit in Our Docs
- Not present at all: QA/QC program, B2B portal, invoicing/payments, accounting integrations, US compliance (TTB/state), distributor/EDI integrations, route/POD, keg deposit program, POS integrations, forecasting/MRP, dashboards, SSO.
- High‑level only (needs concrete APIs/specs): Production (batches/packaging), Distribution, Sales & Orders, Billing, Procurement, Maintenance, Compliance, Analytics, Production Inventory.
- Present or partial: Taproom ops, basic keg inventory, recipe import, low‑volume alerts, big‑board, QR.

## Priority Gaps for US SMB Parity (Ekos/Ollie/Beer30)
- Sales & Billing: customers, price lists/tiers, orders (allocations, approvals), invoices, payments, QuickBooks sync (items/customers/invoices/payments).
- Production & Materials: batches with tanks, packaging runs, basic costing; materials lots/COAs; recipe‑based consumption.
- Compliance & Traceability: TTB/excise reports; lot/batch traceability and recall export.
- POS & B2B: POS integrations (reconciliation), MVP B2B wholesale portal.
- Distribution Basics: shipments with pick/pack/ship and proof‑of‑delivery.

## ERP‑Tier Follow‑Ons (Orchestrated/Vicinity/Crafted/Ohanafy)
- MRP/forecasting, route optimization, WMS with directed picking, DSD route accounting.
- Multi‑entity financials, intercompany, advanced promotions/trade spend.
- Enterprise SSO, fine‑grained permissions, compliance automation.

## Proposed Doc/API Additions (Actionable)
- Production APIs: `/api/v1/production/batches`, `/tanks`, `/packaging-runs` with states, yields, QC gates.
- Materials APIs: `/api/v1/materials/items`, `/lots`, `/receipts`, `/consumptions` with COA and lot tracing.
- Sales & Billing APIs: `/api/v1/customers`, `/price-lists`, `/orders`, `/invoices`, `/payments` with allocation rules.
- Compliance APIs: `/api/v1/compliance/ttb/filings`, `/traceability/exports?lot=...`.
- Distribution APIs: `/api/v1/shipments`, `/picks`, `/routes`, `/pod` (signature/photo attachments).
- Keg Asset APIs: `/api/v1/kegs/deposits`, maintenance logs; add statuses LOST/DAMAGED/QA_HOLD.
- Integrations: webhook/outbox schemas per context; connectors for QuickBooks/Xero, POS (Arryved/Toast/Square), tax (Sovos/Avalara), label/printing, carriers.
- Security: SSO (OIDC) profile and role/permission matrices per bounded context.

## Mapping to Tasks (Suggested Sequencing)
1) Parity Sprint A — Orders/Billing (SMB)
- Add customers/price lists → orders → invoices/payments; QuickBooks export/sync. Minimal UI and OpenAPI.
2) Parity Sprint B — Production/Materials (Core)
- Batch/tank model, packaging runs; materials lots/COAs; recipe consumption; basic costing and yields.
3) Parity Sprint C — Compliance/Traceability
- TTB baseline reports; lot/batch traceability views and exports.
4) Parity Sprint D — POS/B2B/Distribution (MVP)
- POS reconciliation hooks; MVP B2B ordering; shipments with POD.

## Next Steps
- Approve adding skeletal endpoints and aggregate definitions to `docs/api-design.md` and `docs/ddd-design.md` for the “Priority Gaps”.
- Optionally commission a competitive feature matrix (per vendor) and pricing notes to refine priorities.
- Define acceptance criteria per sprint and wire into `docs/techtasks/` for execution.

