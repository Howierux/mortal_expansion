# Fabric port status

**First gameplay slice implemented: personal/chunk Haunt persistence, commands, kill gains, leakage and loaded-chunk simulation. Full gameplay parity is still pending.**

Reference baseline: NeoForge dev f080fdc36b994f3abcd2a0b2e0a0687c5130e545. Fabric development belongs in fabric/ on fabric-dev.

## Setup
- [x] Preserve Git ancestry and NeoForge source reference.
- [x] Isolate the Fabric Gradle build; pin Java 21-compatible versions.
- [x] Preserve the mortal_expansion namespace and mod branding.
- [x] Provide common/client/datagen entry points; omit unused example mixins.
- [x] Separate Fabric CI and loader-specific artifact filenames.
- [x] Client startup and dedicated-server-side loading through four Fabric GameTests.
- [x] First gameplay feature port: [Haunt foundation](FABRIC_HAUNT_FOUNDATION.md).

## Porting sequence
1. Native item/block registration, resource/data providers and tags.
2. Haunt persistence is implemented for fresh Fabric worlds. Knowledge, discoveries, ownership, Spirit Journey and NeoForge save migration remain pending.
3. Event wiring, payloads, permissions and dedicated-server class separation.
4. World generation/structures and Fabric dependencies such as TerraBlender.
5. Mobs, AI, damage, barriers, phasing and equipment.
6. Inventories/screens, machines, fluid and energy handling.
7. Book progression, haunt events, sin secrets, potions, quests and travel.
8. Rendering/models/mixins, shaders and connected textures.
9. Gameplay/migration tests, recipe-viewer integration and parity audit.

## Release gate

A scaffold build proves tooling, not gameplay parity. Validate client/server loading, resources, gameplay/protection hooks, persistence/networking, dependencies and migration; then playtest before creating/promoting stable fabric. Use fresh test worlds until content and migration are implemented.

## Setup validation

The isolated Fabric build passes on Java 21. Native jar metadata, all three Kotlin entry points, Java 21 bytecode, the packaged icon, absence of example/NeoForge mixins, development tasks and excluded caches/logs were checked. Workflow routing is configured; hosted CI results are separate from local build validation. The Haunt update passes four native Fabric GameTests and a client startup smoke check at 30% volume; real multiplayer synchronization/playtesting remains pending.

## Current scope

See [Fabric Haunt foundation](FABRIC_HAUNT_FOUNDATION.md) for storage keys, retained rules, commands, validation and explicit dependent systems not yet ported. There are no Fabric item/block registrations or original mod biomes yet.
