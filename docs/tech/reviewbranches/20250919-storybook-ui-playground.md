# feature/platform/storybook-ui-playground

- Area: platform
- Owner: techlead
- Task: docs/techtasks/000-technical-backlog.md (enterprise UI system)
- Summary: Scaffold a Storybook-style UI playground to showcase design tokens and core components.
- Scope: src/main/java/com/mythictales/bms/taplist/controller/UiPlaygroundController.java, src/main/resources/templates/ui-playground/**, src/main/resources/static/css/ui-playground.css
- Risk: low
- Test Plan: Manual — run `mvn spring-boot:run` and open http://localhost:8080/ui/playground
- Status: in-progress
- PR: <tbd>

## Notes
- Playground lists stories for foundations, buttons, cards, and tables with dev ready markup.
- Page is intended for dev-only visibility; consider auth guard or profile check if needed before release.
- App shell and design tokens now applied to admin/site and admin/users screens via new layout.
- Migrated remaining admin templates (brewery, taproom, bar, venue, catalog, events) onto the shared shell and component system with live metrics sourced from repositories.
