# Release Notes — 2025-09-17

Highlights
- Catalog P0 complete: Recipe import (BeerXML/BeerSmith), child mapping, dedup/force, Problem JSON
- Catalog read APIs: GET /api/v1/catalog/recipes, /{id} with tenant scope and pagination
- Beer–style linking: PATCH/DELETE /api/v1/beers/{id}/styleRef (tenant enforcement)
- Admin UI: Brewery Catalog tab; Recipe editor (core + ingredients + mash) with BeerXML/BeerSmith export
- OpenAPI: examples for recipes list/get/import and BJCP styles list/get
- Docs: recipes-import (curl + Problem JSON), recipe editor guide, styles linking guide

PRs
- #8 platform — Beer↔Brewery link + Flyway V4
- #9 api/admin — Catalog P0 + style link + editor + OpenAPI
- #13 api/admin — tenant scope + tests + docs
- #15 api — styles OpenAPI + import docs polish
- #16 admin — styles UI polish (search + unlink)

Upgrade Notes
- DB schema: V4 adds `beer.brewery_id` and `beer.brewery_name`; run Flyway migration
- API consumers: see Swagger UI for new endpoints and request/response shapes

Validation
- mvn verify passing; Spotless format clean; Swagger UI loads without errors
