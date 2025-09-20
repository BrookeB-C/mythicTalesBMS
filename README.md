# Mythic Tales BMS (Taplist)

[![CI](https://github.com/BrookeB-C/mythicTalesBMS/actions/workflows/ci.yml/badge.svg)](https://github.com/BrookeB-C/mythicTalesBMS/actions/workflows/ci.yml)
[![CodeQL](https://github.com/BrookeB-C/mythicTalesBMS/actions/workflows/codeql.yml/badge.svg)](https://github.com/BrookeB-C/mythicTalesBMS/actions/workflows/codeql.yml)
[![Release](https://github.com/BrookeB-C/mythicTalesBMS/actions/workflows/release.yml/badge.svg)](https://github.com/BrookeB-C/mythicTalesBMS/actions/workflows/release.yml)

A Spring Boot 3 application for managing brewery taplists and related admin workflows. Includes Thymeleaf pages for admins and a REST API scaffold documented with Swagger.

## Docker Compose — Postgres (Staging & Prod)

This repo includes a Compose file that runs two separate Postgres containers for staging and production.

Quick start:

1) Copy the example env and set strong passwords:

```
cp .env.example .env
# edit .env to set passwords and ports as desired
```

2) Start staging or production database:

```
# Staging DB on localhost:5433
make db-up-staging

# Production DB on localhost:5432
make db-up-prod
```

3) Stop containers when done:

```
make db-stop-staging
make db-stop-prod
```

Connection strings (examples):

```
# Staging
jdbc:postgresql://localhost:${STAGING_POSTGRES_PORT:-5433}/${STAGING_POSTGRES_DB}
user: ${STAGING_POSTGRES_USER}
password: ${STAGING_POSTGRES_PASSWORD}

# Production
jdbc:postgresql://localhost:${PRODUCTION_POSTGRES_PORT:-5432}/${PRODUCTION_POSTGRES_DB}
user: ${PRODUCTION_POSTGRES_USER}
password: ${PRODUCTION_POSTGRES_PASSWORD}
```

Compose file: `compose.yaml`. Variables are sourced from `.env`.

## Docker Compose — Kafka Dev Stack

Spin up a single-node Kafka (KRaft) broker plus Kafka UI for local development:

```bash
docker compose -f docker/compose/kafka-dev.yml up -d
```

The stack provisions all domain topics listed under `bms.kafka.domains` using six partitions and seven-day retention. Visit http://localhost:8085 to browse topics and messages.

Stop the stack when finished:

```bash
docker compose -f docker/compose/kafka-dev.yml down
```

Set `BMS_KAFKA_ENABLED=true` (default in the dev profile) so the application boots with the Kafka publisher configuration. Optionally enable the sample publisher for smoke tests:

```bash
BMS_KAFKA_SAMPLE_ENABLED=true mvn spring-boot:run
```

The sample runner publishes a `SampleInventoryEvent` into `prodinventory.events.v1` at startup.

## Spring Profiles — Staging & Production

Profiles are pre-wired to use the Compose Postgres instances:

- `staging`: connects to `localhost:5433` (DB `${STAGING_POSTGRES_DB}`)
- `prod`: connects to `localhost:5432` (DB `${PRODUCTION_POSTGRES_DB}`)

Run the app with a profile:

```
# Staging
SPRING_PROFILES_ACTIVE=staging mvn spring-boot:run

# Production
SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run
```

Notes:
- Staging uses `ddl-auto=validate` and keeps Swagger UI enabled.
- Production uses `ddl-auto=none` and disables Swagger UI and H2 console.

## Quick Start

- Prerequisites: Java 17+, Maven 3.9+
- Run the app:
  ```bash
  mvn spring-boot:run
  ```
- App URLs:
  - Login: http://localhost:8080/login
  - Taplist (requires login): http://localhost:8080/taplist
  - Admin pages (role-gated):
    - Site: http://localhost:8080/admin/site
    - Brewery: http://localhost:8080/admin/brewery
    - Taproom: http://localhost:8080/admin/taproom
    - Bar: http://localhost:8080/admin/bar
  - Swagger UI: http://localhost:8080/swagger-ui.html
  - OpenAPI JSON: http://localhost:8080/v3/api-docs (groups: `/v3/api-docs/api-v1`, `/v3/api-docs/admin`)
  - H2 Console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:testdb`)

### Demo Users (seeded)

All passwords are `password`.
- `siteadmin` (SITE_ADMIN)
- `brewadmin` (BREWERY_ADMIN)
- `tapadmin` (TAPROOM_ADMIN)
- `baradmin` (BAR_ADMIN)
- `tapuser` (TAPROOM_USER)

Additional demo breweries/bars/taprooms and users are seeded; see `DataInitializer`.

## Build & Test

```bash
mvn -q -DskipTests package     # build
mvn test                       # run tests
```

### Formatting

This repo uses Spotless (google-java-format) and EditorConfig.

```bash
mvn spotless:apply            # auto-format Java and resources
mvn spotless:check            # verify formatting

# or via Makefile
make format
make check-format
```

## REST API (Scaffold)

- Base path: `/api/v1`
- Examples:
  ```bash
  # List taps for your context or filter by taproom
  curl -s -u siteadmin:password http://localhost:8080/api/v1/taps
  curl -s -u tapadmin:password  "http://localhost:8080/api/v1/taps?taproomId=1"

  # Tap a keg, then pour, then blow
  curl -s -u tapadmin:password -H 'Content-Type: application/json' \
       -d '{"kegId":42}' http://localhost:8080/api/v1/taps/1/tap-keg
  curl -s -u tapadmin:password -H 'Content-Type: application/json' \
       -d '{"ounces":12}' http://localhost:8080/api/v1/taps/1/pour
  curl -s -u tapadmin:password -H 'Content-Type: application/json' \
       -d '{}' http://localhost:8080/api/v1/taps/1/blow
  ```

Swagger UI documents all available endpoints. See also `docs/api-design.md` for the broader API design plan.

## Project Structure

- `src/main/java/com/mythictales/bms/taplist/domain` — Entities and enums
- `src/main/java/com/mythictales/bms/taplist/repo` — Spring Data JPA repositories
- `src/main/java/com/mythictales/bms/taplist/service` — Business services (e.g., `TapService`)
- `src/main/java/com/mythictales/bms/taplist/controller` — MVC controllers (Thymeleaf)
- `src/main/java/com/mythictales/bms/taplist/api` — REST controllers, DTOs, and mappers
- `src/main/java/com/mythictales/bms/taplist/config/OpenApiConfig.java` — Swagger/OpenAPI config
- `src/main/resources/templates` — Thymeleaf views
- `docs/` — Documentation (`codebase-overview.md`, `api-design.md`)

## Security

- Form login at `/login` with role-based redirects.
- CSRF enabled; cookie token used for browser forms. API clients should use basic auth in dev or integrate a token if you configure one.
- Role gates for admin routes and API mutations (`SITE_ADMIN`, `BREWERY_ADMIN`, `TAPROOM_ADMIN`, `BAR_ADMIN`).

## Notes

- The REST API is a scaffold mirroring existing MVC operations. Pagination, validation, and optimistic concurrency can be added incrementally (see `docs/api-design.md`).
- Default DB is an in-memory H2; data resets on restart.

## License

Proprietary — internal project.
