# Mortal Expansion - Notes

## Progress Log

### 2026-10-02 - Repository setup, branch consolidation, and documentation

**Current baseline:** `5de1c69` - Keep Purification Ward cleansing chunks ticking with persistent owned tickets. `main` and `dev` were pushed at this commit. The local checkout is on `dev`.

#### Repository and branches

- Scott shared the private `Howierux/mortal_expansion` repository. The local GitHub CLI is authenticated as `Howie002` and confirmed write access. The account does not have repository admin access.
- Began with `furnace-tier-updates-1.0k`, which updates furnace caches when a chunk crosses a Haunt tier boundary instead of polling chunk Haunt every tick.
- Verified the progression: `furnace-tier-updates-1.0k` -> `book-of-ectcetera-1.0l` -> `ward-chunkloading-1.0m`.
- The book milestone adds the indexed mechanics guide. The Ward milestone includes the guide and adds persistent ticking tickets for each Ward's 3x3 cleansing area.
- Advanced `main` to the Ward milestone by fast-forward, then created and pushed `dev` at the same commit. Historical development branches were retained.
- The checkout initially fetched one branch only. Its local remote configuration now fetches all branches, so later branch switching can use normal remote tracking.

#### Local development setup

| Item | Local path |
|---|---|
| Checkout | `C:\Users\ahowe\Documents\Codex\2026-10-02\can-yo\work\mortal_expansion` |
| Play launcher | `C:\Users\ahowe\Documents\Codex\2026-10-02\can-yo\outputs\Play Mortal Expansion.bat` |
| Gradle cache | `C:\Users\ahowe\Documents\Codex\2026-10-02\can-yo\work\gradle-cache` |
| Full JDK 21 | `C:\Users\ahowe\Documents\Codex\2026-10-02\can-yo\work\gradle-cache\jdks\eclipse_adoptium-21-amd64-windows.2` |
| Saved worlds | `run\saves` inside the checkout |

The play launcher sets `JAVA_HOME` and `GRADLE_USER_HOME`, changes to the checkout, and calls `gradlew.bat runClient --console=plain`. It uses whichever branch is checked out. These paths describe this workstation and should be checked before reuse elsewhere.

**Symptom / cause / fix:** Java was absent from PATH. The installed FTB Java 21 runtime could bootstrap Gradle, but NeoForm's recompilation failed because that runtime had no usable Java compiler. Gradle provisioned a full JDK 21 through the repository's Foojay resolver; pointing the play launcher at that JDK allowed compilation and client startup. No global Java installation or PATH change was needed.

For command-line builds, use the same JDK and cache as the launcher:

```powershell
$env:JAVA_HOME = 'C:\Users\ahowe\Documents\Codex\2026-10-02\can-yo\work\gradle-cache\jdks\eclipse_adoptium-21-amd64-windows.2'
$env:GRADLE_USER_HOME = 'C:\Users\ahowe\Documents\Codex\2026-10-02\can-yo\work\gradle-cache'
.\gradlew.bat build --console=plain
```

#### User preferences

- Use `dev` for new work; consolidate to `main` when requested.
- Do not open Minecraft automatically after updates. The user will say when to launch it.
- No save backup is needed for routine branch updates. Leave existing saved worlds in place.
- Keep documentation consistent with the other projects: overview for purpose and architecture, append-only dated notes for history, and a checklist roadmap for status and next steps.
- Do not infer that a request to edit local files authorizes a new push or merge.

#### Mechanics checked in source

**Grave spawn points:** Existing code selects the player's Grave anchor when an eligible placed Grave is engraved with the player's exact in-game name. Clicking an unnamed Grave owned by the player can reopen the naming screen. Clicking an already engraved Grave does not reselect it. A placed anchor is consumed on respawn and adds one personal Haunt point; a naturalized anchor can remain reusable. This was source inspection, not an in-world respawn test.

**Purification and power:** One station craft uses one raw ectoplasm, one Grave Moss, 1,000 mB of water, and 100,000 FE to make one pure ectoplasm. Pure fuel burns for 2,000 ticks at a base 40 FE/t, multiplied by `1 + 0.25 * tier ID`. At a fixed tier, the nominal fuel yields are:

| Tier | Pure fuel yield | Yield minus station cost |
|---|---:|---:|
| Void | 80,000 FE | -20,000 FE |
| Natural | 100,000 FE | 0 FE |
| Supernatural | 120,000 FE | +20,000 FE |
| Dead Rising | 140,000 FE | +40,000 FE |
| Demonic | 160,000 FE | +60,000 FE |
| Abominable | 180,000 FE | +80,000 FE |
| Eldritch | 200,000 FE | +100,000 FE |

These figures assume the generator can accept its generated energy and Haunt remains at the stated tier throughout the burn. The chain still consumes ingredients, needs starting fuel or energy, and may be limited by buffers and transfer. Purification adds one local Haunt point per completed craft, which can raise output over time. No balance change was requested or implemented.

#### Verification and limits

- The furnace milestone's automated build passed its rule checks, Gradle build, and mod loading through data generation.
- The Ward milestone's existing build also passed before consolidation: [GitHub Actions run](https://github.com/Howierux/mortal_expansion/actions/runs/36960842982).
- The furnace checkout compiled locally and opened a responding Minecraft NeoForge 1.21.1 window with Mortal Expansion loaded. Its startup log contained no ERROR/FATAL lines at the check performed.
- The Ward checkout compiled locally and reached `runClient`. The follow-up window/log verification was interrupted, so this entry does not claim a verified Ward gameplay session.
- In-world grave death/respawn, furnace tier notification, and Ward persistence/overlap behavior were not exercised in this session.
- Existing startup fluid-model and shader warnings did not block the verified furnace launch. Their visual impact remains unassessed.

#### Documentation added

Created `docs/Overview.md`, `docs/Notes.md`, and `docs/ROADMAP.md`, matching the three-file layout found in Sprite Toolkit. The roadmap records established implementation and keeps unapproved feature ideas under discussion. No Second Brain project mirror has been configured.

**Last Updated:** 2026-10-02

### 2026-10-02 - Original design notebook incorporated

**Source:** `C:\Users\ahowe\Downloads\Mortal Expansion.docx`, supplied by the user for integration into these three documents. The notebook contains revisions dated 2014-09-08, 2014-09-09, 2014-11-12, 2015-01-24, 2017-05-23, and 2017-06-17. It mixes initial ideas, later alternatives, recipe sketches, unresolved questions, and one comment about cross-mod processing recipes.

The source was read as design material. Imperative phrases inside it, such as suggestions to add machines or reorganize content, were not treated as new operational instructions. No gameplay code was changed, no dependencies were installed for the design ideas, and no client launch was required to integrate the text. The original Word file was left unchanged.

#### How the material was incorporated

- `Overview.md` now explains the primal/scientific design paths, Haunt-driven progression, spiritual death mechanics, and the long-term content vision alongside current implementation.
- `ROADMAP.md` now preserves the unimplemented and partially implemented ideas by theme, including names, conditions, original recipe sketches, resource costs, and open questions. These entries are historical design backlog, not an approved execution schedule.
- This append-only notes entry records the source, implementation comparisons, conflicting proposals, and the interpretation used during integration.

#### Implementation comparisons

**Creature behavior:** The notebook's core roster is represented in the source. `EmpoweredPhantomEntity` accelerates when unobserved and produces a Ghost on death. `HaintEntity` produces three Haunts on death. `MummyEntity` produces a Ghost. Passive Spirit movement and the broader creature framework are present. `PharaohlEntity` alternates tangible/intangible phases and summons Mummies. The notebook gives the name's pronunciation as "feral". These checks establish source behavior, not a newly completed gameplay test.

**Tier progression:** `HauntSpawnHandler` contains the broad historical tier progression: Spirits and occasional Zombies at Natural, Skeletons and Witches at Supernatural, Ghosts at Dead Rising, Phantoms/Haunts at Demonic, Ghasts at Abominable, and relaxed Enderman restrictions at Eldritch. Supernatural Skeletons have their held equipment removed for melee behavior. The handler limits these changes to natural Overworld spawns; commands, spawners, and other explicit sources retain their own behavior. Haint witch-hut encounter spawning is implemented separately, while the notebook's temporary Haunt reduction/cap is not established by that handler.

**Energy machines:** The ectoplasm generator and enchantment extraction generator are implemented. The latter calculates energy from enchantment strength and returns the disenchanted item. The Ectchantment Machine provides the Demi-Tangible/material-processing theme. Fluid-fed ectchantment, ordinary-fuel options, a separately named Possessor, and Ectocleanser components remain source proposals; they are not implied by the existing machines.

**Purification:** Current heated-cauldron recipes use raw ectoplasm and Grave Moss. In a full heated water cauldron, one raw ectoplasm makes liquid ectoplasm; Grave Moss can create holy water, and combining the counterpart ingredient creates purified ectoplasm. This differs from the notebook's four-raw-ectoplasm fluid conversion and Blood Bloom ritual recipes. The station's 100,000 FE recipe and the current generator economics remain as recorded in the previous entry.

**Graves:** Existing own-name anchoring captures part of the notebook's grave idea. It is not the proposed choice to detach a spirit, recover a body, keep inventory through consecration, or select the nearest named Grave. Those ideas remain distinct backlog items.

**Furnaces:** The notebook explicitly revises an earlier raw-ectoplasm-only, 2x Haint Furnace idea and later distinguishes Haunt, Haint, and Vortex concepts. The current implementation instead uses ordinary fuel with 1x-10x tier scaling and Haint heat preservation. Historical proposals were retained without replacing or mislabeling the current rules.

**Gear and world generation:** Bloodstone gear, Spiritwood, Graveyards, Grave Moss, and fluids exist. This does not establish hostile Spiritwood trees, spiritual cloth/thread, spirit villagers, Blood Blooms, soul-based garb, or custom pipe/tank machines. Targeted searches did not find those named systems registered in the current source.

#### Design conflicts and choices preserved

- Ectchantments appear first as a flexible raw/pure/fluid ectoplasm machine, then as a Possessor that grants only Demi-Tangible. Decide whether these are variants or separate machines before implementation.
- The document offers Bloodstone tool parts and a Bloodstone modifier as alternative Tinkers' Construct integrations. Neither was selected during this documentation pass.
- The source's early linear creature-strength list is explicitly described by its own later revision as potentially inconsistent with the Haunt tiers. Use the final named-tier sketch as a design reference, without declaring a new balance order approved.
- The End is a proposed exceptional event above ordinary Eldritch behavior, not a new named Haunt tier. The existing End dimension's Eldritch baseline is a different feature.
- Early Holy Water and pure-ectoplasm costs are revised later. Blood Bloom requirements, last-hit Bloodstone drop gating, mob attraction/strength effects, and special bucket containment remain proposals rather than current recipes or loot rules.
- Garb phasing, hunger removal, health-based protection, flight, and soul bonuses include explicit uncertainty. Their interactions and item slots require a design decision before being treated as final specifications.
- Machine duplication costs and throughput sketches require reconciliation. For example, the Duplicator lists 100 buckets at 10 mB/t, 1,000,000 RF at 100 RF/t, raw-ecto input alternatives, and a Nether Star activation requirement; the source does not settle whether all costs are simultaneous or alternative operating modes.
- Original crossover references include Tinkers' Construct, Thaumcraft, Blood Magic, Witchery, Natura-style wood compatibility, and ore-dictionary processing conventions. They are historical targets, not confirmed compatible dependencies for the current Minecraft/NeoForge version.

**Verification:** Compared the source against registries, creature classes, spawn handlers, loot handling, machine processing, and cauldron code. Retained the existing source-versus-gameplay limits. No new feature is marked completed solely because it appears in the design notebook.

**Last Updated:** 2026-10-02

### 2026-10-02 - Gameplay requirements and scope decisions

The user's current decisions supersede the historical notebook where they differ. The roadmap now separates selected requirements, discussion ideas, and explicitly long-term work.

- Elementals need ranged attacks. Attack effect, damage, range, cooldown, and target behavior require design and validation.
- Intangible mobs must be able to attack both tangible and intangible mobs. Review damage rules and attack behavior without changing passive mobs into hostile mobs solely to satisfy this capability.
- Add a discoverable, passive Spirit Villager that offers the familiar trading interface for Mortal Expansion's key items and resources. Its location, spawn conditions, trade inventory, prices, stock, and restocking remain to be specified.
- Recheck mob strengths and spawn odds by mob type and exact conditions. The roadmap calls for a documented review of combat attributes, Haunt/biome/dimension/light/terrain conditions, spawn weights versus acceptance odds, and natural spawning versus encounters, summons, transformations, spawners, and commands. This review has been added as work to do, not reported as already performed.
- A portal system should exist. Its implementation, destination linking, eligibility, costs, dimension scope, safe arrival, persistence, and multiplayer behavior need consideration. Original Spirit Gates are a design reference; spirit-only travel and same-dimension restrictions are not finalized requirements.
- Duplicator and Triplicator are long-term roadmap items. Their historical recipes and tentative costs remain preserved in the dedicated long-term section.
- Spirit Journey is temporarily excluded from active work. Retain the historical concept below for future review if needed.
- Tinkers' Construct compatibility is not needed and has been removed from actionable roadmap work.

#### Spirit Journey - deferred reference

The original proposal offers a choice on death between ordinary respawn and detaching a spirit at the nearest Grave carrying the player's name, followed by body/possession recovery. It differs from the currently selected Grave anchor. Related ideas include spirit-player visibility through spectral detection equipment, vulnerability to spirit-fighting equipment, dedicated spirit armor, visible/persistent bodies, and PvP corpse looting or spawn camping. The source leaves body visibility and persistence unresolved and expects PvP to retain a risk of losing possessions.

The Doomsday Device's alternative player-recovery path through Spirit Journey is deferred with it. These ideas remain historical reference only; no implementation is planned unless the user revisits this decision. Ordinary named-Grave respawning, independent consecration proposals, and the planned portal system do not require Spirit Journey.

#### Tinkers' Construct - excluded historical reference

The notebook proposed either Bloodstone tool parts near an Alumite-like mining tier, with greater durability/handle modifier, lower speed, and stronger attack, or a repeatable modifier giving half the Diamond modifier's durability bonus and increasing mining tier, potentially once per tool component. A Bloodstone-block modifier for Demi-Tangible and Haunt-scaled damage was another sketch. These compatibility ideas are excluded from the current project scope.

**Verification:** Checked the three documents for consistent scope and status. This update changes documentation only; no gameplay implementation, new balance audit, client launch, commit, or push was performed.

**Last Updated:** 2026-10-02

### 2026-10-02 - Bloodstone tool baseline and scaling

The user selected **Natural Haunt** as the tier where Bloodstone tools should match their Diamond counterparts in power and speed. Higher Haunt tiers should increase power and speed. This supersedes the original notebook's Diamond-like Void baseline for tools; it does not select a new armor baseline.

The roadmap records Diamond parity, higher-tier scaling, and validation as planned work. Per-tier values, limits, the controlling Haunt source, and Void behavior remain to be determined. No gameplay values were changed.

### 2026-10-02 - Grave Moss fire cleansing

Added the user's requirement: throwing Grave Moss into fire should decrease Haunt in the fire's chunk by one point per item consumed. The roadmap includes checking stacked items, avoiding duplicate reductions, respecting the minimum Haunt value, and applying the effect to the correct chunk. This is planned behavior; no gameplay code was changed.

### 2026-10-02 - Haunt effect proposal accepted for the roadmap

The user accepted the proposed ambient effects, beneficial effects, protection, encounter rewards, and initial balance values for inclusion in the roadmap. The user also constrained chunk-based effect evaluation to the **4x4 chunks around the player**, sixteen chunks covering 64x64 blocks. Chunks outside that footprint must not influence the effects; personal Haunt remains separate. The exact footprint alignment and aggregation of its chunk values are still unspecified.

The accepted controller checks every thirty seconds, permits one event at a time per player, and gives a sixty-second cooldown after an event. Starting chances per eligible check are Natural 2%, Supernatural 5%, Dead Rising 8%, Demonic 12%, Abominable 16%, and Eldritch 20%; Void has no ambient roll. Harmless atmosphere has higher selection weight than disruptive events. Beneficial world events share a chunk cooldown, whose duration remains to be set.

The roadmap now contains the complete effect tables: harmless sounds, two-second shadows, three-second Slowness II, safe one-block shoves, main-inventory swaps, five-second curated potion effects, nearby passive Spirits, Spiritwell Ghosts, single-recipe furnace completion, single-item plank/cobblestone conversion, and one-durability Ghost's Garb visitation repair. Ghost's Garb and Spiritwells remain dependencies for their corresponding effects.

Protection includes full Ghost's Garb immunity to negative ambient effects, a sixty-second Holy Water blessing with one-point personal cleansing on a cooldown, and Ward suppression of negative ambient events. Encounter Haint defeat removes five points and caps its chunk for ten minutes; successful resolution of the rare Eldritch The End event removes twenty points from affected chunks and caps them for twenty minutes. Encounter completion, effect weights, Holy Water cleansing cooldown, and The End trigger/footprint/resolution still require detailed design. Ordinary mob combat and the existing Haunt simulation retain separate implementation responsibilities.

The user also accepted **personal Haunt** for Bloodstone tool scaling, with +10% power and speed per tier above the Natural Diamond baseline, up to +50% at Eldritch. This resolves the earlier open scaling-source and initial-value questions. Grave Moss fire cleansing remains one point per consumed item in the fire's chunk, clamped at zero.

These are accepted roadmap specifications and starting balance targets, not completed features. Gameplay code was not changed, the client was not launched, and documentation remains local pending a requested commit/push.

### 2026-10-02 - Haunt-altering multiblocks retained as a concept

The user requested preserving the full multiblock discussion in the roadmap **as a concept**, because the mechanics are not yet settled. The concept is naturally occurring structures that substantially increase or decrease local Haunt, with player-built equivalents providing the same effect at their construction location.

The roadmap retains the tentative Haunting Altar/Consecrated Shrine names, material palettes, natural locations, inward/outward particles and sounds, a shared arrangement with different centerpieces, complete-structure activation, repairable natural ruins, and passive operation with possible optional offerings. Suggested daily influence is five points in the central chunk and one in each of eight neighbors, bounded by 0-69. Overlap caps, opposing influence cancellation, loaded-chunk-only operation, and no accumulated unload catch-up remain design suggestions.

The accepted 4x4 area for player-centered effect evaluation is recorded separately from the proposed 3x3 structural influence area. Current source inspection established that sleeping across a day boundary runs the existing daily Haunt simulation once; a structure using that scheduler would pulse too. Structure timing, limits, fuel/costs, stacking, and interaction with Wards, natural recovery/spread, and encounter caps require a later decision.

This concept has not been promoted to an accepted implementation specification. No gameplay code was changed or client launched.

### 2026-10-02 - Documentation publication

The user requested committing and pushing the documentation to `dev` and `main`, then returning the local checkout to `dev`. This release contains the project overview, dated notes, original design references, selected gameplay requirements, accepted Haunt effect proposal, and tentative multiblock concept. The gameplay baseline remains `5de1c69`; this release changes documentation only.

Use fast-forward updates for branch synchronization and verify both remote branch tips after publication. Keep the local checkout on `dev`. No client launch is needed for this documentation release.


### 2026-10-03 - Spirit Journey stage one: death-screen alternative mode

The user reopened Spirit Journey, then requested splitting it into smaller stages and implementing only entry into an alternative game mode on death first. This supersedes the earlier deferral for staged work. The broader proposal remains: optional self-Grave respawn, grayscale spectator-style movement, corpse inventory recovery, and spirit barriers.

Stage one offers a Spirit Journey button only when a dead player has a valid selected self-named Grave. The server checks eligibility for display and again when the choice is accepted. The existing Grave respawn positions the replacement player; a one-shot copied attachment changes its mode to vanilla Spectator and is cleared immediately. Ordinary Respawn, death drops, placed-Grave consumption, one-point personal Haunt cost, and naturalized Grave reuse retain their existing behavior. Vanilla persists the resulting game mode normally; administrators can leave it with ordinary game-mode commands during this first stage.

Corpse creation/recovery, grayscale, spirit barrier enforcement, and a dedicated journey exit are deliberately outside this implementation stage. The Book of Ectcetera documents this limit. The earlier larger implementation draft was not applied to `dev` or `main`. This stage is being build-checked on an isolated branch before updating `dev`; no Minecraft client has been launched.


The initial isolated stage-one commit compiled and passed the existing rule/guide checks. Review also corrected the custom respawn caller to replace the connection's player reference, matching vanilla respawn handling. Final build/mod-loading validation of that correction is pending; client gameplay remains untested.


**Final verification:** The corrected stage-one code at `42dad13` passed all existing rule checks, Book of Ectcetera validation (19 chapters, 89 pages), Gradle build, and mod loading through data generation: [GitHub Actions run](https://github.com/Howierux/mortal_expansion/actions/runs/37154102287). This verifies compilation/loading, not an in-world death-screen session. The passing code and this documentation are being fast-forwarded into `dev`; `main` remains unchanged. In this first stage, use `/gamemode survival` with command permission to leave spectator mode during testing. Corpse recovery, grayscale, and spirit barriers remain deferred.
