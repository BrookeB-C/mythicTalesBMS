# Brewery Management System — Domain‑Driven Design

This document proposes a domain‑driven design (DDD) for a full Brewery Management System (BMS). It generalizes the existing Taplist module and outlines bounded contexts, core aggregates, domain events, integration patterns, and an incremental evolution path from the current Spring Boot application.

## Vision & Scope

- Operate end‑to‑end brewery workflows: production → inventory → distribution → taproom/bar sales → billing → compliance.
- Support multiple breweries (multi‑tenant) with clear data ownership and role‑based access.
- Favor modular monolith initially, with clean context boundaries to enable later extraction to services.

## Ubiquitous Language (Glossary)

- Brewery: The brewery organization (tenant). Owns production, inventory, and internal venues.
- Beer: A commercial product; may be linked to a Recipe (production spec).
- Batch: A produced volume (fermentation/conditioning), packaged into Kegs/Cases.
- Keg: A container tied to a Beer and SizeSpec; moves between Locations and Venues.
- Venue: A place where inventory resides (Taproom, Bar, Warehouse).
- Tap: A draft line at a Venue that may hold at most one Keg at a time.
- Pour: Dispensing ounces from a tapped keg.
- Shipment/Transfer: Movement of inventory between locations/venues.
- Order: A request to transfer/sell inventory (wholesale, internal requisition, retail POS).
- Invoice/Payment: Commercial settlement of Orders.

## Bounded Contexts

1) Identity & Access (IAM)
- Responsibilities: Tenants, users, roles, permissions, authN/Z.
- Aggregates: UserAccount (existing), Role, Tenant (Brewery as tenant).
- Events: UserCreated, RoleAssigned.

2) Catalog
- Responsibilities: Beers, Recipes, Packaging specs (KegSizeSpec exists), pricing rules.
- Aggregates: Beer, Recipe, KegSizeSpec (existing), PriceList.
- Events: BeerCreated, RecipeVersioned, PriceChanged.

3) Production
- Responsibilities: Plan and execute batches; track states (planned → brewing → fermenting → packaged → complete).
- Aggregates: Batch, Fermenter, Tank, PackagingRun.
- Invariants: A Batch packages into one or more PackagingRuns; volumes conserved.
- Events: BatchPlanned, BatchStarted, BatchPackaged, BatchCompleted.

4) KegInventory
- Responsibilities: System of record for keg inventory across Locations and Venues; statuses (FILLED, RECEIVED, TAPPED, BLOWN, EMPTY, RETURNED).
- Aggregates: InventoryItem (Keg as item), Location, Adjustment.
- Invariants: Single owner (tenant); location history immutable; quantity conservation.
- Events: InventoryReceived, InventoryMoved, InventoryAdjusted, KegStatusChanged.

5) Distribution & Logistics
- Responsibilities: Shipments, routes, proof of delivery, returns (empties).
- Aggregates: Shipment, ShipmentItem, ReturnAuthorization.
- Events: ShipmentCreated, ShipmentDispatched, ShipmentDelivered, ReturnReceived.

6) Taproom Ops
- Responsibilities: Taplist (existing), keg lifecycle at taproom, event log.
- Aggregates: Tap, KegPlacement, KegEvent (existing), Venue (Taproom).
- Commands: TapKeg, Pour, Blow, Untap; view projections for list/board.
- Events: KegTapped, BeerPoured, KegBlown, KegUntapped.

7) Bar Partner Ops
- Responsibilities: Similar to Taproom but external/partner venues. Contract/limits may apply.
- Aggregates: PartnerVenue, PartnerAgreement.
- Events: PartnerVenueCreated, AgreementSigned.

8) Sales & Orders
- Responsibilities: Wholesale orders (bars/retail), internal requisitions (taprooms), retail POS outline.
- Aggregates: Order, OrderLine, CustomerAccount.
- States: DRAFT → APPROVED → FULFILLED/PARTIAL → CLOSED/CANCELLED.
- Events: OrderPlaced, OrderApproved, OrderFulfilled, OrderCancelled.

9) Billing & Invoicing
- Responsibilities: Invoices, payments, statements, taxes.
- Aggregates: Invoice, InvoiceLine, Payment, Account.
- Events: InvoiceIssued, PaymentReceived, InvoiceSettled.

10) Procurement
- Responsibilities: Suppliers, purchase orders for ingredients/consumables, receiving.
- Aggregates: Supplier, PurchaseOrder, Receipt.
- Events: PurchaseOrderPlaced, GoodsReceived.

11) Maintenance
- Responsibilities: Equipment, scheduled/unscheduled work orders, downtime tracking.
- Aggregates: Equipment, WorkOrder.
- Events: WorkOrderOpened, WorkOrderCompleted.

12) Compliance & Reporting
- Responsibilities: Alcohol production/sales reporting, excise, traceability.
- Aggregates: ReportDefinition, Filing.
- Events: ReportGenerated, FilingSubmitted.

13) Analytics
- Responsibilities: Cross‑context insights, dashboards.
- Pattern: Read‑only projections fed by domain events.

14) Production Inventory
- Responsibilities: Raw materials and WIP inventory for production (malt, hops, yeast, adjuncts, packaging), lots/COAs, consumption, staging finished goods prior to transfer to KegInventory.
- Aggregates: MaterialItem, MaterialLot, WarehouseLocation, Consumption, FinishedGoodsStaging.
- Invariants: Lot tracking required for regulated ingredients; consumption cannot exceed on‑hand; conversions follow recipe yields.
- Events: MaterialReceived, MaterialConsumed, LotAdjusted, FinishedGoodsStaged.

## Context Map (High‑Level)

- Upstream (providers) → Downstream (consumers):
  - Production → Production Inventory (MaterialReceived/Consumed; BatchPackaged → FinishedGoodsStaged)
  - Production Inventory → KegInventory (FinishedGoodsStaged → InventoryReceived)
  - KegInventory ↔ Distribution (moves, status changes)
  - Sales → Distribution (ShipmentCreated), Billing (InvoiceIssued)
  - Taproom Ops → KegInventory (KegStatusChanged), Analytics
  - IAM serves all contexts (auth), but each context enforces authorization policies.
- Anti‑Corruption Layers (ACLs): any external partner POS, ERP, or carrier API.

## Core Aggregates, Commands, Events (Selected)

Taproom Ops (existing, with refinements)
- Aggregates: Tap, KegPlacement, KegEvent.
- Commands: TapKeg(tapId,kegId,actor), Pour(tapId,ounces,actor), Blow(tapId,actor), Untap(tapId,actor).
- Invariants: A Tap has at most one active KegPlacement; remaining ounces ≥ 0; status transitions valid.
- Events: KegTapped, BeerPoured(ounces), KegBlown, KegUntapped.

KegInventory
- Aggregates: InventoryItem(id=KegId), Location, Movement, Adjustment.
- Commands: ReceiveKeg, MoveKeg(from,to), AssignKegToVenue, AdjustInventory(reason,delta).
- Events: InventoryReceived, InventoryMoved, KegAssignedToVenue, InventoryAdjusted, KegStatusChanged.

Production
- Aggregates: Batch, PackagingRun.
- Commands: PlanBatch, StartBatch, RecordPackagingRun, CompleteBatch.
- Events: BatchPlanned, BatchStarted, BatchPackaged, BatchCompleted.

Sales
- Aggregates: Order, OrderLine.
- Commands: PlaceOrder, ApproveOrder, FulfillOrder, CancelOrder.
- Events: OrderPlaced, OrderApproved, OrderFulfilled, OrderCancelled.

Billing
- Aggregates: Invoice, Payment.
- Commands: IssueInvoice(orderId), RecordPayment(invoiceId, amount, method), Reconcile.
- Events: InvoiceIssued, PaymentReceived, InvoiceSettled.

Distribution
- Aggregates: Shipment, ShipmentItem.
- Commands: CreateShipment, DispatchShipment, ConfirmDelivery.
- Events: ShipmentCreated, ShipmentDispatched, ShipmentDelivered.

## Domain Policies & Invariants (Examples)

- Keg lifecycle: FILLED → RECEIVED (at venue) → TAPPED → BLOWN/UNTAPPED → EMPTY → RETURNED/REFILLED.
- A Keg cannot be tapped unless status=RECEIVED and assigned to the Tap’s Venue.
- Pour never increases remaining ounces; when remaining ≤ 0, status transitions to BLOWN and placement ends.
- Inventory movements are immutable facts; adjustments require reasons and audit trails.
- Orders cannot be approved without available inventory allocations.

## Read Models and CQRS

- Commands mutate aggregates and emit domain events.
- Projections build query‑optimized read models:
  - TaplistView (per venue)
  - KegHistoryView (events, placements, movements)
  - InventoryByVenueView
  - SalesDashboard, ProductionDashboard
- Keep projections inside the same codebase (module per context) initially.

## Integration Patterns

- In‑process domain events (Spring application events) while monolithic.
- Outbox pattern to publish integration events when contexts are separated/services emerge.
- REST APIs for synchronous workflows; async events for cross‑context side‑effects and reporting.

### Event Catalog (non‑exhaustive)

- KegTapped, BeerPoured, KegBlown, KegUntapped
- BatchPlanned, BatchStarted, BatchPackaged, BatchCompleted
- InventoryReceived, InventoryMoved, KegAssignedToVenue, InventoryAdjusted, KegStatusChanged
- OrderPlaced, OrderApproved, OrderFulfilled, OrderCancelled
- ShipmentCreated, ShipmentDispatched, ShipmentDelivered
- InvoiceIssued, PaymentReceived, InvoiceSettled
- MaterialReceived, MaterialConsumed, LotAdjusted, FinishedGoodsStaged

## Security, Tenancy, and Permissions

- Multi‑tenancy: Tenant=Brewery; every aggregate carries tenant ownership. Queries always scoped by tenant.
- Roles map to contexts: SITE_ADMIN, BREWERY_ADMIN, TAPROOM_ADMIN, BAR_ADMIN, TAPROOM_USER, FINANCE, LOGISTICS, PRODUCTION.
- Policies enforce least privilege per bounded context.

## Data Ownership & Storage Strategy

- Single database (schema‑per‑context) inside modular monolith.
- Clear ownership: KegInventory owns truth for keg location/status; Production Inventory owns raw materials/WIP; Taproom owns placements/events for taps; Sales owns orders; Billing owns invoices/payments.
- Shared keys via stable identifiers (UUIDs) to ease future extraction.

## Non‑Functional Requirements

- Observability: structured logs, metrics per context, audit trails for inventory and financials.
- Reliability: idempotent commands, optimistic locking on aggregates.
- Performance: projections for read paths; pagination/filters.
- Migrations: Flyway/Liquibase per context package.
- Testing: unit tests at aggregate level (commands/events), integration tests per context, end‑to‑end smoke tests.

## Module Layout (Monolith, by Context)

- `com.mythictales.bms.iam`
- `com.mythictales.bms.catalog`
- `com.mythictales.bms.production`
- `com.mythictales.bms.keginventory`
- `com.mythictales.bms.prodinventory`
- `com.mythictales.bms.distribution`
- `com.mythictales.bms.taplist` (existing Taproom Ops)
- `com.mythictales.bms.sales`
- `com.mythictales.bms.billing`
- `com.mythictales.bms.procurement`
- `com.mythictales.bms.maintenance`
- `com.mythictales.bms.compliance`
- `com.mythictales.bms.analytics`

Each context keeps: `domain` (aggregates, entities, values), `app` (commands/handlers), `api` (controllers/dto), `repo` (persistence), `infra` (adapters/events).

## API Sketches (Selected)

Taproom Ops (existing flavor)
- `POST /api/v1/taps/{tapId}/tap-keg {kegId}` → 202 + event KegTapped
- `POST /api/v1/taps/{tapId}/pour {ounces}` → 202 + event BeerPoured
- `POST /api/v1/taps/{tapId}/blow` → 202 + event KegBlown

KegInventory
- `POST /api/v1/inventory/receive` → InventoryReceived
- `POST /api/v1/inventory/{kegId}/move {from,to}` → InventoryMoved
- `POST /api/v1/inventory/{kegId}/assign {venueId}` → KegAssignedToVenue

Sales
- `POST /api/v1/orders` → OrderPlaced
- `POST /api/v1/orders/{id}/approve` → OrderApproved
- `POST /api/v1/orders/{id}/fulfill` → ShipmentCreated + OrderFulfilled

Billing
- `POST /api/v1/invoices {orderId}` → InvoiceIssued
- `POST /api/v1/invoices/{id}/payments` → PaymentReceived

## Evolution Plan (Phased Roadmap)

Phase 1 — KegInventory Backbone
- Add KegInventory context: `InventoryItem` for Keg ownership of location/status.
- Emit/consume events from Taproom Ops to keep KegInventory truth aligned.

Phase 2 — Sales & Distribution
- Introduce Orders and Shipments; fulfill from Inventory allocations; basic invoicing.

Phase 3 — Production
- Plan/track Batches; produce PackagingRuns; emit FinishedGoodsStaged to Production Inventory; handoff to KegInventory as InventoryReceived.

Phase 4 — Billing & Compliance
- Robust invoicing/payments; compliance reports fed by events.

Phase 5 — Extract Services (as needed)
- Use Outbox to publish events; peel off contexts with highest independent scaling/SLAs (e.g., POS, Analytics).

## Mapping to Current Codebase

- Keep Taproom Ops as is; refactor packages to `com.mythictales.bms.taplist.*` for clarity (already present).
- Introduce `keginventory` package with `InventoryItem`, `Location`, and listeners to translate Keg events → inventory changes.
- Add eventing abstraction (Spring events + outbox table) without external broker initially.
- DTOs and Controllers per context under `api` packages; avoid cross‑context entity leakage (use IDs and ACLs).

## Open Questions

- POS depth (line‑item taxes, discounts), payment processors.
- External partner integration surface (EDI, carrier APIs) and SLA.
- Regulatory coverage by region.

---

This design keeps context seams explicit so the system can start as a clean modular monolith and evolve into services when required by scale or organizational structure.
