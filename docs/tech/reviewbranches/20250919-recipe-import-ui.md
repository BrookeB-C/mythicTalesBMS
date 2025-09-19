# feature/api/recipe-import-ui

- Area: api
- Owner: genie-catalog
- Task: Add recipe import workflow to admin control center
- Summary: Wire admin recipes page with BeerXML/BeerSmith import flow and display results
- Scope: src/main/java/com/mythictales/bms/taplist/controller/AdminCatalogController.java; src/main/resources/templates/admin/catalog-recipes.html; src/test/java/com/mythictales/bms/taplist/controller/AdminCatalogControllerTest.java; docs/tech/reviewbranches/20250919-recipe-import-ui.md; src/main/resources/templates/admin/brewery.html (spotless)
- Risk: medium
- Test Plan: mvn -q verify; make check-format
- Status: in-progress
- PR: <tbd>

## Notes
- UI shows success/error flash messaging and optional overwrite toggle
- Controller validates file size/type and delegates to RecipeImportService with flash responses
