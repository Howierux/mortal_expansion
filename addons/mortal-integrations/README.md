# Mortal Integrations

A separate NeoForge addon for **Mortal Expansion**, starting with **Just Enough Items (JEI)** integration. It has its own mod ID (`mortal_integrations`) and JAR; Mortal Expansion itself does not depend on JEI. The source is maintained in the Mortal Expansion repository under `addons/mortal-integrations`, not compiled into the base mod.

## Included

- Ectchantment Machine transmutation and Demi-Tangible reference recipes, obtained using the base mod's actual transmutation helper and enchantment eligibility.
- Purification Station recipe, reagent-tag alternatives, water/energy costs and processing time.
- Four heated-cauldron routes, with bucketed-fluid reference diagrams and heat/container guidance.
- Haunt/Haint Furnaces as vanilla smelting/fuel catalysts.
- Four Book of Ectcetera tier variants, with subtype identity based on tier rather than arbitrary secret-mask combinations.
- A ghost-green border and spectral **Secrets of the Dead** ribbon/tooltips on secret crafting and smithing recipes, using the base mod's secret registry rather than a duplicate recipe list.
- A clickable Purification Station progress bar opening its JEI category.

**All recipes remain visible for reference.** Viewing a recipe in JEI does not teach it or bypass Mortal Expansion's server-authoritative knowledge locks. The overlay identifies secret-origin recipes even after learning; permanent player knowledge is not synchronized for this purpose. No mixins, reflection, custom networking, or changes to saved data are added.

## Install

Minecraft 1.21.1, Java 21, NeoForge 21.1.219, Mortal Expansion 1.0.0 (the current repository API baseline), its required TerraBlender dependency, and JEI 19.21.1.300 or a compatible newer 19.x build. JEI and the addon are intended for the client. The addon is also safe to have on a dedicated server without JEI; it adds no server gameplay and need not be installed there. Keep Mortal Expansion's own normal client/server installation requirements.

## Build

Build the parent project first, then this independent project:

```text
gradlew.bat build
gradlew.bat -p addons/mortal-integrations build
```

The addon is produced at `addons/mortal-integrations/build/libs/mortal_integrations-0.1.0.jar`. The base mod and JEI are dependencies, not bundled inside it.

For a standalone source checkout, use this folder's wrapper and provide the base JAR:

```text
gradlew.bat build -PmortalExpansionJar=C:/path/to/mortal_expansion-1.0.0.jar
```

Dependency versions are centralized in `gradle.properties`. Client/server development run tasks are available; `runData` checks the client-side loader with JEI; `runGameTestServer -PwithoutJei` checks dedicated-server loading and the secret catalog with JEI intentionally excluded from runtime. JEI's sources are compile-only; the full JEI mod is development runtime only.

## Integration boundaries and validation

Custom categories are display-only and do not invent server recipes or implement automatic recipe transfer into machines. Normal crafting/smithing remains JEI's native integration. Cauldron fluid amounts are represented with bucket items, not a new fluid renderer. Transmutations enumerate actual registered items/tag membership at JEI registration; reloading a world rebuilds them. If a recipe datapack changes tags in-place, reconnect to refresh those generated displays.

The base mod currently exposes no public declarative cauldron recipe list; the four world-interaction routes are documented display adapters. Keep them aligned with `NaturalPurificationHandler`. Machine constants and the purification reagent tag are read from the base mod. When migrating versions, check these APIs, JEI category/decorator/subtype signatures, metadata sided dependencies and native smithing subclass support.

Build, packaged-resource checks, and headless loading results are recorded with the commit. Live layout readability, recipe navigation, actual transfer-lock feedback and resource-reload behavior remain playtest checks. Broader integrations (including generator fuel categories, custom transfer handlers, and other recipe browsers) can be added independently.

## API references

- [JEI 1.21/1.21.1 plugin guide](https://github.com/mezz/JustEnoughItems/wiki/Creating-Plugins-%5BMinecraft-1.21-and-1.21.1%5D)
- [Official JEI Maven](https://maven.blamejared.com/mezz/jei/)

All Rights Reserved, consistent with the parent project.

## Verified baseline (2026-10-19)

- Standalone Gradle build passed against Minecraft 1.21.1 / NeoForge 21.1.219 / JEI 19.21.1.300.
- Client-side data-loader initialization with JEI passed.
- Dedicated-server GameTest run without JEI passed: 1 required test, including all secret recipe IDs and their classification.
- JAR/package and translation checks passed. No live client visual test was run.
