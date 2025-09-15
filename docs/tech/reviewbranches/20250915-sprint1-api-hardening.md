# feature/api/sprint1-api-hardening

- Area: api
- Owner: ${AGENT_ROLE:-genie-api}
- Task: docs/techtasks/100-genie-api-tasks.md
- Summary: Initial API hardening — add exception handler (Problem JSON), request DTOs, and access policy scaffolding.
- Scope: src/main/java/com/mythictales/bms/taplist/api/**, security/AccessPolicy, config/RestExceptionHandler, service exceptions
- Risk: low
- Test Plan: Build and manual checks via swagger; verify 400/403/404/422 behaviors on representative endpoints
- Status: ready-for-review
- PR: https://github.com/BrookeB-C/mythicTalesBMS/pull/1

## Notes
- Aligns with Priority 0 tasks: consistent error model and validation.
- Next step is to wire authorization scope checks on each endpoint using AccessPolicy.
