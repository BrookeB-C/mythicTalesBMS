# feature/platform/brewery-control-center

- Area: platform
- Owner: techlead
- Task: docs/techtasks/000-technical-backlog.md (brewery control center UX)
- Summary: Reshape the brewery admin landing page into a control center with linked metrics and navigation.
- Scope: src/main/resources/templates/admin/brewery.html, src/main/resources/static/css/app.css
- Risk: medium (navigation changes impact admin workflows)
- Test Plan: `mvn -q -DskipTests compile`, manual check `/admin/brewery` and follow card/control links.
- Status: in-progress
- PR: <tbd>

## Notes
- Cards now link to associated list pages; inline control menu replaces tab strips.
- Default view shows only cards + brewery details; lists render when `tab` query is provided.
