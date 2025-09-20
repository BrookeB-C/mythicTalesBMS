# Genie Production Inventory — Task List (aligned to updated domain model)

Scope: Raw materials and WIP inventory supporting production: materials, lots/COAs, FEFO, BOM‑based consumption, transfers/adjustments, and staging finished goods to KegInventory. See `docs/ddd-design.md` and Production integration in `docs/production/domain-model.md`.

## P0 — Inventory Model
- Entities/Repos: Material, Unit/UoMConversion, Lot (with expiry/COA refs), StockItem (on_hand/reserved/damaged), InventoryTxn (typed), CostLayer scaffold.
- Rules: UoM conversions with rounding policy; FEFO selection; prevent negative stock; optimistic locking on mutable aggregates.
- APIs (paged): list materials/lots/stock by filters (materialId, lot, location, status).
- Events (in‑process initial): MaterialReceived, MaterialConsumed, LotAdjusted.
- Tests: UoM accuracy, FEFO correctness, optimistic lock behavior.

## P1 — Shopping List & Consumption
- Shopping List service: explode Recipe/BOM with UoM normalization and FEFO lot suggestions for a ProductionRun.
- API: GET shopping list for a planned run; POST consume (idempotent) to reserve/consume lots against a run.
- Idempotency: require client key for adjustments/consumption; no double‑apply.
- Authorization: enforce `CurrentUser` brewery/facility scope in services (defense‑in‑depth).
- Tests: BOM expansion correctness; idempotent consumption; negative‑stock guards.

## P2 — Movements, Finished Goods, and Streams Readiness
- Transfers/Adjustments APIs with reason codes and audit trails.
- FinishedGoodsStaged intake: handle Production event to create finished goods records for downstream KegInventory handoff.
- Event publishing (integration): emit StockConsumed/Transferred/Adjusted with headers `breweryId`,`venueId` (optional),`facilityId`,`traceId`.
- Kafka (coord. with Platform): topic `prodinventory.events.v1`, key=`breweryId:facilityId`; JSON payloads; Testcontainers IT.
- Metrics: `inventory.stock.on_hand`, `inventory.txn.count`, discrepancy indicators.
- Tests: movement edge cases; produce/consume round‑trip (platform Testcontainers).
