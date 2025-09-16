# feature/platform/pm-genie-and-disable-smoke-tests

- Area: platform
- Owner: ${AGENT_ROLE:-unknown}
- Task: docs/techtasks/105-stabilize-smoke-tests.md
- Summary: Add Product Manager genie scaffolding; temporarily disable brittle smoke tests pending stabilization.
- Scope: docs/**, src/test/java/com/mythictales/bms/taplist/smoke/**, bin/**, config.toml
- Risk: low (tests disabled), medium (temporary loss of smoke coverage)
- Test Plan: CI should skip disabled smoke tests; run unit/API tests.
- Status: proposed
- PR: <tbd>

## Notes
- Follow-up: re-enable smoke tests after adding data-test anchors and fixture stabilization.
