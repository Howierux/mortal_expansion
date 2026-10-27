# Fabric / NeoForge dev parity audit

Compared on 2026-10-27 against fetched NeoForge `dev` **f080fdc36b994f3abcd2a0b2e0a0687c5130e545**. Starting Fabric `fabric-dev` was **8af036e38153914d4575e69dc19c480883b8d16a**. NeoForge has no newer gameplay changes than the port reference, but this audit found and corrected port omissions. Only Fabric sources/resources and Fabric documentation change; NeoForge main/dev are unchanged.

## Corrections

- Preserve the guaranteed raw ectoplasm drop from vanilla Ghasts, bone-block drops from unformed Mortal Golems, current reference slab loot, and Wisp explosion survival rules.
- Restore public wood, gem, enchanting, garb, damage and biome tag contracts. Bloodstone is a diamond-equivalent gem. Native generated vanilla recipes accept it; all-Bloodstone gear patterns still produce Bloodstone equipment. Fabric ingredient differences protect the nine diamond gear recipes. Vanilla diamond-block compression remains compatible, as in NeoForge.
- Restore Overworld Wither Skeleton/Ghast population entries. Keep the reference rule that only Demons above Demonic Haunt bypass daylight light checks.
- Restore default Ghost/Haint dye components, canonical `/mortalexpansion haunt effect <player> <effect|random>` command behavior, operator cooldown safety and targeted Ghost behavior.
- Use the clicked third when placing graves. Consume owned non-natural graves and add one personal Haunt after successful normal/Journey respawns; preserve natural graves and failed-Journey bed/spawn behavior.
- Index pre-existing/generated Spiritwell block entities when their chunks load.
- Port the optional Sodium 0.6 chunk suppression hook so fancified blocks do not also retain an opaque chunk-rendered copy. No required Sodium dependency is added.

## Coverage and validation

`fabric/tools/compare_neoforge.py` checks the fetched source reference, native registry/model coverage, all **90 ME recipe definitions** (normalizing only representation defaults/categories), every reference public tag member, and **188 texture/sound/shader assets**. All **97 item** and **60 block** catalog fields match. The generated Fabric pack additionally supplies **31 native vanilla diamond recipe overrides**, replacing NeoForge's loader-provided shared-tag behavior. Text line endings are ignored; image/audio bytes are preserved.

The native Java 21 build and **79 required Fabric GameTests** pass. Nine added tests cover actual death/respawn and single-use/natural graves, Journey corpse retention, clicked grave thirds, native death/loot, existing-well indexing, commands, diamond crafting uniqueness, spawn tables and enchantment/damage tags. Biome fixtures now restore neighboring biome samples to avoid test-order pollution.

Native datagen and the resource comparison pass. Client checks use 30% master volume. The Sodium 0.6.13 Fabric check exercises real chunk rendering and fancified glass/chest/redstone synchronization; runtime-only probes and optional test jars are excluded from the gameplay artifact.

## Limits of this result

The defined current gameplay port is aligned with the checked NeoForge dev reference. A resource comparison and passing tests are not a proof of every possible world, multiplayer interaction or modpack combination.

NeoForge save migration, arbitrary protection/backpack/projectile mods, custom Fabric Renderer API models, Iris/shaders and connected-texture providers still require targeted compatibility work/playtesting. The Sodium check covers its native block tessellation hook, not every Sodium/renderer add-on combination. Fresh Fabric worlds remain required.

Spirit Recognition services, Veil Revelation encounters, dedicated followers/decoys, hidden inscription revelations and future named-discovery teleportation are unfinished in NeoForge too; they remain designs rather than missing Fabric implementations. See [remaining features](FABRIC_REMAINING_FEATURES.md).

## Repeating the comparison

Fetch the current NeoForge dev ref into `origin/dev`, confirm root `src` matches it, then run the Fabric wrapper's `runDatagen` and `build` tasks separately. Run `python fabric/tools/compare_neoforge.py`. A future changed reference should fail catalog, recipe, media or public-tag checks until reviewed. Review behavioral changes separately; the script deliberately makes no source-class-count parity claim.
