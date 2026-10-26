# Fabric remaining-feature audit

This audit compares the root NeoForge reference, Fabric implementation, `Missing function` comments, catalog preview tooltips and port-status documents. Existing art/registry coverage is complete; that is not a measure of functional gameplay parity. No percentage of completion is inferred from class or item counts.

## Implemented foundation

Persistent personal/chunk Haunt, natural baselines and moon-weighted loaded-chunk simulation; the full catalog and native equipment/building defaults; both generators; fluid tanks/pipes; Haunt/Haint furnaces and fluid cooking; the Purification Station and Ward; both altars and world-Haunt rituals; Book/Darkhold upgrades and atlas, Secrets of the Dead enforcement, research and independent Haunt-event pools; live EMF readers, native terrain generation and loaded-world resource transformations; spectral combat, ten ME creature types, Spirit merchant expertise, barriers, Demon revelation, equipment/garb abilities, named discovery/tracks and Spirit Journey. See the individual Fabric slice documents for exact limits and verification.

## Remaining functional gaps

| Area | Still missing on Fabric | Important dependencies |
| --- | --- | --- |
| Book and research | Third-party automated crafting integration, non-native backpack knowledge/exposure, remaining content functionality described by the atlas | See [knowledge slice](FABRIC_KNOWLEDGE.md) for implemented progression |
| Haunt events and readers | Quest commissions, expertise services, hidden-site/powerful-Spirit revelations; follower/decoy enhancements and Dreamcatcher interception | Twenty-six selections, commands, Boons, garb prevention and owned-effect cleanup are functional; see [events](FABRIC_HAUNT_EVENTS.md) |
| World generation and discovery | Never-generated-site locating, named-destination travel permissions/costs, third-party biome compatibility | Twenty-block named discovery and ten-step trails are implemented; see [discovery](FABRIC_DISCOVERY.md) |
| Mobs and NPCs | Spirit quest/action menus and grave-created villager conversion; golem construction; Pharaohl pyramid encounters; Haint event encounters; vanilla population rebalance | Native creatures/combat/trades exist; remaining ownership and encounter triggers need their own ports |
| Spirit Journey and travel | Cross-dimensional Grave Porting with fleshed-bone anchors/costs; dedicated multiplayer and death-mod validation | Corpse/journey/well recovery, greyscale, beams and failed-journey vanilla spawn are implemented; see [journey](FABRIC_SPIRIT_JOURNEY.md) |
| Equipment and magic damage | Third-party equipment/projectile/protection compatibility testing | Garb powers, natural prevention feedback, owned-effect cleanup, event-linked garb repair and tool abilities are implemented |
| Remaining blocks | Ectchantment Machine, Fancifier/fading functional blocks, flower-assisted grave naturalization and generated grave identities, dreamcatcher ownership/moon/sleep protection, Wisp transformations and golem frames | Knowledge, entity/protection systems and specialized rendering |
| Consecration and barriers | Holy Water potion/brewing/blessings, special cauldron behavior, consecrated-area behavior, full Spiritwell placement/piston protection; salt and torch collision indexes are now implemented | Protection tracking, damage/phasing and native brewing |
| Darkhold sins | Seven sin unlocks/abilities and non-native backpack integration; basic exposure, personal overflow and Demon revelation/visibility are implemented | Book knowledge, expanded personal-Haunt rules and mobs |
| Integrations and release | Optional recipe-viewer integration, shaders/connected-texture compatibility, multiplayer/modpack validation, NeoForge save migration | Completed subsystem ports and explicit migration design |

Native items/blocks can have ordinary placement, armor, mining or bucket behavior while their special effects remain pending. The preview tooltip and slice documents distinguish those cases.

## Slices completed in this update

Haunt events/protection, named discovery/spectral tracks, and Spirit Journey are implemented. The current build passes 46 native gameplay tests, with a two-launch client persistence/GUI/render check at 30% volume. Exact dependent gaps remain listed above rather than being counted as working features.

## Suggested next slices

1. **Spirit interactions and graves:** unfinished-business quests, action menus, grave-created villager conversion and flower naturalization.
2. **Formation and encounters:** Wisp o' Lantern transformations, flesh/golem formation and remaining encounter triggers.
3. **Remaining machines:** Ectchantment Machine and Fancifier, followed by functional-block opacity interoperability.

Grave Porting, Darkhold sin powers, Dreamcatchers and Holy Water/consecration remain other bounded slices. Twenty reference recipe IDs are still absent: sixteen Spiritwood/Purified Spiritwood building recipes, Spirit Torch, Fancifier, Ectchantment Machine and diamond-from-Bloodstone. The Grave recipe was added for this journey slice.
