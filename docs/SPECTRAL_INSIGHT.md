# Spectral Insight and Secrets of the Dead

Updated 2026-10-16. Book upgrades open subject tiers; **secret recipes are learned gradually through Insight**, rather than automatically when the book is upgraded. The first revelation is a whisper available with a starter book. Raw Ectoplasm (tier 2) makes the book aware; Pure Ectoplasm (tier 3) opens Purification. A Ritual Star upgrades that book to [The Darkhold](DARKHOLD.md), which immediately teaches all ordinary secrets and opens a separate Secrets of the Damned chapter.

Carry the book in ordinary inventory or either hand. A successful Insight reveals one eligible secret family, writes it into the book, teaches the player permanently, awards recipe-book entries, and gives a next-step hint. If no carried book and unrevealed secret meet the requirements, there is no revelation or spent event cooldown.

## Secret families

| Secret | Minimum book tier | Minimum personal Haunt | Prerequisite knowledge | Recipes |
| --- | --- | --- | --- | --- |
| Mortality Research Station | Starter (0) | Natural | None; first eligible revelation | Research station |
| Ectothread | Raw Ectoplasm (2) | Supernatural | None | Eight Ectothread, regular/purified Dreamcatchers and Dreamcatcher upgrade |
| Ghost Garb | Raw Ectoplasm (2) | Dead Rising | Ectothread | Four Ghost armor patterns |
| Haint Garb | Raw Ectoplasm (2) | Demonic | Ectothread and Ghost Garb | Purified Ectothread, four Haint patterns, Ghost-to-Haint smithing |
| Spectralite Haint | Pure Ectoplasm (3) | Abominable | All three weaving families | Matching Haint/Spectralite smithing combinations |
| Altar of Impurity | Raw Ectoplasm (2) | Dead Rising | None | Beacon over Haunt Furnace |
| Altar of Purity | Pure Ectoplasm (3) | Demonic | Altar of Impurity | Beacon over Haint Furnace |

The controller checks these families in the order above and reveals the first eligible unknown family. An ineligible earlier family does not block a later eligible one. Ingredient availability remains separate from knowledge: knowing Purified Dreamcatchers does not supply Purified Ectothread or unlock its own recipe early.

Knowledge survives lowering Haunt, losing a book, death, and logout. A learned book can share secrets with another player, and replacement books absorb their owner's learned secrets when their tier permits them. Stored books do not independently receive revelations.

## Presentation and event protection

Secrets of the Dead has its own research-map chapter from the starting book, with the research whisper as its root. Later nodes retain their book-tier and individual revelation gates. Unrevealed entries conceal their recipes; upgrades through tier 3 alone do not reveal them. The final Darkhold upgrade deliberately reveals every ordinary secret. Dreamcatchers branch from Ectothread. Open guides refresh from synchronized book components.

Insight belongs to the independent beneficial Haunt pool. Full garb permits benefits, while owned loaded Dreamcatchers can intercept both good and bad effects, including research-funded Insight. See [Dreamcatchers](DREAMCATCHER.md). [Mortality Research](MORTALITY_RESEARCH_STATION.md) biases selection; reaching 100% attempts an immediate eligible revelation. Book, personal Haunt, prerequisites, and protection still matter.

## Crafting and automation

Crafting and smithing reject locked recipes before result pickup or ingredient consumption, even with limited crafting disabled. Learning refreshes an already-open menu. Existing gear can be used without knowing its recipe.

Vanilla Crafters cannot bypass knowledge locks. Opening a Crafter after learning teaches it current player knowledge for later automatic crafting. Other mods' automated-crafting integrations remain an explicit missing function.

Recipes retain stable IDs and generated definitions. Insight awards their recipe-book entries in place of ordinary ingredient-triggered advancement unlocks. See [data generation](DATA_GENERATION.md).

## Persistence and migration

Book secret masks and player knowledge use stable bits: Ectothread 1, Ghost 2, Haint 4, combined 8, Impurity 16, Purity 32, and Research 64 (full mask 127). Never renumber these bits.

Legacy unmarked full guides retain the original weaving mask 15; they do not automatically grant newly added altar or research secrets. Earlier weaving recipe knowledge migrates on login. Prior ordinary altar recipe unlocks cannot bypass revelation. Book tier, book memory, and permanent player knowledge are separate concepts.

Narrow mixins gate crafting results, smithing results, and vanilla Crafter execution before consumption. On version migration, verify menu methods, synchronization, native recipe remainders, item/player codecs, and pre-consumption ordering. The recipe upgrade system has its own [compatibility notes](BOOK_OF_ECTCETERA.md).

## Validation

Server tests cover gradual revelation, thresholds, prerequisites, recipe locks, conservation, sharing, save/network roundtrips, legacy migration, and vanilla automatic crafting. Historical milestone counts are superseded by [current release status](RELEASE_STATUS.md). In-game readability, feedback, and balance remain playtest concerns.