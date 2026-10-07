# Mortal Expansion - Roadmap

**Repository:** https://github.com/Howierux/mortal_expansion  
**Branches:** `main` (consolidated baseline), `dev` (active work)  
**Docs:** [Overview](Overview.md), [Notes](Notes.md), [Roadmap](ROADMAP.md)  
**Last Synced:** 2026-10-06 - Mortality Research Station synchronized to `dev`; build, 143 tests and guide/446-resource checks pass. GUI/balance playtest pending.
**Current Phase:** Ambient/beneficial Haunt effects implemented; automated validation passed. Major encounters and Ghost’s Garb armor remain separate work.

> **Checklist legend:** `[ ]` not started, `[x]` completed, `[~]` partially completed. Implementation status and gameplay verification are tracked separately. Discussion items are proposals, not approved implementation work.

## Selected gameplay requirements [Planned]

The user selected these requirements on 2026-10-02. This records the intended work; the documentation update does not mark any gameplay change as implemented.

### Spiritwells [Foundation selected 2026-10-03]

- [x] Add rare intangible Overworld anchors, rendered as a faint Nether Star about two blocks above dry land.
- [x] Persist anchors and prevent block placement in a two-block spherical radius.
- [x] Validate worldgen, placement rollback, multi-placement, piston movement, and persistence on a headless server; all eight server tests, build, guide, and mod-loading checks passed.
- [ ] Visually verify floating appearance in a user-requested client session.
- [~] Personal-Haunt Ghost events and durable deferral are implemented; further Spirit Journey interactions remain planned.
- [ ] Add Spiritwell worldgen to the Nether and End in a later update. Held triggers remain in their original dimension awaiting wells there.

**Selected deferred-event rule (2026-10-04):** When the Spiritwell-spawn Haunt effect is selected and no loaded Spiritwell is within 100 blocks of the triggering player, retain that effect until a player comes within 100 blocks of a loaded Spiritwell. The later player need not be the original triggering player. Resolve proximity in the player's current dimension, and inspect loaded chunks only; this rule must not generate or force-load a Spiritwell. Retain the pending event across saves/restarts. The 100-block lookup only finds an already loaded destination; personal Haunt alone controls new trigger eligibility. Delivery must still satisfy the effect's mob-cap, protection, and safe-spawn rules; keep it pending if delivery cannot succeed. The scheduler stores a per-dimension count of held selections, keeps every trigger, and delivers at most one per second while preserving player cooldowns and mob limits. Delivery does not reroll or require the later player to retain the original triggering tier.

### Spirit Journey [Staged work selected 2026-10-03]

The user requested implementing this feature in smaller parts, starting with entry into an alternative game mode on death. This supersedes the earlier deferral.

- [x] Stage 1 implementation: Offer Spirit Journey only to dead players with a valid selected self-named Grave; respawn at that Grave in vanilla Spectator mode. Retain ordinary Respawn and existing inventory-drop/Grave rules.
- [x] Automated rule/guide checks, Gradle build, and mod loading through data generation passed for the corrected stage-one code.
- [ ] Client gameplay verification of mode entry.
- [x] Stage 2 implementation: Defer eligible death items until the choice; Ordinary Respawn releases them at the death site, while Spirit Journey holds them in serialized player data. Keep Inventory and Vanishing retain vanilla behavior.
- [x] Stage 2 headless death/respawn, serialization, and vanilla-rule validation (four GameTests); build, guide, and data-generation checks passed. Full restart/reconnect and client gameplay remain untested.
- [x] Grayscale post-processing implemented for active Spirit Journey; normal color returns on recovery or disconnect. Client visual verification remains pending.
- [ ] Later: Add dedicated journey movement and remaining state/exit handling.
- [ ] Later: Enforce spirit-blocking mechanisms during the journey.
- [~] Owner-linked persistent corpse and return-within-two-blocks inventory recovery implemented; 19 server tests pass. Spiritwell beams are visible only during an active journey. Manual logout/restart, cross-dimension journeys, and client visuals remain to be verified.
- [ ] Visually validate the death-screen option and mode entry in a client session requested by the user.

The stage order after the first part is a proposed work breakdown, not authorization to implement every stage in this update. Full corpse recovery is required before Spirit Journey becomes a complete survival recovery feature.

### Book of Ectcetera upgrades [Selected 2026-10-05]

- [x] Starting shapeless recipe: Book + Rotten Flesh, with survival/Haunt/biome chapters.
- [x] Sequential shapeless upgrades with Bone, Raw Ectoplasm, then solid Pure Ectoplasm; each consumes one material and retains the book's custom components.
- [x] Cumulative element-related chapters and a final entry at each tier explaining how to obtain the next ingredient; final tier marks completion.
- [x] Tier-aware contents navigation, tooltip guidance, persistent/synced per-book knowledge, and full-content compatibility for existing unmarked books.
- [x] Build and all 46 required server GameTests pass, including actual inventory-grid consumption, sequential/rejected recipes, stack/recipe roundtrips and navigation. Guide/data checks pass.
- [ ] Manual client page rendering and contents-link playtest when requested.

See [Book progression](BOOK_OF_ECTCETERA.md) for chapter groups, compatibility and migration checks. These ingredient upgrades are implemented; the separate research proposal for discovery-based case files remains discussion work.

### Elemental ranged attacks

- [ ] Give Elementals a ranged attack. Define its projectile/effect, damage, range, cooldown, and target behavior alongside the mob balance review.
- [ ] Verify attacks against tangible and intangible targets under the shared spectral combat rules.

### Intangible mob attacks

- [ ] Ensure intangible mobs can attack both tangible and intangible mobs. Check damage handling, melee reach, ranged/projectile behavior, and changes between tangible/intangible phases.
- [ ] Validate both target types for each applicable mob, including mixed-form encounters and summoned mobs. Preserve intended passive behavior and target selection; the ability to damage both types does not make every passive mob hostile.

### Spirit Villager trading

- [ ] **Spirit Villager progression: trades, quests and other actions [Requested 2026-10-05]:** As a Spirit levels up, unlock additional interaction options, including more trades, quests and other actions or services. Keep previously unlocked options available. Define the quest types, rewards, other interactions and their level requirements in a future update; progression should offer more than trading alone.
- [~] Passive **Spirit Villagers** now have the standard trading interface, initial resource trades, and a Villager-engraved Grave workstation. One nearby adult jobless villager converts per grave; breaking its grave reverts only grave-created workers. See [Spirit Villagers](SPIRIT_VILLAGERS.md). Broader natural discovery remains pending.
- [~] Villager-engraved graves provide manual discovery/conversion. Recipe-balanced Novice-to-Master trades, vanilla XP thresholds, limited stock, and daily workstation-aware restocking are implemented. Broader natural spawn/discovery and continued balance tuning remain.
- [~] Server tests verify actual trade payment/output, stock persistence, passive behavior, workstation ownership, and save/load reversion. Client visuals and manual playtesting remain pending. Earlier village-graveyard and Creeper-deterrence ideas remain optional.

### Mob strength and spawn review

- [ ] Recheck each mob type's health, damage, attack speed/cooldown, movement, attack range, resistances, intangible behavior, death transformations, and boss/summon behavior.
- [ ] Record spawn odds by mob type and distinguish spawn-table weights from acceptance probabilities and encounter-trigger chances.
- [ ] Document exact spawn conditions: Haunt tier, biome, dimension, light/time, terrain/height, structure proximity, group size, mob caps, and applicable spawn reason.
- [ ] Check natural spawning separately from structure encounters, summons, death transformations, spawners, and commands. Include the Haint witch-hut encounter and vanilla mobs affected by Haunt.
- [ ] Compare actual conditions and strengths with intended progression, then identify adjustments and validate selected changes in gameplay when a client session is requested.

### Bloodstone tool scaling

- [ ] At **Natural Haunt**, make each Bloodstone tool equivalent to its Diamond counterpart in power and speed, including attack damage, attack speed, and mining speed where applicable.
- [ ] Use **personal Haunt** for Bloodstone tool scaling. Start with +10% power and speed per named tier above Natural: Supernatural +10%, Dead Rising +20%, Demonic +30%, Abominable +40%, Eldritch +50%. Treat these as accepted initial balance values to validate in play.
- [ ] Define behavior at Void separately, and verify Diamond parity at Natural plus the intended increase at each higher tier. This replaces the historical Diamond-like Void baseline for tools; armor balance remains a separate decision.

### Grave Moss fire cleansing

- [ ] When a Grave Moss item thrown into fire is consumed, decrease Haunt in the chunk containing that fire by **one point per Grave Moss item**.
- [ ] Verify single items and stacks apply the reduction once per item consumed, clamp at zero, and affect the correct chunk at chunk boundaries. Five consumed items remove five points, subject to that minimum.

### Haunt effects [Accepted proposal]

The user accepted the effect proposal on 2026-10-02. On 2026-10-04, the user superseded the 4x4-chunk rule: **personal Haunt alone triggers and determines eligibility for every ambient and beneficial effect**. Chunk Haunt still drives the existing world simulation and normal mob-spawn rules.

#### Event controller and personal Haunt evaluation

- [x] Use one shared event controller for timing, eligibility, protection, and cooldowns. Keep inventory changes, spawning, and Haunt adjustments authoritative on the server; send sounds and visual effects to the affected players.
- [x] Use personal Haunt alone for trigger odds and minimum tiers, including world changes and event-created mobs. Do not evaluate surrounding chunks for these effects.
- [x] Remove the sixteen-chunk footprint/aggregation requirement from the ambient controller, per the 2026-10-04 correction. Loaded-chunk lookups remain only for actual nearby targets and Spiritwells.
- [x] Check every **30 seconds**, allow one event per independent benefit/risk pool, and apply a **60-second cooldown per pool** after an event. Give harmless atmosphere higher selection weights than shoving, shuffling, or hostile appearances.
- [x] Start with the following total event chance per eligible check, then select one eligible effect by weight. Void has no ambient event roll. Cooldowns and eligibility affect actual event frequency.

| Evaluated Haunt tier | Chance per pool per 30-second check |
|---|---:|
| Natural | 2% |
| Supernatural | 5% |
| Dead Rising | 8% |
| Demonic | 12% |
| Abominable | 16% |
| Eldritch | 20% |

- [x] Apply a saved **60-second cooldown per affected chunk** for furnace completion and material conversion. Furnace cooldown follows the furnace’s chunk; conversions follow the player’s chunk. Weights: sounds 30, shadows 20, passive Spirit 8, Slowness 6, good/bad potion 5 each, furnace 4, shove/shuffle/plank/cobblestone 3 each, Spiritwell Ghost 2. Ineligible operations are skipped without consuming inventory.

#### Ambient effects

Implement each effect with the minimum tier and behavior below. Thresholds are minimum tiers rather than exclusive bands.

| Status | Effect | Minimum tier | Accepted behavior |
|---|---|---|---|
| [x] | Harmless hit sounds | Natural | Quiet impact or footstep behind the player, with no damage or combat reaction. |
| [x] | Shadows | Supernatural | Faint figure at the edge of vision for two seconds; disappears when approached or directly observed. |
| [x] | Slowness II | Supernatural | Three seconds with a cold breath sound. Skip while swimming, climbing, or under a stronger slowing effect. |
| [x] | One-block shove | Demonic | Short push opposite the player's facing direction. Check the destination and skip blocked spaces, falls, and lava hazards. |
| [x] | Inventory shuffling | Demonic | Swap two occupied main-inventory slots. Preserve hotbar, armor, offhand, and open containers; show brief spectral particles. |
| [x] | Random potion effect | Supernatural | Five seconds of one curated effect: Speed, Haste, Weakness, Slowness, or Mining Fatigue. Avoid lethal effects and preserve stronger existing effects. |
| [x] | Nearby Spirit | Natural | Spawn one passive Spirit within five blocks at a valid position. Prevent repeated appearances while an event-created Spirit remains nearby. |
| [x] | Spiritwell Ghost | Dead Rising | A loaded Spiritwell within 100 blocks releases a real hostile Ghost with a sound/particle warning. If this effect triggers without a loaded Spiritwell in range, hold the trigger until a player is within 100 blocks of a loaded Spiritwell; do not discard it or require a new random roll. Respect mob caps and valid unprotected spawn positions. The saved per-dimension queue delivers held selections to any eligible nearby player without rerolling or force-loading. |

#### Beneficial effects

| Status | Effect | Minimum tier | Accepted behavior |
|---|---|---|---|
| [x] | Finish cooking | Supernatural | Complete one valid recipe in an active furnace within eight blocks. Consume input normally, preserve output and experience, require output space, and complete one operation rather than a whole stack. |
| [x] | Planks to Spiritwood | Supernatural | Convert one ordinary inventory plank into one Spiritwood plank. Show a green shimmer and require room for the result; leave placed buildings intact. |
| [x] | Cobblestone to Gravestone | Supernatural | Convert one inventory cobblestone into one Gravestone using the same single-item, output-space, and visual rules. |
| [~] | Ghost's Garb repair | Dead Rising | During a spectral visitation event, restore one durability to each equipped Ghost's Garb piece. Implemented via the `mortal_expansion:ghosts_garb` item tag; the tag now includes crafted Ghost�s Garb; equipped pieces use the existing repair hook. Unrelated equipment is unaffected. |

#### Protection and major encounters

The user selected ambient/beneficial effects first and tag hooks for Ghost’s Garb. Major Haint/Eldritch encounters and the armor itself are deferred pending their separate design.

| Status | Feature | Accepted behavior |
|---|---|---|
| [~] | Ghost's Garb immunity | A full set blocks negative ambient effects while allowing beneficial events. Full-four-piece protection now uses crafted Ghost�s Garb, with leather defense, iron durability, and Ectothread recipes. Direct mob attacks are unchanged. |
| [x] | Holy Water blessing | Contact grants 60 seconds of negative-ambient-effect protection and removes active effects applied by haunting. Lower personal Haunt by one point at most once per 60 seconds; contact refreshes the blessing. Only unchanged haunting-owned negative potion effects are removed; external applications relinquish ownership. |
| [x] | Purification Ward protection | Suppress negative ambient events inside covered chunks while the existing cleansing continues separately. |
| [ ] | Haint encounter reward | Defeating the encounter Haint removes five Haunt points from its chunk and caps Haunt at the resulting value for ten minutes. Apply once per encounter, including its split into Haunts; define when the split encounter counts as defeated. |
| [ ] | The End event | Rare Eldritch encounter with sky changes and the proposed Wither/terrain effects. After successful resolution, remove twenty points from affected chunks and cap each at its resulting value for twenty minutes. Define event footprint, trigger odds, resolution, and interaction with existing Haunt simulation in a separate event design. |

- [~] Ambient validation passed: personal tier policy, cooldown/save-load, protection/provenance, inventory conservation, shared chunk cooldown, furnace output/XP, safe movement, passive repeat suppression, and deferred well boundaries/delivery. All eighteen server tests and build/loading/guide checks pass. Client visuals, manual restart/reconnect, full garb content, and major-encounter temporary caps remain unverified or deferred. These events do not depend on Spirit Journey.

### Portal system

- [ ] Add a portal system as a planned feature. Work out its implementation before building it: destination linking, activation, travel rules, costs, cooldowns, eligible travelers, dimension scope, and chunk loading.
- [ ] Consider the original **Spirit Gates** concept as one design option, without assuming spirit-only travel, a full-armor requirement, or same-dimension travel is the final choice.
- [ ] Specify safe arrival, blocked/missing destinations, portal removal, persistence, and multiplayer behavior before implementation.

**Scope decisions:** Duplicator and Triplicator are long-term items below. Spirit Journey has reopened as the staged work above; its broader historical references remain in [Notes](Notes.md). Tinkers' Construct compatibility is out of scope.

## Phase 1 - Existing gameplay foundation [Completed]

- [x] Persistent chunk and player Haunt, fractional player points, natural baselines, spread, recovery, and purification.
- [x] Haunted biomes, Graveyard structures, Graves, inscriptions, and own-name Grave respawn anchors.
- [x] Spectral creatures, intangible combat and movement, demi-tangible gear, and spectral defenses.
- [x] Ectoplasm materials, cauldron purification, Ectchantment Machine, generators, and Purification Station.
- [x] Haunt and Haint Furnaces with tier-scaled cooking and Haint heat preservation.

These systems are present in the consolidated source. They are not a claim that every gameplay path has been manually tested.

## Phase 2 - Latest consolidated milestones [Completed]

- [x] `1.0k`: Push chunk tier changes into furnace caches and preserve proportional cooking progress.
- [x] `1.0l`: Book of Ectcetera with indexed chapters and bounded guide resource checks.
- [x] `1.0m`: Persistent Ward-owned ticking tickets across the 3x3 cleansing area, with removal and overlap ownership handling.
- [x] Advance `main` to the Ward milestone and create `dev` for continued work.

## Phase 3 - Development setup and documentation [Completed]

- [x] Authenticated repository access and push permission verified.
- [x] Full JDK 21 and Gradle dependencies prepared locally.
- [x] Reusable Windows play launcher created and verified on the furnace baseline.
- [x] Ward baseline compiled locally without additional source changes.
- [x] Create this docs folder with overview, append-only notes, and roadmap.
- [x] Record launch-on-request and no-routine-save-backup preferences.
- [x] Incorporate the supplied original design notebook and distinguish current features from historical proposals.

## Gameplay regression pass [Discuss]

**Goal:** Exercise the systems already implemented before treating their edge cases as verified. Run gameplay checks when the user requests a client session.

- [ ] Furnace tier transitions: change Haunt during a recipe, confirm cooking progress, heat behavior, and behavior after chunk reload.
- [ ] Graves: engrave the current player name, die and respawn, confirm placed-Grave consumption and personal Haunt cost; repeat with a naturalized Grave.
- [ ] Wards: leave the area, restart the world, remove a Ward, and remove one of two overlapping Wards; confirm ticking tickets and cleansing coverage behave as intended.
- [ ] Book of Ectcetera: verify contents navigation and page rendering in-game alongside automated content checks.
- [ ] Purification power chain: measure fuel yield and station consumption at fixed tiers with supplied ingredients and output extraction.

## Grave selection by click [Discuss]

**Current behavior:** Own-name engraving selects a spawn anchor. A later click on an engraved Grave does not reselect it.

**Possible extension:** Let a player right-click an eligible Grave to select it again, with visible confirmation.

Before implementation, decide whether selection is limited to the player's own named Graves or whether other Graves can be claimed, and whether naming remains part of the activation flow. Preserve the existing consumed-versus-naturalized respawn rules unless the user requests a change.

- [ ] Confirm the intended eligibility and click behavior with the user.
- [ ] Implement and validate the selected behavior if requested.

## Purification and generator balance [Discuss]

**Current behavior:** A pure ectoplasm's nominal generator yield equals its station energy cost at Natural and exceeds it at Supernatural or higher. Raw ectoplasm, Grave Moss, and water are still consumed, and station crafts add local Haunt.

This is documented behavior, not a confirmed balance defect. Decide whether an ingredient-fed power chain with increasing Haunt is intended before changing costs, yields, or side effects.

- [ ] Decide whether the current energy balance should stay as implemented.
- [ ] Scope any requested adjustments with before/after numbers and gameplay verification.

## Haunt-altering multiblock structures [Concept]

**Status:** Preserve this discussion as a concept for further design. The user is not yet settled on how it should work. Names, materials, geometry, generation, activation, range, cadence, costs, stacking, and numerical effects below are tentative rather than selected implementation requirements.

**Core idea:** A recognizable multiblock structure can occur naturally in the world and substantially raise or lower local Haunt. Players can construct an equivalent structure to produce the same effect where they build it. A shared structure with a different centerpiece could determine whether it haunts or cleanses an area.

| Design detail | Haunting Altar - tentative name | Consecrated Shrine - tentative name |
|---|---|---|
| Purpose | Create a haunted location for progression, encounters, and stronger machinery | Establish a safer area or cleanse an existing haunted location |
| Suggested materials | Gravestone, Bloodstone, Spiritwood, ectoplasm centerpiece | Gravestone, purified Spiritwood, Holy Salt, Holy Water centerpiece |
| Possible natural locations | Ruined graveyards, Spiritwood clearings, abandoned ritual sites | Rare village graveyards or secluded sanctuaries |
| Visual/audio feedback | Dark particles moving inward and occasional whispers | Pale particles moving outward and soft chimes |

### Tentative influence and activation

- Pulse **once per Minecraft day**: add/remove **five Haunt points in the central chunk** and **one point in each of the eight neighboring chunks**, producing a **3x3-chunk footprint** with a strong center and gentler surrounding influence.
- Clamp Haunt to its existing **0-69** range. The initial suggestion allows eventual cleansing to Void or haunting to Eldritch; a softer limit remains an alternative to consider.
- Activate only while the complete arrangement is present. Breaking a required piece stops future pulses; Haunt already added or removed remains and then follows the ordinary simulation.
- Consider changing the centerpiece to switch polarity, with visible activation/change feedback. The exact shape, required components, orientation, and validation rules have not been designed.
- Natural and player-built complete structures should use the same rules. Naturally generated ruined/incomplete examples could invite investigation and repair.
- Consider passive operation for complete structures, with consumable offerings providing an optional temporary boost. Required fuel, offering recipes, boost strength, and costs remain undecided.

The discussed 3x3 structure footprint is a tentative local influence area. The **former 4x4-chunk limit for Haunt effect triggering** was superseded on 2026-10-04 by personal Haunt alone; finalize how structural influence and player checks interact during design.

### Tentative overlap and scheduling

- Cap contributions from overlapping structures so multiple nearby altars cannot immediately maximize Haunt. Opposing structures could cancel their overlapping influence. Stacking limits and cancellation arithmetic remain unspecified.
- Apply influence to loaded chunks, with no accumulated burst when an unloaded area returns. Dedicated chunk loading was not proposed for these structures.
- If pulses use the existing daily Haunt scheduler, **sleeping to the next morning triggers the daily pulse**, just as it triggers the current simulation pass. Decide whether to use world-day transitions or elapsed ticking time before implementation.
- Define ordering and interaction with natural spread/recovery, Purification Ward cleansing/protection, and temporary encounter caps. The earlier discussion did not settle these interactions.

### Design work before implementation

- [ ] Revisit the concept and settle structure shape, centerpiece behavior, natural generation frequency, and incomplete variants.
- [ ] Decide pulse strength, footprint, cadence, allowed Haunt limits, and passive versus resource-fed operation.
- [ ] Decide overlap rules, sleep/time-jump behavior, chunk loading, and interactions with existing Haunt systems.
- [ ] Once the design is selected, specify discovery/activation feedback and verification for construction, damage, polarity changes, overlap, persistence, and natural/player-built equivalence.

## Magic and magitech feature planning [Discuss]

Added to planning at the user's request on 2026-10-05. These 22 candidates are recorded for review; adding them to this section does not authorize gameplay implementation. Scope and priority are the research report's recommendations, not a selected work order.

See [Feature Ideas Research](FEATURE_IDEAS_RESEARCH.md) for the reference-mod comparisons, detailed mechanics, suggested progression, source links, and decisions needed before implementation. Existing-backlog expansions refine the concepts below rather than creating separate duplicate features.

**Recommended first design batch:** Spectral Analogue (1), grave offerings (5), Spirit Villager commissions (6), haunted-processing containment (14), and spirit caretaker (11).

### Investigation and discovery

- [ ] **1. Spectral Analogue and field readings** — Show personal and local Haunt separately, with trends and optional redstone alarms. *High priority; small to medium scope; existing backlog expansion.*
- [ ] **2. Case files in the Book of Ectcetera** — Record discoveries and useful experiments in the Book of Ectcetera; retain access to essential recovery instructions. *High priority; medium scope; new proposal.*
- [ ] **3. Haunted-site investigations** — Explore graveyard, workshop, and mausoleum mysteries that can be settled peacefully or exploited. *Medium priority; medium scope; new proposal.*
- [ ] **4. Spirit-visible secrets** — Reveal hidden inscriptions and clues through Spirit Journey or a living-player spectral lens. *Medium priority; medium scope; new proposal.*

### Rituals, graves, and spirit relationships

- [ ] **5. Grave offerings and consecration** — Use offerings, candles, and Graves for cleansing, blessings, spirit calling, and maintained consecration. *High priority; medium scope; existing backlog expansion.*
- [~] **6. Spirit Villager commissions** — Offer investigations, identification, and ritual services alongside recipe-balanced merchant trades. *High priority; medium scope; new proposal.*
- [ ] **7. Spirit bargains** — Exchange explicit, temporary obligations for benefits; provide visible costs, expiry, and cancellation. *Medium priority; medium scope; new proposal.*
- [ ] **8. Graveyard harmony and unrest** — Reward maintained graveyards with limited local benefits while distinguishing site order from Haunt intensity. *Medium priority; medium to large scope; new proposal.*
- [ ] **9. Protective effigies** — Craft owner-bound effigies with visible charges and narrowly defined spectral protections. *Medium priority; small to medium scope; new proposal.*
- [ ] **10. Blood Blooms and ritual gardens** — Cultivate plants in different Haunt conditions for offerings, incense, purification, and equipment. *Medium priority; medium scope; existing backlog expansion.*

### Spirit labor and haunted industry

- [ ] **11. Spirit caretaker** — Bind a willing, offering-maintained spirit to one limited task within a marked area. *High priority; medium scope; new proposal.*
- [ ] **12. Spiritwood constructs** — Build modular Spiritwood workers for hauling, harvesting, or guarding after the simpler caretaker is established. *Later priority; large scope; new proposal.*
- [ ] **13. Ectoplasm infrastructure** — Develop Ecto Pipes, Ectanks, and the Liquefactor with readable flow, storage, and shutdown controls. *High priority; medium scope; existing backlog expansion.*
- [ ] **14. Containment and recoverable processing failures** — Make high-Haunt processing more productive but subject to visible warnings and manageable containment failures. *High priority; medium scope; new proposal.*
- [ ] **15. The Possessor as a spirit-infused machine** — Develop controlled spectral item infusion, starting with the existing Demi-Tangible machine concept. *Medium priority; medium scope; existing backlog expansion.*
- [ ] **16. Paired ritual and industrial purification** — Offer hands-on ritual and automated industrial area cleansing, distinct from material purification. *High priority; medium scope; existing backlog expansion.*
- [ ] **17. Redstone spectral controls** — Expose Haunt thresholds, containment, ward charge, fluid levels, and worker activity through redstone. *Medium priority; small to medium scope; new proposal.*

### Equipment, encounters, and travel

- [ ] **18. A focused spectral skill set** — Provide a small selection of customizable spectral abilities instead of beginning with a full spell engine. *Medium priority; medium to large scope; new proposal.*
- [ ] **19. Boss soul imprints** — Earn limited equipment sidegrades from identity-linked boss imprints with slots, conditions, and tradeoffs. *Medium priority; large scope; existing backlog expansion.*
- [ ] **20. Cleansing and binding encounters** — Resolve a major haunting through combat, binding preparation, or negotiation with clear spectral counterplay. *Medium priority; large scope; new proposal.*
- [ ] **21. Linked grave or Spiritwell travel** — Link discovered Graves or Spiritwells for explicit-cost travel independently of existing death-item recovery. *Later priority; large scope; existing portal backlog expansion.*
- [ ] **22. Spirit-side expeditions** — Consider ritual-accessed spirit expeditions after prototyping spirit-visible secrets in existing terrain. *Long-term priority; very large scope; new proposal.*

**Planning constraints:** Keep personal and local Haunt effects explicit; preserve corpse/Spiritwell recovery and current garb protections; favor readable, reversible costs. Use generated recipes/tags and supported framework APIs, with clear owner, dimension, unload, restart, and cancellation rules for new persistent systems. Integrations with reference mods require a separate current-version/API decision.

## Original design backlog [Discuss]

**Source:** The user-supplied `Mortal Expansion.docx`, with revisions from 2014-2017. The items below retain original concepts and tentative parameters. Inclusion here authorizes documentation, not implementation. Related current systems are identified so the same foundation is not mistakenly rebuilt.

### Haunt progression and ambient events

**Already present:** Chunk/player Haunt, the seven named bands, spread, tier-controlled natural spawning, recovery toward natural floors, and several purification systems.

- [ ] Reconcile the original tier-specific Overworld roster with current balance and encounter behavior:

  | Tier | Historical design additions |
  |---|---|
  | Void | Passive animals, Spiders, Creepers, and rare Endermen; no spirits driving undead activity |
  | Natural | Spirits and rare Zombies |
  | Supernatural | Common Zombies, melee Skeletons, and Witches |
  | Dead Rising | Armed/bow Skeletons and aggressive intangible Ghosts |
  | Demonic | Phantoms, Haunts, and Wither Skeletons |
  | Abominable | Weaker, low-flying, non-block-breaking Overworld Ghasts; a nearby witch-hut/landmark Haint encounter that temporarily lowers and caps Haunt |
  | Eldritch | More Endermen, including daytime behavior, and increased Overworld Wither Skeleton chances |

The **The End event** is now part of the accepted Haunt effect proposal above: a rare Eldritch encounter, twenty-point cleansing on resolution, and a twenty-minute cap. Preserve the original End-like sky, possible surface Wither within 100 blocks, and End Stone patches as design references while finalizing the event scope. It remains separate from the existing End dimension; Eldritch stays the ordinary maximum tier.
- [ ] Decide whether lowered Haunt should dissipate without activity as the notebook suggests, remain at the current natural-floor recovery behavior, or depend on the source of Haunt. Do not silently replace the present simulation.
Ambient and beneficial haunting have moved into the **accepted Haunt effect proposal** above, with initial thresholds, durations, frequencies, and protection rules. Use those selected rules when they differ from the historical sketches.
- [x] Spiritwell worldgen/protection and personal-Haunt Ghost appearances with durable deferral are implemented above. Further well interactions remain planned.
- [ ] Evaluate rotten-flesh furnace processing to raise local Haunt, and a starter Gravestone recipe using rotten flesh or bone so progression need not begin in a haunted biome.

The earlier Skeleton -> Zombie -> Ghost -> Spirit -> Haunt -> Haint strength sketch was explicitly reconsidered in the source. It is not a final progression order.

### World generation, trees, and village content

**Already present:** Spiritwood Forest, Spiritwood building blocks, Graveyard generation, Graves, Grave Moss, and Bloodstone ore.

- [ ] Investigate making Graveyard mini-biomes or village graveyards more common near villages.
- [ ] Design physical **Spiritwood tree mobs** that fight back and drop logs, a chance of ectoplasm, and a small sapling chance.
- [ ] Preserve placed-versus-generated tree/leaf provenance so player-grown trees do not become mobs. Proposed planted saplings are fed ectoplasm, stay ordinary trees after growing, and have increased sapling drops.
- [ ] Decide whether Spiritwood components become a spirit-energy source and how this relates to existing FE machines. Preserve ordinary wood compatibility while requiring actual Spiritwood for its own recipes.
- [ ] Revisit Bloodstone ore replacing Diamond in spiritual or thoroughly haunted biomes; existing ore generation is not proof of this exact replacement rule.
- [ ] Evaluate natural Grave Moss growth on Gravestone sides in forest-tagged haunted chunks, with higher odds in Graveyards or village graveyards. Retain its possible mossy-cobblestone-like decorative role.
- [ ] Add generated Grave inscriptions from creator/friend names and a curated random-name list if requested.

The Spirit Villager is now a selected requirement above. Historical details to consider during trade design include ordinary-to-haunted material exchanges, a rare Diamond-to-Bloodstone trade, optional Blood-to-Ectoplasm bucket trading, spirit classification, hostile-mob indifference, harmless swats, and Creeper deterrence.

### Ectchantments and machine variants

**Already present:** Ectchantment Machine, Demi-Tangible support, material transmutation, Ectoplasmic Generator, and Enchantment Generator.

- [ ] Decide whether the original fluid/raw/pure ectoplasm processor and the later **Possessor** are separate machines. The first sketch accepts ectoplasm plus ordinary fuel or RF; the later Possessor grants only Demi-Tangible, accepts already enchanted items, and proposes any-item support.
- [ ] Define compatibility eligibility for enchantable vanilla and other-mod equipment; do not assume the original any-item wording is already supported.
- [ ] Preserve the Possessor recipe sketch: a Cauldron, four Ectocleansers, two Ecto Pipes, and Bloodstone above/below. The source's wording about cleanser positions is ambiguous and needs a final crafting layout.
- [ ] Scope **Ectocleanser** as a component: pure-ecto bucket in the center, Gold Ingots above/below, Iron Ingots left/right, with two components per craft.
- [ ] Preserve the one-item-at-a-time, clear-output-before-next-operation behavior proposed for the Possessor.
- [ ] Evaluate whether any generators should be conditionally available only when compatible RF consumers are installed, as the old enchantment-generator proposal suggests. The current mod already has internal FE consumers.

### Fluids, transport, storage, and containment

**Already present:** Raw/purified ectoplasm and Holy Water fluids, buckets, cauldrons, and capabilities used by current machines. These do not include the proposed dedicated transport/storage blocks.

- [ ] **Ecto Pipe:** One-bucket capacity per pipe, fast transfer, empty-hand right-click input/output modes, and sixteen pipes per craft. Original recipe sketch: Ectank center, Glass across top/bottom, Gold Ingots at the middle sides.
- [ ] **Ectank:** Cheap, large, deliberately inefficient fluid storage. Original recipe: Gravestone around Glass. One block holds sixteen buckets, accepts input/output from any side, and leaks any fluid at 1 mB/t.
- [ ] **Ecto Liquefactor:** Convert raw ectoplasm with water into ectoplasm fluid, or with Holy Water into purified fluid, using RF or ordinary fuel. Include a liquid-ecto bucket in its recipe so the player learns the in-world production route.
- [ ] Reconcile the old four-raw-ectoplasm-plus-water fluid recipe with the current heated-cauldron process before adopting it. The source includes both in-world and bucket crafting approaches.
- [ ] Evaluate the original cauldron transmutation: throw Stone into liquid ectoplasm to obtain Gravestone, consuming one-third of a bucket per conversion. Reconcile this sketch with current material-transmutation recipes before adding another route.
- [ ] Preserve proposed Liquefactor handling: automatic pipe output, bucket filling by right-click, and an interface bucket slot.
- [ ] Scope special purified-ecto containment: Ecto Pipes and a specialized bucket with Bloodstone in its bottom corners. Ordinary buckets/world placement were discouraged by the source.
- [ ] Define handling side effects: slow chunk Haunt increase while a player carries an ordinary pure-ecto bucket, a small immediate increase when the Liquefactor fills one, and no additional player-carrying effect during machine relocation. These are proposals, not established current bucket behavior.
- [ ] Choose integration boundaries for compatible fluid transport and processing recipes. Preserve the source comment's goal of recognizing external grinding machines, using current tags/capabilities or explicit integrations rather than assuming legacy ore-dictionary APIs.

### Blood Blooms and revised Holy Water

**Already present:** Holy Water, Grave Moss-based cauldron purification, sanctification, and ectoplasm drops from the implemented mob list.

- [ ] **Blood Blooms:** Replace vanilla flowers naturally at Supernatural or above, subject to world-generation and provenance rules.
- [ ] Preserve petal processing sketches: one petal by crafting, two via a grinding machine, three mashed petals from further crafting, and a shaped recipe yielding two dye of any selected color.
- [ ] Define the later Holy Water ritual: three Blood Bloom petals, a cross of water enclosed by Gravestone, and a required water source at the core. Clarify the exact spatial pattern rather than treating the source's ASCII sketch as finalized.
- [ ] Reconcile the source's revised, more expensive purification route with the existing Grave Moss route; do not overwrite current recipes based solely on this historical alternative.
- [ ] Scope Holy Water interactions: block/deter undead crossing and heavy contact damage to undead/spirit-tagged mobs. The player blessing follows the accepted Haunt effect protection rules above.
- [ ] Scope purified-ecto attraction, spirit damage, and simultaneous strength effects as proposed later in the source.
- [ ] Decide whether ectoplasm loot should require a last hit from Bloodstone equipment with roughly a 10% chance. The current drop handler grants ectoplasm for its supported mob types without that proposed condition.

### Furnace variants and spectral cooking

**Already present:** Ordinary-fuel Haunt/Haint Furnaces, deterministic tier-based speed, and Haint heat preservation. The notebook explicitly points from an early furnace sketch to revised ideas later on.

- [ ] Evaluate the early **Haint Furnace** idea only as an alternative: 2x ordinary speed, raw-ectoplasm-only fuel, and possible negative effects. Vis/Blood/nature energy alternatives were suggested to reduce those effects. The source later describes Haint as fast, slightly inefficient, and consistently raising Haunt; neither description replaces the current implementation automatically.
- [ ] **Vortex Furnace:** Higher-tier Bloodstone-plus-Haunt-Furnace build, random +300 cooking-progress bursts above a Haunt threshold, increasing burst chance with Haunt, proposed constant operation at Demonic+, and a quiet whirring sound. This is separate from existing Haint free heat, which still needs initial fuel.
- [ ] Evaluate improved fuel efficiency for the original Gravestone-ring Haunt Furnace concept; the existing crafting shape is not proof of the old efficiency rule.
- [ ] **Spectral output:** Rare Spectral I enchantment on cooked items, an extra output destination, an optional separate target-item slot, and a higher but still very small Vortex chance. Spectral items were proposed as ectoplasm fuel with green sparkle/light effects.
- [ ] Reconcile whether spectralizing applies only to smeltable output or an independent item in the extra slot; the notebook gives both concepts without final eligibility rules.

### Spiritual cloth, garb, souls, and gear progression

**Already present:** Bloodstone/Spectralite equipment and intangible combat protection. The proposed spiritual sets and their set bonuses are distinct.

- [ ] **Spiritweave Cloth / Spirit Threads:** High-Haunt sheep yield wool-equivalent cloth; Spiders killed with Bloodstone equipment drop string-equivalent threads. Four threads in a 2x2 craft make cloth. Placed threads behave like string but trigger only for spectral/intangible entities.
- [ ] **Spirit's Garb:** Cloth-based set granting player intangibility, upward movement through blocks, and no suffocation. The source leaves downward phasing speed, Shift-only descent, flight combination, and falling-through-the-world problems unresolved.
- [ ] **Ghost's Garb:** Spiritweave-plus-Bloodstone recipe, Ghost damage protection, slow repair at medium/high Haunt, and hunger removal/no starvation damage. Full-set ambient immunity and visitation repair follow the accepted Haunt effect proposal above; the remaining properties are historical discussion items.
- [ ] **Haint's Garb:** Upgrade Ghost's Garb using pure ectoplasm and a possible boss component. Proposed Fright makes nearby mobs flee; creative flight and other effects depend on boss souls.
- [ ] **Boss souls:** Preserve differentiated souls, possibly from all mobs. Proposed examples are Ender Dragon flight on the chest, Wither satiation on the helmet, Haint Fright, and a possible Pharaohl Haunt-control ability raising/lowering one tier with a cooldown. Decide whether Pharaohl should become the strongest boss if that ability is selected.
- [ ] Decide whether set mixing is allowed and which components preserve or combine bonuses.
- [ ] Evaluate the later armor proposal: substantial flat-health bonuses, modest normal armor, high enchantability, and eventual enchantment-based protection comparable to basic Diamond. This is an alternative balance model, not current gear behavior.

Bloodstone tools follow the selected Natural-Haunt Diamond baseline and higher-tier power/speed scaling above, superseding the notebook's Void-baseline sketch for tools.

- [ ] Evaluate Haunt-based Bloodstone armor protection and repair that consumes or depends on Haunt separately from the selected tool baseline. Existing gear does not establish those exact scaling/repair mechanics.
- [ ] **Purified Bloodstone gear:** Upgrade handles with Spiritwood and Holy Water; proposed weapons slightly lower Haunt on kills and permanently gain a little damage. Armor retains weaker Haunt scaling, with ordinary Bloodstone potentially winning at the extreme proposed End event.

### Graves and consecration

**Already present:** Grave inscriptions and own-name respawn anchoring, including ordinary placed-Grave consumption and naturalized reuse.

Spirit Journey mode entry and deferred death items are the selected first two stages above; corpse recovery, grayscale, and barriers are later stages. Broader historical spirit-player/PvP mechanics remain unselected in [Notes](Notes.md). Ordinary Grave respawning remains available, and the portal requirement above is independent.

- [ ] Preserve the proposed Grave display, `RIP` plus the entered inscription. Reconcile the original sign-like Grave and its rough crafting sketch with the current block/recipe.
- [ ] **Consecrated Gravestone:** At Supernatural+, direct revival with keep-inventory-like benefits, a maintained ritual, a tentative four uses per consecration, and Haunt costs for both maintenance and revival.

### Detection and cosmetics

- [ ] **Flashlight:** Pointable sunlight-like illumination, potentially with a stationary version, burning mobs normally vulnerable to sunlight and dealing low repeated damage to Endermen. Reconcile the proposed loss of general effectiveness at Demonic+ with the separate Enderman cutoff at Eldritch; cone shape, range, and actual lighting behavior remain unspecified.
- [ ] **Spirit Detector:** Point toward a selected undead/dead-type mob, selected by striking it or crafting with its dropped raw ectoplasm without consuming that ectoplasm. Original recipe: Bones at edge centers, Gold Nuggets at corners, raw ectoplasm in the center.
- [ ] **Spectral Analogue:** Glass thermometer containing fluid Bloodstone, possibly with Gold Nugget corners, measuring Haunt rather than temperature. Heat Bloodstone and combine with a bucket to make fluid Bloodstone, returning the bucket when crafting the instrument; consider using the Liquefactor to avoid redundant processing.
- [ ] **Fancifier:** Reduce block/item-entity opacity through a Fading enchantment up to level four. Clarify the source's undefined opacity decrement and the distinction between enchantment levels and visual transparency.
- [ ] **Schmancifier:** One-time Spectral enchantment/particle upgrade to a block or item. Define which targets are eligible and whether this shares mechanics with spectral furnace outputs.

### Powered defense, mob conversion, and ritual cleansing

**Already present:** Holy Salt/Spirit Torch barriers, sanctification, and Purification Wards. They do not automatically implement the proposed powered shield or altars.

- [ ] **Spectral Shielding:** Pure ectoplasm plus RF sustain a field rejecting undead/spectral aggressors and turning undead away beyond its edge. Proposed activation kills undead already inside and absorbs spirits for temporary resource efficiency, while slowly raising local Haunt. Other non-spectral/non-undead mobs can enter. Preserve the base-defense and mob-farm intent.
- [ ] **Mob spectralization:** Convert otherwise living mobs such as Spiders, Endermen, Cows, Witches, and Iron Golems into undead/spirit variants for farms and encounters.
- [ ] **Doomsday Device:** Configurable nearby spectralization, described initially as killing nearby mobs. Players are excluded in one version; an alternative includes them and allows armor resistance. Eligibility, transformation versus killing, and radius need a decision. The historical Spirit Journey interaction requires a separate design decision for this device.
- [ ] **Ritualistic Purifier:** Gravestone/carpet offering table, Blood Blossom burning for fast resource-intensive cleansing, and a Bloodstone chalice with Holy Water for slow cleansing and manual refill. The source also proposes slow blood/pure-ecto output and distinguishes that output from Haunt-raising Liquefactor bucket production.
- [ ] **Industrial Purifier:** More expensive, automated alternative with fluid input/output, Blood Blossom material input, fuel input, and one-quarter the ritual purifier's effectiveness. Keep it distinct from the current pure-ecto-producing Purification Station.

### Optional integrations and end-game direction

Tinkers' Construct compatibility is excluded by the user's 2026-10-02 decision. The original compatibility sketch is retained only in [Notes](Notes.md) as historical reference.

- [ ] **Thaumcraft:** Consider the proposed Ectis aspect from Exanimis + Spiritus, or using Exanimis/Vis directly. These are historical integration ideas, not installed dependencies.
- [ ] **Blood Magic / Witchery:** Evaluate life-point or nature-energy alternatives for primal machines, only if suitable current-version integrations are chosen.
- [ ] **Natura and other material consumers:** Preserve the goal that Spiritwood behaves as wood for ordinary recipes and interoperates through suitable modern tags/recipes.
- [ ] Decide whether the notebook's tentative end-game **Death Note** is a separate progression item. Do not rename or treat the existing Book of Ectcetera guide as that artifact by default.
- [ ] Extend **Book of Ectcetera** guidance as new systems ship, retaining its deliberate spelling; the indexed book itself is already implemented.

## Long-term roadmap

Duplicator and Triplicator are explicitly long-term features, outside the selected near-term gameplay requirements. Original numbers remain tentative design references.

- [ ] **Duplicator:** One-item duplication, Nether Star activation, large Haunt increase, and a complicated recipe involving a Dragon Egg with Fading IV and Spectral I. Preserve tentative costs: 100 buckets of ectoplasm at 10 mB/t, or 1,000,000 RF at 100 RF/t with a listed 100-raw-ectoplasm requirement for the RF format.
- [ ] **Triplicator:** One input -> three outputs, recipe of nine Duplicators, Nether Star plus twice the resource/time investment, tentative 200 buckets or 2,000,000 RF at 100 RF/t, and 200 raw ectoplasm in the RF format. Haunt increase is slightly above the Duplicator rather than 1.5x its increase.

The duplication notes do not settle whether fluid, RF, and raw ectoplasm costs are alternatives or combined inputs. Clarify the mode, exact Nether Star consumption, eligible items, recipe, and operation duration before implementation.

## Maintenance backlog [Discuss]

- [ ] Replace or adapt the template root README into a player/contributor entry point when requested.
- [ ] Investigate startup fluid-model warnings if missing visuals are observed in play.
- [ ] Establish a Second Brain mirror only if the user requests one; repository docs are the current source.

## Documentation upkeep

Append dated entries to [Notes](Notes.md) when decisions or implementation changes land. Update checklists and verification limits here as evidence changes. Update [Overview](Overview.md) when architecture or scope changes, and update `Last Synced` after repository synchronization.

### 2026-10-04 - Ghost�s Garb selected and implemented

- [x] Ectothread: eight String around Raw Ectoplasm; lime wool placeholder.
- [x] Four Ghost�s Garb pieces: standard armor crafting shapes, lime leather placeholders, leather defense, iron durability.
- [x] Full-set horizontal phasing with existing spectral barriers and intangible damage immunity; intangible attackers still damage wearers.
- [x] Full-set negative ambient haunt protection and haunting-owned effect cleanup; beneficial events remain enabled.
- [ ] Client playtest: dyed placeholder appearance, phasing/prediction, barriers, and recovery combined with Spirit Journey grayscale.

### Haint Garb / Spectralite combination follow-up

Purified Ectothread (eight String around Pure Ectoplasm, output eight) and
light blue leather Haint Garb are implemented. Completed 2026-10-04: template-free
smithing combines a Haint part with the matching Spectralite part into Spectralite
Haint armor, retaining Spectralite stats and spectral damage reduction plus garb
abilities. Ghost Garb upgrades with one Pure Ectoplasm. Full Haint-family sets gain
a Jump Boost III-strength jump; all garb sets have benefit tooltips. Build and all
42 required server GameTests pass; client jump/appearance playtesting remains.
See [GHOST_GARB](GHOST_GARB.md) for recipes, attributes and migration checks.

### 2026-10-05 - EMF Reader implemented

- [x] Iron, Holy Salt and redstone crafting recipe; six colored tier bulbs and exact labeled reading.
- [x] Chunk reading in either hand; held Use points inward and reads personal Haunt.
- [x] Generated recipe/models, server-authoritative transient readings, guide chapter and server regression tests.
- [ ] In-game visual playtest of HUD and inward pose in both hands.

See [EMF Reader](EMF_READER.md) for crafting, behavior and migration details.

### 2026-10-05 - Precise EMF Reader

Implemented a shapeless EMF Reader + Raw Ectoplasm upgrade. Live digits on the held device and HUD show actual personal hundredths and whole-point chunk values with two decimal places. Existing hands, inward use pose, bulbs, and read-only behavior are retained. Recipes/models are generated; transient sample networking preserves hundredths without saved-world changes. Client visual playtesting remains. See [EMF Reader](EMF_READER.md).

### 2026-10-05 - Independent personal Haunt effects

- [x] Separate benefit/risk rolls and cooldowns, cumulative tier eligibility, localized chat hints on successful effects.
- [x] Whispers, spectral empowerment, following footsteps, false apparitions, restless visits, furnace interference, marked presence, pursuers and warned breaches.
- [ ] Hidden inscription revealing, spirit workers, recognition services and veil revelations: explicit `missing function` comments; excluded until supporting systems exist.
- [ ] Visible harmless follower and further machine interference integrations.
- [ ] Client effects/hints and extended live-world encounter playtest.

See [Personal Haunt effects](PERSONAL_HAUNT_EFFECTS.md) for exact behavior and missing dependencies.

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
