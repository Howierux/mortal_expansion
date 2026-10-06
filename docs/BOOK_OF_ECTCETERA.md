# Book of Ectcetera progression

The starting shapeless recipe remains one Book plus one Rotten Flesh. It now
produces a tier-0 book rather than immediately revealing every chapter. Upgrade
the same book with one ingredient at a time in any crafting-grid arrangement:

| Tier | Ingredient added to the previous tier | Newly available subjects | Final entry |
|---|---|---|---|
| 0: Beginnings | Initial Book + Rotten Flesh recipe | First steps, personal/local Haunt, biomes | Obtain a Bone from a Skeleton |
| 1: Bones and Spirits | Bone | Graves, intangible creatures, creature identities, Spiritwells | Obtain Gravestone, craft a Gravestone Sword, hunt a Ghost for Raw Ectoplasm |
| 2: Raw Ectoplasm | Raw Ectoplasm | Ectoplasm, cauldrons, salt, torches, equipment, Ghost Garb, furnaces | Make purified cauldron fluid, collect it, and smelt the bucket into Pure Ectoplasm |
| 3: Purified Ectoplasm | Pure Ectoplasm (solid item) | Sanctification, wards, ectchantments, generators, Purification Station, Haint/Spectralite Haint garb, scepter | All chapters unlocked; guidance for combining the systems |

Chapters unlock cumulatively. The client now presents a tabbed research map,
with locked nodes showing upgrade or Insight requirements. Locked body text
cannot be opened; unrevealed weaving nodes hide their names behind a secret
label. Node access delegates to the existing tier-and-secret chapter rules.
Each tier ends with its ingredient guidance. The final tier has no further book
upgrade. Tooltip text identifies the tier and next ingredient.

Each upgrade consumes the old book and one ingredient, producing exactly one
upgraded copy. It preserves custom name, enchantments and other components.
Repeating an upgrade, skipping a tier, adding extra ingredients, or substituting
fluid buckets/bone meal does not match. The entire sequence fits the inventory's
2x2 grid as well as a crafting table. Recipes show the required tier of input book.

## Compatibility and migration

The existing book item ID is retained. Per-stack `mortal_expansion:ectcetera_tier`
is a persistent integer component (0-3) synchronized to clients. Existing unmarked
books retain all chapters as tier 3; newly crafted books and the creative-tab
starter explicitly carry tier 0. Unmarked command-created books likewise read as
complete. Knowledge is stored on the item, not as a player unlock, so transferring
books transfers their knowledge.

EctceteraProgression centralizes tiers and upgrade ingredients. EctceteraGuide
holds chapter tiers and navigation metadata separately from client rendering.
EctceteraUpgradeRecipe extends supported shapeless crafting and uses NeoForge's
partial DataComponentIngredient to match the input tier while accepting custom
names/components. Its small serializer stores `from_tier`; inputs and result are
constructed from the same gameplay rules. ModRecipes generates the initial
component-bearing result and all three upgrade resources. No mixins or new player
saved data are introduced. During Minecraft migration verify stack component
codecs, custom ingredient matching/previews, normal result-slot consumption,
recipe serialization, and vanilla book link/page behavior.

## Validation

The build and all 46 required server GameTests pass, including actual inventory
crafting with consumed inputs, three sequential upgrades, component preservation,
skip/repeat/extra-input rejection, legacy behavior, saved-item and network recipe
roundtrips, cumulative chapters and navigation offsets. Resource checks cover
30 chapters and 128 pages in the complete book, per-tier endings, translation/page
budgets, the starter component and three generated upgrade recipes. Data generation
checks pass for 403 resources. Manual client page appearance remains for playtest.

The Raw Ectoplasm tier now includes the EMF Reader recipe, controls and bulb scale.

The EMF chapter also covers upgrading with Raw Ectoplasm for decimal readings.

Starter knowledge now includes independent Haunt visits, hints and protection.

Spiritwell guidance now describes named discovery and spectral footprint trails.

## Aware books and Spectral Insight

Raw Ectoplasm books now gradually learn Ectothread, Ghost Garb and Haint Garb through beneficial Insight events, rather than showing their recipes immediately. Pure-upgraded books can learn the combined Spectralite Haint secret. Chapters and recommendations follow remembered knowledge; known book secrets can be shared. See [Spectral Insight](SPECTRAL_INSIGHT.md) for thresholds, actual crafting gates and compatibility.

Equipment guidance includes Haunt-reactive soil made by Bloodstone and Spectralite hoes.

Equipment guidance includes wear-based pickaxe speed and fractional chunk-Haunt repair costs.

The current interface is described in [Research map](ECTCETERA_RESEARCH_MAP.md). Existing page offsets are retained as guide metadata/validation; the UI no longer uses vanilla book contents links.
