# Enterprise UI Data Gaps

This note tracks UI elements in the enterprise desktop experience that currently rely on placeholder data because no API or service supplies the necessary information.

## Global Shell
- **Notifications count** – Requires alert/incident feed per user session.
- **Command palette suggestions** – Needs search/command endpoints spanning taprooms, kegs, recipes, and tasks.
- **Help menu / guided tours** – No content service for contextual walkthroughs.

## Keg Inventory Console
- `GET /api/v1/keg-inventory/summary` delivers hero metrics, queue items, and recent activity; UI now consumes live data.
- **Trend metrics** (`▲/▼` deltas) – Still need historical telemetry for availability/distribution comparisons.
- **Scheduled cleaning queue** – No workflow endpoint for CIP/maintenance backlog.

## Taproom Operations Console
- **Sales velocity** – Requires POS integration or sales API.
- **Staff coverage** – Needs scheduling/roster service.
- **Ticket stream & anomalies** – No taproom ticketing API emitting real-time events.

## Quick Actions
- Placeholder actions (`Assign Keg`, `Log Return`, `Open Taproom`, etc.) do not have linked endpoints in the MVC layer; wiring these requires dedicated REST commands or form flows.

## Identity & Access Console
- Now consumes `/api/v1/users?breweryId=…` but lacks audit/change history and invitation flows.
- Need lightweight endpoint for recent user events (role changes, lockouts) to populate the activity feed with real timestamps.

## Next Steps
1. Prioritize API contracts for high-value metrics (keg activity, taproom tickets) with the API & Security genie.
2. Define event sourcing approach (Kafka topics vs. REST polling) so the activity feeds can subscribe to real data.
3. Extend the command palette spec to map UI intents to actual controller routes or REST endpoints.
