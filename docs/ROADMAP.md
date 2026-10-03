# Mortal Expansion - Roadmap

**Repository:** https://github.com/Howierux/mortal_expansion  
**Branches:** `main` (consolidated baseline), `dev` (active work)  
**Docs:** [Overview](Overview.md), [Notes](Notes.md), [Roadmap](ROADMAP.md)  
**Last Synced:** 2026-10-03 - Spiritwells and Spirit Journey stage-two development on `dev`; `main` retains the documented Ward baseline.  
**Current Phase:** Gameplay requirements and Haunt effect proposal accepted; implementation and gameplay validation remain pending.

> **Checklist legend:** `[ ]` not started, `[x]` completed, `[~]` partially completed. Implementation status and gameplay verification are tracked separately. Discussion items are proposals, not approved implementation work.

## Selected gameplay requirements [Planned]

The user selected these requirements on 2026-10-02. This records the intended work; the documentation update does not mark any gameplay change as implemented.

### Spiritwells [Foundation selected 2026-10-03]

- [x] Add rare intangible Overworld anchors, rendered as a faint Nether Star about two blocks above dry land.
- [x] Persist anchors and prevent block placement in a two-block spherical radius.
- [ ] Validate worldgen, placement rollback, multi-placement, piston movement, and persistence on a headless server.
- [ ] Visually verify floating appearance in a user-requested client session.
- [ ] Design/select additional Haunt effects and Spirit Journey interactions.

### Spirit Journey [Staged work selected 2026-10-03]

The user requested implementing this feature in smaller parts, starting with entry into an alternative game mode on death. This supersedes the earlier deferral.

- [x] Stage 1 implementation: Offer Spirit Journey only to dead players with a valid selected self-named Grave; respawn at that Grave in vanilla Spectator mode. Retain ordinary Respawn and existing inventory-drop/Grave rules.
- [x] Automated rule/guide checks, Gradle build, and mod loading through data generation passed for the corrected stage-one code.
- [ ] Client gameplay verification of mode entry.
- [x] Stage 2 implementation: Defer eligible death items until the choice; Ordinary Respawn releases them at the death site, while Spirit Journey holds them in serialized player data. Keep Inventory and Vanishing retain vanilla behavior.
- [x] Stage 2 headless death/respawn, serialization, and vanilla-rule validation (four GameTests); build, guide, and data-generation checks passed. Full restart/reconnect and client gameplay remain untested.
- [ ] Later: Add grayscale and dedicated journey movement/state/exit handling.
- [ ] Later: Enforce spirit-blocking mechanisms during the journey.
- [ ] Later: Persist a corpse and support inventory recovery by returning to it, including logout/restart and cross-dimension cases.
- [ ] Visually validate the death-screen option and mode entry in a client session requested by the user.

The stage order after the first part is a proposed work breakdown, not authorization to implement every stage in this update. Full corpse recovery is required before Spirit Journey becomes a complete survival recovery feature.

### Elemental ranged attacks

- [ ] Give Elementals a ranged attack. Define its projectile/effect, damage, range, cooldown, and target behavior alongside the mob balance review.
- [ ] Verify attacks against tangible and intangible targets under the shared spectral combat rules.

### Intangible mob attacks

- [ ] Ensure intangible mobs can attack both tangible and intangible mobs. Check damage handling, melee reach, ranged/projectile behavior, and changes between tangible/intangible phases.
- [ ] Validate both target types for each applicable mob, including mixed-form encounters and summoned mobs. Preserve intended passive behavior and target selection; the ability to damage both types does not make every passive mob hostile.

### Spirit Villager trading

- [ ] Add a discoverable, passive **Spirit Villager** with a familiar villager trading interface for Mortal Expansion's key items and resources.
- [ ] Decide where it can be found, its spawn conditions, trade inventory, prices, progression gates, stock limits, and restocking behavior.
- [ ] Verify that trading works and that passive behavior remains intact. Earlier village-graveyard, haunted-material exchange, and Creeper-deterrence ideas are optional details rather than required final behavior.

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

The user accepted the effect proposal on 2026-10-02, including the subsequent **4x4-chunk area around the player** constraint. The values below are starting balance targets; all implementation remains unchecked.

#### Event controller and local Haunt evaluation

- [ ] Use one shared event controller for timing, eligibility, protection, and cooldowns. Keep inventory changes, spawning, and Haunt adjustments authoritative on the server; send sounds and visual effects to the affected players.
- [ ] For personal effects, use the higher of personal Haunt and the evaluated local chunk Haunt. World changes and mob appearances use local chunk Haunt.
- [ ] Limit chunk-based event checks to the **4x4 chunks around the player: sixteen chunks covering 64x64 blocks**. Chunks outside this footprint do not influence these effects. Personal Haunt stays separate. Define the exact alignment of the even-sized footprint and the method for evaluating its sixteen values before implementation; the accepted scope does not specify maximum versus average or another aggregation rule.
- [ ] Check every **30 seconds**, allow one event at a time per player, and apply a **60-second player cooldown** after an event. Give harmless atmosphere higher selection weights than shoving, shuffling, or hostile appearances.
- [ ] Start with the following total event chance per eligible check, then select one eligible effect by weight. Void has no ambient event roll. Cooldowns and eligibility affect actual event frequency.

| Evaluated Haunt tier | Chance per 30-second check |
|---|---:|
| Natural | 2% |
| Supernatural | 5% |
| Dead Rising | 8% |
| Demonic | 12% |
| Abominable | 16% |
| Eldritch | 20% |

- [ ] Apply a shared chunk cooldown to beneficial world events so multiple players cannot multiply furnace completions or material conversions. Set its duration and exact selection weights during implementation.

#### Ambient effects

Implement each effect with the minimum tier and behavior below. Thresholds are minimum tiers rather than exclusive bands.

| Status | Effect | Minimum tier | Accepted behavior |
|---|---|---|---|
| [ ] | Harmless hit sounds | Natural | Quiet impact or footstep behind the player, with no damage or combat reaction. |
| [ ] | Shadows | Supernatural | Faint figure at the edge of vision for two seconds; disappears when approached or directly observed. |
| [ ] | Slowness II | Supernatural | Three seconds with a cold breath sound. Skip while swimming, climbing, or under a stronger slowing effect. |
| [ ] | One-block shove | Demonic | Short push opposite the player's facing direction. Check the destination and skip blocked spaces, falls, and lava hazards. |
| [ ] | Inventory shuffling | Demonic | Swap two occupied main-inventory slots. Preserve hotbar, armor, offhand, and open containers; show brief spectral particles. |
| [ ] | Random potion effect | Supernatural | Five seconds of one curated effect: Speed, Haste, Weakness, Slowness, or Mining Fatigue. Avoid lethal effects and preserve stronger existing effects. |
| [ ] | Nearby Spirit | Natural | Spawn one passive Spirit within five blocks at a valid position. Prevent repeated appearances while an event-created Spirit remains nearby. |
| [ ] | Spiritwell Ghost | Dead Rising | A nearby Spiritwell releases a real hostile Ghost with a sound/particle warning. Respect mob caps and valid unprotected spawn positions. Spiritwell anchors are implemented; the Ghost event remains planned. |

#### Beneficial effects

| Status | Effect | Minimum tier | Accepted behavior |
|---|---|---|---|
| [ ] | Finish cooking | Supernatural | Complete one valid recipe in an active furnace within eight blocks. Consume input normally, preserve output and experience, require output space, and complete one operation rather than a whole stack. |
| [ ] | Planks to Spiritwood | Supernatural | Convert one ordinary inventory plank into one Spiritwood plank. Show a green shimmer and require room for the result; leave placed buildings intact. |
| [ ] | Cobblestone to Gravestone | Supernatural | Convert one inventory cobblestone into one Gravestone using the same single-item, output-space, and visual rules. |
| [ ] | Ghost's Garb repair | Dead Rising | During a spectral visitation event, restore one durability to each equipped Ghost's Garb piece. Requires the garb to exist; unrelated equipment is unaffected. |

#### Protection and major encounters

| Status | Feature | Accepted behavior |
|---|---|---|
| [ ] | Ghost's Garb immunity | A full set blocks negative ambient effects while allowing beneficial events. Direct mob attacks follow the armor's separate combat rules. |
| [ ] | Holy Water blessing | Contact grants 60 seconds of negative-ambient-effect protection and removes active effects applied by haunting. Lower personal Haunt by one point, with a cooldown on that reduction; define the cooldown duration and effect provenance handling. |
| [ ] | Purification Ward protection | Suppress negative ambient events inside covered chunks while the existing cleansing continues separately. |
| [ ] | Haint encounter reward | Defeating the encounter Haint removes five Haunt points from its chunk and caps Haunt at the resulting value for ten minutes. Apply once per encounter, including its split into Haunts; define when the split encounter counts as defeated. |
| [ ] | The End event | Rare Eldritch encounter with sky changes and the proposed Wither/terrain effects. After successful resolution, remove twenty points from affected chunks and cap each at its resulting value for twenty minutes. Define event footprint, trigger odds, resolution, and interaction with existing Haunt simulation in a separate event design. |

- [ ] Validate tier thresholds, protection, cooldowns, multiplayer overlap, inventory conservation, recipe output/experience, safe movement, mob limits, and temporary cap expiry. These events do not depend on Spirit Journey.

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

The discussed 3x3 structure footprint is a tentative local influence area. The accepted **4x4-chunk limit for player-centered Haunt effect evaluation** remains a separate constraint; finalize how structural influence and player checks interact during design.

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
- [~] Spiritwell worldgen/protection foundation is implemented above. Define additional event rules if the Ghost-spawn idea is selected.
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
