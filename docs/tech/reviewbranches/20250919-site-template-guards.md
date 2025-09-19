# feature/platform/site-template-guards

- Area: platform
- Owner: techlead
- Task: docs/techtasks/000-technical-backlog.md (Thymeleaf hardening)
- Summary: Guard admin/site template against missing Brewery address fields.
- Scope: src/main/resources/templates/admin/site.html
- Risk: low
- Test Plan: `mvn -q -DskipTests compile` and hit /admin/site.
- Status: in-progress
- PR: <tbd>

## Notes
- Production data model for Brewery lacks address fields; template must render placeholders.
