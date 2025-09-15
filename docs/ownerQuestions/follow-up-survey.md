%s
# Mythic Tales BMS — Follow‑Up Survey (Prioritized)

Goal: Confirm details for pilot, alerts, big‑board, mobile, and ops so engineering can proceed with P0/P1 tasks. Please select options or add brief notes.

Instructions
- Check one option per line unless noted; add notes where helpful.
- Section A unblocks P0; Section B informs P1.

## A) Immediate Decisions

1) Pilot specifics
- Target date: __________ (defaults to 2025‑10‑31)
- Pilot personas/venues: __________________________________________

2) API auth tokens for `/api/**`
- [ ] Session only for now
- [ ] Add JWT in P2 (feature‑flagged)
- [ ] Add JWT in P1 (earlier)

3) Low‑volume alert (15% default)
- Channel(s): [X] UI indicator  [ ] Email  [ ] Slack/Webhook  [ ] Other: ______
- Scope of threshold: [ ] Global  [ ] Per‑venue configurable  [ ] Per‑tap configurable
- Notify which roles automatically: [ ] TAPROOM_ADMIN  [ ] BREWERY_ADMIN  [ ] BAR_ADMIN  [ ] None

4) Receive‑before‑tap policy
- [X] Optional (allow DISTRIBUTED → TAPPED)
- [ ] Require RECEIVED before TAPPED
- Notes: __________________________________________

5) Barcode/QR scanning
- Input method: [ ] Keyboard‑wedge scanner  [ ] Camera/PWA  [ ] Both
- Symbologies: [ ] Code128  [ ] QR  [ ] EAN‑13/UPC  [ ] Other: ______
- Content format: [ ] Serial only  [ ] URI (e.g., keg:SERIAL)  [ ] JSON payload  [ ] Other: ______

6) Big‑board taplist mode
- Refresh: [ ] 15s  [ ] 30s  [ ] 60s  [ ] Manual only
- Fields: [X] Beer name  [X] Style  [X] ABV  [X] Fill %  [ ] Keg size  [ ] Other: ______
- Theme: [ ] Light  [ ] Dark  [ ] Auto
- Multi‑venue rotation needed: [ ] No  [ ] Yes (list venues or pattern): __________

7) Archiving >24 months
- Storage: [ ] DB archive tables  [ ] Object storage (S3/CSV)  [ ] Data warehouse (Snowflake/BigQuery)  [ ] Other: ______
- Access: [ ] Admin export endpoint  [ ] Scheduled export  [ ] On request only

8) Weekly release window
- Day/time (local): ________________________
- Change freeze before release: [ ] None  [ ] 24h  [ ] 48h  [ ] Other: ______
- Rollback strategy: [ ] Redeploy prior image  [ ] DB rollback steps documented  [ ] Other: ______

## B) Near‑Term Preferences

9) Mobile targets
- Devices: [X] Phone  [X] Tablet  [ ] Desktop only
- Breakpoints: [ ] Bootstrap‑like defaults  [ ] Custom (list): __________
- Offline tolerance: [ ] Not required  [ ] Nice to have (read‑only)  [ ] Required (queue actions)

10) KegInventory API path naming
- [X] Keep `/api/v1/inventory` (backwards compat)
- [ ] Use `/api/v1/keg-inventory` (explicit) — plan redirect later

11) Observability and error reporting
- Metrics dashboard: [ ] Micrometer/Prometheus  [ ] DataDog  [ ] Other: ______
- Tracing/correlation: [ ] TraceId in logs only  [ ] OpenTelemetry spans
- Error reporting: [ ] Sentry  [ ] Rollbar  [ ] None  [ ] Other: ______

12) Data privacy
- PII scope: [ ] Username only  [ ] Name/email  [ ] Other: ______
- Data Processing Agreement needed with partners: [ ] No  [ ] Yes (list): ______

---

Optional notes / comments

- _____________________________________________________________
- _____________________________________________________________
