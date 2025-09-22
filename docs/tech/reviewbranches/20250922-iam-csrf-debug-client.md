# feature/api/iam-csrf-debug-client

- Area: api
- Owner: genie-api
- Task: <docs/techtasks/...>
- Summary: Emit client-side diagnostics for CSRF token/header behavior in IAM console.
- Scope: src/main/resources/templates/admin/brewery.html
- Risk: low
- Test Plan:
  - [ ] Reproduce IAM modal POST; inspect browser console for debug logs.
- Status: proposed
- PR: <tbd>

## Notes
- Aids debugging persistent 403 by logging tokens and response headers.
