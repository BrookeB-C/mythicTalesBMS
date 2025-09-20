# feature/platform/taplist-projection-double

- Area: platform
- Owner: techlead
- Task: docs/techtasks/000-technical-backlog.md (Flyway + staging parity)
- Summary: Fix taplist projection migration to use Postgres-compatible double precision types.
- Scope: src/main/resources/db/migration/V5__taplist_projections.sql
- Risk: low
- Test Plan: `SPRING_PROFILES_ACTIVE=staging mvn -Dflyway.cleanDisabled=false flyway:clean flyway:migrate` (if safe) or run staging startup after container reset
- Status: in-progress
- PR: <tbd>

## Notes
- Postgres rejects `double`; use `double precision` for numeric columns in projection table.
