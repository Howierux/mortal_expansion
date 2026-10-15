# Mortal Expansion - Notes

## Progress Log

### 2026-10-04 - Code-authored data generation

Added independent NeoForge/Mojang providers for all 344 existing block states,
block/item models, recipes, block loot tables, and tags across block, item,
entity, damage-type, and biome registries. Runtime item/block registration stays
in `ModItems` and `ModBlocks`; providers run only through `runData`.

Moved verified output to `src/generated/resources` and removed its handwritten
duplicates. The migration preserves prior content, including recipe counts and
categories, unlock policy, empty extension tags, leaf/ore drops, named loot
random sequences, and the mod's existing bottom-corner stair rotations.
Standard NeoForge shape helpers replace exhaustive wood state tables where
their output matches. The generated cache is ignored and excluded from the jar.

`tools/check_datagen.py` checks a semantic snapshot of the original 344 resources,
duplicate paths, and mod-owned model/texture references. CI regenerates and
rejects stale output. See [Data generation](DATA_GENERATION.md) for authoring and
version-migration instructions. Textures, translations, the custom Book unlock
advancement, and worldgen definitions remain authored resources.

Validation passed: full data generation, Gradle build, all 19 headless server
GameTests, the 344-resource semantic/reference check, and the 20-chapter/100-page
guide check. Regeneration wrote zero changed files. The built jar includes the
generated recipes, states, and tags and excludes generator cache metadata.

### 2026-10-04 - Playtest apparitions, corpse recovery, and journey beacons

The shadow Haunt effect now renders a Steve-shaped Herobrine apparition with white eyes at 75% opacity, distinct from the 20% intangible mobs. It still disappears when looked at directly, approached, or after two seconds.

Choosing Spirit Journey creates an invulnerable, persistent owner-linked corpse at the death site. Returning within two blocks in the active journey restores the previous non-spectator game mode and held death items, then consumes the corpse. Recovery checks terrain clearance and prevents repeated item recovery. Keep Inventory and Vanishing keep their existing handling. The body uses the owner's available skin; full equipment rendering is not implemented.

Spiritwells emit pale cyan vanilla beacon beams only for the player's active Spirit Journey. Server state is sent on entry, login, and recovery; ordinary spectator mode does not reveal the beam. Rendering extends up 1024 blocks with horizontal visibility up to 256 blocks for loaded wells. Disconnect clears the client state.

Validation: Gradle build and all 19 headless server GameTests passed, including corpse location, owner persistence, linked-only recovery, and no duplicate items. Client visuals and full manual restart/reconnect or cross-dimension journeys still need playtesting.

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

### 2026-10-03 - Spirit Journey stage two: defer death items until choice

The user selected item handling as stage two: choosing Spirit Journey must not drop items, and dropping must wait for a selection. For a player eligible for Spirit Journey at death, the server saves the actual vanilla ItemEntity drops into a serialized, copy-on-death attachment before removing them from the world spawn collection. Pending items survive player save/load. Ordinary Respawn emits them at their original position and dimension; Spirit Journey moves them into held batches for later corpse recovery. Missing dimensions or failed emissions remain queued for a later respawn/login retry.

Players without a valid self-named Grave use ordinary immediate drops. Keep Inventory and Curse of Vanishing are processed by vanilla before capture. Item counts, components, armor, offhand, and entity drop data are retained; XP handling is unchanged. Held journey batches do not auto-release on login or subsequent ordinary respawns. A still-dead reconnect preserves the pending choice.

This stage does not create a corpse or expose recovery yet. Grayscale, dedicated spirit movement, barriers, and exit/recovery remain later work. The guide now explains the held inventory limit. Validation is being run on an isolated branch with real headless server death/respawn GameTests, including player serialization, both choices, no-Grave drops, Keep Inventory, and Vanishing; no Minecraft client has been launched.

**Stage-two verification:** Code at `7a20dde` passed the existing standalone rules, guide checks (19 chapters, 91 pages), Gradle build, mod loading through data generation, and all four real headless GameTests: [GitHub Actions run](https://github.com/Howierux/mortal_expansion/actions/runs/37155318364). The tests exercise fatal inventory handling, both respawn choices, pending-item player save/load, preserved stack components/counts, Vanishing, no-Grave drops, and Keep Inventory. This tests the serialized data path; a full process restart/reconnect and client death-screen session remain untested.

The first headless launch exposed eager loading of client screen handlers during common packet registration. Grave-name and Spirit Journey clientbound handlers now use deferred lambdas, allowing dedicated-server startup. CI also requires the completed four-test success summary, since the Gradle launch task alone can return success after a server startup failure. The passing code and this documentation are being fast-forwarded to `dev`; `main` remains unchanged. Held items have no player recovery action until the later corpse-recovery stage.

### 2026-10-03 - Spiritwells: rare intangible worldgen anchors

The user requested rare world-generated Spiritwells resembling a low-opacity floating Nether Star, with blocks unable to be placed in a small surrounding area. The follow-up specifies floating about two blocks above the surface. This stage adds only the anchor and protection; additional Haunt effects and Spirit Journey interactions remain later work.

Defaults: one placement attempt per 256 newly generated Overworld chunks, subject to valid dry-land/clearance checks; visual center two blocks above the surface with a small bob, a slow camera-facing rotation, and 22% opacity. A dedicated collision-free block entity makes the anchor persist with its chunk and provides a stable type for later mechanics. It has no item, recipe, drops, gameplay tick, or collection/mining behavior. Placement is denied within a spherical radius of two block positions, including multi-block placement, piston destinations/heads, and fluid-created solid blocks. Nearby checks only inspect loaded chunks.

Generation rejects water, canopy/log tops, and occupied protection volumes, without removing terrain. Existing explored chunks are not retroactively populated. The Book of Ectcetera documents both current behavior and the future-mechanics limit. Validation is being run on an isolated branch, including four additional real headless GameTests for decoded worldgen, height/clearance, persistence, collision, actual item placement rollback, multi-placement, piston movement, and fluid solidification. Client appearance has not been visually tested.

**Spiritwell verification:** Code at `a1dbbcb` passed the standalone rule checks, guide validation (20 chapters, 94 pages), Gradle build, mod loading through data generation, and all eight headless GameTests (four Spiritwell checks plus four existing deferred-item checks): [GitHub Actions run](https://github.com/Howierux/mortal_expansion/actions/runs/37161237366). The positive worldgen test verifies the exact two-block surface offset, collision-free state, and block-entity save/load. Negative tests verify water/occupied-volume rejection without terrain removal. Actual BlockItem placement tests verify denied placement leaves both the world and held item count unchanged and that placement outside the radius still succeeds. Multi-placement, piston destinations, and fluid-created solids are checked through the registered event bus. Client rendering was compiled but not visually inspected, and no client was launched. The passing implementation and docs are being fast-forwarded to `dev`; `main` remains unchanged.

### 2026-10-04 - Hold Spiritwell-spawn effects until a loaded well is nearby

The user specified that, if the Haunt effect which spawns a spirit at a nearby Spiritwell triggers but there is no loaded Spiritwell within 100 blocks, the effect must be held until the next time a player is within 100 blocks of a Spiritwell. The existing accepted event table calls this the Spiritwell Ghost effect; its hostile Ghost type remains unchanged by this proximity request.

The roadmap now records the selected rule: retain the trigger rather than discard it or roll again; a later player can be different from the triggering player. Only a loaded well in the nearby player's current dimension qualifies, and lookup must not generate or force-load chunks. Pending events should survive saving/restarting, and failed delivery under existing cap/protection/safe-spawn rules stays pending. The 100-block well lookup is separate from the accepted sixteen-chunk Haunt-evaluation footprint. Queue ownership, stacking, and release cadence remain scheduler design details.

Current source review confirms that Spiritwell anchors and ordinary Haunt-controlled mob spawning exist, but the ambient Spiritwell Ghost event/scheduler does not yet exist. This update changes its specification only; it does not introduce an unused queue or activate random encounters. Implementation and gameplay validation remain unchecked. No build or gameplay tests are needed for this documentation-only change.

### 2026-10-04 - Ambient and beneficial Haunt effects

The user authorized implementation on dev, selected ambient effects before major encounters, and selected Ghost’s Garb tag hooks before creating armor. The follow-up supersedes the former sixteen-chunk evaluator: personal Haunt alone controls all trigger odds and minimum tiers, including world changes and mob appearances.

The shared server controller checks every 30 seconds, uses the accepted 2/5/8/12/16/20% tier probabilities, favors harmless atmosphere by weight, and gives each completed event a sixty-second player cooldown. Creative, spectator, and dead players are skipped. Furnace/material events share a persisted sixty-second affected-chunk cooldown. Active effects do not overlap for a player.

Implemented operations: quiet nondamaging rear sounds; render-only two-second spectral figures which disappear on approach/direct observation; three-second Slowness II; a checked one-block backward displacement; two occupied main-inventory slot swaps; five-second curated potion effects; passive Spirit appearances within five blocks with nearby-event-mob suppression; and real Spiritwell Ghosts with warnings, safe locations, loaded-only lookup, and mob limits. Missing/blocked wells retain each selected event in a persisted per-dimension count. Any eligible player within 100 blocks can receive it without a new roll or tier check; release is limited to one per second/dimension plus player cooldown and encounter limits.

Beneficial operations finish one valid active vanilla/Haunt/Haint furnace recipe through vanilla cooking (including XP ledger) or convert one carried vanilla plank/cobblestone with output-room validation. Existing components and stack counts are preserved. Ghost’s Garb repair/immunity are wired to the empty ghosts_garb item tag; armor content and major Haint/End encounters are not added.

Holy Water contact refreshes sixty seconds of negative-event protection and reduces personal Haunt by one point at most once per minute. Only haunting-owned negative potion effects are removed. External potion applications relinquish provenance so the blessing cannot erase merged/replaced unrelated potions. Ward-covered chunks suppress negative ambient events while allowing beneficial effects. These rules do not block ordinary enemy attacks.

Validation is underway on an isolated branch: ten new real server regression tests plus effect policy checks and the existing eight server tests. Client shadows/sound presentation is compiled but not yet visually verified. No client has been launched.


**Implementation clarifications:** The user confirmed that Spiritwells will eventually be added to the Nether and End. Queue ownership stays per dimension so those triggers remain saved until wells exist there; this update does not generate wells in those dimensions. Held selections are not active events and do not spend their cooldown before delivery, allowing entry into range to release a held selection immediately when no other event is active. Delivery searches all loaded nearby wells rather than getting stuck on a blocked nearest well. Same-tick spawn reservations prevent multiplayer event spawns from exceeding the vanilla cap snapshot. A boundary regression verifies that 100 blocks is included and 100.01 blocks is excluded.

**Final ambient-effect verification:** Code at `4a1a0a3` passed all standalone rule checks, the effect-tier/protection policy check, Book of Ectcetera validation (20 chapters, 100 pages), Gradle build, mod loading through data generation, and all eighteen real headless GameTests: [GitHub Actions run](https://github.com/Howierux/mortal_expansion/actions/runs/37212500685). Ten new effect regressions cover personal-only eligibility, player cooldown serialization, Holy Water provenance and cleansing cooldown, stronger external potions, Ward suppression with beneficial effects allowed, inventory stack/component conservation, output-room validation and persisted shared chunk cooldowns, one-recipe furnace completion with vanilla XP ledger, wall/lava/fall-safe displacement, saved deferred queues and delivery without a new tier roll, passive Spirit repeat prevention, and the exact 100-block/removed-anchor boundary. The existing eight death-item/Spiritwell regressions also pass.

The empty default garb tag means actual armor behavior awaits its future content; the hook and Dead Rising repair guard are compiled. Shadows/sound presentation is compiled but not visually verified, and a full process restart/reconnect was not manually exercised. No client was launched. Passing code and final docs are being fast-forwarded to `dev`; `main` remains unchanged. Major Haint/End encounters and Nether/End Spiritwell generation remain deferred by the selected scope.

### 2026-10-04 - Spirit Journey grayscale

Playtesting confirmed that the deferred grayscale stage was absent. Added a client-only
render-frame handler using GameRenderer post-processing APIs without a new mixin.
The generated post-chain uses vanilla color_convolve with zero saturation and a
blit pass. Active server-synced journey state controls activation; recovery, loss
of spectator mode, and disconnect remove the owned effect. Resource reloads and
camera changes reapply it. HUD/menu colors remain unchanged. Build/data-generation
checks passed; visual confirmation is still required after restarting the client.

### 2026-10-04 - Crafted Ghost�s Garb and Ectothread

Implemented the user-selected full set with leather defense and iron durability.
Ectothread uses eight String around Raw Ectoplasm, yielding one. Standard armor
patterns consume Ectothread. Vanilla lime wool and default lime-dyed leather models
serve as placeholders. Full-set abilities are derived from equipment: horizontal
phasing with existing spectral barriers, intangible damage rules, and negative
ambient haunt protection with owned-effect cleanup. Intangible attackers now
qualify as spectral damage in the shared combat API. Admin/void bypass sources
remain effective. Narrow mixins isolate collision, server wall validation, client
wall ejection, and translucent player-body rendering; no game-mode/noPhysics
state changes are needed. Vanilla floor collision remains intact. Models, recipes,
advancements, and armor tags are generated. Build, client mod/mixin loading through
data generation, 367-resource checks, guide checks, and all 23 headless server
tests passed. Manual client visuals, movement prediction, and full combined
Spirit Journey/garb playtesting remain pending.

### 2026-10-04 - Grave inscription face/depth correction

Client playtesting found floating or hidden inscriptions across grave rotations
and placement thirds. The renderer used a fixed centered anchor, ignored DEPTH,
and offset toward the opposite face. Added a state-derived front-plane anchor
for all four facings and three depths, 0.002 blocks outside the actual front
surface (clearing the natural shimmer expansion). Corrected the text X scale
for readable front-facing text, enabled normal depth testing, and capped text
width at 0.88 blocks. The geometry regression checks all 12 face/depth combinations.
Client visual confirmation remains pending after restart.

### 2026-10-04 - Cauldron bucket prediction and smelting vessel returns

Client playtesting found Holy Water placed above the cauldron immediately after
bucket extraction. The interaction listener had skipped client-side consumption.
Matching cauldron interactions now consume clicks on both sides, while only the
server changes inventory/blocks; server cancellation uses CONSUME. Added matching
Purified Ectoplasm refill handling. Regression tests check the three mod fluids,
one filled bucket, empty cauldron, no fluid above it, and deliberate refill.

Vanilla furnace cooking does not use crafting remainders. A scoped burn-path
mixin returns one empty bucket in the vacated input slot only after successfully
smelting Holy Water, Liquid Ectoplasm, or Purified Ectoplasm. The same path serves
mod furnaces and instant Haunt cooking. Existing recipes and JSON remain unchanged.
Tests check Holy Salt output, the returned bucket, no duplication on subsequent
ticks, and blocked-output preservation. Build and all 26 required server tests
passed. The original client interaction needs retesting after restart.

### 2026-10-04 - Grave Moss on sapling-grown Spiritwood leaves

Spiritwood saplings now decorate newly grown canopy leaves with Grave Moss after
successful growth, including bone meal. Each exposed suitable face has a 25%
placement chance, with at least one patch when a valid face exists. Decoration
uses vanilla multiface attachment/survival and merges patches without replacing
occupied blocks. Preexisting Spiritwood leaves are excluded, and stage changes
or failed growth do not decorate neighboring trees. Natural worldgen definitions
remain unchanged. The bounded scan is isolated in SpiritwoodSaplingMoss and covers
the current 4-6-block trunk/radius-two foliage; adjust those bounds if the tree
configuration grows. No new mixins, saved data, or resource schemas are required.
Build and all 27 required server tests passed, including actual sapling growth
and moss attachment checks. Already-grown trees are not retroactively decorated.

### 2026-10-04 - Cross-mod magical damage bypass

The user selected magic/magical-tagged damage as an intangibility exception.
DemiTangibleDamage now checks NeoForge�s standard is_magic damage-type tag and
recognizes damage tags whose final path component is magic, magical, is_magic,
or is_magical in any namespace. This uses registry tag membership rather than
hardcoded mod damage IDs or broad substring matches. Shared behavior covers
intangible mobs and Ghost�s Garb wearers. Ordinary armor and other damage/effect
rules remain applicable. NeoForge�s tag also categorizes vanilla poison, Wither,
Thorns, and dragon breath as magical. Build and all 28 required server tests
passed, including actual magic hits on both target types, continued ordinary
fire immunity, cross-namespace tag names, and a non-magical tag rejection.

### 2026-10-04 - Demi-tangible weapon damage tagged as magic

Player and mob melee hits with demi-tangible weapons now use dedicated generated damage types in neoforge:is_magic. Item tags, the Demi-Tangible component, and legacy enchanted items share one predicate. Ordinary weapons remain physical; armor mitigation, normal difficulty scaling, death messages, and attacker/weapon credit are preserved. A focused damage-source factory mixin is needed because incoming-damage events cannot replace their source. NeoForge registry data generation owns the damage type definitions and tags. Data checks pass for 370 resources; build and all 29 required server GameTests pass, including real mob weapon damage against Ghost Garb, item-tagged Bloodstone weapons, and source attribution.

### 2026-10-04 - Purified Ectothread and Haint Garb

The original Ectothread recipe now yields eight. Eight String around Pure Ectoplasm yield eight Purified Ectothread with a light blue wool placeholder. Four Haint Garb pieces craft in standard armor patterns using purified thread; their item and worn armor placeholders use light blue dyed leather. Both garb sets share a material builder for leather defense and iron durability, with separate repair ingredients and stable item/material IDs. A shared spectral_garb tag supplies existing phasing, translucency, combat immunity, and haunt protection, including mixed Ghost/Haint sets. Haint armor supports normal dyeing and enchantments and appears in the creative tab. A later Spectralite combination remains planned. Recipes/models/tags/advancements are generated in code. Build and all 31 required GameTests pass; data-generation checks cover 387 resources and the Ectcetera guide check passes.

### 2026-10-04 - Immediate client Spirit Torch barrier updates

The client LevelChunk skips block onPlace/onRemove callbacks, leaving its loaded-chunk torch index stale after placement or removal. SpiritTorchClientUpdatesMixin observes completed Level.onBlockStateChange transitions on the client and delegates to the shared tracker. Both standing and wall torches update immediately; repeated updates are idempotent and server corrections after denied predicted breaks restore the correct boundary. Server lifecycle callbacks and chunk load/unload discovery remain intact. Version-sensitive hook rationale is recorded in GHOST_GARB.md. Build and all 32 required server GameTests pass, including immediate endpoint removal and correction transition regression checks; actual client movement still requires playtesting.

### 2026-10-04 - Spirit Villager merchant and grave workstation

Spirits now use the vanilla villager model/base texture with spectral opacity and the vanilla merchant screen, inventory and offer codecs. Their stable entity ID, passive behavior, intangible defenses, and shared horizontal movement rules remain. A grave engraved Villager (case insensitive) claims one nearby adult jobless villager. Ownership and original villager provenance persist; breaking the workstation restores only its grave-created Spirit to a jobless villager, including after trading or reload. Natural/legacy Spirits do not revert. Name, age, original biome/gossip, current equipment/inventory and health are retained; vanilla conversion allocates a new UUID. Loaded checks do not force chunks, and workstation dimension is persisted. Initial trades cover Raw Ectoplasm, both thread types, and Holy Salt; stock replenishes once daily, with grave workers requiring their nearby workstation. Catalog and workstation rules are isolated from entity APIs; no new mixins or block-state-only POI registration needed. Build and all 35 required server tests pass including actual merchant payment/output/stock and engraved-grave ownership/reversion. Data and Ectcetera checks pass. Client appearance and manual interaction remain for playtest. See SPIRIT_VILLAGERS.md.

### 2026-10-04 - Recipe-balanced Spirit trade expertise

Spirit merchants now progress through Novice, Apprentice, Journeyman, Expert and Master using vanilla cumulative merchant-XP thresholds 10/70/150/250. Synced expertise/XP feed the normal menu and XP bar, promotions append their two offers without rerolling previous stock, and current offers/XP/demand persist. Thread services retain one matching ectoplasm and eight-thread recipe yield with an Emerald fee replacing String; purified thread still requires Pure Ectoplasm. Expert Ghost Garb and Master Haint Garb assembly consume every thread in their actual crafting patterns plus Emerald labor fees. Holy Salt supply is one per two Emeralds at Apprentice with limited stock. Novice raw buying/selling cannot produce an Emerald loop; Pure Ectoplasm sells for twice the per-unit raw value. Daily restocking updates vanilla demand; advanced stock is constrained. Old flat-catalog saves rebuild once as balanced Novices without changing workstation provenance or inventory. XP crediting follows vanilla Villager behavior and inherits the no-op Merchant.overrideXp so the merchant result slot cannot credit XP twice. Build and all 37 required server tests pass, including real two-input merchant payment/output, tier progression/XP/stock persistence, legacy migration and comparison with registered crafting ingredients. Generated resource checks remain at 387. Full base-price catalog is in SPIRIT_VILLAGERS.md.

### 2026-10-04 - Garb wall breathing

Full Ghost Garb and Haint Garb sets now hold their breath when the head overlaps a solid collision shape, including glass, using NeoForge LivingBreatheEvent and the existing underwater air meter. Native consumption preserves Respiration/OXYGEN_BONUS; emergence refills four air per tick. Water Breathing/Conduit Power and creative exemptions follow underwater behavior. Head-only detection avoids draining air when standing on floors or with only feet embedded. Native empty-air drowning deals one heart at its regular cadence; a narrow combat exception permits those hits only while garb wearers are inside walls. Other intangible immunities remain intact. No new mixin or persistent data is required. Build and all 39 required server GameTests pass, including actual base-tick consumption, glass overlap, depletion/damage/refill, Haint Respiration III, Water Breathing and creative behavior. Manual HUD/client playtesting remains. Migration details are recorded in GHOST_GARB.md.

### 2026-10-04 - Haint jump and Spectralite Haint smithing

Haint-family full sets gain +0.3 native jump strength, equivalent to Jump Boost III, through a transient keyed player attribute modifier on both sides. Mixed Haint/Spectralite Haint pieces retain the jump; Ghost mixtures retain phasing/haunt protection without the jump. Eight generated template-free smithing recipes upgrade Ghost parts with Pure Ectoplasm or combine Haint parts with matching Spectralite parts. Vanilla base-component preservation retains enchantments, names and wear. Combined armor shares Spectralite material, durability, item fire resistance and reduction against intangible attackers, with generated membership tags preserving phasing, negative-haunt protection and wall breathing. All three sets now have benefit tooltips. The small supported smithing serializer extension inherits vanilla assembly and makes only intentionally empty templates complete; no new mixins or saved-state migration required. Build and all 42 required server GameTests pass, including real smithing menu consumption, all eight upgrades, rejection rules, recipe network roundtrips, actual jump/removal, defense/durability/spectral reduction and wall air. Data-generation checks pass for 400 resources. Manual client appearance and jump remain for playtesting; details in GHOST_GARB.md.


### 2026-10-05 - Magic and magitech proposals added to planning

At the user's request, the Roadmap now includes all 22 candidates from FEATURE_IDEAS_RESEARCH.md, grouped by investigation, ritual/spirit relationships, industry, and equipment/encounters/travel. Existing-backlog expansions are identified, recommended priorities and relative scope are retained, and the proposed first design batch is recorded without treating it as an approved implementation order. Detailed source comparisons and decisions remain in the research report. Documentation-only update to GitHub dev; main and the local project are unchanged.

### 2026-10-05 - Sequential Book of Ectcetera upgrades

Book plus Rotten Flesh now crafts the starter guide. Shapeless Bone, Raw Ectoplasm and solid Pure Ectoplasm upgrades unlock cumulative related chapter groups, each ending with guidance for the next ingredient. The complete guide has 26 chapters and 114 pages, including current Ghost/Haint/Spectralite Haint guidance. The same item ID uses a persistent/synced tier component; existing unmarked books retain full contents and newly crafted/creative starter stacks carry tier zero. A supported shapeless recipe extension with partial DataComponentIngredient preserves names/enchantments/custom components and rejects skipped/repeated tiers; Java data generation supplies all recipes. Client contents links filter and recalculate page offsets per tier. No mixins or new player saves required. Build and all 46 required server GameTests pass with actual inventory-grid consumption, sequential upgrades/rejections, legacy behavior, persistence/network roundtrips and chapter offsets. Guide and 403-resource data-generation checks pass; manual client rendering remains for playtest. Published to GitHub dev only; the local playtest copy remains unchanged. See BOOK_OF_ECTCETERA.md.

### 2026-10-05 - Portable EMF reader

Added a craftable six-bulb reader for chunk and personal Haunt, with an exact labeled HUD. Either hand reads the chunk; held Use turns inward for personal readings. Network-only samples are scoped to owner, dimension and chunk and never persist in saved items. Java data generation supplies recipe/models. No shield protection is granted by its pose. Added three gameplay tests and an Ectcetera chapter. Client visual playtesting remains.

### 2026-10-05 - Precise EMF Reader

Implemented a shapeless EMF Reader + Raw Ectoplasm upgrade. Live digits on the held device and HUD show actual personal hundredths and whole-point chunk values with two decimal places. Existing hands, inward use pose, bulbs, and read-only behavior are retained. Recipes/models are generated; transient sample networking preserves hundredths without saved-world changes. Client visual playtesting remains. See [EMF Reader](EMF_READER.md).

### 2026-10-05 - Separate Haunt benefit and risk pools

Benefits and risks now roll independently with separate cooldowns; neither causes the other. Added nine available effects plus honest localized chat hints for all successful ambient events. Six proposed benefits depend on missing systems and are explicitly marked `missing function` and excluded. Encounter placement/caps/protection and saved-state migration are documented in [Personal Haunt effects](PERSONAL_HAUNT_EFFECTS.md). New furnace interference pauses without consuming materials or fuel. Manual visual and extended live AI testing remain.

### 2026-10-05 - Named discoveries and spectral footprints

- [x] Per-player Spiritwell/Cemetery discovery within 20 blocks, stable generated names, saved records retained on death, and `/spectralsites` listing.
- [x] Second Sight now draws spectral footprints toward the nearest recorded undiscovered site in the player's dimension. Names and coordinates provide a future TP hook.
- [ ] Teleport costs/permissions/safe landing, hidden inscription revealing and never-generated-site locating remain missing functions.
- [ ] Manual footprint, Cemetery elevation and restart/death playtesting.

See [Spectral discovery](SPECTRAL_DISCOVERY.md).

### 2026-10-05 - Spectral Insight and aware books

- [x] Raw-or-higher books gradually reveal secret families through beneficial personal-Haunt events, with whispers and next-step recommendations.
- [x] Ectothread, Ghost Garb, Haint Garb and combined Spectralite Haint recipe knowledge, filtered guide chapters, synchronized book memories and persistent player learning.
- [x] Actual crafting/smithing locks and teachable vanilla Crafters; legacy recipe unlocks and full guides retain knowledge.
- [x] Build, 67 required server tests, 127-page guide checks and 413-resource data checks.
- [ ] Manual guide refresh, whispers and recipe-book visuals; other-mod automation integration remains a missing function.

See [Spectral Insight](SPECTRAL_INSIGHT.md).

### 2026-10-05 - Basic EMF reader presentation

Corrected the basic reader face orientation for both hands and personal scanning, and removed its HUD overlay. The Precise reader retains its numeric display/HUD. Revised in-game visual verification remains pending.

### 2026-10-05 - Upward intangible mob movement

Intangible mobs now rise through ordinary blocks while floors still block descent. Spectral materials, Holy Salt and Spirit Torch barriers remain effective across swept upward movement. Spectral pursuit steers toward higher targets; tangible phases and player garb movement are unchanged. See [Upward phasing](UPWARD_PHASING.md). Build and 71 server tests pass; live movement playtesting remains.

### 2026-10-05 - Garb drowning correction

Ghost, Haint and Spectralite Haint wearers take normal drowning damage underwater as well as from wall-air exhaustion. Vanilla air/Respiration rules and other intangible defenses remain unchanged. Build and 72 required server tests pass.

### 2026-10-05 - Haunt-reactive farmland

Bloodstone/Spectralite tilling creates special farmland with tier-dependent bonemeal pulses based on chunk Haunt. Dead Rising and higher maintain full moisture. Ordinary farming survival, tool wear and dirt drops remain; losing farmland clears its bonus. Build and 77 server tests pass. See [Haunted farmland](HAUNTED_FARMLAND.md). Visual/balance playtesting remains.

### 2026-10-05 - Active Spectralite Hoe growth

Right-click crops with the Spectralite Hoe to apply repeated bonemeal toward maturity, at one target-chunk Haunt point per accepted pulse. Empty chunks stop further growth; mature/unsupported targets and event vetoes cost nothing. Bloodstone and normal tilling behavior remain unchanged. Build and 82 required server tests pass. See [Spectralite Hoe growth](SPECTRALITE_HOE_GROWTH.md).

### 2026-10-05 - Area shovels

Bloodstone/Spectralite shovels mine a centred vertical 3x3x1 face; sneaking preserves single-block digging and normal paths. A main-hand Spectralite shovel charges for two seconds and releases up to 64 face-connected shovel-mineable blocks. Native drops, enchantments, durability and protections are preserved. Build and 88 server tests pass; client aiming/feedback playtesting remains. See [Area shovels](AREA_SHOVELS.md).

### 2026-10-05 - Worn pickaxe speed and Haunt repair

Bloodstone/Spectralite pickaxes approach 2x mining speed as they wear and repair one durability point at tier-dependent intervals (Natural 60s to Eldritch 2s). Repair costs 0.01 current-chunk Haunt and is disabled at Void. Chunk hundredths preserve the tiny cost; the Precise EMF reader now shows them. Build and 94 server tests pass. See [Haunt pickaxes](HAUNT_PICKAXES.md); client/balance playtesting remains.

### 2026-10-06 - Tree axes and targeted Haunt testing

Bloodstone/Spectralite axes fell connected tree logs with normal harvesting and durability; sneak for one log. Leaves decay normally. Spectralite sapling bonemeal costs 0.10 target-chunk Haunt. Operators can trigger specific or random events with `/mortalexpansion haunt effect <player> <effect|random>`, retaining underlying protections and effect requirements. See [Tree axes](TREE_AXES.md) and [Haunt commands](HAUNT_EFFECT_COMMANDS.md). Build and all 103 required server tests pass; guide validation passes with 30 chapters and 128 pages. Client and balance playtesting remain.

### 2026-10-06 - Prevented haunting feedback

Full Ghost, Haint and Spectralite Haint sets now retain the normal personal-tier negative event chance and weighted selection. A successful roll shows the action-bar message "Your spectral garb prevented a haunting." and starts the existing 60-second risk cooldown without executing the effect, assigning an active negative duration or queuing a ghost. Benefits remain independent. Failed rolls, Void and cooldown suppression produce no prevention message. Blessings/wards without garb keep their existing suppression behavior. No saved-data or networking changes.

Validation: build and all 105 required server tests passed, including all three spectral sets, successful/failed rolls, Void, risk cooldown and independent benefits. Guide and generated-resource checks pass; action-bar visuals remain a client playtest check.

### 2026-10-06 - Shovel face targeting

The normal Bloodstone/Spectralite shovel area follows the targeted block face, independently of player facing: side faces give vertical planes, top/bottom faces give horizontal planes. Mining-start face is retained through STOP and cleared on ABORT; a matching native ray hit supports server-driven breaks. Unknown faces do not trigger an area. The charged connected dig and ordinary harvest/protection semantics remain. Build and 107 server tests pass, including all six faces with both shovels, changed aim during mining and aborted targeting. Guide/resource checks pass; client aiming remains a playtest check. See [Area shovels](AREA_SHOVELS.md).

### 2026-10-06 - Single-click Spectralite sapling growth

One Spectralite axe right-click repeats native bonemeal until the sapling grows, charging 0.10 target-chunk Haunt once after the first accepted pulse. Free retries keep normal grower hooks, stages and tree selection. Growth stops on a veto, unsupported target or 64-attempt safety limit; a remaining sapling gets a space/conditions hint. Initial rejection spends nothing, while an accepted blocked attempt pays the single starting cost. No saved-state changes. See [Tree axes](TREE_AXES.md).

Validation: build and all 108 server tests pass, including stage-zero Oak/Spiritwood growth in one click, one-payment retries under blocked growth, insufficient funds and initial bonemeal cancellation. Guide/resource checks pass; client feedback and growth balance remain playtest checks.

### 2026-10-06 - Upward phasing for spectral-garb players

Full Ghost, Haint and Spectralite Haint wearers can move upward through ordinary ceilings using the shared mob swept-volume check. Floors, spectral-solid materials, Holy Salt and Spirit Torch barriers remain; unloaded columns cannot be crossed. No flight or automatic ascent is added. Existing breathing, Respiration and drowning behavior remain. Build and all 110 server tests pass; guide and resource checks pass. Client jumping/swimming and movement feedback remain playtest checks. See [Upward phasing](UPWARD_PHASING.md).

### 2026-10-06 - Sapling growth attempt limit

Spectralite axe sapling growth stops after 15 total native bonemeal attempts per click if the tree has not grown. The initial accepted attempt is included in that total and the single 0.10 chunk Haunt payment remains unchanged. The blocked-growth test asserts exactly 15 attempts.

Validation: build and all 110 required server tests passed, including successful Oak/Spiritwood growth and the exact 15-attempt blocked-growth limit.

### 2026-10-06 - Spirit unfinished-business mini-quests

Spirits offer optional mob-slaying tasks via sneak-right-click and clickable chat acceptance. Target selection favors hostile types 75%; three generated difficulty tags control mob pools, kill quotas and tiered emerald/ectoplasm/XP rewards. Progress persists without the giver, and final credited kills pay immediately with inventory-overflow drops. The previously missing Spirit Commission Haunt benefit now offers the same quest directly without a permanent NPC. Build and 115 server tests pass; guide/resource checks pass. Client/respawn/balance playtesting remains. See [Spirit quests](SPIRIT_QUESTS.md).

### 2026-10-06 - Spirit interaction buttons

Right-clicking a Spirit now opens Trade, Unfinished Business and Leave options. Quest offers have Accept/Decline/Back buttons; active quests show progress. Native merchant behavior remains behind Trade. Temporary Haunt encounters retain chat offers. Server-bound ephemeral tokens validate player, giver, distance and dialog mode and prevent replay. Build and 118 tests pass; guide/resource checks pass. Client layout remains a playtest check. See [Spirit options](SPIRIT_INTERACTION_MENU.md).

### 2026-10-06 - Graveyard Stone weathering

Stone slowly converts to Gravestone anywhere its actual biome is Graveyard, inside or outside cemeteries. Loaded chunks sample at most 64 positions per day and convert at most four blocks, with no Haunt cost or tier requirement. Surface and buried Stone can be chosen; unrelated materials/biomes and unloaded terrain are untouched. Weathering RNG is independent of existing Haunt simulation. Build and all 121 server tests pass; guide/resource checks pass. Visual and long-term rate playtesting remain. See [Graveyard Stone](GRAVEYARD_STONE.md).

### 2026-10-06 - Ectoplasmic Generator Haunt emissions

Completed raw ectoplasm burns add 1 chunk Haunt; purified burns add 3. Emission happens once on the final productive tick, so full-buffer stalls and unfinished burns cannot emit early. The fuel's pending output persists with burn progress, and old active saves infer their output from the existing base FE rate. Existing fractions and the 69-point cap are preserved. Build and 124 server tests pass; guide/resource checks pass. Long-term energy/Haunt balance remains a playtest check. See [Generator Haunt](ECTOPLASMIC_GENERATOR_HAUNT.md).

### 2026-10-06 - Machine crafting recipes

Added generated crafting recipes for the Enchantment Generator (Grindstone, Redstone, Haunt Furnace and 6 Iron), Ectoplasmic Generator (Raw Ectoplasm, Haunt Furnace and 7 Gold), and Ectchantment Machine (Enchanting Table, Purified Ectoplasm and 7 Purified Gravestone). Each yields one machine and has a recipe-book unlock advancement. Data generation, build and guide/resource validation pass; the existing 124 gameplay tests are unchanged. See [Machine recipes](MACHINE_RECIPES.md).

### 2026-10-06 - User-supplied tapping audio

The tapping HIT Haunt effect plays the supplied Warframe Rell "Rap tap tap" recording. Converted to positional mono Ogg Vorbis; generated sound definition, stable registry key, original pitch and subtitle added. Playback remains private to the affected player with existing protection/cooldown behavior. Third-party source attribution is recorded. Build, data generation, 428-resource/guide checks and all 124 existing server tests pass. Client audibility/volume remains a playtest check. See [Tapping audio](TAPPING_AUDIO.md).

### 2026-10-06 - Book research-map UI

The Book of Ectcetera now has four category tabs, a draggable/zoomable connected-entry map, locked-node guidance and parchment entry panels. Existing upgrades, ingredient handoffs, gradual Insight revelations and crafting locks remain authoritative. Unrevealed weaving names stay hidden. No saved-data or networking changes. Build and all 124 existing tests pass; guide/428-resource validation passes. GUI-scale/mouse readability remains a client playtest check. See [Research map](ECTCETERA_RESEARCH_MAP.md).

### 2026-10-06 - Book UI blur correction

The book was drawing its atlas/entry panel before calling Screen.render, whose background pass blurred the already drawn book. Background blur now runs once before custom UI; the final pass renders only registered widgets. Both map and reading panels remain crisp. Build passes; client confirmation remains a playtest check. No gameplay, progression or resource changes, and no new tests were added for this render-order fix.

### 2026-10-06 - Secrets of the Dead book section

Added a dedicated Secrets of the Dead research tab/chapter from the Raw Ectoplasm book stage onward. Insight guidance, Ectothread, Ghost Garb, purified weaving/Haint Garb and Spectralite Haint entries are grouped there, rather than scattered across material tabs. Spectralite Haint now has a separate presentation node using the existing Haint page text; the Haint entry displays only its first two pages. Each secret keeps its own Insight bit and existing personal/book-tier gates. Unrevealed entries hide their titles and text. No recipes, saved data or unlock events changed. Build and existing server/guide checks pass; client tab/layout confirmation remains pending.

### 2026-10-06 - Altar of Purity

Added a furnace-style offering altar with accumulated, persisted burn time and a bonfire-fill GUI/remaining-time tooltip. Grave Moss and Spiritwood Log add 30s; Holy Salt and Purified Spiritwood Log add 60s; Purified Ectoplasm adds 120s. One offering is consumed at most every 12.5s, with a one-hour reserve and no item waste at capacity. While burning, loaded chunks in the centered 3x3 square lose 0.10 Haunt every 10s. No chunkloading tickets or personal protection are added. Native automation, container returns, menu transfers, break drops and a generated survival recipe are included. Build, 130 server tests and guide/436-resource checks pass. Client GUI and balance playtesting remain. See [Altar of Purity](ALTAR_OF_PURITY.md).

### 2026-10-06 - Positive altar offering Haunt events

Added Offering Gift (Natural+, one Holy Salt delivered to a nearby compatible altar) and Offering Blessing (Supernatural+, next offering adds 50% extra burn time without extra consumption). Both use loaded altars within eight blocks, normal benefit rolls/hints and shared world cooldowns. Blessings persist, cannot stack and clear on one consumption; full/incompatible storage and absent altars fail without mutation. Build and 134 tests pass; guide/resource checks pass. Client feedback/balance remains pending. See [Altar of Purity](ALTAR_OF_PURITY.md).

### 2026-10-06 - Inverse altar, secret recipes and furnace tints

Added the Altar of Impurity with the same offering/storage rules and opposite nine-chunk Haunt pulses. Both altar recipes are now separately revealed Secrets of the Dead: Beacon over Haunt Furnace for Impurity, Beacon over Haint Furnace for Purity. Expanded knowledge masks preserve old weaving data without auto-granting new recipes; existing altars remain functional. Haunt/Haint furnaces use recognizable vanilla textures with dark-gray/icy-blue tints. Build and 138 tests pass; data/guide checks pass. Client colors and balance remain pending. See [Paired altars](ALTARS_AND_SECRETS.md).

### 2026-10-06 - Mortality Research Station

Added an Enchanting Table-style station recipe using four Obsidian, two Holy Salt and a Book of Ectcetera returned with its components intact. Study materials add player-owned research percentage points: Rotten Flesh 1, Bone 5, Raw Ectoplasm 10, Purified Ectoplasm 20. Bonuses stack to 100; raw/purified types cannot mix within one pending bonus. An eligible next Haunt selection gains the stated Insight probability points; books, personal Haunt and actual secret prerequisites remain required. Pending bonuses/types persist across death/logout and reset when spent. Natural rolls and random test commands share the bias; explicit named commands retain research. Build and 143 tests pass; guide/446-resource checks pass. Client GUI/balance remains pending. See [Mortality Research](MORTALITY_RESEARCH_STATION.md).

### 2026-10-06 - First research whisper

Mortality Research is now the first Secrets of the Dead reveal, available by whispered Insight at Natural personal Haunt with any book tier. Its blueprint stays locked until learned; the book remains unawakened and later secrets keep their existing gates. The section appears from the starter book, with research as its root. Expanded seven-bit masks preserve previous knowledge and support research in low-tier books. Build and 145 tests pass; client whisper/early-tab feedback pending. See [First whisper](FIRST_RESEARCH_WHISPER.md).

### 2026-10-12 - New players start at Natural Haunt

The personal tier attachment now defaults to Natural instead of Void, giving new players the normal Natural midpoint (15.00 Haunt). Saved player values remain authoritative, including explicit Void; death-copy behavior and chunk defaults are unchanged. The existing first-whisper test now verifies the new start without commands and loading a saved Void value. Build and all 145 server tests pass.

### 2026-10-12 - Temporary ingredient-informed item art

Replaced ten block/borrowed foliage inventory icons with transparent pixel sprites: both threads, three machines, Mortality Research Station, both altars, Grave Moss and Spiritwood sapling. Designs reflect crafting ingredients or natural sources, and use distinct temporary filenames. Java-generated item models point to the new sprites; world blocks and gameplay are unchanged. Build/data generation and guide/resource checks pass. Client readability remains a playtest check. See [Item art and prompts](TEMPORARY_ITEM_ART.md).


### 2026-10-14 - Close superseded cleansing and Spiritwell expansion plans

The user confirmed that Grave Moss fire cleansing is covered by the existing Altar of Purity and Altar of Impurity systems. The separate one-point-per-item dropped-moss fire mechanic and its validation checklist are closed as superseded, not claimed as implemented.

Spiritwells are required only in the Overworld. Nether and End worldgen is removed from future scope. This decision supersedes the 2026-10-04 note about eventual generation in those dimensions; existing dimension-aware indexes are retained. No gameplay code changes are needed for these two planning updates.


### 2026-10-14 - Grave Porting across dimensions and void damage

The user selected Grave Porting: a loaded naturalized Grave bearing its owner's player name is eligible when a fully fleshed (64) Bone Block is within three blocks. Right-clicking an Overworld Spiritwell lists eligible loaded Graves across every dimension. A successful port consumes the entire nearby frame and adds one personal Haunt point. The Grave remains. Loaded-only lookup, ownership, one-use confirmation tokens, safe arrival and dimension-travel cancellation are validated; no lookup generates terrain or adds chunk tickets.

Void damage explicitly bypasses intangible defense for spectral mobs and garb wearers. Dedicated checks verify ordinary damage amounts. All 175 server gameplay tests, build, book-layout and generated-resource checks passed. Client menu appearance remains a playtest check. Local playtest files are updated only when requested.

See [Grave Porting](GRAVE_PORTING.md) for mechanics and migration details. The broader portal/ritual travel network remains a separate proposal.


### 2026-10-14 - Higher flower-tended Grave naturalization chance

Raised the existing daily flower-tended naturalization chance from 5% to 20%. Supernatural-or-higher chunk Haunt, flower search radius, one-point local cost, loaded-day scheduling and no catch-up rolls remain unchanged. The Book and Grave Porting documentation state the new chance.


### 2026-10-14 - Visible green square spectral footprints

Changed Second Sight trails from lightning/weather-target bootprints to full-bright green square steps with a soft halo on the main world render target. Explicit buffer flush, downward surface collision rays and a short packet grace interval address hidden/flickering marks. Server site discovery and effect timing are unchanged. See [Spectral discovery](SPECTRAL_DISCOVERY.md).


### 2026-10-14 - Decorative Ecto Pipes and Ectanks

Added Ectank, Purified Ectank and Ecto Pipe block/item shells with original framed-glass native models using their recipe materials. Tanks craft from eight matching Gravestone around Glass; sixteen pipes craft from Gravestone top/bottom rows and Glass middle sides. Pipes connect visually in six directions. Fluid storage, transport and capacity values are explicitly deferred, with Purified Ectank intended as the capacity upgrade. No ticking block entities or fluid networks are introduced. Generated recipes/models/states/loot/tags accompany providers. The Book includes a raw-Ectoplasm-tab entry describing recipes and the decorative-only scope.
