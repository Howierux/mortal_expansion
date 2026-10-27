# Mortal Expansion 1.1.0-beta.1: Fabric candidate

Prepared 2026-10-27. Minecraft 1.21.1, Java 21. Promote `fabric-dev` to `fabric` through the draft release PR after the final playtest. NeoForge is promoted separately from `dev` to `main`; do not merge the complete Fabric branch into NeoForge main.

## CurseForge changelog

First public Fabric beta of Mortal Expansion, bringing the current NeoForge gameplay to Fabric 1.21.1.

- Explore Spiritwood Forests, Graveyards and named Spiritwells; meet spectral creatures, trade with Spirit Villagers and accept unfinished-business quests.
- Discover crafting knowledge through the upgradeable Book of Ectcetera, Mortality Research and the Darkhold's seven Secrets of the Damned.
- Experience personal/chunk Haunt, Spirit Journey, corpse recovery, Grave Porting, ritual offerings, spectral equipment and Mortal Golems.
- Build ectoplasm machinery, generators, specialized furnaces, purification equipment, Ecto Pipes and Ectanks.
- Adjust block opacity with the Fancifier, including functional blocks, a live preview and 1–100% selection.
- Use Holy Water bottles and Splash Potions of Consecration; protect your home with Dreamcatchers, Holy Salt, Spirit Torches and wards.
- Includes optional JEI recipe/machine/altar pages. JEI is not required.
- Includes the native parity fixes for graves, loot, Bloodstone diamond compatibility, enchanting tags, spawn rules and optional Sodium 0.6 rendering.

Requires Fabric API, Fabric Language Kotlin and TerraBlender for Fabric. Team Reborn Energy API is bundled. Use a fresh Fabric world: moving an existing NeoForge world to Fabric is not supported. Broader modpack/shader/connected-texture compatibility remains under validation.

## Upload settings

- Upload the gameplay jar named `mortal_expansion-fabric-1.1.0-beta.1.jar` as a regular project file, not an additional/source attachment.
- Display name: Mortal Expansion 1.1.0-beta.1 — Fabric.
- Type: Beta. Minecraft: 1.21.1. Loader: Fabric only. Java: 21. Environment: Client & Server.
- Required dependency relations: Fabric API, Fabric Language Kotlin, TerraBlender. Select builds of those projects for Fabric 1.21.1.
- Optional dependency relation: Just Enough Items (JEI). Do not add the old separate Mortal Integrations addon.
- Do not upload sources, dev jars, dependency jars, cached files or test worlds.

## Validation and remaining release gate

Native build/GameTests, metadata and packaged-resource inspection must pass for the final versioned artifact. The prior parity audit passed all 79 tests, datagen, registry/resource comparisons and a Sodium client check at 30% volume. See [parity audit](FABRIC_PARITY_AUDIT.md).

Before publishing, playtest the final candidate in a fresh Fabric instance with its required dependencies, exercise book/research, a generator, pipes/tanks and Spirit Journey, and check the desired optional modpack. No cross-loader save migration or arbitrary compatibility claim is made.
