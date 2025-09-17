# Production Inventory — Milestones (MVP → Streams‑Ready)

## Milestone 1 — Core Inventory Foundations
- Domain and rules: Material, Lot, StockItem; FEFO selection; UoM conversions; prevent negative stock; optimistic locking on mutables.
- Services: StockService (query/reserve/consume), LotService (create/close/FEFO), BOMService (explode recipe).
- Repos: Spring Data repos + Specifications with paging/filters by material/location/lot/status.
- Observability: outline domain events (names/payloads), baseline metrics (`inventory.stock.on_hand`, `inventory.txn.count`).
- Tests: UoM accuracy, FEFO selection correctness, optimistic lock happy path.
- Handoffs: draft REST DTOs/endpoints to API genie; initial Flyway table/index spec to Platform genie.
- Exit: unit tests passing; specs/docs drafted and shared.

## Milestone 2 — Movements, Idempotency, Audit
- Flows: Transfers and Adjustments with idempotency keys and reason codes; authorization scope checks in services (defense‑in‑depth).
- Persistence: InventoryTxn model + queries by time/ref keys; audit log schema.
- Observability: emit StockTransferred/StockAdjusted with correlation ids; metrics for txn counts and on‑hand gauges.
- Tests: idempotency (no double‑apply), negative‑stock guards, transfer edge cases.
- Handoffs: API contracts for transfers/adjustments; Flyway for txns/audit/idempotency tables to Platform genie.
- Exit: movement flows green; contracts agreed; migrations reviewed.

## Milestone 3 — Cycle Counts & Costing Readiness
- CycleCountService: open session, record counts, reconcile (emits adjustments); FEFO with expiry considered.
- Cost layers: FIFO scaffolding + hooks (valuation‑ready, not full accounting).
- Performance/Resilience: key indexes; pagination defaults; retry semantics for optimistic locking.
- Tests: end‑to‑end cycle count scenarios; concurrency retries.
- Docs: data dictionary, state diagrams, acceptance matrix mapped to Problem JSON.
- Handoffs: cycle count endpoints to API; Flyway for count tables and cost layers to Platform genie.
- Exit: cycle counts validated; costing hooks in place; docs complete.

## Streams Readiness (applies across milestones)
- Event contracts finalized per domain event; include headers: `breweryId`, `venueId`, `facilityId`, `traceId`.
- Partitioning/keying decision: default key = `breweryId:facilityId`; topic per top‑level domain (e.g., `prodinventory.events.v1`).
- Local dev: Kafka KRaft via Docker Compose; topics bootstrapped; Spring Kafka config prepared; Testcontainers plan for ITs.

