%s
# Mythic Tales BMS — Follow‑Up Survey (Prioritized)

Goal: Confirm details for pilot, alerts, big‑board, mobile, and ops so engineering can proceed with P0/P1 tasks. Please select options or add brief notes.

Instructions
- Check one option per line unless noted; add notes where helpful.
- Section A unblocks P0; Section B informs P1.

## A) Immediate Decisions

1) Pilot specifics
- Target date: __________ (defaults to 2025‑10‑31)
- Pilot personas/venues: ____Brewery Owner, Brewer, Taproom operator______________________________________

2) API auth tokens for `/api/**`
- [X] Session only for now
- [ ] Add JWT in P2 (feature‑flagged)
- [ ] Add JWT in P1 (earlier)

3) Low‑volume alert (15% default)
- Channel(s): [X] UI indicator  [ ] Email  [ ] Slack/Webhook  [ ] Other: ______
- Scope of threshold: [ ] Global  [X] Per‑venue configurable  [ ] Per‑tap configurable
- Notify which roles automatically: [X] TAPROOM_ADMIN  [ ] BREWERY_ADMIN  [X] BAR_ADMIN  [ ] None

4) Receive‑before‑tap policy
- [X] Optional (allow DISTRIBUTED → TAPPED)
- [ ] Require RECEIVED before TAPPED
- Notes: __________________________________________

5) Barcode/QR scanning
- Input method: [ ] Keyboard‑wedge scanner  [X] Camera/PWA  [ ] Both
- Symbologies: [ ] Code128  [X] QR  [ ] EAN‑13/UPC  [ ] Other: ______
- Content format: [ ] Serial only  [ ] URI (e.g., keg:SERIAL)  [X] JSON payload  [ ] Other: ______

6) Big‑board taplist mode
- Refresh: [X] 15s  [ ] 30s  [ ] 60s  [ ] Manual only
- Fields: [X] Beer name  [X] Style  [X] ABV  [X] Fill %  [ ] Keg size  [ ] Other: ______
- Theme: [ ] Light  [ ] Dark  [X] Auto
- Multi‑venue rotation needed: [X] No  [ ] Yes (list venues or pattern): __________

7) Archiving >24 months
- Storage: [ ] DB archive tables  [ ] Object storage (S3/CSV)  [ ] Data warehouse (Snowflake/BigQuery)  [ ] Other: ______
- Access: [X] Admin export endpoint  [ ] Scheduled export  [ ] On request only

8) Weekly release window
- Day/time (local): ______Friday__________________
- Change freeze before release: [X] None  [ ] 24h  [ ] 48h  [ ] Other: ______
- Rollback strategy: [ X Redeploy prior image  [X] DB rollback steps documented  [ ] Other: ______

## B) Near‑Term Preferences

9) Mobile targets
- Devices: [X] Phone  [X] Tablet  [ ] Desktop only
- Breakpoints: [ ] Bootstrap‑like defaults  [ ] Custom (list): __________
- Offline tolerance: [X] Not required  [ ] Nice to have (read‑only)  [ ] Required (queue actions)

10) KegInventory API path naming
- [ ] Keep `/api/v1/inventory` (backwards compat)
- [X] Use `/api/v1/keg-inventory` (explicit) — plan redirect later

11) Observability and error reporting
Look at later
- Metrics dashboard: [ ] Micrometer/Prometheus  [ ] DataDog  [ ] Other: ______
- Tracing/correlation: [ ] TraceId in logs only  [ ] OpenTelemetry spans
- Error reporting: [ ] Sentry  [ ] Rollbar  [ ] None  [ ] Other: ______

12) Data privacy
Look at later
- PII scope: [ ] Username only  [ ] Name/email  [ ] Other: ______
- Data Processing Agreement needed with partners: [ ] No  [ ] Yes (list): ______

---

Optional notes / comments

- _____________________________________________________________
- _____________________________________________________________
