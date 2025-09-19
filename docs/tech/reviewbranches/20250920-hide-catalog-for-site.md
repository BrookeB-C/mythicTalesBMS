# feature/platform/hide-catalog-for-site

- Area: platform
- Owner: techlead
- Task: docs/techtasks/000-technical-backlog.md (control center UX)
- Summary: Hide the catalog navigation section for site administrators.
- Scope: src/main/resources/templates/layouts/app.html
- Risk: low
- Test Plan: `mvn -q -DskipTests compile`, load /admin/site (no Catalog nav) and /admin/brewery (Catalog still visible).
- Status: in-progress
- PR: <tbd>

## Notes
- Site admins focus on control centers; catalog tools remain available for brewery/production roles.
