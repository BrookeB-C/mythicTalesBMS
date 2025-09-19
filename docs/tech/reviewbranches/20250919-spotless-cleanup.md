# feature/platform/spotless-cleanup

- Area: platform
- Owner: genie-catalog
- Task: Resolve Spotless formatting drifts flagged in CI
- Summary: Run repo-wide Spotless apply to bring controllers/templates back to policy
- Scope: src/main/java/com/mythictales/bms/taplist/controller/AdminBarController.java; src/main/java/com/mythictales/bms/taplist/controller/AdminBreweryController.java; src/main/java/com/mythictales/bms/taplist/controller/AdminSiteController.java; src/main/java/com/mythictales/bms/taplist/controller/AdminTaproomController.java; src/main/java/com/mythictales/bms/taplist/controller/AdminUserController.java; src/main/java/com/mythictales/bms/taplist/controller/TaplistController.java; src/main/java/com/mythictales/bms/taplist/controller/UiPlaygroundController.java; src/main/java/com/mythictales/bms/taplist/domain/TaplistView.java; src/main/java/com/mythictales/bms/taplist/repo/TaplistViewRepository.java; src/main/java/com/mythictales/bms/taplist/service/TapService.java; src/main/java/com/mythictales/bms/taplist/service/TaplistProjectionUpdater.java
- Risk: low
- Test Plan: mvn -q spotless:apply; make check-format; mvn -q verify
- Status: in-progress
- PR: <tbd>

## Notes
- Automated formatting only; no behavior changes expected
