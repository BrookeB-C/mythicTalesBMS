# feature/api/iam-csrf-debug

- Area: api
- Owner: genie-api
- Task: <docs/techtasks/...>
- Summary: Add diagnostics for PageImpl warnings and CSRF fetch flow.
- Scope: src/main/java/com/mythictales/bms/taplist/api/UserApiController.java
- Risk: low
- Test Plan:
  - [ ] `mvn verify`
  - [ ] Manual: Reproduce IAM API calls and inspect logs for new debug/warn output.
- Status: proposed
- PR: <tbd>

## Notes
- Logging will help pinpoint why PageImpl serialization occurs.
