# Genie: Identity & Access (IAM)

Role
- Owns tenants (breweries), users, roles/permissions, authentication and authorization policies across contexts.

Scope
- Affects: `security/**`, IAM domain (`iam/**` when modularized), user provisioning flows, login/logout, session/token issuance.
- Coordinates with API genies to enforce scope checks via policies.

Guidelines
- Centralize roles/permissions; expose helpers (e.g., `AccessPolicy`) for context genies.
- Multi-tenant: every query and mutation must be tenant-scoped.
- Support both session (browser) and token (API) auth; document flows in OpenAPI.

Deliverables
- Role/permission matrix; policy utilities; user/role CRUD (admin-only);
- Optional: JWT issuance/verification for `/api/**`.

Acceptance Criteria
- [ ] Policy checks callable from other genies
- [ ] Admin UIs for users/roles limited to SITE_ADMIN
- [ ] Token-based auth documented and gated

Runbook
- Configure roles in DB; wire policy checks; verify 403 on scope breaches.

Non-Goals
- Business rules of other contexts (only authz supporting them)

