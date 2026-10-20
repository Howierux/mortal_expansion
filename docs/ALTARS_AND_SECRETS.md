# Paired altars and secret crafting

The Altar of Impurity is the inverse of Purity: while burning it adds 0.10 Haunt every ten active seconds to each loaded chunk in its centered 3x3 square. Purity removes the same amount. Both clamp at the normal Haunt boundaries, preserve fractional values, affect no personal Haunt and do not force-load terrain. Burn reserves, 12.5-second offering consumption, one-hour capacity, container returns, automation and save/load behavior share one implementation. The Impurity GUI uses warmer colors; the bonfire and remaining-time tooltip work the same way.

By default, Impurity accepts the same five offerings through `mortal_expansion:impurity_offerings`, which includes the purity_offerings tag. Datapacks can configure membership separately. Positive offering gifts and blessings remain exclusive to Purity.

## Secret recipes

```text
Altar of Impurity       Altar of Purity

Beacon                  Beacon
Haunt Furnace           Haint Furnace
```

Each shaped vertical two-item recipe produces one altar. The previous Campfire/Holy Salt/Gravestone Purity recipe is replaced. These recipes have no ingredient-based recipe-book unlock advancement and require their separate Secrets of the Dead knowledge:

- **Altar of Impurity:** aware Raw Ectoplasm book or higher, Dead Rising personal Haunt or higher. New secret bit 16, with no weaving prerequisite.
- **Altar of Purity:** purified book, Demonic personal Haunt or higher, and Impurity knowledge. New secret bit 32.

Each successful Spectral Insight event reveals only one eligible family, retaining the previous order for weaving secrets. The two new entries show their recipes after revelation; book upgrading alone grants neither. Crafting and vanilla Crafter knowledge gates apply. Existing learned knowledge still transfers through books as before.

## Existing-world compatibility

The same knowledge component/attachment IDs now accept 0–63 instead of 0–15; old values remain valid. Old unmarked full books become explicit tier-3 books retaining the old weaving bits (15), with no automatic altar knowledge. Their next genuine Insights can reveal the new blueprints. The original ingredient-unlocked Purity recipe cannot bypass its new knowledge requirement; login removes unlearned old altar recipe-book entries. Existing crafted altars and saved burn reserves continue working. Pending Purity blessings are not accepted on inverse altars.

## Temporary furnace appearance

Both furnace variants use the vanilla Furnace side, top and off/on front textures, preserving recognizable Furnace geometry and orientation. Generated faces carry tint index 0. Haunt uses dark gray (`#686868`); Haint uses bright icy blue (`#C4E8FF`) to match the purified material theme. Native block and item color handlers apply the same tint to placed blocks and inventory icons. No bitmap edits, external artwork or behavior changes were introduced. Client lighting/appearance still needs playtesting.

## Verification

Build and 138 required server tests pass. New tests verify inverse nine-chunk pulses, upper cap, separate native crafting unlocks and ingredient consumption, old recipe-book migration, legacy books, expanded saved knowledge and exclusion from positive Purity benefits. Existing altar, offering, weaving, crafting and persistence tests remain passing. Data generation and guide checks pass. GUI names/colors, furnace tints in both states/orientations and longer-term Haunt balance remain client playtest checks.

## Ritual Star sacrifices

Both altars accept a Ritual Star in the Overworld: Purity lowers current Haunt in every chunk by ten points, while Impurity raises it by ten. Recovery baselines remain unchanged. Craft the star with a Nether Star surrounded by eight Bloodstone. See [Ritual Star](RITUAL_STAR.md) for delivery, repeatability and persistence.
