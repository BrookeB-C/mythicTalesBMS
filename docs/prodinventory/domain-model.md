# Production Inventory Domain Model — Materials, Lots, Locations, and Consumption

This document outlines the proposed aggregates for the Production Inventory domain. It aligns with the genie scope in `docs/agents/genie-prodinventory.md` and the backlog in `docs/techtasks/116-genie-prodinventory-tasks.md`.

## Goals
- Track raw materials, adjuncts, and packaging supplies down to lot/COA level.
- Maintain accurate on-hand quantities per warehouse location.
- Enforce consumption rules so usage never exceeds available stock.
- Provide auditability for receipts, consumption, adjustments, and staging finished goods.
- Serve shopping list requests from Production runs and support staging outputs for downstream domains.

## Core Aggregates & Entities

### MaterialItem
- `materialId` (PK)
- `sku`
- `name`
- `category` (`MALT|HOP|YEAST|ADJUNCT|PACKAGING|CHEMICAL|OTHER`)
- `unitOfMeasure` (`KG|G|LB|OZ|LITER|GALLON|EA`)
- `density` (optional for conversions)
- `defaultSupplier`
- `safetyStock`
- `isActive`
- Relationships:
  - `lots`: collection of `MaterialLot` IDs
  - `conversions`: value objects mapping between units (e.g., sack → kg)

### WarehouseLocation
- `locationId` (PK)
- `facilityId`
- `name`
- `type` (`BULK|COLD_STORAGE|DRY_STORAGE|STAGING|QUARANTINE`)
- `temperatureRange`
- `isActive`
- Relationships:
  - `materialBalances`: map of `materialId` → on-hand summary

### MaterialLot
- `lotId` (PK)
- `materialId`
- `locationId`
- `lotCode`
- `supplierLotCode`
- `receivedAt`
- `coo` (country of origin)
- `coaDocumentId` (link to QA docs)
- `bestByDate`
- `quantityOnHand`
- `quantityUnit`
- `initialQuantity`
- `status` (`AVAILABLE|QUARANTINED|RESERVED|DEPLETED`)
- `specs` (e.g., alpha acids, color, moisture—model as key/value map)
- `tags` (organic, gluten-free, etc.)
- Relationships:
  - `transactions`: ordered list of MaterialTransaction IDs

### MaterialTransaction (event/audit record)
- `transactionId` (PK)
- `lotId`
- `materialId`
- `locationId`
- `type` (`RECEIPT|CONSUMPTION|ADJUSTMENT|TRANSFER|RESERVATION|RELEASE`)
- `quantity`
- `quantityUnit`
- `occurredAt`
- `enteredBy`
- `reference` (ProductionRun ID, PurchaseOrder ID, Adjustment reason, etc.)
- `notes`
- **Invariant**: sum of transaction quantities for an `lotId` equals `quantityOnHand` (within rounding tolerance).

### InventoryBalance (read model)
- Aggregated projection per material/location/lot combination
- Fields: `materialId`, `locationId`, `lotId?`, `onHand`, `reserved`, `available`, `unit`
- Serves API for quick listing without scanning transactions.

### ConsumptionRequest / Reservation
- Entity representing intent to consume materials for a ProductionRun
- Fields: `reservationId`, `productionRunId`, `materialId`, `lotId?`, `quantity`, `unit`, `status` (`REQUESTED|RESERVED|CONSUMED|CANCELLED`)
- Allows soft-reserving stock ahead of brew day and later confirms actual consumption.

### FinishedGoodsStaging
- `stagingId` (PK)
- `productionRunId`
- `locationId` (often staging or brite tank area)
- `productCode`
- `plannedVolume`
- `actualVolume`
- `unit`
- `packagingFormat` (keg, can, bottle)
- `status` (`AWAITING_PACKAGING|PACKAGED|TRANSFERRED`)
- Links packaging outcomes back to inventory.

## Command & Event Flow

1. **ReceiveMaterials**
   - Input: material reference, lot metadata, quantity, location.
   - Actions: create new `MaterialLot`; persist `MaterialTransaction` of type `RECEIPT`; update balance.
   - Validation: quantity > 0, location active, duplicates flagged by lot code + supplier when configured.

2. **ConsumeMaterials**
   - Input: production run reference, material, quantity, optional lot preference.
   - Workflow:
     - Locate eligible lots ordered by FIFO or FEFO policy.
     - Ensure sum of available balances ≥ request.
     - Create `RESERVATION` transactions (status `RESERVED`).
     - When brew executes, convert to `CONSUMPTION` transactions, decrementing on-hand.
   - Validation: consumption cannot exceed `available`.

3. **AdjustLot**
   - Input: lot ID, reason code, delta quantity (positive or negative).
   - Actions: create `ADJUSTMENT` transaction; update `quantityOnHand`.
   - Validation: negative adjustments cannot drop quantity below zero; reasons tracked for audit.

4. **TransferMaterials**
   - Move quantities between locations (e.g., warehouse to staging).
   - All transfers recorded as paired `TRANSFER_OUT` / `TRANSFER_IN` transactions or a single transaction with source/target references.

5. **StageFinishedGoods**
   - After packaging, create `FinishedGoodsStaging` entry.
   - Optionally convert staged volume into Packaging/Keg Inventory once released.
   - Enforce volume conservation by linking packaging outputs to original consumption/reservation totals.

## Policies & Invariants
- Lot tracking mandatory; every movement references a `lotId` unless the material is configured as lotless (packaging supplies).
- Units normalized via `MaterialItem.unitOfMeasure`; conversions applied before updating balances.
- Balances must never be negative; reservation workflow prevents over-allocation.
- All commands emit domain events (`MaterialsReceived`, `MaterialsReserved`, `MaterialsConsumed`, `LotAdjusted`, `FinishedGoodsStaged`) for projections and integrations.

## Read Models & API Hints
- `GET /api/v1/prodinventory/materials` → list of MaterialItem with stock summaries.
- `GET /api/v1/prodinventory/materials/{id}/lots` → lot-level details.
- `POST /api/v1/prodinventory/lots/{id}/consume` → convenience endpoint for consumption.
- `GET /api/v1/prodinventory/reservations?productionRunId=...` → track reservations.
- `GET /api/v1/prodinventory/finished-goods` → staging status for packaged product awaiting transfer.
- Shopping list integration: `GET /api/v1/production/runs/{id}/shopping-list` queries inventory to highlight shortages or required transfers.

## Integration Touchpoints
- **Production**: consumption requests originate from ProductionRun planning/execution.
- **Catalog**: recipe compositions inform material requirements.
- **Procurement** (future): materials received tie back to purchase orders.
- **Quality/Compliance** (future): lot specs and COA data feed QA workflows.

---
Refine this model as implementation proceeds, adding concrete schema, events, and validation rules.
