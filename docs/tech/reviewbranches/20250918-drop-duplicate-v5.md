# feature/platform/drop-duplicate-v5

- Area: platform
- Owner: genie-keginventory
- Task: docs/techtasks/101-genie-platform-data-tasks.md (Flyway baseline guardrails)
- Summary: Remove duplicate Flyway V5 migration causing startup failure
- Scope: src/main/resources/db/migration/V5__add_siteadmin_user.sql
- Risk: low — deletes redundant migration file already superseded
- Test Plan: mvn flyway:validate
- Status: in-progress
- PR: <tbd>

## Notes
- Staging branch introduced `V5__taplist_projections.sql`; this branch keeps that migration as the sole V5.
