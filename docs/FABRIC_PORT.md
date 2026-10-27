# Fabric port status

**Haunt foundation, the complete item/block appearance catalog, both generators, fluid tanks/pipes, Haunt furnaces, the Purification Station/Ward, both altars and world-Haunt rituals, book progression, research, EMF readers, native terrain generation, spectral combat and the ten ME creatures are implemented. Full gameplay parity is still pending.**

Reference baseline: NeoForge dev f080fdc36b994f3abcd2a0b2e0a0687c5130e545. Fabric development belongs in fabric/ on fabric-dev.

## Setup
- [x] Spectral damage, mobs, Spirit merchants and Demon revelation: [Fabric combat](FABRIC_COMBAT.md).
- [x] Live readers and native terrain: [EMF](FABRIC_EMF.md), [terrain](FABRIC_TERRAIN.md).
- [x] Book atlas, upgrades, learned recipes and research: [Fabric knowledge](FABRIC_KNOWLEDGE.md).
- [x] Preserve Git ancestry and NeoForge source reference.
- [x] Isolate the Fabric Gradle build; pin Java 21-compatible versions.
- [x] Preserve the mortal_expansion namespace and mod branding.
- [x] Provide common/client/datagen entry points; omit unused example mixins.
- [x] Separate Fabric CI and loader-specific artifact filenames.
- [x] Client startup and dedicated-server-side loading through four Fabric GameTests.
- [x] Altars and world rituals: [Fabric altars](FABRIC_ALTARS.md).
- [x] Furnaces, station and Ward: [Fabric purification](FABRIC_PURIFICATION.md).
- [x] Generators, fluid tanks and pipe functions: [Fabric machinery](FABRIC_MACHINES.md).
- [x] First gameplay feature port: [Haunt foundation](FABRIC_HAUNT_FOUNDATION.md).

## Porting sequence
1. Native item/block registration, artwork and defined gameplay interactions are implemented: 97 items, 60 blocks, native tags and loot.
2. Haunt persistence is implemented for fresh Fabric worlds. Discoveries, ownership and Spirit Journey are implemented; NeoForge save migration remains pending.
3. Event wiring, payloads, permissions and dedicated-server class separation.
4. Native biomes, features/structures and TerraBlender climate insertion are implemented; naming/discovery are implemented.
5. Mobs, combat AI, intangible damage, barriers and mob phasing are implemented. Player garb powers are implemented; pyramid/hut encounters and Wisp/golem formation are implemented.
6. Both generators, their inventories/screens, energy export, tanks and pipes are implemented. Haunt/Haint furnaces, the Purification Station and Ward are also functional. Altars and world rituals are implemented; research is implemented; the Ectchantment Machine and Fancifier are implemented.
7. Book progression, defined Haunt events, seven sin powers, potions and Grave Porting are implemented. Proposal-only Spirit service/revelation effects remain pending.
8. Rendering/models/mixins, shaders and connected textures.
9. Gameplay/migration tests, recipe-viewer integration and parity audit.

## Release gate

A scaffold build proves tooling, not gameplay parity. Validate client/server loading, resources, gameplay/protection hooks, persistence/networking, dependencies and migration; then playtest before creating/promoting stable fabric. Use fresh test worlds until content and migration are implemented.

## Setup validation

The isolated Fabric build passes on Java 21. Native jar metadata, all three Kotlin entry points, Java 21 bytecode, the packaged icon, absence of example/NeoForge mixins, development tasks and excluded caches/logs were checked. Workflow routing is configured; hosted CI results are separate from local build validation. The Haunt update passes four native Fabric GameTests and a client startup smoke check at 30% volume; real multiplayer synchronization/playtesting remains pending.

## Current scope

See [Fabric Haunt foundation](FABRIC_HAUNT_FOUNDATION.md) for storage keys, retained rules, commands, validation and explicit dependent systems not yet ported. See [Fabric content catalog](FABRIC_CONTENT_CATALOG.md) for item/block registration, reused artwork and explicit limitations. Both original biomes now generate through TerraBlender.

The current build passes 70 native Fabric GameTests. Real integrated-server/client checks opened both generator screens, verified live status and fluid synchronization, and confirmed 30% master volume. NeoForge save migration and third-party modpack interoperability playtests remain pending.

The purification update passed station/furnace screen checks and a two-launch save/restart check: exact stored station resources survived, and a distant Ward restored chunk tickets independently of the player. One focused cooking mixin returns the three fluid buckets because native smelting has no input-remainder event.

The remaining-feature audit and proposed next slices are in [Fabric remaining features](FABRIC_REMAINING_FEATURES.md). Altar validation includes native inventory/remainder handling, fractional pulses, ordered ritual replay, dimension limits, both client screens and a two-launch ritual/future-chunk check.

The knowledge slice adds native saved player knowledge, recipe gates including vanilla Crafters, book upgrade/remainder serializers, the atlas, Research Station and gradual Insight. See [Fabric knowledge](FABRIC_KNOWLEDGE.md) for verification and explicit Darkhold/event limitations.

The reader/terrain slice preserves server-authoritative hundredths, uses transient item samples and ports native generated biomes/features/structures. Spiritwood sapling moss, daily Graveyard weathering and tiered Bloodstone conversion are implemented. TerraBlender Fabric is required.

The combat slice adds ten native creatures and spawn eggs, entity loot, Spirit expertise trades, phased boss combat, Demon variants/visibility and durable projectile provenance. Integrated client checks at 30% volume verified model loading, owner knowledge synchronization and merchant menus. Player garb powers, Spirit quests and construction/encounter hooks are now implemented.

The equipment slice ports garb phasing/jumps and native wall air, armor protection, Bloodstone scaling, pickaxe repair, face-based/connected shovel mining, tree axes, spectral growth, Haunted Farmland and Scepter charges. Twenty equipment recipes use native datagen. Seven new gameplay tests and an integrated client movement/render check passed at 30% volume. Natural negative-haunting prevention still needs the negative-event dispatcher. See [Fabric equipment](FABRIC_EQUIPMENT.md).

Three follow-up slices add independent Haunt pools/commands/garb feedback, persistent named discovery with visible tracks, and Spirit Journey with actual death-drop escrow, corpses, private beams, greyscale and safe return/failure rules. Nine new native tests exercise these integrations, including keepInventory and Vanishing. A two-launch client check at 30% volume verified persisted journey items/corpse and real GUI recovery. See [Haunt events](FABRIC_HAUNT_EVENTS.md), [discovery](FABRIC_DISCOVERY.md) and [Spirit Journey](FABRIC_SPIRIT_JOURNEY.md). Spirit Commission uses quests; Grave Porting, Dreamcatchers and Holy Water/consecration are now implemented in the final gameplay slices.

Three more slices add Spirit interaction menus/quests and grave workstations, Wisp/golem formation and loaded pyramid/hut encounters, then Ectchantment and functional native-block Fancifier opacity. See [Spirit interactions](FABRIC_SPIRIT_INTERACTIONS.md), [formation/encounters](FABRIC_FORMATION_ENCOUNTERS.md) and [Fancifier/Ectchantment](FABRIC_FANCIFIER_ECTCHANTMENT.md). Client slider/screens/rendering and two-launch persistence passed at 30% volume; optional renderer interoperability remains pending.

The remaining defined gameplay slices add [Dreamcatchers](FABRIC_DREAMCATCHERS.md), [Holy Water/consecration and spirit circuits](FABRIC_CONSECRATION.md), [Grave Porting](FABRIC_GRAVE_PORTING.md), [all seven Darkhold powers](FABRIC_DARKHOLD_POWERS.md) and [optional JEI](FABRIC_JEI.md). Native gameplay tests and two-launch client checks cover these features, including a client without JEI followed by one with it. The remaining audit now separates unfinished NeoForge design proposals from provider-specific compatibility and release validation. This is still a development branch, with no NeoForge save migration claim.
