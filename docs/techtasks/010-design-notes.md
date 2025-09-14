# Design Notes & Decisions

## API Authorization Scope
Enforce scope based on `CurrentUser` affiliations (breweryId, taproomId, barId). Prefer service-layer checks with clear exceptions to avoid duplication across controllers.

## Tap/Venue Normalization
Keep `Tap.venue` as primary; derive taproom/bar via linked venue or remove direct `Tap.taproom`/`Tap.bar` links in future migration.

## State Transitions
Guard allowed `KegStatus` transitions in one place. Consider a simple map of allowed transitions or a small state machine class.

## Concurrency Model
Use JPA `@Version` for optimistic locking on mutable aggregates (`Keg`, `Tap`, `KegPlacement`). Expose `version` in API and handle 409s.

## Error Model
Adopt a Problem Details-like JSON model with `status`, `error`, `message`, `details`, `timestamp`, `traceId`.

## Profiles & Migrations
- `dev`: H2, Swagger UI, seed data, relaxed CORS, verbose logs.
- `prod`: Postgres, Flyway migrations, Swagger UI off by default, CORS locked, structured logs only.

## Observability
Actuator + Micrometer Prometheus registry; MDC with `traceId` and `userId` for log correlation; optional OpenTelemetry later.

Last updated: YYYY-MM-DD

