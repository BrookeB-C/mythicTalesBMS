# feature/api/iam-csrf-header

- Area: api
- Owner: genie-api
- Task: <docs/techtasks/...>
- Summary: Prevent IAM helper functions from referencing undefined globals for CSRF header resolution.
- Scope: src/main/resources/templates/admin/brewery.html
- Risk: low
- Test Plan:
  - [ ] `mvn verify`
  - [ ] Manual: Use IAM create-user modal in Safari; confirm no `consoleEl`/`csrfHeaderName` reference errors.
- Status: proposed
- PR: <tbd>

## Notes
- Hoist console element, cache objects, and csrf header name to top-level script scope so shared helpers remain safe.
