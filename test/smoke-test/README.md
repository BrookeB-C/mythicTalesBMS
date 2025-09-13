# Smoke Tests

This folder contains simple curl-based smoke tests for the Taplist app.

What it does
- Logs in as a test user (defaults: tapadmin/password)
- Loads the Admin Taproom page
- Loads the Taplist, taps an available keg into an empty tap, pours 16 oz, then blows the keg
- Verifies the Admin Events page shows TAP, POUR, and BLOW entries

Prerequisites
- App running locally at http://localhost:8080
- Seed data available (in-memory H2 is seeded on app start via `DataInitializer`)
- bash, curl, sed, grep, awk available on PATH

Usage
- Run end-to-end smoke test:
  - `bash test/smoke-test/run_smoke.sh`
- Override credentials or base URL:
  - `BASE_URL=http://localhost:8080 USERNAME=tapadmin PASSWORD=password bash test/smoke-test/run_smoke.sh`

Notes
- If you’ve previously performed actions and the in-memory data no longer matches initial assumptions, restart the app to re-seed.
- These are lightweight smoke checks, not exhaustive tests.

