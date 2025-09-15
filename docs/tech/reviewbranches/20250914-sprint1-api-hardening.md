# feature/api/sprint1-api-hardening

- Area: api
- Owner: ${AGENT_ROLE:-unknown}
- Task: docs/techtasks/100-genie-api-tasks.md (Sprint 1)
- Summary: Implement consistent API error model, request validation, authorization scope checks, pour invariants, and expose optimistic concurrency metadata for taps. Ensure MVC flows remain redirect-based while API returns Problem JSON.
- Scope:
  - src/main/java/com/mythictales/bms/taplist/api/**
  - src/main/java/com/mythictales/bms/taplist/security/** (AccessPolicy)
  - src/main/java/com/mythictales/bms/taplist/service/** (pour business rules)
  - src/main/java/com/mythictales/bms/taplist/config/** (API exception handler)
  - src/main/java/com/mythictales/bms/taplist/controller/** (MVC pour behavior)
- Risk: low
- Test Plan:
  - Build: mvn -DskipTests package (Java 17 or 21)
  - Format: make check-format (or make format)
  - SpotBugs: make spotbugs-strict
  - API smoke: hit /api/v1/taps and action endpoints; verify 400/403/404/409/422 return Problem JSON schema
  - MVC smoke: exercise tap admin flows to confirm redirect-based responses and no API-style 422s
  - Tests: mvn test (Java 17/21) — verified green locally per user
- Status: ready-for-review
- PR: https://github.com/BrookeB-C/mythicTalesBMS/pull/1

## Notes
- API error model (Problem JSON) is scoped to REST controllers via @RestControllerAdvice(basePackages = "com.mythictales.bms.taplist.api"), avoiding cross-talk with MVC.
- Added request DTOs with javax/jakarta validation and @Validated on Tap API.
- Centralized authorization scope in AccessPolicy using CurrentUser affiliations (brewery/taproom/bar) for read/write.
- Pour invariants: reject non-positive pours; overpour returns 422 unless explicitly allowed. MVC pour path enables auto-blow to keep redirects; API can opt-in via flag.
- Exposed Tap.version to DTO for future optimistic concurrency support (Sprint 2 alignment).
- Repository addition: findByVenueBreweryId for brewery-scoped lists.
