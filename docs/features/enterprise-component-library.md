# Enterprise Component Library Proposal

## Purpose
Establish a reusable, accessibility-first component library that encapsulates the enterprise desktop shell, layout primitives, and interactive widgets required by Mythic Tales BMS. The library must support incremental adoption within Thymeleaf views while preparing the platform for future SPA or micro-frontend experiences.

## Guiding Principles
- **Single Source of Truth:** Ship design tokens (color, spacing, typography) and interaction patterns once, consume across all domains.
- **Framework Agnostic Delivery:** Prefer Web Components + lightweight utilities so server-rendered Thymeleaf pages and potential React/Vue clients can share components.
- **Progressive Enhancement:** Components render meaningful HTML/CSS without JavaScript; enhanced behaviors load as modules when available.
- **Accessibility & Compliance:** WCAG 2.1 AA baked into components with automated testing and manual review before release.
- **Versioned & Documented:** Semantic versioning, changelog, Storybook/Pattern Library with usage guidelines and code samples.

## Scope (Phase 1)
1. **Design Tokens & Foundations**
   - Color palette, typography scale, radius, shadows, spacing.
   - Motion + focus states aligned with enterprise desktop spec.
2. **Layout & Shell**
   - `mt-app-shell`: top bar, nav rail, workspace panes; slots for breadcrumbs and actions.
   - `mt-context-drawer`, `mt-command-overlay` for filters and command palette.
3. **Data Display**
   - `mt-hero-grid`, `mt-hero-card` for KPI ribbons.
   - `mt-queue-list`, `mt-activity-feed` with status/badge variations.
   - `mt-quick-actions` for responsive action grids.
4. **Utility Components**
   - `mt-toast-region` with queueing, `mt-chip`, density toggle, button styles.
5. **Composability Helpers**
   - JS controller for state sync (nav selection, density) using event-based API.

## Technical Approach
- **Tech Stack:**
  - Web Components built with [Lit](https://lit.dev/) for DX and SSR readiness.
  - TypeScript for type safety and IDE support.
  - Vite build pipeline targeting ES2019 + module/nomodule bundles, plus CSS extraction.
  - Storybook for documentation and visual review.
- **Packaging:**
  - Publish as internal npm package (`@mythic-tales/ui-shell`).
  - Provide CDN-friendly distribution via `/static/vendor` until package registry is ready.
- **Integration with Thymeleaf:**
  - Include bundle via `<script type="module">` + `<link rel="stylesheet">` in base layout fragment.
  - Replace existing Nav/Shell fragments with `mt-app-shell` while leaving content slots rendered by Thymeleaf.
  - Provide Spring MVC helper to pass JSON data to components (e.g., hero cards) via `data-*` attributes or progressive hydration script.

## Release & Rollout Plan
1. **Foundations Sprint**
   - Finalize tokens; implement `mt-app-shell` skeleton using existing prototype styles.
   - Set up build/test tooling (Vite, Storybook, Playwright for a11y snapshots).
2. **Pilot Domain Migration**
   - Adopt in Taproom landing page template; measure performance and authoring experience.
   - Gather feedback from UI/QA/Accessibility.
3. **Broader Adoption**
   - Migrate Production, Keg, and Sales landing pages.
   - Publish usage guidelines in Storybook; add lint rule to discourage legacy fragments for covered areas.
4. **General Availability**
   - Tag v1.0.0; document upgrade guide.
   - Integrate into CI (build, lint, visual regression, bundle size budgets).

## Testing Strategy
- **Unit:** Lit testing library for property/state coverage.
- **Visual Regression:** Storybook Chromatic or Playwright screenshots per component state.
- **Accessibility:** axe-core automated checks + manual keyboard testing per release.
- **Performance:** Lighthouse CI for shell render time; track bundle size (<150KB gzipped initial target).

## Governance & Ownership
- Component library owned by UI/Frontend guild (to be defined); release cadence tied to sprint reviews.
- Change requests require accessibility review and Storybook documentation.
- Semantic versioning enforced; breaking changes need migration notes and deprecation policy.

## Dependencies & Risks
- Need npm registry or GitHub Packages for distribution (coordinate with Platform/Data team).
- Lit + Storybook tooling adds node-based build chain; ensure CI runners have Node 18+.
- Training for Thymeleaf-focused developers on component usage.
- Data hydration patterns must secure API payloads; coordinate with API genie.

## Acceptance Criteria
1. Repository hosts `/ui-library` workspace with build tooling, Storybook, and CI checks.
2. Minimal component set (`mt-app-shell`, hero cards, queue, activity, quick actions) available as reusable Web Components.
3. Thymeleaf layout integrates shell component serving at least one domain landing page in dev mode.
4. Automated accessibility + visual regression checks run in CI for the component library.
5. Documentation site (Storybook) explains usage, theming, and migration steps.

## Next Actions
- Kick-off alignment with Tech Lead + Platform Genie to provision package registry and CI resources.
- Create techtask epic for component library foundations referencing this proposal.
- Schedule design review to validate tokens and Lit conventions prior to implementation.
