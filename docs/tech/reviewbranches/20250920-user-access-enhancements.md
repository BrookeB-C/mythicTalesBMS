# feature/platform/user-access-complete

- Area: platform
- Owner: techlead
- Task: docs/techtasks/000-technical-backlog.md (complete user access management)
- Summary: Enable scoped user management for site, brewery, and taproom admins using the new enterprise UI shell.
- Scope: src/main/java/com/mythictales/bms/taplist/controller/AdminUserController.java, src/main/java/com/mythictales/bms/taplist/controller/AdminBreweryController.java, src/main/java/com/mythictales/bms/taplist/controller/AdminTaproomController.java, templates/admin/users.html, templates/admin/brewery.html, templates/admin/taproom.html, static/css/app.css
- Risk: medium (user provisioning touches authentication data)
- Test Plan: `mvn -q -DskipTests compile`, manual checks for /admin/users, /admin/brewery?tab=users, /admin/taproom?tab=users
- Status: in-progress
- PR: <tbd>

## Notes
- Adds creation/deletion flows so each role can manage accounts within its scope.
- Forms reuse the enterprise layout and expose context-sensitive role options.
