## Summary

API Sprint 1: Implement consistent Problem JSON error model, request validation, authorization scope checks, and pour invariants for Tap endpoints; preserve MVC redirect behavior. Branch entry: docs/tech/reviewbranches/20250914-sprint1-api-hardening.md.

## Scope of changes

- Areas: api
- Key paths touched:
  - `src/main/java/com/mythictales/bms/taplist/api/**`
  - `src/main/java/com/mythictales/bms/taplist/security/AccessPolicy.java`
  - `src/main/java/com/mythictales/bms/taplist/service/**`
  - `src/main/java/com/mythictales/bms/taplist/config/RestExceptionHandler.java`
  - `src/main/java/com/mythictales/bms/taplist/controller/TaplistController.java`

## Validation

- [x] Built locally (`mvn -DskipTests package`)
- [ ] SpotBugs high-severity clean (`make spotbugs-strict`)
- [ ] Swagger UI loads (`/swagger-ui.html`)
- [x] Tests green (`mvn test` on maintainer’s JDK)
- Manual test steps and results:
  - API: Verified `/api/v1/taps` and tap actions return Problem JSON on 400/403/404/409/422; success returns DTOs.
  - MVC: Verified tap admin pour/tap/blow flows redirect; over-pour auto-blows without 422.

## Risks & Rollback Plan

- Risk: Low. Changes standardize error responses for API only; MVC flows preserved via advice scoping and explicit over-pour handling.
- Rollback: Revert commit `API Sprint 1: Problem JSON, validation, scope checks, pour invariants; preserve MVC redirects`.

## References

- Task: `docs/techtasks/100-genie-api-tasks.md`
- Branch entry: `docs/tech/reviewbranches/20250914-sprint1-api-hardening.md`

