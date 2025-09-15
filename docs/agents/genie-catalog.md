# Genie: Catalog

Role
- Owns beers, recipes, packaging specs (KegSizeSpec), and pricing lists.

Scope
- Affects: `catalog/**` module (future), `domain` for Beer/Recipe/SizeSpec, API for CRUD (admin-only), validations.

Guidelines
- Maintain product master data with versioning for recipes.
- Validate ABV, style, naming conventions; prevent deletion if referenced.

Deliverables
- Beer and Recipe CRUD with validation; KegSizeSpec governance; PriceList endpoints.

Acceptance Criteria
- [ ] Recipe versioning with immutable history
- [ ] Prevent deletion when in use
- [ ] OpenAPI documented; tests for validations

Runbook
- Seed sample beers/recipes in dev; manage via API/UI for admins.

Non-Goals
- Production execution and inventory movement

