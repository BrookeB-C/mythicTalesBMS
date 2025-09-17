# Catalog — Recipe Editor (Admin)

Overview
- Navigate to Admin → Brewery → Catalog tab to access recipe tools.
- Use “Import Recipe” to upload BeerXML or BeerSmith XML files.
- Click “Edit Recipe” to open the editor. You can update core fields and manage ingredients and mash steps.

Editing
- Core: name, style name, type, batch size (L), boil time (min), notes.
- Ingredients:
  - Fermentables: name, amount (kg), yield %, color (Lovibond), late addition, type.
  - Hops: name, alpha %, amount (g), time (min), use, form, IBU contribution.
  - Yeasts: name, lab, product id, type, form, attenuation.
  - Misc: name, type, amount + unit, use.
  - Mash Steps: name, type, step temp (°C), step time (min), infuse amount (L).

Export
- From the edit page, export the recipe in either format:
  - Export BeerXML → downloads BeerXML 1.0
  - Export BeerSmith XML → downloads BeerSmith-style XML

Security & Scope
- Only SITE_ADMIN or BREWERY_ADMIN can import, edit, or export.
- BREWERY_ADMIN actions are restricted to their own brewery’s recipes.

API Reference
- List recipes: `GET /api/v1/catalog/recipes?breweryId={id}` (paged)
- Recipe detail: `GET /api/v1/catalog/recipes/{id}`
- Import: `POST /api/v1/catalog/recipes/import` (multipart)
  - Constraints: non-empty file, <= 2MB; duplicate detection with 409 on `force=false`.
- Style link (beer): `PATCH /api/v1/beers/{id}/styleRef` with `{ "styleId": <long> }`
- Unlink style: `DELETE /api/v1/beers/{id}/styleRef`

Problem JSON
- Errors return `application/problem+json` with fields: `status`, `error`, `message`, `details`, `timestamp`.

Notes
- Child-level editing is basic; future enhancements can add inline editing and reordering.
