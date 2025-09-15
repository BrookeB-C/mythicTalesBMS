# feature/api/tests-sprint1

- Area: api
- Owner: ${AGENT_ROLE:-genie-api}
- Task: docs/techtasks/100-genie-api-tasks.md (Sprint 1 tests)
- Summary: Add tests for AccessPolicy authorization scoping and Problem JSON error handling for API controllers.
- Scope:
  - src/test/java/com/mythictales/bms/taplist/security/AccessPolicyTest.java
  - src/test/java/com/mythictales/bms/taplist/api/RestExceptionHandlerTest.java
- Risk: low
- Test Plan:
  - Build/tests: mvn test (or make test)
  - Verify AccessPolicy grants/denies for roles and affiliations
  - Verify 400/403/404/422 Problem JSON shape from API advice
- Status: ready-for-review
- PR: https://github.com/BrookeB-C/mythicTalesBMS/pull/2

## Notes
- WebMvcTest uses a minimal test controller in the API package to exercise the scoped RestExceptionHandler without touching MVC controllers.
