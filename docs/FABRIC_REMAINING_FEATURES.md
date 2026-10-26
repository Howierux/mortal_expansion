# Fabric remaining-feature audit

This audit compares the root NeoForge reference, Fabric implementation, `Missing function` comments, catalog preview tooltips and port-status documents. Existing art/registry coverage is complete; that is not a measure of functional gameplay parity. No percentage of completion is inferred from class or item counts.

## Implemented foundation

Persistent personal/chunk Haunt, natural baselines and moon-weighted loaded-chunk simulation; the full catalog and native equipment/building defaults; both generators; fluid tanks/pipes; Haunt/Haint furnaces and fluid cooking; the Purification Station and Ward; both altars and world-Haunt rituals; Book/Darkhold upgrades and atlas, Secrets of the Dead enforcement, research and a limited positive-event pool; live EMF readers, native terrain generation and loaded-world resource transformations; spectral combat, ten ME creature types, Spirit merchant expertise, barriers and Demon revelation. See the individual Fabric slice documents for exact limits and verification.

## Remaining functional gaps

| Area | Still missing on Fabric | Important dependencies |
| --- | --- | --- |
| Book and research | Third-party automated crafting integration, non-native backpack knowledge/exposure, remaining content functionality described by the atlas | See [knowledge slice](FABRIC_KNOWLEDGE.md) for implemented progression |
| Haunt events and readers | Full event selection/commands beyond the Insight/altar benefit pool, boon visitation, equipment prevention notifications | Knowledge and owner-only synchronization; some events need mobs/discovery |
| World generation and discovery | Cemetery/Spiritwell discovery and naming, spectral footprints, protection/travel hooks; third-party biome compatibility | Native terrain is implemented; per-player discoveries remain pending |
| Mobs and NPCs | Spirit quest/action menus and grave-created villager conversion; golem construction; Pharaohl pyramid encounters; Haint event encounters; vanilla population rebalance | Native creatures/combat/trades exist; remaining ownership and encounter triggers need their own ports |
| Spirit Journey and travel | Corpses/death transitions, GUI respawn choices, Spiritwell beams/return, Grave Porting costs and eligibility, greyscale | Ownership/discoveries, safe respawn and travel networking |
| Equipment and magic damage | Garb intangibility/jumps/haunting prevention, wall-air and drowning rules, Haunt-scaled tool abilities and repairs, Scepter charges | Native events and focused damage/movement hooks; above-Eldritch progression |
| Remaining blocks | Ectchantment Machine, Fancifier/fading functional blocks, grave inscriptions/naturalization, dreamcatcher ownership/moon/sleep protection, Wisp transformations and golem frames | Knowledge, entity/protection systems and specialized rendering |
| Consecration and barriers | Holy Water potion/brewing/blessings, special cauldron behavior, consecrated-area behavior; salt and torch collision indexes are now implemented | Protection tracking, damage/phasing and native brewing |
| Darkhold sins | Seven sin unlocks/abilities and non-native backpack integration; basic exposure, personal overflow and Demon revelation/visibility are implemented | Book knowledge, expanded personal-Haunt rules and mobs |
| Integrations and release | Optional recipe-viewer integration, shaders/connected-texture compatibility, multiplayer/modpack validation, NeoForge save migration | Completed subsystem ports and explicit migration design |

Native items/blocks can have ordinary placement, armor, mining or bucket behavior while their special effects remain pending. The preview tooltip and slice documents distinguish those cases.

## Slice selected in this update

**Spectral combat and creatures.** Ten ME mobs, natural/explicit spawn roles, native damage/enchantment/projectile rules, loaded-only barriers, Spirit expertise trading and the first Demon revelation are implemented. See [Fabric combat](FABRIC_COMBAT.md). Garb powers, Spirit quests/conversion, golem formation and travel remain separate features.

## Suggested next slices

1. **More Haunt events:** expand beyond the implemented Insight/altar benefit pool, add forced-event commands and prevention feedback. Dependent encounters need their mobs.
2. **Discovery and exploration:** persist named cemetery/Spiritwell discoveries and add the footprint trail using generated destinations.
3. **Garb and Spirit Journey:** build player phasing/breathing and event protection on the shared damage/collision rules, then corpses and safe travel.

The Ectchantment Machine is another bounded machinery port, but it does not on its own resolve missing knowledge/progression. New slices should retain native Fabric APIs, generated data, loaded-chunk safeguards and explicit save/network boundaries.
