# feature/api/iam-modal-csrf

- Area: api
- Owner: genie-api
- Task: <docs/techtasks/...>
- Summary: Fix IAM create-user modal CSRF handling to avoid 403 responses.
- Scope: src/main/resources/templates/admin/brewery.html
- Risk: low
- Test Plan:
  - [ ] `mvn verify`
  - [ ] Manual: Use IAM quick action modal to create a user without CSRF errors.
- Status: proposed
- PR: <tbd>

## Notes
- Ensures header/token pair comes from Spring and refreshes after POST requests.
