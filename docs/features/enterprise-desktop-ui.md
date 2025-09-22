# enterprise-desktop-ui

Importing multiple files is available to Plus Users - [you can try Plus](craftdocs://openSubscription).

# Enterprise Desktop UI & Domain Landing Pages

## Vision

Deliver a cohesive, enterprise-grade desktop experience for Mythic Tales BMS operators that mirrors the control, density, and precision of rich client software while staying web-friendly. The interface should prioritize multitasking, at-a-glance telemetry, and guided workflows tuned to each operational domain.

## Experience Goals

- Feel familiar to power users of desktop ERPs and brewing floor systems.
- Provide consistent navigation, terminology, and status cues across domains.
- Surface critical alerts, tasks, and KPIs immediately on landing pages.
- Support rapid drill-down into detailed records without losing context.
- Maintain accessibility (WCAG 2.1 AA) and responsiveness for large monitors down to tablets.

## Enterprise Desktop Design System

### Layout

- **Shell:** Fixed application chrome with left rail (primary navigation), top command bar, and main workspace panes.
- **Workspace:** Uses a two-pane default (overview + detail) with optional slide-out drawers for filters and activity logs.
- **Density Modes:** Default to compact table density; allow per-user toggle between comfortable and compact.
- **Color Palette:** Muted graphite base, deep teal accents, and status colors (success=oxygen green, warning=amber, alert=rust red). Ensure 4.5:1 contrast.

### Navigation & Wayfinding

- Persistent top command bar with global search, quick actions, and user/session context.
- Left rail organizes domains using collapsible groups (Operations, Sales, Compliance, Support).
- Breadcrumbs within domain workspaces reflect hierarchy (Brewery → Facility → Entity ID).

### Interactions

- Inline editing in tables with optimistic UI and conflict warnings.
- Toast notifications with undo where feasible; modal dialogs reserved for destructive actions.
- Keyboard shortcuts advertised in tooltips (e.g., `⌘+K`/`Ctrl+K` for command palette).

### Responsiveness & Accessibility

- Snap points optimised for 1440px+, 1280px, and 1024px widths.
- Preserve keyboard focus order when drawers open; include ARIA landmark roles for shell regions.
- Ensure data visualizations provide textual summaries for screen readers.

## Landing Page Framework

- Each domain landing page renders within the shell using a consistent grid: `Hero KPIs` (top), `Operational Queue` (left column), `Recent Activity` (right column), `Quick Actions` (bottom strip).
- Provide filter chips and context switchers (e.g., Brewery, Taproom, Region) near the hero bar.
- Embed a "What changed" ribbon highlighting alerts, blocked tasks, or approvals awaiting decision.
- Offer guided tours/tooltips for new users via the command bar help menu.

## Domain Landing Pages

### Identity & Access (`/console/iam`)

- Hero KPIs: Active users by role, pending access reviews, MFA adoption.
- Operational Queue: Access requests, expiring permissions, policy violations.
- Recent Activity: Role changes, login anomalies, revoked sessions.
- Quick Actions: Create User, Approve access, generate report, lock account.

### Keg Inventory (`/console/keginventory`)

- Hero KPIs: Kegs available by size, turn-around time, maintenance overdue, dirty kegs.
- Operational Queue: Keg prep/cleaning backlog and return processing.
- Recent Activity: Tap/untap events, shipping confirmations.
- Quick Actions: Scan keg, assign to route, mark maintenance.
- API support: `GET /api/v1/keg-inventory/summary` provides hero metrics, queue, activity, and quick-action metadata scoped to a brewery.

### Taproom Operations (`/console/taproom`)

- Hero KPIs: Taproom sales velocity, keg blow risk, staffing coverage.
- Operational Queue: Service tickets, keg changeovers, event prep tasks.
- Recent Activity: Ticket resolution, pour anomalies, shift notes.
- Quick Actions: Open ticket, trigger keg swap, update taplist.

### Catalog (`/console/catalog`)

- Hero KPIs: Active SKUs, seasonal lineup readiness, compliance checklist status.
- Operational Queue: Recipe approvals, artwork reviews, distribution eligibility.
- Recent Activity: Recipe changes, cost updates, label approvals.
- Quick Actions: Create SKU, duplicate recipe, submit for compliance.

### Production Inventory (`/console/prodinventory`)

- Hero KPIs: Raw material stock vs reorder point, WIP lot counts, shrinkage rate.
- Operational Queue: Receiving/issuance tasks, pending lot releases.
- Recent Activity: Material movements, holds, lab approvals.
- Quick Actions: Create transfer, adjust inventory, print labels.

### Production (`/console/production`)

- Hero KPIs: Batch status (in-progress, conditioning, packaged), fermenter capacity, upcoming brews.
- Operational Queue: Today's brew steps with stage progression and dependencies.
- Recent Activity: Batch transitions, quality checks, variance reports.
- Quick Actions: Start batch, log deviation, schedule CIP.

### Sales (`/console/sales`)

- Hero KPIs: Bookings vs target, open opportunities, order fulfillment status.
- Operational Queue: Quotes awaiting approval, expiring offers, deliveries needing confirmation.
- Recent Activity: Order changes, customer touchpoints, credit issues.
- Quick Actions: Create order, log interaction, schedule tasting.

### Distribution & Logistics (`/console/distribution`)

- Hero KPIs: Fleet utilization, on-time delivery %, route exceptions.
- Operational Queue: Pending dispatches, route optimizations, carrier escalations.
- Recent Activity: Proof of delivery uploads, route deviations, temperature alerts.
- Quick Actions: Assign route, notify carrier, print manifests.

### Procurement (`/console/procurement`)

- Hero KPIs: Purchase spend vs budget, supplier OTIF, contract renewals.
- Operational Queue: RFQs awaiting bid, POs pending approval, expiring contracts.
- Recent Activity: Supplier score changes, receiving exceptions, invoice mismatches.
- Quick Actions: Create PO, launch RFQ, record supplier note.

### Maintenance (`/console/maintenance`)

- Hero KPIs: Equipment uptime, open work orders, PM compliance.
- Operational Queue: Critical work orders, preventive tasks due, parts shortages.
- Recent Activity: Completed jobs, condition monitoring alerts, technician assignments.
- Quick Actions: Create work order, request part, log downtime.

### Analytics (`/console/analytics`)

- Hero KPIs: Net revenue, brew efficiency, taproom performance index.
- Operational Queue: Scheduled report deliveries, anomaly investigations, predictive model runs.
- Recent Activity: Dashboard updates, alert dismissals, forecast revisions.
- Quick Actions: Share report, annotate metric, configure alert.

### Billing & Finance (`/console/billing`)

- Hero KPIs: AR aging, cash flow, unapplied payments.
- Operational Queue: Invoices pending approval, disputes, dunning tasks.
- Recent Activity: Payment receipts, write-offs, credit memos.
- Quick Actions: Issue invoice, apply payment, escalate dispute.

### Compliance & QA (`/console/compliance`)

- Hero KPIs: Audit readiness, permit status, incidents by severity.
- Operational Queue: Outstanding filings, CAPA tasks, inspection prep checklists.
- Recent Activity: Incident reports, corrective actions, document updates.
- Quick Actions: File report, assign CAPA, download certificates.

## Acceptance Criteria

1. A shared UI shell with left navigation, top command bar, and workspace panes is defined and documented for implementation.
2. Every domain landing page follows the standard grid (Hero KPIs, Operational Queue, Recent Activity, Quick Actions) with contextual content listed above.
3. Navigation taxonomy, color palette, and interaction expectations are captured for designers/devs.
4. Accessibility, density, and responsiveness requirements are articulated for QA sign-off.
5. Open questions and dependencies are documented before engineering kick-off.

## Open Questions & Dependencies

- Confirm priority order for domain roll-out (e.g., Users, Keg Inventory & Taproom first?).
- Align with Tech Lead on reusable component library (Thymeleaf vs. web components).
- Validate data availability for hero KPIs (requires API/service support).
- Determine analytics tooling for hero sparkline visualizations.
<<<<<<< Updated upstream
- Coordinate with compliance on brand/color approval for external auditors.

## Prototype
- Component-driven prototype lives at `src/main/resources/static/prototypes/enterprise-desktop.html` and renders `<mt-enterprise-console>` from the shared library.
- Bundled assets (`/ui/mt-ui.es.js`, `/ui/mt-ui.umd.js`) are produced by `ui-library` builds; reload the page after running `npm run build` to pick up changes.
- Load via Spring Boot static resources (`http://localhost:8080/prototypes/enterprise-desktop.html`) once the app is running.
- Toggle density, open the command palette, and trigger quick action toasts inside the component to validate interaction principles.
- Brewery Admin view hydrates the Identity console from `/api/v1/users?breweryId=…`; other domains currently use server-provided data placeholders.
=======
- Coordinate with compliance on brand/color approval for external auditors.
>>>>>>> Stashed changes
