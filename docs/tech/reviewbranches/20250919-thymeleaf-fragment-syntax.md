# feature/platform/thymeleaf-fragment-syntax

- Area: platform
- Owner: techlead
- Task: docs/techtasks/000-technical-backlog.md (Thymeleaf modernization)
- Summary: Wrap layout fragment expressions with `~{}` to silence Thymeleaf 3.1 warnings and prevent parse errors.
- Scope: src/main/resources/templates/admin/*.html
- Risk: low
- Test Plan: `mvn -q -DskipTests compile`, then hit /admin/site in dev profile.
- Status: in-progress
- PR: <tbd>

## Notes
- Thymeleaf 3.1 warns when fragment expressions omit the `~{}` wrapper; upcoming versions will fail outright, so update now.
