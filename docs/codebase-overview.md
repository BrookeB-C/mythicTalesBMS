# Mythic Tales BMS — Codebase Overview

This document explains the structure and behavior of the Spring Boot application, including domain model, persistence, services, MVC controllers, security, and the new REST API scaffold. For API endpoint design details, see `docs/api-design.md`.

## High-Level Architecture
- Spring Boot 3.3 application for brewery taplists and admin operations.
- Layers:
  - Domain entities + JPA repositories
  - Service layer for tap/keg operations
  - MVC controllers rendering Thymeleaf views
  - REST API controllers (`/api/v1/**`) returning JSON DTOs
  - Security via Spring Security roles
- Build: Maven, Java 17, H2 for dev/runtime testing.

## Build & Entry Points
- Build file: `pom.xml` (Spring Boot starters for web, thymeleaf, security, data-jpa, validation; springdoc OpenAPI).
- Main app: `src/main/java/com/mythictales/bms/taplist/TaplistApplication.java`.
- Optional seed: `DataInitializer` (creates sample data when present/enabled).

## Configuration & Security
- `SecurityConfig`:
  - Form login at `/login`, role-based post-login redirect.
  - CSRF using cookie repository; static assets allowed; admin routes restricted by role; `/taplist` requires auth.
  - Method security enabled for fine-grained access (`@PreAuthorize`).
- OpenAPI: `OpenApiConfig` exposes `/v3/api-docs` and `/swagger-ui.html` with groups:
  - `api-v1` → `/api/**`
  - `admin` → `/admin/**`, `/taplist/**`
- Security Schemes declared for Swagger (bearer token and session cookie).

## Domain Model (JPA)
- Core Entities (package `domain`):
  - `Beer` — basic beer metadata.
  - `Brewery` — brewery entity.
  - `Bar` — bar linked to a `Brewery`.
  - `Venue` — named location; `VenueType` is `TAPROOM` or `BAR`; may link to a `Brewery`.
  - `Taproom` — logical grouping under a `Brewery`.
  - `Tap` — physical tap; relates to `Venue`, `Taproom`, `Bar`, and current `Keg`.
  - `Keg` — carries `Beer`, owning `Brewery`, `KegSizeSpec`, `status`, `remainingOunces`, optional `assignedVenue`, `serialNumber`.
  - `KegSizeSpec` — volume spec with gallons/ounces/liters.
  - `KegPlacement` — a keg’s interval on a tap (`startedAt`→`endedAt`).
  - `KegEvent` — action against a placement (types: `TAP|POUR|BLOW|UNTAP`), optional `actor` and `ounces`.
  - `UserAccount` — auth principal with `Role` and optional affiliations (`brewery`, `bar`, `taproom`).
  - Enums: `KegStatus` (EMPTY, CLEAN, FILLED, DISTRIBUTED, RECEIVED, TAPPED, BLOWN, RETURNED), `KegEventType`, `TapStatus`, `VenueType`, `Role`.

## Persistence Layer (JPA Repositories)
- Repositories in `repo` provide typed finders used by controllers/services:
  - `TapRepository` — `findByTaproomId`, `findByBarId`, `findByVenueId`, and `*AndKegIsNull` variants.
  - `KegRepository` — filters by brewery, status, assigned venue; split for assigned/unassigned.
  - `KegEventRepository` — `findVenueEvents(venueId)` joins placement→tap→venue ordered by time.
  - `VenueRepository` — by brewery, by type, by name.
  - `TaproomRepository`, `BreweryRepository`, `BarRepository`, `BeerRepository`, `KegSizeSpecRepository`, `UserAccountRepository`.

## Service Layer
- `TapService` encapsulates core workflows:
  - `tapKeg(tapId, kegId, actor)` — ends prior placement, assigns keg to tap’s venue, sets status `TAPPED`, creates `KegPlacement`, records `KegEvent(TAP)`.
  - `pour(tapId, ounces, actor)` — decrements `remainingOunces`, records `KegEvent(POUR)`; if empty, sets `BLOWN`, ends placement with `UNTAP`, clears keg from tap.
  - `blow(tapId, actor)` — sets `BLOWN`, ends placement with `BLOW`, clears tap.

## MVC Controllers (Thymeleaf)
- `TaplistController` (`/taplist`): lists taps by user context; POST actions for tap-keg/pour/blow with redirects.
- Admin:
  - `AdminBreweryController` (`/admin/brewery`): brewery overview, taprooms/venues, keg distribution/return/clean, taproom add/delete.
  - `AdminTaproomController` (`/admin/taproom`): per-taproom taps, kegs by status; receive/return blown kegs.
  - `AdminBarController` (`/admin/bar`): taps for a bar.
  - `AdminVenueController` (`/admin/venue/{venueId}`): venue details and taps.
  - `AdminEventController` (`/admin/venue/{venueId}/events`): recent events for venue.
  - `AdminUserController` (`/admin/users`): list users (SITE_ADMIN).
  - `AuthController` (`/login`): login page with cache headers.

## REST API (Scaffolded)
- Package: `com.mythictales.bms.taplist.api`.
- DTOs: `BeerDto`, `KegSizeSpecDto`, `KegDto`, `VenueDto`, `TaproomDto`, `BreweryDto`, `BarDto`, `TapDto`, `KegEventDto`, `UserDto`.
- Mapping: `ApiMappers` converts entities→DTOs.
- Controllers:
  - `TapApiController` — `GET /api/v1/taps`; `POST /taps/{id}/tap-keg|pour|blow`.
  - `KegApiController` — `GET /api/v1/kegs` (filters), `GET /{id}`, `POST` create, actions: `/{id}/distribute|receive|return|clean`.
  - `VenueApiController` — list/get venues.
  - `TaproomApiController` — list/get; `POST` create (also creates `Venue` TAPROOM), `DELETE`.
  - `BreweryApiController` — list/get; `PATCH` name.
  - `BarApiController` — list/get bars.
  - `BeerApiController` — list/get beers.
  - `EventApiController` — `GET /api/v1/venues/{venueId}/events`.
  - `UserApiController` — `GET /api/v1/users` (SITE_ADMIN only).
- Security: method-level role guards on mutating endpoints. See `docs/api-design.md` for full design, error model, and concurrency plan.

## Typical User Flows
- Login → role-based redirect to admin landing or `/taplist`.
- Brewery admin: manage taprooms/venues; create and distribute/return/clean kegs; view taps per taproom.
- Taproom/bar admin: see taps; receive kegs; perform tap/pour/blow; return blown kegs.
- Events: view recent events per venue for auditing and status.

## OpenAPI & Docs
- Swagger UI at `/swagger-ui.html` and JSON at `/v3/api-docs`.
- Grouped docs:
  - `/v3/api-docs/api-v1` — REST endpoints
  - `/v3/api-docs/admin` — current MVC endpoints
- API design: see `docs/api-design.md`.

## Directory Layout (key paths)
- `src/main/java/com/mythictales/bms/taplist/` — application entry/config.
- `.../domain/` — entities and enums.
- `.../repo/` — JPA repositories.
- `.../service/` — service layer.
- `.../controller/` — MVC controllers.
- `.../api/` — REST controllers and DTOs.
- `src/main/resources/templates/` — Thymeleaf views.
- `src/main/resources/static/` — static assets.
- `docs/` — docs and API design.

## Notes & Future Work
- REST layer currently mirrors MVC behaviors; add pagination, validation, and optimistic concurrency (`If-Match`/`version`) to align with the API design.
- Consider token-based auth for `/api/**` and CSRF tuning for API clients.

