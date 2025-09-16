# Manual QA Test Suite — Mythic Tales BMS Core Features

This guide outlines high-value manual regression tests for the functionality currently implemented in Mythic Tales BMS. Execute these on every release candidate before sign-off, or when major related changes land. All tests assume the application is running locally with the default `dev` profile (`mvn spring-boot:run`) and seeded demo data from `DataInitializer`.

## Test Setup
- Browsers: Verify once in Chromium-based browser (Chrome/Edge) and once in Firefox.
- Base URL: `http://localhost:8080`
- Demo accounts (username / password):
  - `siteadmin` / `password`
  - `brewadmin` / `password`
  - `tapadmin` / `password`
  - `tapuser` / `password`
  - `baradmin` / `password`
- Useful IDs from seeded data (can also be read from the UI/API if data changes):
  - First MythicTales tap IDs: 1 and 2 (taproom), 3 (bar)
  - Common venue IDs: MythicTales Taproom → `1`, MythicTales Downtown Bar → `2`
  - Sample keg IDs visible on brewery admin "Kegs" tab.
- Resetting state: stop the app, `mvn clean`, and restart to re-run the initializer, or clear the H2 database file.

## Authentication & Access Control

### QA-AUTH-01 — Site admin login redirects to Site Admin
- **Feature**: Role-based login redirect
- **Precondition**: User logged out
- **Steps**:
  1. Navigate to `/login`.
  2. Sign in as `siteadmin` / `password`.
- **Expected**: Redirect to `/admin/site`, page lists breweries/bars and shows "Manage Users" button.

### QA-AUTH-02 — Invalid credentials rejected
- **Feature**: Login validation
- **Precondition**: Browser on `/login`
- **Steps**:
  1. Submit username `siteadmin` with password `wrongpass`.
- **Expected**: Stay on login page with `?error` flag set; visible error message.

### QA-AUTH-03 — Brewery admin blocked from Site Admin
- **Feature**: Authorization enforcement
- **Precondition**: Logged in as `brewadmin`
- **Steps**:
  1. After login redirect to `/admin/brewery`, manually browse to `/admin/site`.
- **Expected**: Access denied (403) or redirect back to `/login`.

### QA-AUTH-04 — Logout clears session
- **Feature**: Logout handling
- **Precondition**: Logged in as any user
- **Steps**:
  1. Visit `/logout`.
  2. Attempt to revisit `/taplist`.
- **Expected**: Redirected to `/login`.

## Taplist Operations

### QA-TAP-01 — Taproom user sees scoped taplist
- **Feature**: Taplist filtering by user affiliation
- **Precondition**: Logged in as `tapuser`
- **Steps**:
  1. Open `/taplist`.
- **Expected**: Only taps for MythicTales Taproom (IDs 1-2) appear; bar tap (ID 3) hidden.

### QA-TAP-02 — Pour updates keg fill level
- **Feature**: Tap pour workflow
- **Precondition**: Logged in as `tapadmin`; at `/taplist`
- **Steps**:
  1. Choose a tap with beer remaining.
  2. Use the "Pour" form to serve 16 oz.
- **Expected**: Page refresh via AJAX, fill meter drops roughly 16 oz, keg remaining value decreases, new POUR event recorded (see QA-TAPROOM-04).

### QA-TAP-03 — Blow empties and clears tap
- **Feature**: Blow keg action
- **Precondition**: Logged in as `tapadmin`; choose a tap nearly empty
- **Steps**:
  1. Use the "Blow" button.
- **Expected**: Tap row shows "Empty" state, actions swap to tap keg selector, keg appears under Taproom Admin "Blown Kegs" tab.

### QA-TAP-04 — Tap empty tap from received inventory
- **Feature**: Tap keg action
- **Precondition**: Logged in as `tapadmin`; tap with empty slot
- **Steps**:
  1. Visit `/admin/taproom?tab=kegs` and confirm at least one keg in RECEIVED status.
  2. Switch to `Taplist` tab.
  3. Pick "Tap Keg" on an empty tap and select one of the received kegs.
- **Expected**: Tap row shows new beer, fill meter near 100%, keg removed from `Kegs` tab, TAP event logged.

## Taproom Administration

### QA-TAPROOM-01 — Receive inbound keg
- **Feature**: Taproom inbound → received flow
- **Precondition**: `tapadmin` logged in; `/admin/taproom?tab=inbound`
- **Steps**:
  1. Locate a keg in DISTRIBUTED status.
  2. Click "Receive".
- **Expected**: Keg moves to `Kegs` tab with status RECEIVED.

### QA-TAPROOM-02 — Return blown keg to brewery
- **Feature**: Taproom Return
- **Precondition**: Have a blown keg (from QA-TAP-03)
- **Steps**:
  1. Open `/admin/taproom?tab=blown`.
  2. Click "Return to Brewery" for the blown keg.
- **Expected**: Keg removed from list; appears under Brewery Admin `Returned` tab for `brewadmin`.

### QA-TAPROOM-03 — Update taproom name persists
- **Feature**: Taproom metadata edit
- **Precondition**: `tapadmin` on `/admin/taproom`
- **Steps**:
  1. Change the taproom name via the form and submit.
  2. Refresh page.
- **Expected**: Updated name persists; visible in `brewadmin` taproom list.

### QA-TAPROOM-04 — Events tab reflects recent activity
- **Feature**: Event feed
- **Precondition**: Complete QA-TAP-02 and QA-TAP-03 first
- **Steps**:
  1. Visit `/admin/taproom?tab=events`.
- **Expected**: Most recent events include TAP, POUR, and BLOW with correct timestamps, tap numbers, volumes, and acting user when applicable.

## Brewery Administration

### QA-BREW-01 — View taprooms and metrics
- **Feature**: Taproom overview metrics
- **Precondition**: Logged in as `brewadmin`
- **Steps**:
  1. On `/admin/brewery`, review the Taprooms tab.
- **Expected**: Table lists all taprooms with tap counts and active keg counts; "Admin" link opens taproom admin page.

### QA-BREW-02 — Distribute keg to taproom
- **Feature**: Keg distribution workflow
- **Precondition**: `brewadmin` on `/admin/brewery?tab=kegs`
- **Steps**:
  1. Select a FILLED keg without assigned venue.
  2. Choose "MythicTales Taproom" from the venue dropdown and submit "Distribute".
- **Expected**: Keg leaves Unassigned table, appears in `Assigned` tab with status DISTRIBUTED, shows selected venue.

### QA-BREW-03 — Return assigned keg resets inventory
- **Feature**: Brewery return/reset workflow
- **Precondition**: Keg distributed earlier (QA-BREW-02)
- **Steps**:
  1. Open `/admin/brewery?tab=assigned`.
  2. Find the keg and click "Return".
  3. Switch to `Returned` tab then use "Reset to Empty".
- **Expected**: Keg appears under `Returned` with status RETURNED, then back under `Kegs` tab as EMPTY with remaining ounces reset to full size.

### QA-BREW-04 — Filter brewery users by venue
- **Feature**: Brewery user directory filters
- **Precondition**: `brewadmin` on `/admin/brewery?tab=users`
- **Steps**:
  1. Use "Filter by Venue" dropdown to choose "MythicTales Taproom".
- **Expected**: Table restricts to users assigned to that taproom; clearing filter shows all brewery-related users.

### QA-BREW-05 — Add new taproom
- **Feature**: Taproom creation
- **Precondition**: `brewadmin` on `/admin/brewery`
- **Steps**:
  1. Enter unique taproom name in "Add Taproom" form and submit.
- **Expected**: New taproom row appears with zero taps; `taprooms` table row has working Admin link.

## Beer Catalog Maintenance

### QA-BEER-01 — Link BJCP style to beer
- **Feature**: BJCP style assignment
- **Precondition**: `brewadmin` chooses "Beers" tab or visits `/admin/beer`
- **Steps**:
  1. Use year selector (2015 vs 2021) to refresh style list.
  2. For a beer lacking a BJCP link, choose a style and submit "Link".
- **Expected**: Linked style appears in final column for that beer; persists on refresh.

## Site Administration

### QA-SITE-01 — Site admin user list access
- **Feature**: Site-level user directory security
- **Precondition**: Logged in as `siteadmin`
- **Steps**:
  1. From `/admin/site`, click "Manage Users".
- **Expected**: `/admin/users` loads table of all accounts with role and affiliation columns.

### QA-SITE-02 — Non-site admin blocked from user list
- **Feature**: Access control for `/admin/users`
- **Precondition**: Logged in as `brewadmin`
- **Steps**:
  1. Attempt to browse to `/admin/users`.
- **Expected**: Access denied (403) or redirect to login.

## Bar Administration

### QA-BAR-01 — Bar admin sees assigned taps only
- **Feature**: Bar-specific taplist
- **Precondition**: Logged in as `baradmin`
- **Steps**:
  1. Verify login redirect to `/admin/bar`.
- **Expected**: Table lists only taps attached to MythicTales Downtown Bar (Tap #3 by default). Actions column should reflect read-only view (no forms).

## REST API Smoke
Use curl or Postman with session cookie established via login (or run `curl -u` with HTTP basic if configured). Ensure CSRF token handled for POST requests (copy from browser or disable via `X-XSRF-TOKEN` header using cookie).

### QA-API-01 — List taps scoped to taproom admin
- **Precondition**: Authenticate as `tapadmin`; reuse session cookie
- **Steps**:
  1. `GET /api/v1/taps`
- **Expected**: 200 with JSON page containing only taps for taproom `tapadmin` manages; includes `fillPercent` and `keg` details.

### QA-API-02 — Pour tap via API
- **Precondition**: Identify a tap ID from QA-API-01
- **Steps**:
  1. `POST /api/v1/taps/{tapId}/pour` with body `{ "ounces": 8 }` and CSRF header.
- **Expected**: 200 with updated tap payload showing decreased `remainingOunces`; event available via `/api/v1/venues/{venueId}/events`.

### QA-API-03 — Unauthorized taproom access blocked
- **Precondition**: Use `baradmin` session; target tap belonging to taproom
- **Steps**:
  1. `POST /api/v1/taps/{taproomTapId}/pour`.
- **Expected**: 403 Forbidden.

### QA-API-04 — Keg distribution lifecycle endpoints
- **Precondition**: `brewadmin` session; locate keg ID from `/api/v1/kegs?status=FILLED`
- **Steps**:
  1. `POST /api/v1/kegs/{id}/distribute` with `{ "venueId": 1 }`.
  2. `POST /api/v1/kegs/{id}/receive`.
  3. `POST /api/v1/kegs/{id}/return`.
- **Expected**: Each call returns 200 with updated status (`DISTRIBUTED`, `RECEIVED`, `EMPTY`); verify via follow-up `GET /api/v1/kegs/{id}`.

### QA-API-05 — Site admin user listing secured
- **Precondition**: `brewadmin` session
- **Steps**:
  1. `GET /api/v1/users`.
- **Expected**: 403 Forbidden.
  2. Repeat as `siteadmin` — expect 200 with list of users.

## Observability & Docs

### QA-DOCS-01 — Swagger UI available in dev
- **Feature**: API documentation access
- **Precondition**: App running default profile
- **Steps**:
  1. Visit `/swagger-ui.html` while logged in as any user.
- **Expected**: Swagger UI loads with API groups `api-v1` and `admin`; endpoints reflect recent changes.

### QA-OBS-01 — Actuator health endpoint
- **Feature**: Basic health check (when enabled)
- **Steps**:
  1. `GET /actuator/health` without auth.
- **Expected**: 200 with `"status":"UP"`. (If secured in future, adjust test accordingly.)

---
Update this suite when new features ship, when API error model changes, or when role scopes are updated. Link this file from release checklists and PR templates when manual QA is required.
