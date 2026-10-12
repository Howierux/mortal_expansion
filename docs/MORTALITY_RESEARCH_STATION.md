# Mortality Research Station

Place a material in the station and press Research to contribute one item to your own pending research bonus. Contributions add percentage points to the chance that the next eligible Haunt selection chooses Spectral Insight:

| Material | Added percentage points |
| --- | --- |
| Rotten Flesh | 1 |
| Bone | 5 |
| Raw Ectoplasm | 10 |
| Purified Ectoplasm | 20 |

Flesh and bones stack with other contributions. Raw and purified ectoplasm are mutually exclusive within a pending bonus: repeated contributions of the chosen type are allowed, but mixing types is rejected without consumption. Total bonus is capped at 100; capped players and players who already know every secret cannot consume further research materials. A contribution that crosses the cap is limited to 100. The GUI shows the pending percentage and selected ectoplasm type.

## Selection and spending

Research changes selection probability, not the normal chance/timing of a Haunt event happening. If the ordinary selection chance for Insight is p, research adds its percentage points to p, capped at 100%. Eligible negative selections can also be redirected to the positive Insight effect. A redirected event uses the benefit cooldown, while an ordinary remaining negative roll keeps its usual protection behavior. Independent benefit/risk scheduling remains.

The player must be able to receive an actual unrevealed secret: an aware book, sufficient personal Haunt, book upgrade and prerequisite knowledge are still required. Natural selection also requires the benefit cooldown to be ready. If those requirements are unavailable, research waits rather than being wasted. Once an eligible selection succeeds (Insight or another effect), or garb prevents its selected negative event, the pending bonus and ectoplasm choice reset together. Failed effect attempts do not spend it.

A full bonus guarantees Insight selection when an eligible event rolls, not immediate revelation on submitting an item. The `random` Haunt testing command honors the bonus and retains real revelation requirements; explicitly named effect commands do not spend or redirect research. Like other explicit debug commands, random bypasses normal player event cooldowns.

The bonus belongs to the researching player, survives logout/death, and is not shared with other players using the same station. Stations store input materials but never consume them automatically or apply research without an acting player. Remote/invalid menu actions and spectator research are rejected. Research does not add personal or chunk Haunt.

## Crafting

Craft using the Enchanting Table pattern with two Holy Salt items in place of Diamonds and the Book of Ectcetera atop the Obsidian base:

```text
Empty       Book of Ectcetera  Empty
Holy Salt   Obsidian           Holy Salt
Obsidian    Obsidian           Obsidian
```

The two salts and four Obsidian are consumed. The book is returned with its original tier, secrets, custom name, enchantments and other components. Any book upgrade tier can be used. The station uses a temporary vanilla enchanting-table model; the live item book is not embedded into block inventory by crafting.

## Persistence and migration

Two new player attachments store `mortality_research_bonus` (0â€“100) and `mortality_research_ecto` (0: none, 1: raw, 2: purified), copied on death and defaulting to zero in old saves. A new block/item/entity/menu ID stores only native item-handler inventory. The custom supported ShapedRecipe serializer uses a fixed typed pattern and overrides remaining items only for the book. The recipe, model/state, loot and mining tag are generated from Java. Native menu buttons/data slots handle contributions and display; no custom payloads, new mixins or automatic crafting integrations are introduced.

Migration checks: native recipe remainder behavior, player attachment codecs/copy-on-death, menu action validation, held book eligibility and Haunt selection weighting. Bonus selection is isolated in MortalityResearch and shared by natural rolls and random test commands.

## Verification

Build and all 143 server tests pass. Research tests cover exact material values, repeated same-type stacking, rejected mixing, cap/no wasted extra items, native station crafting with book-component retention, exact salt/Obsidian consumption, a real protected-player risk redirected to Insight, separate cooldown handling, waiting for eligible knowledge, random/named command behavior, player ownership, saved bonus/type and remote button rejection. GUI layout/opening and long-term selection balance still need client playtesting.

The research station is now the first secret, whispered into even a starter book at Natural personal Haunt. Secrets of the Dead is available from the starting book; other secrets keep their upgrade gates. Its blueprint requires this first reveal, and knowledge masks now support 0â€“127. See [First research whisper](FIRST_RESEARCH_WHISPER.md).

## Recipe synchronization

The fixed station recipe uses a stateless stream codec that reconstructs a new instance on decode. Do not replace it with StreamCodec.unit: shaped recipe instances use identity equality, so a unit codec rejects the separately loaded recipe when the server sends the login recipe-update packet. The packet roundtrip GameTest covers all loaded recipes and checks the reconstructed station pattern and book remainder. No recipe ID, JSON format, or saved-world migration is changed. If the recipe gains configurable fields, serialize those fields explicitly.
