Keg Inventory API Samples

Base path: `/api/v1/keg-inventory` (configurable via `bms.keginventory.apiBasePath`).

Endpoints
- POST `/assign` — Assign a keg to a venue (status→DISTRIBUTED)
- POST `/receive` — Receive a keg at a venue (status=RECEIVED)
- POST `/move` — Move a keg between venues (status→DISTRIBUTED unless RECEIVED)
- POST `/return` — Return a keg to the brewery (status=EMPTY)

Examples

curl -X POST localhost:8080/api/v1/keg-inventory/assign \
  -H 'Content-Type: application/json' \
  -d @assign.json

curl -X POST localhost:8080/api/v1/keg-inventory/receive \
  -H 'Content-Type: application/json' \
  -d @receive.json

curl -X POST localhost:8080/api/v1/keg-inventory/move \
  -H 'Content-Type: application/json' \
  -d @move.json

curl -X POST localhost:8080/api/v1/keg-inventory/return \
  -H 'Content-Type: application/json' \
  -d @return.json

