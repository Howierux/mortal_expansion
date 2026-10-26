# Fabric remaining-feature audit

This audit compares the root NeoForge reference, Fabric implementation, `Missing function` comments, catalog preview tooltips and port-status documents. Existing art/registry coverage is complete; that is not a measure of functional gameplay parity. No percentage of completion is inferred from class or item counts.

## Implemented foundation

Persistent personal/chunk Haunt, natural baselines and moon-weighted loaded-chunk simulation; the full catalog and native equipment/building defaults; both generators; fluid tanks/pipes; Haunt/Haint furnaces and fluid cooking; the Purification Station and Ward; both altars and world-Haunt rituals; Book/Darkhold upgrades and atlas, Secrets of the Dead enforcement, research and independent Haunt-event pools; live EMF readers, native terrain generation and loaded-world resource transformations; spectral combat, ten ME creature types, Spirit merchant expertise, barriers, Demon revelation, equipment/garb abilities, named discovery/tracks and Spirit Journey. See the individual Fabric slice documents for exact limits and verification.

## Remaining functional gaps

| Area | Still missing on Fabric | Important dependencies |
| --- | --- | --- |
| Book and research | Third-party automated crafting integration, non-native backpack knowledge/exposure, remaining content functionality described by the atlas | See [knowledge slice](FABRIC_KNOWLEDGE.md) for implemented progression |
| Haunt events and readers | Expertise services, hidden-site/powerful-Spirit revelations; follower/decoy enhancements and Dreamcatcher interception | Twenty-six selections, commands, Boons, garb prevention and owned-effect cleanup are functional; see [events](FABRIC_HAUNT_EVENTS.md) |
| World generation and discovery | Never-generated-site locating, named-destination travel permissions/costs, third-party biome compatibility | Twenty-block named discovery and ten-step trails are implemented; see [discovery](FABRIC_DISCOVERY.md) |
| Mobs and NPCs | Additional Spirit expertise services and quest types; extended encounter/protection interoperability | Native creatures/combat/trades exist; remaining ownership and encounter triggers need their own ports |
| Spirit Journey and travel | Cross-dimensional Grave Porting with fleshed-bone anchors/costs; dedicated multiplayer and death-mod validation | Corpse/journey/well recovery, greyscale, beams and failed-journey vanilla spawn are implemented; see [journey](FABRIC_SPIRIT_JOURNEY.md) |
| Equipment and magic damage | Third-party equipment/projectile/protection compatibility testing | Garb powers, natural prevention feedback, owned-effect cleanup, event-linked garb repair and tool abilities are implemented |
| Remaining blocks | Generated grave identities, dreamcatcher ownership/moon/sleep protection; third-party Fancifier renderer interoperability | Knowledge, entity/protection systems and specialized rendering |
| Consecration and barriers | Holy Water potion/brewing/blessings, special cauldron behavior, consecrated-area behavior, full Spiritwell placement/piston protection; salt and torch collision indexes are now implemented | Protection tracking, damage/phasing and native brewing |
| Darkhold sins | Seven sin unlocks/abilities and non-native backpack integration; basic exposure, personal overflow and Demon revelation/visibility are implemented | Book knowledge, expanded personal-Haunt rules and mobs |
| Integrations and release | Optional recipe-viewer integration, shaders/connected-texture compatibility, multiplayer/modpack validation, NeoForge save migration | Completed subsystem ports and explicit migration design |

Native items/blocks can have ordinary placement, armor, mining or bucket behavior while their special effects remain pending. The preview tooltip and slice documents distinguish those cases.

## Slices completed in this update

Spirit interactions/quests and graves, formation/encounters, and Ectchantment/Fancifier are implemented. See [Spirit interactions](FABRIC_SPIRIT_INTERACTIONS.md), [formation and encounters](FABRIC_FORMATION_ENCOUNTERS.md), and [remaining machines](FABRIC_FANCIFIER_ECTCHANTMENT.md). The generated recipe gap of twenty reference IDs is closed. Existing content art is reused; custom third-party opacity rendering remains an explicit compatibility boundary.

## Suggested next slices

1. **Dreamcatchers:** owner tracking, sleep protection, Haunt interception and symbolic moon display.
2. **Consecration:** Holy Water bottles, brewing, blessings, cauldrons and consecrated-area behavior.
3. **Grave Porting:** loaded cross-dimensional grave anchors, destination UI, consumed flesh/bone and personal Haunt cost.

Darkhold sin powers, advanced Spirit expertise/revelation services, recipe-viewer integration and third-party rendering/protection validation remain other bounded slices.
