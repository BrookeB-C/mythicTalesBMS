# feature/platform/user-access-nav

- Area: platform
- Owner: techlead
- Task: docs/techtasks/000-technical-backlog.md (user access navigation)
- Summary: Point the User Access menu link to the context-specific user management page.
- Scope: src/main/resources/templates/layouts/app.html
- Risk: low
- Test Plan: `mvn -q -DskipTests compile` and click User Access for site, brewery, and taproom roles.
- Status: in-progress
- PR: <tbd>

## Notes
- Site admins go to /admin/users, brewery admins to /admin/brewery?tab=users, taproom admins to /admin/taproom?tab=users.
