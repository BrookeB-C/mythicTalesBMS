# Styles Linking (Admin)

You can link beers to BJCP styles and manage links from the Admin → Beers page.

- Filters
  - Guideline Year selector (e.g., 2015/2021)
  - Search Beers box to filter the beer list by name
- Linking
  - Select a BJCP style from the grouped dropdown (by category) and click Link
  - The current linked style code/name is shown per beer row
  - Click Unlink to remove the association
- Authorization
  - Requires admin role; scope follows the brewery of the admin account
- API for automation
  - Link: `PATCH /api/v1/beers/{id}/styleRef` with `{ "styleId": <long> }`
  - Unlink: `DELETE /api/v1/beers/{id}/styleRef`

Notes
- Styles list/search API: `GET /api/v1/catalog/styles?year=2015&q=ipa`
- The UI groups styles by category for faster selection.
