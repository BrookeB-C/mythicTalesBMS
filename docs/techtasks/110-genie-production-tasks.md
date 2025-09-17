# Genie Production — Task List (aligned to updated domain model)

Scope: Production planning and execution — facilities, brew systems, fermentors, ProductionRuns, packaging, and resource scheduling per `docs/production/domain-model.md`.

## P0 — Run Planning MVP
- Entities/Repos: ProductionFacility, BrewSystem, Fermentor, ProductionRun, BrewScheduleSlot, FermentorAllocation.
- Endpoints:
  - GET facilities, brew systems, fermentors (paged, filter by facility).
  - POST/PUT ProductionRun (plan/update) with optional fermentor assignment.
  - Shopping list preview endpoint (delegates to Production Inventory for BOM/UoM calc).
- Scheduling:
  - Reserve BrewScheduleSlot on plan; compute duration incl. turnaround.
  - Optional auto‑assign FermentorAllocation per facility policy.
- Warnings/Validation:
  - Return warnings array on plan (e.g., no fermentor available, capacity mismatch).
  - Problem JSON on conflicts/invalid states.
- Events (in‑process for now): RunPlanned, BrewSystemReserved, FermentorReserved.
- Tests: API + repo tests for slot creation, non‑overlap on same brew system, policy‑driven fermentor pick.

## P1 — Scheduling & Conflicts
- Enforce non‑overlapping BrewScheduleSlots while status in {RESERVED, IN_PROGRESS}.
- Enforce fermentor allocation rules (capacity ≤ vessel; CIP gap when required; phase‑aware overlap policies).
- Views: BrewSystemScheduleView, FermentorScheduleView (paged/time‑windowed).
- Commands: Start Brewing (transition run → IN_PROGRESS; slot → IN_PROGRESS). Transfer to Fermentor (slot → DONE; allocation → ACTIVE).
- Events: BatchStarted.
- Tests: calendar projections, overlap detection, transitions.

## P2 — Packaging & Metrics
- PackagingRun scaffold; conservation of volume across runs into packaged output.
- Complete Run: allocation → COMPLETE, run → COMPLETE; emit BatchPackaged/BatchCompleted.
- Integration hooks:
  - Emit FinishedGoodsStaged for Production Inventory when packaging completes (quantity, lot/batch linkage).
  - Request/consume shopping list from Production Inventory earlier in plan flow.
- Metrics: counts of planned/active/completed runs; schedule utilization.
- Tests: volume conservation, event emission on completion.
