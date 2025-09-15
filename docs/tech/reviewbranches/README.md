# Review Branches Workflow

This folder tracks active feature branches for review by the Tech Lead and collaborating genies.

## Workflow (required)
- Create a feature branch per task before making changes.
- Use a verbose commit message summarizing context, rationale, and validation.
- Add a branch entry file in this folder using the template below.
- Open a PR linking to the branch entry file.

## Branch naming
- Format: `feature/<area>/<slug>`
  - `<area>`: `api` or `platform`
  - `<slug>`: short-kebab summary, optionally with date or id, e.g. `2025-09-pour-validation`

Examples:
- `feature/api/pour-validation-422`
- `feature/platform/flyway-baseline-v1`

## Commit message guidance
- Subject line: concise summary (<= 72 chars)
- Body: why, what, how; list of notable changes; risks; validation steps
- Footer: references (issue, task in docs/techtasks)

## Entry file template
Create a new file named: `YYYYMMDD-<branch>.md`

```
# <branch-name>

- Area: api|platform
- Owner: <github-handle or AGENT_ROLE>
- Task: <docs/techtasks/... link>
- Summary: <one–two line purpose>
- Scope: <key paths to touch>
- Risk: <low|medium|high>
- Test Plan: <how to validate>
- Status: proposed|in-progress|ready-for-review|merged
- PR: <link once opened>

## Notes
- <design notes, tradeoffs, follow-ups>
```

## Helper script
Use `bin/start-feature.sh` to generate a branch name and create the entry file. See usage in that script.

