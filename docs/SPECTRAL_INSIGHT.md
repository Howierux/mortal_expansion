# Spectral Insight and aware Ectcetera books

Implemented 2026-10-05. Knowledge arrives gradually through the independent beneficial Haunt pool. A Book of Ectcetera becomes aware at its Raw Ectoplasm upgrade (tier 2); the Pure Ectoplasm upgrade (tier 3) enables its final current secret. Merely upgrading or collecting recipe ingredients does not teach the locked recipes.

Carry an aware book in the ordinary inventory or either hand. Each successful Spectral Insight reveals one eligible secret family, writes it into the book, teaches the player permanently, awards the matching vanilla recipe-book entries, and whispers a next-step recommendation in chat. A missing book, insufficient personal Haunt, or no new eligible secret means no revelation and no spent cooldown. Benefits remain independent from risks and full garb does not suppress Insight.

| Secret family | Minimum book tier | Minimum personal Haunt | Unlocks |
| --- | --- | --- | --- |
| Ectothread | Raw Ectoplasm (2) | Supernatural | Ectothread crafting, output eight |
| Ghost Garb | Raw Ectoplasm (2) | Dead Rising | Four Ghost Garb armor patterns |
| Haint Garb | Raw Ectoplasm (2) | Demonic | Purified Ectothread, four Haint patterns, Ghost-to-Haint smithing |
| Spectralite Haint | Pure Ectoplasm (3) | Abominable | Four Haint-plus-Spectralite smithing combinations |

Secrets are learned in that order. Earlier families remain known after upgrading, lowering Haunt, losing the book, dying or logging out. Raw-tier awareness can learn both Ghost and Haint Garb; ingredients still impose their normal material progression. Learning Haint weaving reveals its chapter even in a Raw-tier book. The combined Spectralite recipe stays concealed until its own secret is learned.

## Book behavior

The book's tooltip shows awareness and remembered secret count. Its contents hide Ectothread/Ghost/Haint recipe chapters until the book knows them. The new awakening chapter recommends a next step according to current knowledge. An open guide refreshes from synchronized item components when a revelation arrives. Existing upgrade instructions remain at the end of each tier; full contents stay within 128 pages.

A learned book can teach another player while carried or read. An aware replacement book remembers knowledge its owner has already learned. Storing the book in a chest prevents new personal revelations; stored books do not independently gain secrets.

## Actual crafting restrictions

Player-operated crafting and smithing reject locked recipes before pickup or ingredient consumption, even if the pattern is entered manually and limited crafting is disabled. Learning refreshes an already-open menu. Unrelated recipes, garb stats, ingredients, smithing templates and component preservation remain unchanged. Existing gear can still be worn or traded without knowing its crafting recipe.

Vanilla Crafters cannot bypass the locks. Open a Crafter after learning secrets to teach it your current knowledge; it retains that knowledge and can then work automatically without its teacher being online. An untaught Crafter preserves its inputs and emits the normal failed-crafting sound. Other mods' automated-crafting integrations remain an explicit missing function.

The ten ingredient-triggered recipe advancements for Ectothread, Purified Ectothread and garb are intentionally removed through Java data generation. Insight awards recipes instead. The recipes and their stable IDs remain present, so learning changes eligibility rather than adding/removing global recipes.

## Persistence and migration

The book stores a synchronized/persistent integer secret mask. Player learned knowledge is a codec-backed attachment copied on death. Stable bits are 1 (thread), 2 (Ghost), 4 (Haint), 8 (combined); do not renumber them when adding secrets. Native recipe-book unlocks from earlier builds migrate on login with prerequisites. Existing unmarked legacy full guides retain all current knowledge. Tagged books without the new component start with no personal book revelations; previously learned player recipes remain craftable. Existing equipment is preserved.

Crafting inputs and ordinary recipes have no player context. Post-craft events occur after consumption. Three narrow mixins therefore gate the shared crafting result path, smithing results and vanilla Crafter execution. They delegate all secret rules to focused common helpers. On version migration, verify the targeted menu methods, result-slot synchronization and pre-consumption ordering with the server tests. No new recipe serializer or packet is needed.

## Validation

Build and 67 server GameTests pass. New tests cover gradual thresholds/prerequisites, manual crafting, input conservation, live menu refresh, smithing locks, permanent/book/network roundtrips, upgrade preservation, legacy migration, sharing, inventory-order safety, filtered contents and actual automatic crafting. Data generation now has 413 resources after removing ten automatic-unlock advancements. Static and dynamic guide text checks pass. In-game page refresh, hints and recipe-book presentation still need visual playtesting.

Two additional Secrets of the Dead now gate altar crafting: Impurity (bit 16, book tier 2, personal Dead Rising) and Purity (bit 32, book tier 3, personal Demonic, prerequisite bit 16). Knowledge masks support 0–63. Old weaving bits and books migrate without granting these new secrets; prior ordinary Purity recipe unlocks cannot bypass revelation. See [Paired altars](ALTARS_AND_SECRETS.md).
