# Optional JEI integration (Mortal Integrations)

JEI support is built into Mortal Expansion. There is no separate addon JAR and JEI is not required to install, load, or play the mod. Install a compatible JEI 19.x build (tested with 19.21.1.300) on the client to enable the integration. TerraBlender remains required for Mortal Expansion independently of JEI.

**Upgrading from the prototype:** Remove the earlier `mortal_integrations` addon JAR when using this integrated version, otherwise both plugins can register duplicate process pages. The separate addon project is superseded; its history remains in Git.

## Included

- Ectchantment Machine transmutations and Demi-Tangible operations, using the machine's public result helper and actual enchantment eligibility.
- Purification Station recipe, reagent-tag alternatives, water/energy amounts, process time and clickable progress bar.
- Four heated-cauldron routes with fluid-bucket diagrams and heat/container guidance.
- Haunt/Haint Furnaces as native smelting/fuel catalysts.
- Separate Purity and Impurity altar-offering categories, using each altar's loaded offering tag and authoritative burn-time helper.
- Distinct Book of Ectcetera tier subtypes and ingredient guidance.
- A ghost-green border and spectral Secrets of the Dead ribbon/tooltips on secret crafting/smithing recipes.

All recipes stay visible for reference, as requested. Seeing a recipe in JEI does not teach it or bypass server crafting restrictions. The notice identifies secret-origin recipes even after learning; permanent player knowledge is not synchronized for that display. JEI's normal crafting/smithing support remains in use; custom machine pages are display-only and do not add machine recipe-transfer handlers.

## Implementation and migration

Client code is isolated under `com.howierux.mortalexpansion.integration.jei`. JEI discovers the `@JeiPlugin` class when present. Common mod initialization does not reference that class or JEI APIs. Metadata declares an optional client dependency, API artifacts are compile-only, and JEI is never bundled in the Mortal Expansion JAR. No mixins, reflection, new packets, or saved-data changes are added.

Dependency versions remain in `gradle.properties`. Normal builds and runs omit JEI at runtime. For development only:

```text
gradlew.bat runClient -PwithJei
gradlew.bat runData -PwithJei
```

Run `gradlew.bat runData` and `gradlew.bat runGameTestServer` without that flag to validate loading without JEI. Existing CI checks cover the no-JEI path; a second workflow checks loading with JEI.

Custom recipes read machine constants, purification reagent tags and the shared secret registry. Transmutation displays enumerate current items and tags at JEI registration; reconnect after in-place datapack tag changes to rebuild them. The four cauldron routes remain display adapters because the base interaction handler has no declarative recipe registry. Fluids are shown through bucket items. Generator fuel categories and custom machine transfer handlers can be added later.

For a version migration, verify JEI discovery, decorator/subtype APIs, vanilla smithing-subclass behavior, metadata sided dependencies, machine helpers and recipe constants. Keep JEI imports out of common event registration and initialization.

## Validation limits

Build, optional dependency packaging, client-side data loading with and without JEI, and server GameTests are checked for this update. Live recipe navigation, banner readability, tooltips, and recipe-transfer feedback still require a client playtest.

## API references

- [JEI plugin guide for Minecraft 1.21/1.21.1](https://github.com/mezz/JustEnoughItems/wiki/Creating-Plugins-%5BMinecraft-1.21-and-1.21.1%5D)
- [Official JEI Maven](https://maven.blamejared.com/mezz/jei/)

Verified 2026-10-19: the Gradle build, both client-side data-loading configurations, all 195 required server GameTests without JEI, 622-resource validation and 30-chapter/128-page guide checks passed. Packaged JAR checks confirm a single mod, optional JEI metadata, no bundled JEI classes and no JEI references in common initialization.

## Altar offerings

The Altar of Purity and Altar of Impurity each have their own JEI category and catalyst. View recipes for an altar to browse its offerings; view uses for an offering to find compatible altars. Each item gets its own indexed entry, including Raw Ectoplasm (60 seconds), Purified Ectoplasm (120 seconds), Spirit Boons and both accepted logs. Displays show burn duration, the altar's cleansing/corruption rate, its loaded 3x3 chunk footprint, and the normal consumption interval.

Membership comes from `mortal_expansion:purity_offerings` and `mortal_expansion:impurity_offerings` separately. The Impurity tag inherits Purity offerings by default; datapacks can change either. Durations come from `PurityOfferings.burnTicks`, rather than a second JEI fuel table. As with existing tag-driven integrations, reconnect after datapack changes so JEI rebuilds its catalog.

An altar is displayed as a catalyst, not a fabricated output item. If a datapack offering leaves a crafting container, its actual returned item appears as an output; current solid offerings have no remainder. Ritual Stars are separate world rituals and are not presented as ordinary fuel. Secret-based altar crafting notices remain on the existing crafting recipes; browsing offering information never unlocks the altar recipe.

The display-data record has no JEI or client dependencies. The plugin/category classes remain optional and isolated; no packets, mixins, saved-data changes or gameplay recipes are introduced. Server regression checks can therefore verify the offering catalog without JEI installed. Live JEI navigation and layout remain client playtest checks.

Verified 2026-10-21 for altar offerings: build and all 217 required server tests pass without JEI; build/data loading with `-PwithJei` passes; generated-resource and book checks pass. The new regression checks both tags, indexed fuel entries, actual burn durations, completeness, exclusions and native container remainders without requiring JEI on the server.
