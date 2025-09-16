#!/usr/bin/env bash
set -euo pipefail

# Small helper to load env/profile for a genie instance from config.toml
# Usage:
#   bin/genie.sh <role> [--] <command ...>
# Where <role> exists as [genies.<role>] in config.toml
# Example: bin/genie.sh api mvn spring-boot:run

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")"/.. && pwd)"
CONFIG_FILE="${GENIE_CONFIG:-$ROOT_DIR/config.toml}"

usage() {
  echo "Usage: $0 <role> [--] <command ...>" >&2
  echo "Where <role> exists as [genies.<role>] in config.toml" >&2
  echo "Example: $0 api mvn spring-boot:run" >&2
}

if [[ $# -lt 1 ]]; then usage; exit 1; fi
ROLE="$1"; shift || true

if [[ ! -f "$CONFIG_FILE" ]]; then
  echo "Config not found: $CONFIG_FILE" >&2
  exit 1
fi

SECTION="[genies.$ROLE]"

# Extract env block for the chosen section from config.toml
ENV_OUTPUT=$(awk -v section="$SECTION" '
  BEGIN{started=0; inenv=0}
  $0 ~ /^\[/ {
    if (started && !inenv) exit;
    started = ($0 == section)
  }
  started && /env[[:space:]]*=/ && /\{/ { inenv=1; next }
  inenv {
    gsub(/#.*/, "");           # strip comments
    if ($0 ~ /}/) { inenv=0; exit }
    gsub(/[[:space:]]+/, "");
    if ($0 == "") next
    # Expect KEY="VALUE", optionally trailing comma
    split($0, a, "=");
    key=a[1]; val=a[2];
    sub(/,$/, "", val);
    gsub(/^\"|\"$/, "", val);
    if (key ~ /^[A-Z_][A-Z0-9_]*$/) print key "=" val;
  }
' "$CONFIG_FILE")

if [[ -z "${ENV_OUTPUT}" ]]; then
  echo "No env block found for $SECTION in $CONFIG_FILE" >&2
  echo "Tip: add a section like:\n[genies.$ROLE]\nrole=\"$ROLE\"\nenv={ AGENT_ROLE=\"$ROLE\", SPRING_PROFILES_ACTIVE=\"dev\" }" >&2
  exit 1
fi

# Export env vars from parsed output KEY=VALUE per line
while IFS='=' read -r key val; do
  [ -z "$key" ] && continue
  export "$key"="$val"
done <<< "$ENV_OUTPUT"

echo "Loaded genie profile: $ROLE"
echo " - AGENT_ROLE=$AGENT_ROLE"
echo " - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-}"
echo "From: $CONFIG_FILE"

if [[ $# -gt 0 ]]; then
  echo "Executing: $*"
  exec "$@"
else
  echo "Environment set. Start your tool or run:"
  echo "  AGENT_ROLE=$AGENT_ROLE SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev} <your-command>"
fi

