# Fabric book, knowledge and research

This slice ports the Book of Ectcetera atlas, upgrades, Secrets of the Dead and Mortality Research Station to native Fabric 1.21.1 APIs. Work belongs on `fabric-dev`; NeoForge sources remain the reference.

## Player progression

Craft a book with rotten flesh for the starter Book of Ectcetera. Upgrade it successively with a bone, raw ectoplasm, purified ectoplasm and a Ritual Star. Upgrades preserve custom components and learned secrets. The final tier is the Darkhold and grants all seven Secrets of the Dead. Unmarked books from earlier Fabric catalog previews remain starter books.

The atlas retains chapter tabs, connected research nodes, dragging, zooming, entry pages and locked-secret hints. It warns that some documented features still await their Fabric implementation. The Monster Hunter advancement gives only its player a private, persistent crafting hint.

Spectral Insight reveals one eligible secret at a time. The Research Station is the first whisper and can be revealed with the starter book at Natural personal Haunt. Ectoplasm and purified book tiers enable later secrets according to their existing personal-Haunt thresholds and prerequisites. Learned knowledge is saved per player, survives death and is shared with eligible carried books. Opening a book waits for the server's authoritative state.

## Research Station

The secret recipe uses any Book of Ectcetera above an obsidian center, Holy Salt on either side and three obsidian along the bottom. Crafting returns the exact book tier and components.

Insert a research material and press Research. Rotten flesh adds 1 percentage point, bone 5, raw ectoplasm 10 and purified ectoplasm 20. Contributions stack; raw and purified ectoplasm can both contribute repeatedly. Their checkboxes indicate prior contributions, not exclusive selections. Bonuses belong to the contributing player, while the input slot belongs to the station.

At 100%, the next eligible secret is revealed immediately and the bonus resets. If the carried book or personal Haunt is not eligible for another secret, the charge remains until eligibility changes. Menu actions validate distance, material and player on the server.

## Events and recipe enforcement

Every 30 seconds, eligible survival players have a tier-dependent positive-event check: Natural 2%, Supernatural 5%, Dead Rising 8%, Demonic 12%, Abominable 16%, Eldritch 20%. Successful positive events have a 60-second cooldown. This is a deliberately limited functional pool: Spectral Insight (weight 4), nearby Purity altar offering gift (weight 3) and offering blessing (weight 3). Only available actions enter the pool; research adds percentage points to Insight selection. Altar searches inspect already loaded nearby chunks and a 16-block radius.

Operators can use `/mortalexpansion insight [targets]` to attempt one eligible secret. It respects book, personal-Haunt and prerequisite requirements.

Fabric supplies no player-aware recipe-output veto or Crafter authorization event. Three narrowly scoped mixins therefore enforce the server result boundaries; storage, networking, menus, events and datagen use native/Fabric APIs.

Secret recipe data has no ingredient advancement that automatically reveals it. Server-side gates cover inventory crafting, crafting tables, template-free smithing and vanilla Crafters. Awarding a recipe through `/recipe` does not replace knowledge. Interacting with a Crafter teaches it the player's permanent knowledge through a saved block-entity attachment. Other mods' automated crafting machines need their own integration.

Recipes are generated through native Fabric data generation, including public recipes for the already implemented machinery and building intermediates. This does not complete natural acquisition of all resources.

## Boundaries

The [combat slice](FABRIC_COMBAT.md) now implements basic Darkhold exposure, native nested containers/bundles, personal overflow, Demon awakening and sight. The seven sin unlocks/powers, native/Fabric ItemStorage Darkhold exposure, garb, Dreamcatchers and Spirit Journey are now implemented in the linked follow-up slices. Opaque backpack formats still need adapters. This update does not claim full Haunt-event parity or NeoForge save compatibility.

## Validation

The build passes 21 native Fabric GameTests. Four knowledge tests exercise native recipe serialization and matching, every upgrade/book remainder, actual crafting/inventory/smithing results, vanilla Crafter enforcement and saved knowledge, research eligibility and both ectoplasm flags, native player NBT and respawn persistence. Existing foundation, machinery, purification and altar tests remain passing.

A real integrated client at 30% master volume decoded the recipe/login payloads, opened starter and Darkhold atlases, and exercised the Research button through the native network packet. The client received the correct +10% bonus and raw-ectoplasm indicator. Screenshots were inspected for clarity and layout; temporary probes were removed. Real remote multiplayer and third-party crafting integrations remain untested.
