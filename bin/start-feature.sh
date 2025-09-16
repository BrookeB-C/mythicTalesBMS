#!/usr/bin/env bash
set -euo pipefail

# Start a feature workflow: create a branch name and review entry file.
# By default, this script is DRY-RUN for git actions. Pass --do to execute git operations.
#
# Usage:
#   bin/start-feature.sh <api|platform> <slug> [--do]
#
# Examples:
#   bin/start-feature.sh api pour-validation-422 --do
#   bin/start-feature.sh platform flyway-baseline-v1

ROLE="$1"; shift || true
SLUG="${1:-}"; [[ -n "$SLUG" ]] || { echo "Missing slug" >&2; exit 1; }
DO=0
[[ "${2:-}" == "--do" ]] && DO=1

DATE=$(date +%Y%m%d)
case "$ROLE" in
  api|platform) ;;
  *) echo "Role must be 'api' or 'platform'" >&2; exit 1;;
esac

BRANCH="feature/${ROLE}/${SLUG}"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")"/.. && pwd)"
REV_DIR="$ROOT_DIR/docs/tech/reviewbranches"
ENTRY_FILE="$REV_DIR/${DATE}-${SLUG}.md"

mkdir -p "$REV_DIR"

if [[ ! -f "$ENTRY_FILE" ]]; then
  cat > "$ENTRY_FILE" <<EOF
# $BRANCH

- Area: $ROLE
- Owner: \${AGENT_ROLE:-unknown}
- Task: <docs/techtasks/...>
- Summary: <purpose>
- Scope: <paths>
- Risk: low
- Test Plan: <steps>
- Status: proposed
- PR: <tbd>

## Notes
- <design notes>
EOF
  echo "Created review entry: $ENTRY_FILE"
else
  echo "Review entry already exists: $ENTRY_FILE"
fi

echo "Branch: $BRANCH"
if [[ $DO -eq 1 ]]; then
  echo "Creating and switching to branch..."
  git checkout -b "$BRANCH"
else
  echo "Dry run. To create branch, re-run with --do or run:"
  echo "  git checkout -b $BRANCH"
fi

echo "Next steps:"
echo "  - Fill the review entry: $ENTRY_FILE"
echo "  - Make changes and commit with a verbose message"
echo "  - Push and open a PR linking back to the entry file"

