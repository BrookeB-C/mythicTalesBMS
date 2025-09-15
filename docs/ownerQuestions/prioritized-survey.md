# Mythic Tales BMS — Prioritized Short Survey

Goal: Capture fast product decisions to unblock design/build for the next phases. Please answer by selecting an option or adding brief notes.

Instructions
- For each item, pick one option (or write a short note if “Other”).
- Priority: Items in Section A unblock Taproom Ops and architecture; Section B helps sequence near‑term roadmap.

## A) Immediate Decisions (Top Priority)

1) Deployment model (tenancy)
- [ ] Single tenant per deployment
- [ ] Multi‑tenant (shared deployment, row‑level scoping)
- [ ] Multi‑tenant with stronger isolation (schema/db per tenant)

2) Authentication & SSO
- [ ] Local accounts only (for now)
- [ ] Add SSO (OIDC/SAML)
- Notes:

3) Roles in scope for MVP
- [ ] Current set is enough (SITE_ADMIN, BREWERY_ADMIN, TAPROOM_ADMIN, BAR_ADMIN, TAPROOM_USER)
- [ ] Add roles (list): ______________________________

4) Taproom pour sizes
- [ ] Fixed presets (4/8/12/16/20 oz)
- [ ] Configurable per venue
- [ ] Other: ______________________________

5) Low‑volume threshold & alerts
- Alert threshold (percent): [ ] 5%  [ ] 10%  [ ] 15%  [ ] Other: ____%
- Channel: [ ] UI only  [ ] Email  [ ] Slack/Webhook  [ ] Other: ______

6) “Receive before tap” rule
- [ ] Required: a keg must be RECEIVED at the venue to be tappable
- [ ] Optional: tap can advance DISTRIBUTED → TAPPED directly

7) KegInventory system of record (near term)
- [ ] Taproom Ops (current module) remains source of truth
- [ ] Introduce dedicated KegInventory context now

8) Orders & Distribution in near term (next 1–2 releases)
- [ ] Out of scope for now
- [ ] Basic wholesale orders + shipments
- [ ] Internal requisitions only (taproom pulls from brewery)

9) Invoicing & payments in near term (next 1–2 releases)
- [ ] Out of scope for now
- [ ] Simple invoices (manual payments)
- [ ] Invoices + payment recording (basic taxes)

10) API auth for `/api/**`
- [ ] Session cookie only (dev/admin usage)
- [ ] Add Bearer token (JWT) now

11) KPIs to optimize first (pick up to 3)
- [ ] Draft loss %  [ ] Stockouts  [ ] Time‑to‑tap  [ ] Keg turnaround time  [ ] Invoice latency  [ ] Other: ______

12) Pilot timeline & scope
- Target date: __________
- Pilot personas/venues: ______________________________

## B) Next‑Up Decisions (Helpful Soon)

13) Serial numbers & scanning
- [ ] Manual entry only
- [ ] Barcodes/QR
- [ ] RFID (future)

14) Manifold support (multiple taps, one keg)
- [ ] Not needed (1:1 only)
- [ ] Needed (allow 1:N placements)

15) Compliance reporting (near term)
- [ ] None
- [ ] Basic exports
- [ ] Specific filings (list regions): __________________

16) External integrations to plan for this year
- [ ] POS (name): __________
- [ ] Accounting (QB/Xero)
- [ ] E‑commerce / Direct‑to‑consumer
- [ ] Carriers / route planning

17) UI/UX targets
- [ ] Desktop web only
- [ ] Include mobile admin needs
- [ ] Big‑board display mode for taplist

18) Environments & release cadence
- Envs: [ ] Dev  [ ] Staging  [ ] Prod
- Release cadence: [ ] Weekly  [ ] Bi‑weekly  [ ] Monthly  [ ] Other: ______

19) Data retention
- [ ] Keep all event history
- [ ] Retain 24 months, archive thereafter
- [ ] Other: ______________________

20) Accessibility level
- [ ] Best effort
- [ ] WCAG AA target
- [ ] Other: ______________________

---

Optional notes / comments

- _____________________________________________________________
- _____________________________________________________________

21) Production Inventory scope (near term)
- [ ] Out of scope for now
- [ ] Track raw materials (lots, receipts, adjustments)
- [ ] Track WIP + finished goods staging and handoff to KegInventory
