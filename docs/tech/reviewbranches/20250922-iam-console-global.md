# feature/api/iam-console-global

- Area: api
- Owner: genie-api
- Task: <docs/techtasks/...>
- Summary: Make IAM console globals safe for CSRF/token refresh helpers to avoid ReferenceErrors.
- Scope: src/main/resources/templates/admin/brewery.html
- Risk: low
- Test Plan:
  - [ ] `mvn verify`
  - [ ] Manual: Open IAM quick actions (assign, create user) and confirm no console errors.
- Status: proposed
- PR: <tbd>

## Notes
- Exposes console & caches as globals shared by helper functions defined outside the setup IIFE.
