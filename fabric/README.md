# Mortal Expansion: Fabric development

**The defined gameplay slices are ported for fresh Fabric worlds: Haunt, equipment and spectral combat, mobs and Spirit quests, terrain/discovery, Spirit Journey/Grave Porting, book research and all seven Darkhold powers, Dreamcatchers, Holy Water/consecration, machinery and native-block Fancifier opacity. Optional JEI pages are included without requiring JEI.** The development branch still needs provider-specific compatibility and release validation. Original NeoForge source remains at the repository root as the reference.

Develop on **fabric-dev**. The stable **fabric** branch is reserved until a validated release exists.

## Build

Open this fabric/ directory as the Gradle project in your IDE, with JDK 21 for the Gradle JVM/toolchain.

~~~text
cd fabric
gradlew.bat build
~~~

Linux/macOS: run ./gradlew build inside fabric/. Output: build/libs/mortal_expansion-fabric-1.0.0-fabric-dev.jar. Use fresh test worlds: NeoForge save migration is not ported.

Other development tasks: runClient, runServer, runDatagen. Before launching a client, set soundCategory_master:0.3 in fabric/run/options.txt, preserving other options. Server launches require normal Minecraft EULA acceptance.

## Toolchain

Minecraft 1.21.1; Fabric Loader 0.20.1; Fabric API 0.116.17+1.21.1; Java 21; Gradle 9.8.2; Loom 1.17.21; official Mojang mappings. Existing Kotlin bootstrap uses Kotlin 2.4.21 and Fabric Language Kotlin 1.14.2+kotlin.2.4.21. Java gameplay can be ported without changing languages.

Namespace: mortal_expansion. The artifact filename includes -fabric. Datagen supplies Haunt biome tags, native block/item tags, loot tables and native machinery, knowledge and fluid-cooking recipes. Mod metadata retains All Rights Reserved and original branding; template provenance is in TEMPLATE_LICENSE.txt.

The build runs 79 isolated Fabric GameTests automatically. These tests are not packaged in the gameplay jar. See the [Haunt foundation](../docs/FABRIC_HAUNT_FOUNDATION.md) for commands and exact scope.

See [branch policy](../docs/LOADER_BRANCHES.md) and [port status](../docs/FABRIC_PORT.md). TerraBlender Fabric is required. JEI is optional; `gradlew.bat -PwithJei runClient` adds its development runtime.

See [content catalog scope](../docs/FABRIC_CONTENT_CATALOG.md) for reused art, baseline functionality and pending systems.

[Fabric machinery](../docs/FABRIC_MACHINES.md): generator operation, tank capacities, pipe side controls and transfer rates. Team Reborn Energy 4.1.0 is bundled for standard Fabric energy interoperability; fluid and item automation use Fabric Transfer API.

[Fabric purification](../docs/FABRIC_PURIFICATION.md): furnace timing and fuel rules, station resources/automation, Ward chunk tickets and save behavior.

[Altars and rituals](../docs/FABRIC_ALTARS.md) provide local cleansing/corruption and repeatable Overworld-wide shifts. See the [remaining-feature audit](../docs/FABRIC_REMAINING_FEATURES.md) for subsequent gameplay slices.

[Book and research](../docs/FABRIC_KNOWLEDGE.md): atlas/upgrades, gradual Insight, persistent recipe knowledge and research contributions. Darkhold exposure and all seven sin powers are implemented; proposal-only expertise/revelation events remain future work.

[EMF readers](../docs/FABRIC_EMF.md) and [terrain generation](../docs/FABRIC_TERRAIN.md): live device readings, two native biomes, Spiritwood growth, Bloodstone, Spiritwell anchors and cemetery structures. TerraBlender **Fabric** 1.21.1-4.1.0.8 is now a required external dependency. Use a fresh world. Named discovery, Spirit Journey, loaded Grave Porting and custom mobs are implemented.

[Spectral combat and creatures](../docs/FABRIC_COMBAT.md): intangible defenses/phasing, Demi-Tangible enchantment, saved projectile attribution, native mob AI/loot/rendering, Spirit expertise trades and Demon revelation. Pyramid/hut encounters, Wisp conversions and golem construction are implemented.

See [Dreamcatchers](../docs/FABRIC_DREAMCATCHERS.md), [consecration and spirit circuits](../docs/FABRIC_CONSECRATION.md), [Grave Porting](../docs/FABRIC_GRAVE_PORTING.md), [Darkhold powers](../docs/FABRIC_DARKHOLD_POWERS.md), and [optional JEI](../docs/FABRIC_JEI.md). The [remaining audit](../docs/FABRIC_REMAINING_FEATURES.md) distinguishes unfinished reference designs from compatibility/release work.

See the [NeoForge/Fabric parity audit](../docs/FABRIC_PARITY_AUDIT.md) and run `python fabric/tools/compare_neoforge.py` after datagen to check current reference resource contracts.
