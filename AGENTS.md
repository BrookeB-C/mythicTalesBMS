# Agents Guide — Mythic Tales BMS

This repository is configured for AI agents (“genies”) collaborating on the Mythic Tales Brewery Management System. Follow the rules below to work safely and consistently.

## Scope & Precedence
- Scope: This file applies to the entire repository unless a deeper AGENTS.md overrides a point.
- Precedence (highest → lowest): Direct user/developer instructions > deeper AGENTS.md > this file.

## Project Snapshot
- Stack: Java 17, Spring Boot 3, Maven, H2 (dev) / Postgres (prod planned), Thymeleaf (MVC), springdoc-openapi.
- Key Areas:
  - API: `src/main/java/.../api/**` (REST controllers, DTOs, mappers)
  - MVC: `src/main/java/.../controller/**` (Thymeleaf views)
  - Domain/Repos/Service: `.../domain/**`, `.../repo/**`, `.../service/**`
  - Config/Security/OpenAPI: `.../config/**`, `.../security/**`
  - Docs: `docs/`, with technical tasks in `docs/techtasks/`

## Genies & Responsibilities
- API & Security Genie — see `docs/agents/genie-api.md`
  - Owns REST endpoints `/api/v1/**`, validation, authorization scope, error model, OpenAPI docs, pagination, and optimistic concurrency.
  - May propose service-layer changes only where needed for authz/validation.

- Platform, Data & Observability Genie — see `docs/agents/genie-platform-data.md`
  - Owns DB migrations (Flyway), environment profiles (dev/prod), security toggles for H2/Swagger, Actuator/Micrometer/logging, CI/CD improvements.
  - May add Testcontainers integration tests and Docker/compose tooling.

## Global Conventions
- Java: 17, Spring Boot 3 idioms; avoid static state; prefer constructor injection.
- Style: Use Spotless (google-java-format). Run `make format` before creating patches.
- Tests: Add or update tests when changing behavior. Use JUnit 5, Spring Test/MockMvc; for DB ITs, prefer Testcontainers.
- Security:
  - Enforce authorization scope using `CurrentUser` affiliations (breweryId, taproomId, barId).
  - Do not expose H2 console or Swagger UI in `prod` profile.
- Errors: Use a consistent Problem JSON (`status`, `error`, `message`, `details`, `timestamp`, `traceId`).
- API: Prefer `Pageable` for collections; avoid returning unbounded lists.
- DTOs/Mappers: Keep DTOs in `api/dto`, convert in `ApiMappers`. Do not leak JPA entities through REST.
- Logging: No secrets; include correlation ids where available.

## Safety & Boundaries
- Do not rewrite git history, delete unrelated files, or change licenses.
- Keep changes minimal and focused; avoid broad refactors without an explicit instruction.
- Do not introduce new external services or network calls without approval.

## Validation Before Hand-off
- Build & test: `mvn verify` (or `make test`)
- SpotBugs: `make spotbugs-strict` (CI fails on HIGH severity)
- Swagger: `http://localhost:8080/swagger-ui.html` and `/v3/api-docs` load without errors
- Formatting: `make check-format`

## Collaboration Rules
- If your change touches both API and platform concerns, split into two small, reviewable patches when possible.
- When altering shared contracts (DTOs, error model), update OpenAPI annotations and docs in `docs/`.
- Leave clear notes in commit/patch messages and reference items in `docs/techtasks/` when applicable.

## Feature Branch Workflow (Required)
- Before making changes, create a feature branch:
  - Naming: `feature/<area>/<slug>` (area: `api` or `platform`)
  - Use `bin/start-feature.sh <api|platform> <slug> [--do]` to generate the branch and add a review entry under `docs/tech/reviewbranches/`.
- Commit messages should be verbose and explain: context, rationale, changes, risks, and validation steps.
- Open a Pull Request and link the review entry file. Use the PR template.

## Commit Messages (Template)
- Use the commit template included at `.gitmessage.txt` (Markdown variant in `.github/COMMIT_TEMPLATE.md`).
- To configure locally:
  - `git config commit.template .gitmessage.txt`
- Each commit should include:
  - Summary line (<= 72 chars)
  - Context/Motivation
  - Changes (bulleted)
  - Risks and rollback plan
  - Validation steps (commands, URLs, tests)
  - References (docs/techtasks item, review entry, issue/PR links)



## Role Selection (Tell each instance who it is)
- Set the environment variable `AGENT_ROLE` for each running agent instance:
  - `AGENT_ROLE=genie-api` for the API & Security Genie
  - `AGENT_ROLE=genie-platform-data` for the Platform, Data & Observability Genie
  - `AGENT_ROLE=techlead` for the Tech Lead coordinator role (planning/docs/workflow)
  - `AGENT_ROLE=product-manager` for the Product Manager (docs/resources/release notes)
  - Domain roles: `genie-production`, `genie-keginventory`, `genie-catalog`, `genie-taproom`, `genie-sales`, `genie-distribution`, `genie-prodinventory`, `genie-procurement`, `genie-maintenance`, `genie-analytics`, `genie-billing`, `genie-compliance`, `genie-iam`
- Behavior:
  - Agents should read `AGENT_ROLE` at start and strictly follow the corresponding guide in `docs/agents/`.
  - If `AGENT_ROLE` is not set, default to read-only exploration and ask for clarification.
- Optional file-based selection for tools without env support:
  - Create a file `docs/agents/role.txt` with one of the supported roles (see above).
  - Agents should prefer `AGENT_ROLE` when both are present.
