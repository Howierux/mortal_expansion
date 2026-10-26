# Fabric remaining-feature audit

This audit compares the root NeoForge reference, Fabric implementation, `Missing function` comments, catalog preview tooltips and port-status documents. Existing art/registry coverage is complete; that is not a measure of functional gameplay parity. No percentage of completion is inferred from class or item counts.

## Implemented foundation

Persistent personal/chunk Haunt, natural baselines and moon-weighted loaded-chunk simulation; the full catalog and native equipment/building defaults; both generators; fluid tanks/pipes; Haunt/Haint furnaces and fluid cooking; the Purification Station and Ward; both altars and world-Haunt rituals; Book/Darkhold upgrades and atlas, Secrets of the Dead enforcement, research and a limited positive-event pool. See the individual Fabric slice documents for exact limits and verification.

## Remaining functional gaps

| Area | Still missing on Fabric | Important dependencies |
| --- | --- | --- |
| Book and research | Third-party automated crafting integration, backpack knowledge/exposure, remaining content functionality described by the atlas | See [knowledge slice](FABRIC_KNOWLEDGE.md) for implemented progression |
| Haunt events and readers | Full event selection/commands beyond the Insight/altar benefit pool, boon visitation, equipment prevention notifications, live basic/precise EMF readings | Knowledge and owner-only synchronization; some events need mobs/discovery |
| World generation and discovery | Custom biomes/structures, Spiritwood tree growth and Grave Moss generation, cemetery/Spiritwell discovery/naming, spectral footprints, stone/Bloodstone weathering | Configured features/structures and per-player discoveries |
| Mobs and NPCs | Ghosts, trading/quest Spirits, Elementals, Mummies/Pharaohls, Mortal Golems and Demon variants/AI/spawn rules | Entity registration/rendering, damage/phasing, NPC and quest storage |
| Spirit Journey and travel | Corpses/death transitions, GUI respawn choices, Spiritwell beams/return, Grave Porting costs and eligibility, greyscale | Ownership/discoveries, safe respawn and travel networking |
| Equipment and magic damage | Garb intangibility/jumps/protection, movement and drowning rules, demi-tangible enchantment/projectiles, Haunt-scaled tool abilities and repairs, Scepter charges | Native events and focused damage/movement hooks; above-Eldritch progression |
| Remaining blocks | Ectchantment Machine, Fancifier/fading functional blocks, grave inscriptions/naturalization, dreamcatcher ownership/moon/sleep protection, Wisp transformations and golem frames | Knowledge, entity/protection systems and specialized rendering |
| Consecration and barriers | Holy Water potion/brewing/blessings, special cauldron behavior, Holy Salt/Spirit Torch signals/barriers and cleanup | Protection tracking, damage/phasing and native brewing |
| Darkhold sins | Exposure in inventories/backpacks, personal overflow and seven sin unlocks/abilities, Demon visibility/secret gating | Book knowledge, expanded personal-Haunt rules and mobs |
| Integrations and release | Optional recipe-viewer integration, shaders/connected-texture compatibility, multiplayer/modpack validation, NeoForge save migration | Completed subsystem ports and explicit migration design |

Native items/blocks can have ordinary placement, armor, mining or bucket behavior while their special effects remain pending. The preview tooltip and slice documents distinguish those cases.

## Slice selected in this update

**Book upgrades → eligible Insight whispers → learned recipes → research bonuses.** The atlas, all book upgrades, permanent knowledge and actual crafting restrictions are implemented. Both altar benefit hooks now have a small positive-event dispatcher. See [Fabric knowledge](FABRIC_KNOWLEDGE.md).

## Suggested next slices

1. **Live Haunt observation and events:** basic/precise EMF readings and owner synchronization, expansion beyond the implemented Insight/altar benefit pool and forced-event commands. Dependent encounters should remain clearly deferred until their mobs exist.
2. **Spiritwood/cemetery exploration:** sapling growth, native worldgen and discoverable named anchors; add footprints once destinations really exist.
3. **Spectral combat:** entity/damage registration, one ghost encounter and demi-tangible/intangible rules before expanding mobs, garb and Spirit Journey.

The Ectchantment Machine is another bounded machinery port, but it does not on its own resolve missing knowledge/progression. New slices should retain native Fabric APIs, generated data, loaded-chunk safeguards and explicit save/network boundaries.
