#!/usr/bin/env bash
set -euo pipefail

# Simple curl-based smoke test for the Taplist app

BASE_URL=${BASE_URL:-http://localhost:8080}
USERNAME=${USERNAME:-tapadmin}
PASSWORD=${PASSWORD:-password}

WORKDIR=$(cd "$(dirname "$0")" && pwd)
TMPDIR="$WORKDIR/tmp"
COOKIES="$TMPDIR/cookies.txt"
ADM_HTML="$TMPDIR/admin_taproom.html"
TAPLIST_HTML="$TMPDIR/taplist.html"
TAPLIST2_HTML="$TMPDIR/taplist2.html"
TAPLIST3_HTML="$TMPDIR/taplist3.html"
EVENTS_HTML="$TMPDIR/events.html"

mkdir -p "$TMPDIR"
rm -f "$COOKIES" "$ADM_HTML" "$TAPLIST_HTML" "$TAPLIST2_HTML" "$TAPLIST3_HTML" "$EVENTS_HTML"

pass() { echo "[PASS] $*"; }
fail() { echo "[FAIL] $*"; exit 1; }

http_login() {
  local login_url="$BASE_URL/login"
  local out
  out=$(curl -s -i -c "$COOKIES" -X POST -d "username=$USERNAME&password=$PASSWORD" "$login_url" | tr -d '\r')
  echo "$out" | grep -q "HTTP/1.1 302" || fail "Login did not redirect (expected 302)"
  pass "Logged in as $USERNAME"
}

load_admin_taproom() {
  curl -s -b "$COOKIES" "$BASE_URL/admin/taproom" -o "$ADM_HTML"
  grep -q "Taproom Admin" "$ADM_HTML" || fail "Admin Taproom page did not render"
  pass "Admin Taproom page renders"
}

load_taplist() {
  curl -s -b "$COOKIES" -c "$COOKIES" "$BASE_URL/taplist" -o "$TAPLIST_HTML"
  grep -q "<table" "$TAPLIST_HTML" || fail "Taplist page missing table markup"
  pass "Taplist page loads"
}

extract_hidden_csrf() {
  # Extract first hidden input name and value as CSRF pair
  CSRF_NAME=$(grep -oE '<input type=\"hidden\" name=\"[^\"]+\"' "$1" | head -n1 | sed -E 's/.*name=\"([^\"]+)\"/\1/')
  CSRF_TOKEN=$(grep -oE "<input type=\\\"hidden\\\" name=\\\"${CSRF_NAME}\\\" value=\\\"[^\\\"]+\\\"" "$1" | head -n1 | sed -E 's/.*value=\"([^\"]+)\"/\1/')
  [ -n "${CSRF_NAME:-}" ] && [ -n "${CSRF_TOKEN:-}" ] || fail "Failed to extract CSRF token"
}

extract_tap_and_keg() {
  # Find an empty tap's tapKeg action and an available keg option
  TAP_KEG_ACTION=$(grep -oE 'action=\"/taps/[0-9]+/tapKeg\"' "$TAPLIST_HTML" | head -n1 | sed -E 's/action=\"([^\"]+)\"/\1/')
  if [ -z "${TAP_KEG_ACTION:-}" ]; then
    echo "No empty tap found; attempting to blow all taps to empty..."
    # Derive all tap ids from any pour or blow action on the page
    mapfile -t IDS < <(grep -oE 'action=\"/taps/[0-9]+/pour\"|formaction=\"/taps/[0-9]+/blow\"' "$TAPLIST_HTML" | sed -E 's/.*\/taps\/([0-9]+)\/.*/\1/' | sort -u)
    [ ${#IDS[@]} -gt 0 ] || fail "No tap actions found to recover"
    extract_hidden_csrf "$TAPLIST_HTML"
    for id in "${IDS[@]}"; do
      curl -s -i -b "$COOKIES" -X POST -d "${CSRF_NAME}=${CSRF_TOKEN}" "$BASE_URL/taps/${id}/blow" >/dev/null || true
    done
    # Reload taplist and try again
    curl -s -b "$COOKIES" -c "$COOKIES" "$BASE_URL/taplist" -o "$TAPLIST_HTML"
    TAP_KEG_ACTION=$(grep -oE 'action=\"/taps/[0-9]+/tapKeg\"' "$TAPLIST_HTML" | head -n1 | sed -E 's/action=\"([^\"]+)\"/\1/')
    [ -n "${TAP_KEG_ACTION:-}" ] || fail "Still no empty tap with tapKeg form after blowing taps"
  fi
  TAP_ID=$(echo "$TAP_KEG_ACTION" | sed -E 's#.*/taps/([0-9]+)/tapKeg#\1#')
  KEG_ID=$(grep -oE '<option value=\"[0-9]+\"' "$TAPLIST_HTML" | sed -E 's/.*value=\"([0-9]+)\"/\1/' | head -n1)
  [ -n "${TAP_ID:-}" ] && [ -n "${KEG_ID:-}" ] || fail "Failed to extract TAP_ID or KEG_ID"
}

tap_keg() {
  extract_hidden_csrf "$TAPLIST_HTML"
  extract_tap_and_keg
  local url="$BASE_URL$TAP_KEG_ACTION"
  local out
  out=$(curl -s -i -b "$COOKIES" -X POST -d "${CSRF_NAME}=${CSRF_TOKEN}&kegId=${KEG_ID}" "$url" | tr -d '\r')
  echo "$out" | grep -q "HTTP/1.1 302" || fail "Tap Keg did not redirect"
  pass "Tapped keg $KEG_ID into tap $TAP_ID"
}

pour_16() {
  curl -s -b "$COOKIES" "$BASE_URL/taplist" -o "$TAPLIST2_HTML"
  extract_hidden_csrf "$TAPLIST2_HTML"
  local url="$BASE_URL/taps/${TAP_ID}/pour"
  local out
  out=$(curl -s -i -b "$COOKIES" -X POST -d "${CSRF_NAME}=${CSRF_TOKEN}&ounces=16" "$url" | tr -d '\r')
  echo "$out" | grep -q "HTTP/1.1 302" || fail "Pour did not redirect"
  pass "Poured 16 oz on tap $TAP_ID"
}

blow_tap() {
  curl -s -b "$COOKIES" "$BASE_URL/taplist" -o "$TAPLIST3_HTML"
  extract_hidden_csrf "$TAPLIST3_HTML"
  local url="$BASE_URL/taps/${TAP_ID}/blow"
  local out
  out=$(curl -s -i -b "$COOKIES" -X POST -d "${CSRF_NAME}=${CSRF_TOKEN}" "$url" | tr -d '\r')
  echo "$out" | grep -q "HTTP/1.1 302" || fail "Blow did not redirect"
  pass "Blew tap $TAP_ID"
}

verify_events() {
  # Get venue id from Admin Taproom link
  curl -s -b "$COOKIES" "$BASE_URL/admin/taproom" -o "$ADM_HTML"
  local venue_link
  venue_link=$(grep -oE 'href=\"/admin/venue/[0-9]+/events\"' "$ADM_HTML" | head -n1)
  [ -n "${venue_link:-}" ] || fail "No venue events link found on Admin Taproom"
  local venue_id
  venue_id=$(echo "$venue_link" | sed -E 's#.*/admin/venue/([0-9]+)/events.*#\1#')
  curl -s -b "$COOKIES" "$BASE_URL/admin/venue/${venue_id}/events" -o "$EVENTS_HTML"
  grep -q ">TAP<" "$EVENTS_HTML" || fail "No TAP event found"
  grep -q ">POUR<" "$EVENTS_HTML" || fail "No POUR event found"
  grep -q ">BLOW<" "$EVENTS_HTML" || fail "No BLOW event found"
  pass "Events page shows TAP, POUR, BLOW for venue ${venue_id}"
}

main() {
  echo "Running smoke tests against $BASE_URL as $USERNAME"
  http_login
  load_admin_taproom
  load_taplist
  tap_keg
  pour_16
  blow_tap
  verify_events
  echo "All smoke tests passed"
}

main "$@"
