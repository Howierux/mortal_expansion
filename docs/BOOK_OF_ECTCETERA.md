# Book of Ectcetera progression

Earning vanilla **Monster Hunter** (`minecraft:adventure/kill_a_mob`) sends only that player a chat hint: combine a Book with Rotten Flesh in any crafting grid to make the Book of Ectcetera. The hint uses NeoForge's advancement-earned event, not a broadcast or periodic check. It does not grant a book or unlock secret recipes. Existing completed advancements are not retroactively replayed; revoking and earning the advancement again sends the hint again.

The starting shapeless recipe remains one Book plus one Rotten Flesh. It now
produces a tier-0 book rather than immediately revealing every chapter. Upgrade
the same book with one ingredient at a time in any crafting-grid arrangement:

| Tier | Ingredient added to the previous tier | Newly available subjects | Final entry |
|---|---|---|---|
| 0: Beginnings | Initial Book + Rotten Flesh recipe | First steps, personal/local Haunt, biomes | Obtain a Bone from a Skeleton |
| 1: Bones and Spirits | Bone | Graves, intangible creatures, creature identities, Spiritwells | Obtain Gravestone, craft a Gravestone Sword, hunt a Ghost for Raw Ectoplasm |
| 2: Raw Ectoplasm | Raw Ectoplasm | Ectoplasm, cauldrons, salt, torches, equipment, Ghost Garb, furnaces | Make purified cauldron fluid, collect it, and smelt the bucket into Pure Ectoplasm |
| 3: Purified Ectoplasm | Pure Ectoplasm (solid item) | Sanctification, wards, ectchantments, generators, Purification Station, Haint/Spectralite Haint garb, scepter | Combine with a Ritual Star for the final upgrade |
| 4: The Darkhold | Ritual Star | All Secrets of the Dead; hidden Secrets of the Damned chapter | First damned revelation at 70 personal Haunt |

Subject tiers unlock cumulatively. Secret recipes do not unlock merely by upgrading through tier 3; the starter book can receive the first Mortality Research whisper, and later secrets have their own requirements. The client now presents a tabbed research map,
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
is a persistent integer component (0-4) synchronized to clients. Existing unmarked
books retain subject tier 3; secret recipes still follow the rules below; newly crafted books and the creative-tab
starter explicitly carry tier 0. Unmarked command-created books likewise read as
the purified subject tier, not the Darkhold. Book tier is stored on the item; secret knowledge is also permanently learned by the player and can be shared through books. Legacy unmarked books retain the original four weaving secret bits, not automatic knowledge of newer altar/research secrets. See [Spectral Insight](SPECTRAL_INSIGHT.md).

EctceteraProgression centralizes tiers and upgrade ingredients. EctceteraGuide
holds chapter tiers and navigation metadata separately from client rendering.
EctceteraUpgradeRecipe extends supported shapeless crafting and uses NeoForge's
partial DataComponentIngredient to match the input tier while accepting custom
names/components. Its small serializer stores `from_tier`; inputs and result are
constructed from the same gameplay rules. ModRecipes generates the initial
component-bearing result and all four upgrade resources. The upgrade recipe itself adds no mixins or player saved data; the separate Insight system uses persistent player knowledge and narrow recipe-lock mixins. During Minecraft migration verify stack component
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

## Final upgrade: The Darkhold

A purified tier-3 book plus one Ritual Star produces tier 4, **The Darkhold**, preserving components. It reveals all Secrets of the Dead and opens a hidden Secrets of the Damned tab. Carried copies slowly raise personal Haunt, including standard backpack inventories. The first damned revelation occurs at 70. See [Darkhold rules and migration](DARKHOLD.md). Legacy unmarked books remain tier 3.
