# UI Playground

The dev-only playground (Storybook-style) lives at `/ui/playground` and is active when the `dev` Spring profile is enabled.

## Run locally

```bash
mvn spring-boot:run
# browser → http://localhost:8080/ui/playground
```

## Structure

- `UiPlaygroundController` — lists available stories and maps query string `story` → Thymeleaf fragment.
- `templates/ui-playground/index.html` — shell layout with persistent navigation and stage area.
- `templates/ui-playground/stories/` — individual story fragments (`foundations`, `buttons`, `cards`, `tables`, `forms`, `overlays`, `visualizations`).
- `static/css/ui-playground.css` — design token showcase + component styling.

## Current stories

- Foundations — color tokens, typography scale, spacing rhythm.
- Buttons — primary/secondary/ghost/danger states.
- Cards — metric and content cards with supporting text.
- Tables — enterprise data grid with toolbar and pagination.
- Forms — input grids, validation messaging, and filter chips.
- Overlays & Dialogs — modal, slide-over, and toast patterns.
- Visualizations — sparkline, capacity ring, segmented distribution bar.

## Adding a new story

1. Create a fragment under `templates/ui-playground/stories/<name>.html` exposing `th:fragment="story"`.
2. Register the story in `UiPlaygroundController.STORIES` with id, label, and description.
3. Add a `th:case` entry in `templates/ui-playground/index.html` to load the new fragment.
4. Provide component-specific styling within the existing CSS or a scoped addition.

> Keep examples self-contained and focused on enterprise admin use cases so stakeholders can sign off quickly before porting styles into production templates.
