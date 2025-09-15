# Genie: Production Inventory

Role
- Owns raw materials and WIP inventory (malt, hops, yeast, packaging), lots/COAs, consumption, staging finished goods.

Scope
- Affects: `prodinventory/**` module; aggregates: MaterialItem, MaterialLot, WarehouseLocation, Consumption, FinishedGoodsStaging; integrates with Production and KegInventory.

Guidelines
- Lot tracking required; consumption cannot exceed on-hand; conversions respect recipe yields.

Deliverables
- ReceiveMaterials, ConsumeMaterials, AdjustLot, StageFinishedGoods commands; read models by item/lot.

Acceptance Criteria
- [ ] Lot and quantity invariants enforced
- [ ] Audit trail of receipts/consumptions/adjustments

Runbook
- Dev: receive example materials; consume per recipe; stage finished goods.

Non-Goals
- Finished goods sales, billing, and taproom operations

