# Mortal Expansion - Overview

**Repository:** https://github.com/Howierux/mortal_expansion  
**Branches:** `main` (consolidated baseline), `dev` (active development)  
**Last Updated:** 2026-10-03

Mortal Expansion is a Minecraft mod built around Haunt, spectral creatures, graveyards, spirit defenses, and ectoplasm-powered machines. Haunt is both a danger and a resource: higher tiers sustain supernatural activity while improving generators and furnaces. Players can cleanse an area or deliberately work in a haunted location.

This overview separates the current implementation from the original design direction. [Notes](Notes.md) records decisions, source reconciliation, and verification history; the [Roadmap](ROADMAP.md) tracks completed work and proposed next steps.

## Original design direction

The user supplied `Mortal Expansion.docx`, an evolving design notebook with dated revisions from 2014, 2015, and 2017. It describes a world where death, spirits, technology, and ritual interact through Haunt. Its suggestions are design references, not instructions to implement every feature immediately. Current behavior is established by the repository, while unfinished ideas are preserved in the roadmap.

The design has two paths into the same systems. A primal path uses cauldrons, holy water, offerings, flowers, and spiritual defenses. A scientific path uses ectoplasm-powered machines, generators, fluid transport, and automation. Optional integrations with other mods were proposed for both paths, but those older compatibility targets require a fresh decision for Minecraft 1.21.1.

Haunt connects progression and consequences: stronger spirits appear as an area becomes more haunted, some equipment and machines benefit, and players gain reasons to deliberately raise or cleanse Haunt. The notebook proposes personal disturbances, spectral gear, and late-game events in addition to the simulation already implemented.

The selected gameplay direction includes ranged Elemental attacks, intangible mobs able to attack tangible and intangible targets, a discoverable passive Spirit Villager with trading for key mod resources, a review of mob strengths and spawn conditions, and a portal system whose implementation still needs design. Duplicator and Triplicator are long-term features. Spirit Journey has reopened as staged work: stage one adds the self-Grave death choice and spectator-mode entry; stage two defers eligible death items until that choice; corpse recovery and journey-only Spiritwell beams are now implemented; grayscale and spirit barriers follow later. Tinkers' Construct compatibility remains out of scope.

Other longer-term design concepts include Spiritwood trees that fight back, Blood Blooms, spiritual armor sets, soul-based upgrades, and consecrated Graves with maintained revival benefits. Ordinary named-Grave respawning is already implemented. The original spirit-only gates are one possible portal design, not a settled travel restriction.

The accepted Haunt effect roadmap adds occasional atmospheric, disruptive, and beneficial events with tier thresholds, cooldowns, and protection from garb, Holy Water, and Wards. The 2026-10-04 correction makes personal Haunt alone control ambient triggers and minimum tiers. Bloodstone tools use personal Haunt, with Diamond parity at Natural and initial +10% power/speed per higher tier. Grave Moss cleansing is covered by the Purity/Impurity altar systems; a separate fire-burning mechanic is not planned. Ambient/beneficial effects are now implemented; major encounters and actual Ghost’s Garb content remain separate design work.

### Design compared with the current baseline

| Design area | Current baseline | Remaining design scope |
|---|---|---|
| Chunk and player Haunt | Persistent values, named tiers, spread, purification, and tier-controlled spawning | Ambient haunt events, more ways to raise Haunt, and the special The End event |
| Graves and death | Inscriptions, own-name anchor, placed-Grave consumption, and naturalized reuse | Consecration, maintained keep-inventory behavior, and click reselection; staged Spirit Journey corpse recovery |
| Spiritwood and Graveyards | Forest biome, wood family, Graveyards, structures, and Graves | Hostile tree entities, ectoplasm-fed saplings, village association, and randomized inscriptions |
| Creature progression | Spirit, Elemental, Ghost, Phantom, Haunt, Haint, Mummy, and Pharaohl classes; several proposed death transformations | Elemental ranged attacks, attacks across tangibility states, strength/spawn review, passive Spirit Villager trading, and other encounter ideas |
| Ectchantments and generators | Ectchantment Machine, Demi-Tangible, ectoplasm generator, and enchantment extraction generator | Possessor and Ectocleanser variants, fluid/fuel power choices, and optional integrations |
| Fluids and purification | Ectoplasm fluids, heated cauldrons, Grave Moss, Purification Station, and Wards | Ecto Pipe, Ectank, Liquefactor, Blood Bloom ritual recipes, and handling side effects |
| Furnaces | Haunt and Haint Furnaces with deterministic tier-scaled speed and heat rules | Vortex variant, random spectral outputs, and rotten-flesh Haunt generation |
| Equipment | Bloodstone/Spectralite gear and intangible damage protection | Spiritual garb, soul upgrades, Haunt repair/scaling, and purified Bloodstone gear |
| Detection and defense | Salt and Spirit Torch barriers, sanctification, and cleansing Wards | Spirit Detector, Spectral Analogue, flashlight, powered shielding, and purification altars |
| Travel | Existing Grave respawn anchors | Planned portal system; linking, eligibility, costs, and dimension rules need design |
| Advanced processing | Material transmutation and an indexed Book of Ectcetera | Fancifier, Schmancifier, long-term Duplicator/Triplicator, and end-game progression choices |

These are thematic comparisons, not declarations that every detail of a historical proposal has shipped. The roadmap preserves the individual mechanics, recipes, costs, and unresolved choices.

### Creature identities in the design

| Creature | Role and behavior described in the notebook |
|---|---|
| Ghost | Aggressive intangible spirit |
| Phantom | Empowered, corporeal Ghost; faster when unobserved; leaves a Ghost when slain |
| Spirit | Normally peaceful intangible merchant; ranged demi-tangible retaliation when damaged |
| Elemental | Empowered Spirit; ranged demi-tangible retaliation when damaged |
| Haunt | Intangible miniboss |
| Haint | Empowered Haunt that splits into several Haunts when slain |
| Mummy | Tangible lesser enemy that leaves a Ghost when slain |
| Pharaohl | Boss pronounced "feral", alternating tangible/intangible states and summoning Mummies |

The creature classes are present, including the Phantom, Haint, and Mummy death transformations and Pharaohl's changing form. The historical roles describe design intent; exact combat balance and progression remain subject to validation.

## Technology and runtime

| Component | Current implementation |
|---|---|
| Minecraft | 1.21.1 |
| Mod loader | NeoForge 21.1.219 |
| Java | JDK 21 |
| Build | Gradle wrapper 9.2.1, ModDevGradle 2.0.140 |
| World generation integration | TerraBlender 1.21.1-4.1.0.8 |
| Mod ID / declared version | `mortal_expansion` / `1.0.0` |
| Local development client | `gradlew.bat runClient` |

The historical branch suffixes, such as `1.0k` and `1.0m`, label development milestones. They are separate from the declared mod version in `gradle.properties`.

## Main systems

### Haunt

Chunks and players have separate persistent Haunt values. Named tiers run from Void through Natural, Supernatural, Dead Rising, Demonic, Abominable, and Eldritch. Each band spans ten points, with an overall range of 0-69. Player Haunt also retains fractional points across death and logout.

World generation establishes natural baselines, including Supernatural in Spiritwood Forest, Dead Rising in Graveyards, Demonic in the Nether, and Eldritch in the End. Simulation handles spread, recovery, player leakage, and purification.

### Graves and spectral combat

Graves support inscriptions, player ownership, natural provenance, and naturalization. Engraving a placed Grave with the player's exact in-game name selects a respawn anchor. An ordinary placed anchor is consumed on respawn and adds one personal Haunt point; a naturalized anchor can remain reusable. Clicking an already engraved Grave does not currently reselect an anchor.

Spirit Journey offers an optional death-screen choice for a valid selected self-named Grave. Choosing it respawns at the Grave in vanilla Spectator mode. Stage two holds eligible death items in persistent player data until a choice: Ordinary Respawn releases them at the original death location; Spirit Journey leaves an owner-linked corpse and holds them until the spirit returns within two blocks of the body. Without a valid self-Grave, normal drops remain immediate. Keep Inventory and Curse of Vanishing retain vanilla behavior. Returning to the corpse revives the owner and restores held items. Grayscale and spirit barriers are later stages. Existing Grave consumption, Haunt cost, and naturalized reuse remain in effect.

Intangible creatures use custom combat and movement rules. Demi-tangible equipment, spectral projectiles, and other supported damage sources can reach them. Holy Salt and linked Spirit Torches provide barriers; sanctification and Purification Wards provide cleansing mechanics.

### Ectoplasm and machines

- The Ectoplasmic Generator burns raw or pure ectoplasm for FE; output increases with local Haunt. Completed raw burns add one chunk Haunt, pure burns add three.
- The Enchantment Generator extracts energy from enchanted items.
- The Ectchantment Machine transmutates materials and supports demi-tangible upgrades using ectoplasmic charge or FE.
- The Purification Station consumes raw ectoplasm, Grave Moss, water, and FE to produce pure ectoplasm, adding local Haunt per completed craft.
- Haunt and Haint Furnaces use ordinary smelting recipes with cooking speed scaled from 1x at Void to 10x at Eldritch. Haint retains idle heat and preserves active heat at Demonic or higher.

### Wards and the guide

Purification Wards keep their 3x3 chunk cleansing area loaded and ticking through persistent, per-ward tickets. Breaking a Ward releases its tickets; overlapping Wards retain their own tickets.

The Book of Ectcetera is an indexed, four-tier in-game guide. Craft a Book with Rotten Flesh, then upgrade the same book with a Bone, Raw Ectoplasm, and solid Pure Ectoplasm to unlock related chapters cumulatively. Each tier ends with guidance for the next ingredient. Existing unmarked books retain all chapters. Chapter metadata is shared with navigation and resource validation. See [Book progression](BOOK_OF_ECTCETERA.md).

## Source map

| Location | Responsibility |
|---|---|
| `src/main/java/com/howierux/mortalexpansion/MortalExpansion.java` | Mod entry point and event registration |
| `src/main/java/com/howierux/mortalexpansion/registry/` | Blocks, items, entities, attachments, menus, capabilities |
| `src/main/java/com/howierux/mortalexpansion/haunt/` | Haunt simulation, purification, furnace timing, Ward chunk tickets |
| `src/main/java/com/howierux/mortalexpansion/block/` and `blockentity/` | Block interactions, machine processing, inventories, persistence |
| `src/main/java/com/howierux/mortalexpansion/respawn/` | Grave respawn selection handling |
| `src/main/java/com/howierux/mortalexpansion/combat/`, `entity/`, `mixin/` | Spectral combat, creature behavior, Minecraft integration |
| `src/main/java/com/howierux/mortalexpansion/worldgen/` | Biomes, features, and structures |
| `src/main/java/com/howierux/mortalexpansion/client/`, `menu/`, `network/`, `guide/` | Screens, rendering, interactions, and guide navigation |
| `src/main/resources/` | Recipes, models, textures, translations, world data |
| `tests/java/` and `tools/check_ectcetera.py` | Standalone rule checks and guide resource validation |
| `.github/workflows/build.yml` | Automated checks, build, and mod loading through data generation |
| `run/` | Local client configuration, logs, and saved worlds; ignored by Git |
| `docs/` | Overview, dated notes, and roadmap |

## Working workflow

Do new work on `dev`. Consolidate a reviewed state into `main` when requested. Preserve local changes and historical branches unless the user asks otherwise.

Build and rule validation can run without opening the game. Launch Minecraft only when the user explicitly requests it. Routine repository updates do not need a save backup, per the user's preference.

At the start of future work, read this file, the latest entry in [Notes](Notes.md), and the [Roadmap](ROADMAP.md). Append dated notes as decisions or changes land; update roadmap status with evidence. Update the overview when the architecture or project scope changes. These repository docs are the current documentation source; no external vault synchronization has been established for this project.

### Spiritwells

Spiritwells are Overworld-only; Nether and End generation is not required. Rare worldgen-only Overworld anchors appear as a faint Nether Star floating about two blocks above dry land. The star has no collision, cannot be mined/collected, and prevents normal block placement within a two-block spherical radius, including multi-placement and piston movement into the area. The initial rate is one attempt per 256 newly generated chunks, with terrain and clearance checks reducing successful placements. Anchors persist with their chunks; the personal-Haunt controller now uses them for hostile Ghost appearances; active Spirit Journey reveals pale beacon beams over loaded wells.

The planned Spiritwell-spawn Haunt effect retains its trigger if no loaded Spiritwell is within 100 blocks. It waits until a player enters that range of a loaded well, without another random roll or forced chunk loading. The server now persists these held selections per dimension and delivers them to eligible nearby players without another tier check; player cooldowns, protection, and mob caps still apply.


### Ambient effects

Personal Haunt alone drives 30-second rolls and accepted tier probabilities; each delivered event has a sixty-second player cooldown. Held Spiritwell selections start that cooldown on delivery. Effects range from sounds and fleeting render-only shadows to brief potions, safe one-block shoves, main-inventory swaps, passive Spirits, and Spiritwell Ghosts. Beneficial effects finish one active cooking operation or convert one carried material, with a shared sixty-second chunk cooldown. Holy Water grants sixty seconds of negative-event protection, removes only unchanged haunting-owned negative potions, and cleanses one personal point per minute. Wards protect their covered chunks. Ghost’s Garb immunity/repair hooks exist through an empty item tag; the armor and major encounters are not yet added.

The Altar of Purity stores offering burn time and cleanses loaded chunks in its 3x3 area. See [Altar of Purity](ALTAR_OF_PURITY.md) for fuel, crafting, GUI and persistence details.

Spirit and Elemental ranged retaliation is implemented: see [Spectral retaliation](SPECTRAL_RETALIATION.md).
