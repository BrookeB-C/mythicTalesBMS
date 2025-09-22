# feature/api/create-user-quick-action

- Area: api
- Owner: genie-api
- Task: <docs/techtasks/...>
- Summary: Add IAM quick action/modal to create brewery scoped users from enterprise console.
- Scope: src/main/resources/templates/admin/brewery.html; src/main/java/com/mythictales/bms/taplist/controller/AdminBreweryController.java
- Risk: low
- Test Plan:
  - [ ] `mvn verify`
  - [ ] Manual: Trigger "Create User" quick action, submit modal and confirm new user appears in IAM queue.
- Status: proposed
- PR: <tbd>

## Notes
- Adds client-side CSRF support for the modal POST.
