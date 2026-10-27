# Mortality Research Station

Place a material in the station and press Research to contribute one item to your own pending research bonus. Contributions add percentage points to the chance that the next eligible Haunt selection chooses Spectral Insight:

| Material | Added percentage points |
| --- | --- |
| Rotten Flesh | 1 |
| Bone | 5 |
| Raw Ectoplasm | 10 |
| Purified Ectoplasm | 20 |

Flesh and bones stack with other contributions. Raw and purified ectoplasm may both contribute to the same pending bonus in either order. Repeated contributions of either type are allowed. Total bonus is capped at 100; capped players and players who already know every secret cannot consume further research materials. A contribution that crosses the cap is limited to 100. The GUI shows the pending percentage and checkboxes indicating whether Raw or Purified Ectoplasm has been contributed.

## Selection and spending

Below 100%, research changes selection probability while retaining normal Haunt event timing. If the ordinary selection chance for Insight is p, research adds its percentage points to p, capped at 100%. Eligible negative selections can also be redirected to the positive Insight effect. A redirected event uses the benefit cooldown, while an ordinary remaining negative roll keeps its usual protection behavior. Independent benefit/risk scheduling remains.

The player must be able to receive an actual unrevealed secret: a carried book at the required upgrade tier, sufficient personal Haunt and prerequisite knowledge are still required; the first station whisper can use a starting book. Natural selection also requires the benefit cooldown to be ready. If those requirements are unavailable, research waits rather than being wasted. Once an eligible selection succeeds (Insight or another effect), or garb prevents its selected negative event, the pending bonus and ectoplasm choice reset together. Failed effect attempts do not spend it.

Reaching 100% immediately reveals one eligible secret, bypassing natural event timing and cooldowns. An ineligible full charge waits until its book and personal Haunt requirements are met. The `random` Haunt testing command honors the bonus and retains real revelation requirements; explicitly named effect commands do not spend or redirect research. Like other explicit debug commands, random bypasses normal player event cooldowns.

The bonus belongs to the researching player, survives logout/death, and is not shared with other players using the same station. Stations store input materials but never consume them automatically or apply research without an acting player. Remote/invalid menu actions and spectator research are rejected. Research does not add personal or chunk Haunt.

## Crafting

Craft using the Enchanting Table pattern with two Holy Salt items in place of Diamonds and the Book of Ectcetera atop the Obsidian base:

```text
Empty       Book of Ectcetera  Empty
Holy Salt   Obsidian           Holy Salt
Obsidian    Obsidian           Obsidian
```

The two salts and four Obsidian are consumed. The book is returned with its original tier, secrets, custom name, enchantments and other components. Any book upgrade tier can be used. The station uses ingredient-informed temporary block art; the live item book is not embedded into block inventory by crafting.

## Persistence and migration

Two player attachments store `mortality_research_bonus` (0-100) and `mortality_research_ecto` (bitmask: 0 none, 1 raw, 2 purified, 3 both; existing 0/1/2 saves remain valid), copied on death and defaulting to zero in old saves. The station stores a native item-handler inventory. Its supported ShapedRecipe serializer returns the original book. Recipe, models, loot, and tags are generated in Java; native menu buttons/data slots handle research without custom payloads or new mixins.

Migration checks: native recipe remainder behavior, player attachment codecs/copy-on-death, menu action validation, held book eligibility and Haunt selection weighting. Bonus selection is isolated in MortalityResearch and shared by natural rolls and random test commands.

## Verification

Build and all 143 server tests pass. Research tests cover exact material values, repeated same-type stacking, rejected mixing, cap/no wasted extra items, native station crafting with book-component retention, exact salt/Obsidian consumption, a real protected-player risk redirected to Insight, separate cooldown handling, waiting for eligible knowledge, random/named command behavior, player ownership, saved bonus/type and remote button rejection. GUI layout/opening and long-term selection balance still need client playtesting.

The research station is now the first secret, whispered into even a starter book at Natural personal Haunt. Secrets of the Dead is available from the starting book; other secrets keep their upgrade gates. Its blueprint requires this first reveal, and knowledge masks now support 0ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Å“127. See [First research whisper](FIRST_RESEARCH_WHISPER.md).

## Recipe synchronization

The fixed station recipe uses a stateless stream codec that reconstructs a new instance on decode. Do not replace it with StreamCodec.unit: shaped recipe instances use identity equality, so a unit codec rejects the separately loaded recipe when the server sends the login recipe-update packet. The packet roundtrip GameTest covers all loaded recipes and checks the reconstructed station pattern and book remainder. No recipe ID, JSON format, or saved-world migration is changed. If the recipe gains configurable fields, serialize those fields explicitly.

The screen lists Flesh/Bone rates separately, then Ecto 10% and Purified 20% with read-only checkboxes after each label. Each check marks an ectoplasm type already contributed to the pending bonus. Both can be checked together, and both clear after the bonus is spent. Indicators use existing synchronized menu data and reflect both independent contribution flags.

## Completing research

Reaching 100% immediately runs Secrets of the Dead (Spectral Insight) to reveal one eligible secret family, bypassing the natural event cooldown and random roll. Successful revelation clears the research bonus and ectoplasm selection and starts the benefit cooldown. Existing book, personal Haunt and secret prerequisite requirements remain. If no eligible secret can be revealed, a message explains the missing eligibility and the full charge remains stored; a server check once per second retries it, including charges restored from older saves. Failed attempts never consume the charge. Partial research retains its existing selection bias.
