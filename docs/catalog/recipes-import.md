# Recipe Import — BeerXML and BeerSmith

This document describes how the Catalog supports importing beer recipes from BeerXML and BeerSmith XML, and how the data maps into our schema.

## Scope
- Supported formats: BeerXML 1.0, BeerSmith (current XML export)
- Import target: Catalog `Recipe` and child components (`RecipeFermentable`, `RecipeHop`, `RecipeYeast`, `RecipeMisc`, `MashStep`).
- Storage: Normalized relational model via tables created in `V2__catalog_recipe_schema.sql`.

## Mapping Overview

Recipe (BeerXML/BeerSmith) → `recipe`
- Name → `name`
- Style/Category → `style_name`
- Type (All Grain/Extract/Partial Mash) → `type`
- Batch Size (L) → `batch_size_liters`
- Boil Time (min) → `boil_time_minutes`
- IBU/ABV/OG/FG/Efficiency → `ibu`, `abv`, `og`, `fg`, `efficiency`
- Equipment Profile name → `equipment`
- Notes → `notes`
- Source format → `source_format` (BEERXML|BEERSMITH)
- Source hash (SHA‑256 of normalized XML) → `source_hash`

Fermentables → `recipe_fermentable`
- Name → `name`
- Amount (kg) → `amount_kg`
- Yield (%) → `yield_percent`
- Color (Lovibond/SRM) → `color_lovibond`
- Late addition (BeerSmith) → `late_addition`
- Type (Grain/Extract/Sugar/Adjunct) → `type`

Hops → `recipe_hop`
- Name → `name`
- Alpha Acid (%) → `alpha_acid`
- Amount (g) → `amount_grams`
- Time (min) → `time_minutes`
- Use (Boil/Dry Hop/Whirlpool/Mash) → `use_for`
- Form (Pellet/Leaf/Plug) → `form`
- IBU contribution (if provided) → `ibu_contribution`

Yeast → `recipe_yeast`
- Name → `name`
- Laboratory → `laboratory`
- Product ID/Code → `product_id`
- Type (Ale/Lager/etc.) → `type`
- Form (Liquid/Dry) → `form`
- Attenuation (%) → `attenuation`

Misc → `recipe_misc`
- Name → `name`
- Type (Spice/Fining/Water Agent/etc.) → `type`
- Amount (+ unit) → `amount` + `amount_unit`
- Use (Boil/Fermentation/etc.) → `use_for`

Mash Steps → `mash_step`
- Name → `name`
- Type (Infusion/Temperature/Decoction) → `type`
- Step Temp (°C) → `step_temp_c`
- Step Time (min) → `step_time_minutes`
- Infuse Amount (L) → `infuse_amount_liters`

## Assumptions & Conversions
- Volumes converted to liters; weights to kilograms/grams; temps to Celsius.
- Missing metrics (IBU/ABV) can be null; calculator integration optional later.
- Style taxonomies are stored as strings initially; can be normalized later.
- BeerSmith fields that have no BeerXML equivalent are stored when sensible (e.g., late additions).

## Import API (planned)
- `POST /api/v1/catalog/recipes/import` (multipart file)
  - Accepts `format=BEERXML|BEERSMITH` (auto‑detect when possible)
  - Persists `Recipe` + children; returns created id and summary
- `GET /api/v1/catalog/recipes/{id}` → Recipe with children

## Idempotency & Dedup
- Importers compute `source_hash` from normalized XML (trimmed, comments removed) to detect duplicates.
- If a duplicate is detected, respond 200 with existing recipe id unless `?force=true`.

## Future Enhancements
- Profiles as first‑class: style/equipment/mash profiles normalized into separate tables
- Calculator integration for IBU/ABV when missing or inconsistent
- Versioning of recipes; user annotations

