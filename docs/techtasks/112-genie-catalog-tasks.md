# Genie Catalog — Initial Task List

Scope: Beer catalog, recipes, BJCP styles, imports.

Dispatch assignments (from 130-techlead-dispatch.md)
- TLD-010 Pageable collection endpoints (apply to styles and recipes lists)
- TLD-011 Complete OpenAPI annotations (catalog endpoints + examples)
- TLD-003 Validate API input payloads (import DTO validation)
- TLD-002 REST exception handling with Problem JSON (align controllers)
- TLD-001 Enforce authorization scope (brewery‑scoped catalog APIs)

## P0 — Recipe Import Expansion
- Complete child entity mapping and validation; read endpoints; dedup/force semantics.
 - Validation: DTO bean constraints for import; reject oversize or invalid files with 400/422 Problem JSON.
 - Authz: Enforce brewery scope on import/read; 403 on cross‑tenant access.
 - OpenAPI: Add schemas and examples for import and read endpoints (coordinate with API genie).

## P1 — Styles Lookup & Linking
- Expose styles endpoints; link beers to styles; filters + pagination.
 - Pagination: Use `Pageable` for list endpoints with `page,size,sort`.
 - OpenAPI: Tag endpoints; add examples; document filters.
 - Authz: Tenant scoping for link/unlink behaviors.

## P2 — Admin UX Docs
- Keep `docs/catalog/recipes-import.md` in sync with API capabilities.
 - Include example payloads and Problem JSON; link to Swagger UI.
