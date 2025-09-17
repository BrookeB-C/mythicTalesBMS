# feature/api/keginventory-reports-it-and-docs

- Area: api
- Owner: genie-keginventory
- Task: docs/techtasks/111-genie-keginventory-tasks.md
- Summary: Add ITs for reconciliation and status counts; extend manual QA; finalize P1/P2 checks
- Scope: src/test/java/.../keginventory/api/*, docs/testing/manual-qa.md
- Risk: low
- Test Plan:
  - mvn verify
  - Run history/reconciliation/counts endpoints via curl with session cookie
- Status: ready
- PR: <to be created>

## Notes
- QA: Validates /history (with optional time range), /reconciliation, /reports/statusCounts
- Staging validation: use stone_brewadmin in staging; browser-based session handles CSRF
