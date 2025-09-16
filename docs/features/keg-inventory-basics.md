# Keg Inventory Basics

Purpose
- Provide a simple, consistent API to track keg status and location across venues, aligned with Taproom events.

Base Path
- Configurable via `bms.keginventory.apiBasePath` (default: `/api/v1/keg-inventory`).

Operations
- Assign — POST `{base}/assign` → Assign a keg to a venue (status→DISTRIBUTED)
- Receive — POST `{base}/receive` → Receive a keg at a venue (status=RECEIVED)
- Move — POST `{base}/move` → Move a keg between venues (status→DISTRIBUTED unless RECEIVED)
- Return — POST `{base}/return` → Return a keg to the brewery (status=EMPTY, clears assignment)

Requests
- Assign
  {
    "kegId": 123,
    "venueId": 45
  }
- Receive
  {
    "kegId": 123,
    "venueId": 45
  }
- Move
  {
    "kegId": 123,
    "fromVenueId": 45,
    "toVenueId": 46
  }
- Return
  {
    "kegId": 123
  }

Responses
- Success: 200 application/json — returns `KegDto` (id, beer, breweryId, size, totals, status, assignedVenueId, serialNumber, version)
- Errors: `application/problem+json` with fields: `status`, `error`, `message`, `details`, `timestamp`

Common Error Cases
- 400 Bad Request — Bean validation failed (missing/null ids)
  {
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "details": {"kegId": "must not be null"},
    "timestamp": "..."
  }
- 403 Forbidden — Scope/affiliation violation
  {
    "status": 403,
    "error": "Forbidden",
    "message": "Forbidden",
    "timestamp": "..."
  }
- 422 Unprocessable Entity — Unsupported transition (e.g., move tapped keg)
  {
    "status": 422,
    "error": "Unprocessable Entity",
    "message": "Cannot move a tapped keg",
    "timestamp": "..."
  }

Curl Examples
- Assign: `curl -X POST localhost:8080/api/v1/keg-inventory/assign -H 'Content-Type: application/json' -d @src/main/resources/samples/keg-inventory/assign.json`
- Receive: `curl -X POST localhost:8080/api/v1/keg-inventory/receive -H 'Content-Type: application/json' -d @src/main/resources/samples/keg-inventory/receive.json`
- Move: `curl -X POST localhost:8080/api/v1/keg-inventory/move -H 'Content-Type: application/json' -d @src/main/resources/samples/keg-inventory/move.json`
- Return: `curl -X POST localhost:8080/api/v1/keg-inventory/return -H 'Content-Type: application/json' -d @src/main/resources/samples/keg-inventory/return.json`

Notes
- Endpoints require authenticated roles: `SITE_ADMIN`, `BREWERY_ADMIN`, `TAPROOM_ADMIN`, or `BAR_ADMIN`.
- Event integration: Taproom events (tap/pour/blow/untap) update the keg status and remaining ounces.

Acceptance Criteria
- Assign
  - Given a FILLED or CLEAN keg at the brewery and a venue in the same brewery
  - When POST `{base}/assign` with `kegId` and `venueId`
  - Then response is 200 with `status=DISTRIBUTED` and `assignedVenueId=venueId`
  - And a 403 is returned if the user’s brewery scope does not match either the keg or the venue

- Receive
  - Given a keg assigned to a venue (DISTRIBUTED)
  - When POST `{base}/receive` with `kegId` and `venueId`
  - Then response is 200 with `status=RECEIVED` and `assignedVenueId=venueId`
  - And invalid body yields 400 with Problem JSON details

- Move
  - Given a keg assigned to venue A
  - When POST `{base}/move` with `fromVenueId=A` and `toVenueId=B`
  - Then response is 200 with `assignedVenueId=B` and `status=DISTRIBUTED` unless it was `RECEIVED`
  - And if `fromVenueId == toVenueId`, respond 422 with Problem JSON message

- Return
  - Given a keg at a venue
  - When POST `{base}/return` with `kegId`
  - Then response is 200 with `status=EMPTY` and `assignedVenueId=null`, and remaining resets to size ounces
  - And unauthorized scope returns 403 Problem JSON
