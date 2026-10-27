# Fabric remaining-feature audit

The defined gameplay port slices are implemented for fresh Fabric 1.21.1 worlds. This does not mean every proposal in the planning documents exists, nor that arbitrary NeoForge worlds or third-party mods are compatible.

## Completed gameplay slices

Haunt storage/simulation and world rituals; all registered item/block art; terrain and resource transformations; EMF readers; spectral mobs/combat, equipment and garb; generators, tanks/pipes, furnaces, purification and altars; book research/recipe gates; independent Haunt events; named discovery/tracks; Spirit Journey; Spirit quests and grave conversion; Wisp/golem formation and structure encounters; Ectchantment and native functional-block Fancifier opacity; Dreamcatchers; Holy Water/cauldrons/consecrated enclosures/spirit circuits; cross-dimensional Grave Porting; all seven Darkhold powers; optional JEI machine/altar/secret recipe pages.

See [Dreamcatchers](FABRIC_DREAMCATCHERS.md), [consecration](FABRIC_CONSECRATION.md), [Grave Porting](FABRIC_GRAVE_PORTING.md), [Darkhold powers](FABRIC_DARKHOLD_POWERS.md), [JEI](FABRIC_JEI.md) and the earlier subsystem documents for exact rules and validation. All 90 reference crafting recipe IDs have generated Fabric equivalents.

## Planning features also unfinished in the NeoForge reference

These are future designs rather than skipped ports:

- Spirit Recognition expertise services and Veil Revelation hidden-site/powerful-Spirit encounters. Their command selections remain explicitly unavailable, with Missing function comments matching the reference.
- Visible non-hostile followers, dedicated decoy tracks, hidden inscription revelations and future haunt-sensitive machine interference hooks. Existing footsteps/apparitions and current machine interference remain functional.
- Locating never-generated sites without forced generation, and teleporting to named discoveries as a separate network. Grave Porting has its own implemented owned-anchor rules.
- Additional Spirit expertise actions, quest types and worker jobs beyond current trading and kill quests.

## Compatibility and release work

- NeoForge saved-world migration, including old fading proxy blocks. Use fresh Fabric worlds; registry names alone do not ensure migration.
- Dedicated multiplayer/modpack, third-party protection/teleport/death mods and unusual equipment/projectiles. Native claim cancellation is tested; this is not a guarantee for every mod.
- Fancifier custom Fabric Renderer API/Iris/connected-texture renderers and shader packs. The optional Sodium 0.6.13 chunk hook is checked; custom renderer appearance and combinations still need provider-specific validation.
- Backpacks without native container/bundle components or Fabric ItemStorage; third-party automated crafting ownership; other recipe viewers and biome interoperability.

These need named target mods/test environments or new feature designs. They are not marked implemented by a successful build.

The [current dev parity audit](FABRIC_PARITY_AUDIT.md) records the reference SHA, corrections and 79-test validation.
