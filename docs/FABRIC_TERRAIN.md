# Fabric terrain and resource generation

This ports the current coordinated cemetery and world-generation rules to Fabric 1.21.1. Native registry bootstraps generate both biomes, configured/placed features and the cemetery structure/set. The two biomes keep the reference's climate-independent atmosphere, vanilla terrain decoration and ordinary vanilla spawn lists. Mortal Expansion creatures and dynamic spawn requirements are now implemented by the [combat slice](FABRIC_COMBAT.md).

## Natural Overworld generation

TerraBlender Fabric 1.21.1-4.1.0.8 is a required external dependency, resolved from the Forge Maven for development. Install the Fabric version alongside the mod in a normal game. The `terrablender` entrypoint registers the same two independent weight-1 regions: Graveyard replaces inland Plains/Sunflower Plains climates; Spiritwood replaces the broader inland forest/land climate set. Rivers and oceans retain their vanilla climate mappings. Existing terrain is not retroactively replaced; use a fresh world for testing.

Spiritwood Forest has dense Spiritwood trees. Graveyards have sparse mixed trees. Native saplings grow a 4–6 block trunk with blob foliage, and decorate newly grown leaves with Grave Moss. Vanilla leaf decay/sapling drops and wood tags remain in use. Natural cemetery mausoleums also grow Grave Moss.

Bloodstone veins use the reference's size 4, one attempt per Overworld chunk, 50% discard on air exposure and low-height triangular distribution. Stone and deepslate replacement variants are retained. Fabric biome modifications add the ore and Spiritwell placements to Overworld biomes.

Spiritwells get one placement attempt per 256 chunks on average, subject to dry solid ground and a clear radius-two sphere. Placement never clears terrain and explicitly rejects dimensions other than the Overworld. These are terrain anchors only: discovery, names, protection, beams and travel remain unported.

## Cemeteries

The native structure set uses spacing 9, separation 7 and salt 18470631. The 56×56 footprint must lie entirely within Graveyard quart cells. Each cemetery contains its perimeter, six grave sections and four mausoleums. All writes remain clipped to Minecraft's supplied chunk bounds; mausoleum centers stay within a chunk. The older independent border/row/mausoleum features are superseded by this coordinated structure and are not generated a second time.

Generated graves retain their natural state and orientation. Grave ownership, inscriptions, naturalization interactions and travel eligibility still require the grave gameplay slice. Cemetery structure type and piece serializers preserve native structure saves.

## Loaded-world transformations

Graveyard Stone weathering runs daily with at most 64 samples and four conversions per loaded chunk. It requires the actual Graveyard biome and consumes no Haunt.

Diamond-to-Bloodstone conversion checks loaded chunks at dawn, sunset and midnight. Chance per check is 0.5% at Dead Rising, 1% Demonic, 2% Abominable and 4% Eldritch. A successful conversion consumes one chunk Haunt point only above the ordinary biome baseline, matching the reference. It first checks section palettes and scans only sections containing diamond, avoiding an unconditional whole-chunk scan. Missed intervals never replay in a burst.

## Validation and limits

The build passes 25 native server GameTests, including real sapling growth/moss, dynamic registry and biome-tag loading, formation cadence/chances and actual diamond conversion. Existing 21 tests remain passing. The fresh normal-world client located both natural biomes, generated a native cemetery at (4464, -576), and rendered the readers at 30% volume. Direct piece placement and native piece-NBT reload cover the cemetery state-property regression.

Discovery footprints, Spiritwell travel, remaining magical equipment and NeoForge save migration are not part of this slice. Matching registry names do not make NeoForge saves safe to import. Third-party biome-mod compatibility still requires playtesting.
