**Overview**
- Scope: REST API for the taplist platform covering taps, kegs, venues, taprooms, breweries, events, users, and basic auth, aligned with the current Spring MVC services and domain model.
- Style: Resource‑oriented under `"/api/v1"`, JSON requests/responses, standard pagination and filtering.
- Auth: Session cookie for browser flows or Bearer token (JWT or opaque). Protect admin endpoints with roles.
- Concurrency: Optimistic locking via entity `version` or `If-Match`/ETag where applicable.

**Versioning & Conventions**
- Base path: `/api/v1`
- Media type: `application/json; charset=utf-8`
- Timestamps: ISO‑8601 UTC (e.g., `2025-09-14T12:34:56Z`)
- Pagination: `page` (0-based), `size` (default 20, max 200), `sort=field,(asc|desc)`
- Errors: Consistent problem shape
  - `status` int, `error` short code, `message` human text, `details` map, `timestamp` ISO
- Filtering: Simple query params; list endpoints accept filters indicated below

**Auth**
- `POST /api/v1/auth/login`
  - Body: `{ "username": string, "password": string }`
  - 200: Session established (Set‑Cookie) or token `{ "token": string, "expiresAt": string }`
  - 401: Invalid credentials
- `POST /api/v1/auth/logout`
  - 204: Session invalidated

Roles (from codebase): `SITE_ADMIN`, `BREWERY_ADMIN`, `TAPROOM_ADMIN`, `BAR_ADMIN`.

**Resources**

— Taps —
- `GET /api/v1/taps`
  - Returns taps for the current user context: taproom, bar, or all (site admin).
  - Query: `venueId`, `taproomId`, `barId` (overrides user context); `page`, `size`, `sort`
  - 200: `{ content: [ Tap ], page, size, totalElements }`
- `GET /api/v1/venues/{venueId}/taps`
- `GET /api/v1/taprooms/{taproomId}/taps`
- `GET /api/v1/bars/{barId}/taps`
  - 200: `[ Tap ]`
- `POST /api/v1/taps/{tapId}/tap-keg` (roles: brewery/taproom/bar/site where permitted)
  - Body: `{ "kegId": number, "actorUserId"?: number, "expectedTapVersion"?: number }`
  - Effects: Assigns keg to tap; records event; sets keg status if needed.
  - 200: `Tap` updated; 409 if version conflict.
- `POST /api/v1/taps/{tapId}/pour`
  - Body: `{ "ounces": number, "actorUserId"?: number, "expectedKegVersion"?: number }`
  - 200: `Keg` with updated `remainingOunces` and derived fill percent; 422 if exceeds remaining.
  - Constraints: `ounces` must match fixed presets (4/8/12/16/20) unless admin override flag is set.
- `POST /api/v1/taps/{tapId}/blow`
  - Body: `{ "actorUserId"?: number, "expectedKegVersion"?: number }`
  - Effects: Marks keg blown; detaches from tap; records event.
  - 200: `Tap` with `keg=null` and `Keg` status `BLOWN`.

Tap shape:
```
{
  "id": 1,
  "number": 4,
  "venueId": 10,
  "taproomId": 5,
  "barId": null,
  "keg": Keg | null,
  "version": 7
}
```

— Kegs —
- `GET /api/v1/kegs`
  - Query: `breweryId` (required for brewery admin unless site admin), `status` (EMPTY|CLEAN|FILLED|DISTRIBUTED|RECEIVED|BLOWN|RETURNED), `assignedVenueId`, `q` (serial or beer name contains), pagination params
  - 200: `{ content: [ Keg ], page, size, totalElements }`
- `GET /api/v1/kegs/{id}` → 200: `Keg`
- `POST /api/v1/kegs`
  - Body: `{ "beerId": number, "breweryId": number, "sizeSpecId": number, "serialNumber": string }`
  - 201: `Keg` with `status=EMPTY`, `totalOunces` from size
- `PATCH /api/v1/kegs/{id}`
  - Body: partial update: size change resets totals; supports conditional `If-Match` using `version`
  - 200: `Keg`, 409 on version conflict
- `POST /api/v1/kegs/{id}/distribute` (brewery admin)
  - Body: `{ "venueId": number }`
  - 200: `Keg` with `status=DISTRIBUTED`, `assignedVenue`
- `POST /api/v1/kegs/{id}/receive` (taproom/bar admin)
  - 200: `Keg` with `status=RECEIVED`
- `POST /api/v1/kegs/{id}/return` (brewery/taproom/bar admin as appropriate)
  - Brewery view: clears `assignedVenue`, sets `status=EMPTY`, resets remaining to total
  - Taproom view: for blown kegs: sets `status=RETURNED`, clears `assignedVenue`
- `POST /api/v1/kegs/{id}/clean` (brewery admin)
  - 200: `Keg` with `status=CLEAN`

Keg shape:
```
{
  "id": 42,
  "beer": { "id": 9, "name": "Pils", "style": "Pilsner", "abv": 5.1 },
  "breweryId": 3,
  "size": { "id": 2, "name": "Half Barrel", "ounces": 1984 },
  "totalOunces": 1984,
  "remainingOunces": 1024,
  "status": "RECEIVED",
  "assignedVenueId": 10,
  "serialNumber": "KEG-2024-0001",
  "version": 11
}
```

— Events —
- `GET /api/v1/venues/{venueId}/events`
  - 200: `[ KegEvent ]` ordered newest first

KegEvent (simplified):
```
{ "id": 77, "venueId": 10, "tapId": 1, "kegId": 42, "type": "TAP|POUR|BLOW|RECEIVE|RETURN|DISTRIBUTE|CLEAN", "ounces"?: 10.0, "createdAt": "...", "actorUserId"?: 5 }
```

— Venues —
- `GET /api/v1/venues`
  - Query: `breweryId`, `type` (TAPROOM|BAR), `q` (name contains), pagination
- `GET /api/v1/venues/{id}` → 200: `Venue`

Venue shape:
```
{ "id": 10, "name": "Main Taproom", "type": "TAPROOM", "breweryId": 3 }
```

— Taprooms —
- `GET /api/v1/taprooms?breweryId=...`
- `POST /api/v1/taprooms` (brewery admin)
  - Body: `{ "name": string, "breweryId": number }`
  - 201: `Taproom` and a matching `Venue` (type TAPROOM) are created
- `DELETE /api/v1/taprooms/{id}` (brewery admin)
  - 204 on success; 409 if taps exist or referential integrity prevents deletion

Taproom shape:
```
{ "id": 5, "name": "North", "breweryId": 3 }
```

— Breweries —
- `GET /api/v1/breweries`
- `GET /api/v1/breweries/{id}`
- `PATCH /api/v1/breweries/{id}` (brewery admin)
  - Body: `{ "name"?: string }`

— Bars —
- `GET /api/v1/bars`
- `GET /api/v1/bars/{id}`
- `GET /api/v1/bars/{id}/taps`

— Beers —
- `GET /api/v1/beers`
- `GET /api/v1/beers/{id}`
- Optional admin endpoints for CRUD if needed by back office

— Users —
- `GET /api/v1/users` (SITE_ADMIN)
  - 200: `[ { "id", "username", "role", "breweryId"?, "barId"?, "taproomId"? } ]`

**Filtering & Sorting Examples**
- `GET /api/v1/kegs?breweryId=3&status=DISTRIBUTED&assignedVenueId=10&page=0&size=50&sort=serialNumber,asc`
- `GET /api/v1/taps?taproomId=5&sort=number,asc`

**Error Model**
```
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Keg 999 not found",
  "details": { "resource": "keg", "id": 999 },
  "timestamp": "2025-09-14T12:34:56Z"
}
```

**Security & Access Control**
- Authentication: Spring Security session or Bearer token. CSRF enabled for browser sessions on state‑changing operations.
- Authorization:
  - SITE_ADMIN: full access to all organizations/breweries/bars.
  - BREWERY_ADMIN: access scoped to their `breweryId` resources (taprooms, venues, kegs).
  - TAPROOM_ADMIN: access scoped to their `taproomId`/venue and taps/kegs at that venue.
  - BAR_ADMIN: access scoped to their `barId` and related taps/kegs.

**Optimistic Concurrency**
- Entities with a `version` field (`Tap`, `Keg`) should enforce conditional updates:
  - Accept header `If-Match: W/"{version}"` or request field `expectedVersion`/`expectedKegVersion`.
  - On mismatch return `409 CONFLICT` with error model above.

**OpenAPI (Future Work)**
- Expose generated OpenAPI at `/v3/api-docs` and UI via springdoc-openapi.
- Add schemas for `Tap`, `Keg`, `Venue`, `Taproom`, `Brewery`, `Bar`, `Beer`, `KegEvent`, `UserAccount`.

**Migration Notes From MVC**
- Current controllers render Thymeleaf views. The endpoints above mirror their responsibilities with REST semantics.
- Tap actions map directly from:
  - `/taps/{id}/tapKeg` → `POST /api/v1/taps/{id}/tap-keg`
  - `/taps/{id}/pour` → `POST /api/v1/taps/{id}/pour`
  - `/taps/{id}/blow` → `POST /api/v1/taps/{id}/blow`
- Admin pages split into resource collections (breweries, taprooms, venues, kegs) with filterable list endpoints.

**Minimal JSON Examples**
- Tap pour
```
POST /api/v1/taps/1/pour
{ "ounces": 12.0 }

200 OK
{ "id": 42, "remainingOunces": 1012.0, "status": "RECEIVED", "version": 12 }
```

- Distribute a keg
```
POST /api/v1/kegs/42/distribute
{ "venueId": 10 }

200 OK
{ "id": 42, "assignedVenueId": 10, "status": "DISTRIBUTED" }
```

**Non-Goals (Now)**
- Public unauthenticated endpoints, complex reporting, and bulk upload are out of scope for this iteration.

Additional Notes (from product decisions)
- Low‑volume alert: when a keg’s remaining falls below 15%, emit an internal event for alerting/analytics and show a UI indicator.
- Receive‑before‑tap: API allows `tap-keg` even if keg is DISTRIBUTED (RECEIVED is not strictly required).
