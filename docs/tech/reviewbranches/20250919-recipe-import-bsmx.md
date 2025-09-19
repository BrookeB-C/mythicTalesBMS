# feature/api/recipe-import-bsmx

- Area: api
- Owner: genie-catalog
- Task: Enable BeerSmith `.bsmx` uploads in the recipe import flow
- Summary: Extend recipe import pipeline to accept BeerSmith archives and improve UI hints
- Scope: src/main/java/com/mythictales/bms/taplist/catalog/service/RecipeImportService.java; src/main/java/com/mythictales/bms/taplist/catalog/api/RecipeImportController.java; src/main/java/com/mythictales/bms/taplist/controller/AdminCatalogController.java; src/main/resources/templates/admin/catalog-recipes.html; src/test/java/com/mythictales/bms/taplist/controller/AdminCatalogControllerTest.java; src/test/java/com/mythictales/bms/taplist/catalog/api/RecipeImportControllerTest.java
- Risk: medium (new file handling path)
- Test Plan: mvn -q verify; make check-format
- Status: in-progress
- PR: <tbd>

## Notes
- Adds Zip detection in RecipeImportService so both UI and API support BeerSmith `.bsmx`
