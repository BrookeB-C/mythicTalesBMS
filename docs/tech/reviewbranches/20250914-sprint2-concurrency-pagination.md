# feature/api/sprint2-concurrency-pagination

- Area: api
- Owner: ${AGENT_ROLE:-genie-api}
- Task: docs/techtasks/100-genie-api-tasks.md (Sprint 2)
- Summary: Add optimistic concurrency checks to write endpoints and paginate Tap listings with default sort.
- Scope:
  - src/main/java/com/mythictales/bms/taplist/api/dto/* (add expectedVersion)
  - src/main/java/com/mythictales/bms/taplist/api/TapApiController.java (If-Match alternative via expectedVersion; Pageable list)
  - src/main/java/com/mythictales/bms/taplist/repo/TapRepository.java (Pageable finders)
  - src/test/java/com/mythictales/bms/taplist/api/RestExceptionHandlerTest.java (add 409 test)
- Risk: low
- Test Plan:
  - Build/tests: mvn test (JDK 17)
  - Verify 409 when expectedVersion mismatches; ensure 409 Problem JSON
  - Verify /api/v1/taps accepts pageable params and sorts by number by default
- Status: in-progress
- PR: <tbd>

## Notes
- We chose request field `expectedVersion` over If-Match ETag for simplicity; handler already maps OptimisticLockingFailureException to 409.
