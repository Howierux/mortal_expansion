# Mortal Expansion

> **Fabric development:** build the scaffold in [fabric/](fabric/README.md). Root sources remain the NeoForge reference; gameplay is not yet ported. See [branch policy](docs/LOADER_BRANCHES.md) and [port status](docs/FABRIC_PORT.md).

Mortal Expansion is a supernatural magic and technology mod for Minecraft. Explore haunted graveyards, bargain with spirits, uncover forbidden crafting knowledge, and build machines powered by ectoplasm. Haunt follows both players and places, bringing opportunities, disturbances, and increasingly dangerous supernatural activity.

## Explore, discover, and build

- **Haunted worlds:** Spiritwood Forests, Graveyards, named Cemetery and Spiritwell discoveries, and glowing spectral trails toward undiscovered sites.
- **Secrets of the Dead:** An upgradeable Book of Ectcetera with a connected research map. Whispers gradually reveal recipes; a Mortality Research Station encourages discoveries.
- **Life beyond death:** Named Graves, Spirit Journey, recoverable corpses, and Spiritwell recovery. Prepared naturalized Graves support cross-dimensional Grave Porting.
- **Spirits and creatures:** Trading Spirit Villagers, unfinished-business quests with automatic rewards, hostile spectral mobs, and flesh-built Mortal Golems.
- **Spectral equipment:** Bloodstone and Spectralite tools and armor, intangible Ghost and Haint Garb, combined Spectralite Haint armor, and mineral armor trims.
- **Ectoplasm technology:** Generators, Haunt and Haint Furnaces, purification and ectchantment machines, configurable pipes, and fluid tanks.
- **Control the haunting:** EMF Readers, Holy Salt, Spirit Torches, Purification Wards, opposing offering altars, and Dreamcatchers that intercept Haunt events.

Beneficial and negative Haunt events use separate pools. Raising Haunt can improve some systems while encouraging supernatural encounters; cleansing and protection offer different ways to manage it.

## Requirements and installation

The source targets **Minecraft 1.21.1**, **NeoForge 21.1.219**, **Java 21**, and **TerraBlender 1.21.1-4.1.0.8**. Install matching builds of Mortal Expansion and TerraBlender in the instance's `mods` folder. Multiplayer clients and the server need matching mod builds and dependencies.

This is an actively developed playtest build. `main` is the consolidated baseline; `dev` receives ongoing changes. A source update is not a published release. See [current status and remaining work](docs/RELEASE_STATUS.md) before preparing a public build.

Optional **JEI** support is built in: install JEI to browse machine processes and recipe guidance. Secret recipes remain visible with a Secrets of the Dead notice; their server unlock requirements still apply. No separate Mortal Integrations addon is needed. See [JEI integration](docs/JEI_INTEGRATION.md).

## Getting started

Craft a Book with Rotten Flesh to make the **Book of Ectcetera**. Add Bone, Raw Ectoplasm, then solid Purified Ectoplasm to upgrade it in order. Upgrades open subject areas; secret recipes must still be learned through Insight. The Mortality Research Station is the first secret and can be whispered to a player carrying even the starting book at Natural personal Haunt.

Use an EMF Reader to distinguish local chunk Haunt from personal Haunt. Its ordinary reading shows the chunk; hold right-click to scan yourself. The basic reader uses bulbs on the item; the Precise upgrade adds a decimal display.

See the [documentation index](docs/README.md) for recipes, construction requirements, protections, and testing commands.

## Build and contribute

With JDK 21 installed, use the included wrapper:

```text
gradlew.bat build
gradlew.bat runClient
gradlew.bat runGameTestServer
```

On Linux/macOS use `./gradlew` instead. Artifacts appear in `build/libs/`. Development normally targets `dev`; promotion to `main` is deliberate.

Items and blocks are registered in Java. Recipes, models, loot, and tags use Mojang/NeoForge data generators; commit providers and generated resources together. See [data generation](docs/DATA_GENERATION.md) and [development guidelines](AGENTS.md).

## Credits and licensing

Created by Howierux with ChatGPT assistance. The repository declares **All Rights Reserved**. Some art is temporary; provenance and prompts are retained in the documentation. Tapping audio uses vanilla Minecraft wood impacts without bundling game audio; see [audio provenance](docs/THIRD_PARTY_AUDIO.md).
