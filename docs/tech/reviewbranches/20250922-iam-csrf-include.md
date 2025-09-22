# feature/api/iam-csrf-include

- Area: api
- Owner: genie-api
- Task: <docs/techtasks/...>
- Summary: Ensure IAM console fetches send cookies and refresh CSRF token cache to avoid Safari 403s.
- Scope: src/main/resources/templates/admin/brewery.html
- Risk: low
- Test Plan:
  - [ ] `mvn verify`
  - [ ] Manual: IAM create-user modal and keg quick actions succeed in Safari without CSRF errors.
- Status: proposed
- PR: <tbd>

## Notes
- Hoists console globals and syncs CSRF token after each fetch when available.
