# Mythic Tales BMS — Open Questions for Sponsor/User

This list captures product questions to clarify vision, scope, constraints, and success criteria. Grouped by domain area for quick review.

## Vision & Success
- What is the primary goal for the next 3–6 months (taproom ops excellence, inventory backbone, sales/billing, or integrations)?
- What are the must‑have outcomes for an initial GA (what must be true to call it successful)?
- Which KPIs matter most (e.g., draft loss %, stockouts, time to tap, invoice latency)?

## Tenancy & Organizations
- Will one deployment serve multiple breweries (multi‑tenant) or one per brewery?
- If multi‑tenant, do we need tenant isolation at DB level (schema-per-tenant) or row‑level filtering is sufficient?
- Will partner bars be managed by breweries, or can they be independent tenants that opt-in to share data?

## Users, Roles, Permissions
- Current roles: SITE_ADMIN, BREWERY_ADMIN, TAPROOM_ADMIN, BAR_ADMIN, TAPROOM_USER. Any others (e.g., FINANCE, PRODUCTION, LOGISTICS)?
- Do taproom/bar users need finer‑grained permissions (e.g., can pour but not blow; view-only)?
- Is SSO needed (OIDC/SAML) or local accounts are fine for now?

## Taproom Operations
- Pour sizes: fixed presets only or configurable per venue? Need support for custom pours or flights?
- Low-volume thresholds and alerts: what levels and notification channels (UI only, email, Slack)?
- Do we need keg lockouts (e.g., maintenance mode, stop pours under certain thresholds)?
- Should “blow” auto‑end placement always, or support partial detach scenarios?
- Do we support multiple taps feeding a single keg (manifold) or always 1:1?

## Keg Lifecycle & Inventory
- Authoritative states include EMPTY, CLEAN, FILLED, DISTRIBUTED, RECEIVED, TAPPED, BLOWN, RETURNED. Any additional states (e.g., LOST, DAMAGED, QA_HOLD)?
- Who owns the truth of inventory location/status in the short term (Taproom Ops vs. a dedicated Inventory context)?
- Should “receive” events be required before a taproom can tap a keg (strict workflow) or can they tap distributed kegs directly?
- Returns: what is the intended flow for blown kegs back to brewery (scan on pickup, batch close, etc.)?
- Serial numbers: do we scan barcodes/RFIDs or enter manually? Any format constraints?

## Production & Catalog
- Do we need to model Recipes and Batches now, or is a simple Beer catalog enough for phase 1?
- Pricing by product/packaging: defined here or in Sales context later?
- Any regional variations for naming, compliance (ABV rounding), or label data we should store?

## Sales, Orders, Distribution
- Are wholesale orders in scope for the current phase? If so, who creates/approves them and how do we fulfill?
- Distribution logistics: do we need routes, proof of delivery, or simple transfer records suffice initially?
- Will external distributors be integrated, or only first‑party movements at launch?

## Billing & Payments
- Is invoicing in scope for near term? If yes, what payment methods and tax rules do we support?
- Do we need integration to accounting systems (e.g., QuickBooks/Xero) or export files are sufficient?

## Compliance & Reporting
- What compliance reports are required (by region/state)? Frequency and format?
- How long do we retain keg, placement, and event history? Any audit requirements?

## API & Integrations
- External systems to integrate soon (POS, e‑commerce, carrier APIs)? What data flows and SLAs?
- For API auth, should we add token‑based auth for `/api/**` (JWT) in addition to session?
- Webhooks or event streams: any consumers we should plan for (Analytics, data warehouse)?

## UI/UX
- Which interfaces are core: taproom admin, brewery admin, partner bar admin, site admin. Any mobile requirements?
- Accessibility targets (WCAG level)? Any specific brand/design guidelines to follow?
- Do we need a “big board” taplist display mode (read‑only, auto‑refresh)?

## Observability & Operations
- What environments do we target (dev, staging, prod)? Any CI/CD constraints we should adopt?
- Error tracking/telemetry preferences (e.g., OpenTelemetry, Sentry) and required metrics/dashboards?

## Data & Migration
- Will we migrate historical data from another system? If yes, what sources and cutover plan?
- For demo/staging, is the current DataInitializer volume appropriate, or should we reduce/parameterize it?

## Performance & Scale
- Expected sizes: number of breweries, taprooms, taps per venue, concurrent users?
- Availability targets and acceptable maintenance windows?

## Legal & Privacy
- Any PII/consent considerations for user accounts? Data residency constraints?
- Data retention and deletion policies (right to be forgotten)?

## Roadmap & Priorities
- Please rank the near‑term priorities among: Taproom Ops polish, Inventory backbone, Orders/Distribution, Billing, Analytics.
- What is the earliest date we need a pilot ready, and with which personas?

## Acceptance & Change Management
- How will we validate features (UAT criteria)? Who signs off?
- Preferred cadence for releases and stakeholder demos?

---

If helpful, I can convert this into a living questionnaire in issues or a short survey grouped by phase.
