# Fabric remaining-feature audit

This audit compares the root NeoForge reference, Fabric implementation, `Missing function` comments, catalog preview tooltips and port-status documents. Existing art/registry coverage is complete; that is not a measure of functional gameplay parity. No percentage of completion is inferred from class or item counts.

## Implemented foundation

Persistent personal/chunk Haunt, natural baselines and moon-weighted loaded-chunk simulation; the full catalog and native equipment/building defaults; both generators; fluid tanks/pipes; Haunt/Haint furnaces and fluid cooking; the Purification Station and Ward; both altars and world-Haunt rituals. See the individual Fabric slice documents for exact limits and verification.

## Remaining functional gaps

| Area | Still missing on Fabric | Important dependencies |
| --- | --- | --- |
| Book and research | Interactive Book/Darkhold screens, upgrades, learned Secrets, recipe enforcement, Spectral Insight recommendations, Mortality Research bonuses | Player knowledge persistence and networking; recipe/datagen integration |
| Haunt events and readers | Event selection/commands, secret reveal events, boon visitation, altar blessing/gift dispatch, equipment prevention notifications, live basic/precise EMF readings | Knowledge and owner-only synchronization; some events need mobs/discovery |
| World generation and discovery | Custom biomes/structures, Spiritwood tree growth and Grave Moss generation, cemetery/Spiritwell discovery/naming, spectral footprints, stone/Bloodstone weathering | Configured features/structures and per-player discoveries |
| Mobs and NPCs | Ghosts, trading/quest Spirits, Elementals, Mummies/Pharaohls, Mortal Golems and Demon variants/AI/spawn rules | Entity registration/rendering, damage/phasing, NPC and quest storage |
| Spirit Journey and travel | Corpses/death transitions, GUI respawn choices, Spiritwell beams/return, Grave Porting costs and eligibility, greyscale | Ownership/discoveries, safe respawn and travel networking |
| Equipment and magic damage | Garb intangibility/jumps/protection, movement and drowning rules, demi-tangible enchantment/projectiles, Haunt-scaled tool abilities and repairs, Scepter charges | Native events and focused damage/movement hooks; above-Eldritch progression |
| Remaining blocks | Ectchantment Machine, research, Fancifier/fading functional blocks, grave inscriptions/naturalization, dreamcatcher ownership/moon/sleep protection, Wisp transformations and golem frames | Knowledge, entity/protection systems and specialized rendering |
| Consecration and barriers | Holy Water potion/brewing/blessings, special cauldron behavior, Holy Salt/Spirit Torch signals/barriers and cleanup | Protection tracking, damage/phasing and native brewing |
| Darkhold sins | Exposure in inventories/backpacks, personal overflow and seven sin unlocks/abilities, Demon visibility/secret gating | Book knowledge, expanded personal-Haunt rules and mobs |
| Integrations and release | Optional recipe-viewer integration, shaders/connected-texture compatibility, multiplayer/modpack validation, NeoForge save migration | Completed subsystem ports and explicit migration design |

Native items/blocks can have ordinary placement, armor, mining or bucket behavior while their special effects remain pending. The preview tooltip and slice documents distinguish those cases.

## Slice selected in this update

**Offerings → stored altar burn → local Haunt control → Ritual Star world shifts.** This uses the existing Haunt, native inventory/menu and persistence foundations without claiming the unported book or mob systems exist. It delivers repeatable player interactions, both inverse altar functions, clear UI feedback and a saved-world effect. Full details: [Fabric altars](FABRIC_ALTARS.md).

Altar crafting remains withheld pending Secrets enforcement. The Ritual Star's public crafting recipe is available. Positive offering hooks are implemented but explicitly wait for the Haunt-event dispatcher.

## Suggested next slices

1. **Knowledge and first research loop:** real book upgrades/screens, persisted knowledge, first secret/research station, recipe gating and an Insight reveal path. This makes the already ported machinery reachable through intended progression.
2. **Live Haunt observation and events:** basic/precise EMF readings and owner synchronization, a small fully functional event pool, forced-event commands and altar blessing/gift integration. Dependent encounters should remain clearly deferred until their mobs exist.
3. **Spiritwood/cemetery exploration:** sapling growth, native worldgen and discoverable named anchors; add footprints once destinations really exist.
4. **Spectral combat:** entity/damage registration, one ghost encounter and demi-tangible/intangible rules before expanding mobs, garb and Spirit Journey.

The Ectchantment Machine is another bounded machinery port, but it does not on its own resolve missing knowledge/progression. New slices should retain native Fabric APIs, generated data, loaded-chunk safeguards and explicit save/network boundaries.
