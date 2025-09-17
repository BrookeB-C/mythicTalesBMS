# Production Domain Model — Facilities, Brew Systems, Fermentors, and Scheduling

This document captures the proposed aggregates and relationships for managing production facilities, brew systems, fermentors, and resource scheduling. It supports the Production backlog in `docs/techtasks/110-genie-production-tasks.md` and the API outline in `docs/api-design.md`.

## Overview
- **ProductionFacility** owns the physical site, defines scheduling policies, and aggregates all brew systems and fermentors.
- **BrewSystem** represents a brewhouse or brewhouse line within a facility; capacity and turnaround drive brew scheduling.
- **Fermentor** models vessels used for fermentation/conditioning with occupancy tracking.
- **ProductionRun** ties recipes to equipment, coordinating brew and fermentation timelines.
- **BrewScheduleSlot** and **FermentorAllocation** provide explicit booking records to prevent conflicts and drive calendars.

## Aggregates & Entities

### ProductionFacility
- `facilityId` (PK)
- `breweryId` (optional, when facilities are brewery-specific)
- `name`
- `address` (street, city, state, postal, country)
- `timezone`
- `notes`
- `isActive`
- Relationships:
  - `brewSystems`: list of BrewSystem IDs
  - `fermentors`: list of Fermentor IDs
  - `schedulePolicy`: FacilitySchedulePolicy

### FacilitySchedulePolicy (value object)
- `defaultBrewWindow`: e.g. `{ start: 06:00, end: 22:00 }`
- `maintenanceDays`: list of weekday names or specific dates
- `maxConcurrentBrews`
- `autoAssignFermentorStrategy`: enum (`NONE|MATCH_CAPACITY|FIRST_AVAILABLE`)
- `defaultFermentationDurationDays`

### BrewSystem
- `brewSystemId` (PK)
- `facilityId`
- `name`
- `capacityValue`
- `capacityUnit` (`BBL|LITER|GALLON`)
- `heatSource` (`STEAM|DIRECT_FIRE|ELECTRIC`)
- `mashTunType` (`INFUSION|DECOCTION|STEP`)
- `turnaroundMinutes` (cleanup/reset time)
- `isActive`
- Associations:
  - `scheduleSlots`: list of BrewScheduleSlot IDs
  - `maintenanceWindows`: optional blackout periods

### Fermentor
- `fermentorId` (PK)
- `facilityId`
- `name`
- `vesselType` (`CONE_FV|UNITANK|BRITE|AGING`)
- `capacityValue`
- `capacityUnit`
- `maxPressurePsi`
- `coolingZone`
- `requiresCipBetweenRuns` (bool)
- `defaultConditioningDays`
- `isActive`
- Associations:
  - `allocations`: list of FermentorAllocation IDs
  - `maintenanceWindows`

### ProductionRun
- `runId` (PK)
- `breweryId`
- `facilityId`
- `brewSystemId`
- `fermentorId` (optional at planning time)
- `recipeId` (optional; may link to catalog)
- `catalogRecipeVersion`
- `targetVolume`
- `volumeUnit`
- `startAt`
- `expectedCompleteAt`
- `fermentationStart`
- `fermentationComplete`
- `status` (`PLANNED|IN_PROGRESS|FERMENTING|PACKAGING|COMPLETE|CANCELLED`)
- `notes`
- `warnings` (list of domain warnings, e.g., fermentor conflict)
- `brewScheduleSlotId`
- `fermentorAllocationId`

### BrewScheduleSlot
- `slotId` (PK)
- `facilityId`
- `brewSystemId`
- `runId`
- `startAt`
- `endAt`
- `status` (`RESERVED|IN_PROGRESS|DONE|CANCELLED`)
- `createdBy`
- `createdAt`
- `updatedAt`
- **Invariants**:
  - No overlapping slots for the same `brewSystemId` while `status` is `RESERVED` or `IN_PROGRESS`.
  - Slot duration must accommodate `turnaroundMinutes` before next slot.

### FermentorAllocation
- `allocationId` (PK)
- `facilityId`
- `fermentorId`
- `runId`
- `phase` (`PRIMARY|SECONDARY|CONDITIONING|STORAGE`)
- `startAt`
- `endAt`
- `status` (`RESERVED|ACTIVE|COMPLETE|CANCELLED`)
- `volumeAssigned`
- `temperatureSetPoint`
- `createdBy`
- `createdAt`
- `updatedAt`
- **Invariants**:
  - Overlap rules: a fermentor cannot have overlapping allocations unless `phase` and policy allow (e.g., conditioning vs storage).
  - `volumeAssigned` ≤ fermentor capacity.
  - When `requiresCipBetweenRuns` is true, enforce minimum gap between allocations.

## Scheduling Flow
1. **Plan ProductionRun**
   - Validate facility exists and brew system is active.
   - Compute slot window (`startAt` + brew duration + turnaround).
   - Reserve a `BrewScheduleSlot`; reject if conflict.
   - Optionally auto-select fermentor based on policy and create `FermentorAllocation` covering fermentation window.
   - Emit warnings if no fermentor available or `volumeAssigned` exceeds capacity.

2. **Start Brewing**
   - Transition ProductionRun → `IN_PROGRESS`.
   - Update `BrewScheduleSlot.status` → `IN_PROGRESS`.

3. **Transfer to Fermentor**
   - When brew completes, mark slot `DONE` and start fermentor allocation (`ACTIVE`).
   - Capture actual volumes poured/assigned; adjust run/fermentor allocations accordingly.

4. **Packaging**
   - Optionally create `PackagingRun` (future extension) that may also consume fermentor/brite tank availability.
   - Ensure `volumeAssigned` matches actual packaged volume to maintain conservation.

5. **Completion**
   - Set ProductionRun status `COMPLETE`.
   - Release fermentor allocation (`COMPLETE`) and free resource for new bookings.

## Read Models & APIs
- `FacilityOverview` projection aggregates facility, brew systems, fermentors with current status.
- `BrewSystemScheduleView` outputs calendar-style data for `/production/brew-systems/{id}/schedule`.
- `FermentorScheduleView` provides timeline of current and upcoming allocations with available windows.
- `ProductionRunSummary` includes recipe metadata, equipment assignments, and warnings for UI/API responses.

## Extension Points
- **Maintenance Windows**: allow facility managers to block out equipment for cleaning/upgrades.
- **Auto-Assignment Strategies**: plugins that choose best fermentor based on capacity, availability, beer style, or previous usage.
- **Conflict Resolution**: ability to soft-reserve slots with warnings, requiring manual confirmation to overbook.
- **Integration**: hooks to Production Inventory shopping lists and Packaging domain when those modules mature.

---
Update this document as we implement the Production domain to reflect concrete entity classes, database schema, and event flows.
