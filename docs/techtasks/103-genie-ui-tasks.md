# Genie UI — Taproom Ops & Big‑Board Tasks

Scope: Thymeleaf MVC, client‑side enhancements (AJAX, QR scanning), responsive/mobile, accessibility.

## Sprint 1 — Board & Indicators
- Big‑board route
  - Add `GET /taplist/board?venueId=...` (read‑only, no auth prompts beyond session)
  - Auto‑refresh every 15s (config: `bms.ui.bigboard.refreshSeconds`)
  - Fields: beer name, style, ABV, fill %, low‑volume indicator at configured threshold
  - Responsive layout suitable for TV displays
- Low‑volume UI
  - Show visual warning at threshold; ensure color contrast meets WCAG AA

## Sprint 2 — QR Scanning (Camera/PWA)
- Implement camera scanner using modern browser APIs (BarcodeDetector/WebRTC)
  - Fallback: manual serial entry form
  - Parse QR JSON payload with `serial` and `breweryId`
  - Post appropriate actions (e.g., tap/receive) using existing endpoints
- Mobile responsiveness
  - Ensure forms and tables adapt to phone/tablet breakpoints; large taplists remain scrollable and readable

## Sprint 3 — UX Polish & A11y
- Keyboard navigation and focus management for action forms
- ARIA labels for SVG pint and controls; error summaries on validation failures
- Optional: persistent toast notifications for pour/blow/tap actions

Artifacts to touch
- `src/main/resources/templates/**` (new board template + tweaks)
- `src/main/resources/static/css/**` (responsive and board styles)
- MVC controllers (`/taplist/board`)

Done criteria
- Board route renders and auto‑refreshes; low‑volume indicators accurate
- QR scanning works on modern mobile browsers with graceful fallback
- Pages pass basic accessibility checks (contrast, labels, keyboard navigation)
