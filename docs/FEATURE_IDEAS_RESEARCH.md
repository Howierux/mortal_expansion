# Mortal Expansion: magic and magitech feature research

Research date: 2026-10-04. Status: discussion proposals only; no gameplay implementation or roadmap approval implied.

## Recommendation

Make the relationship between death, Haunt, and industry the center of progression. Players should investigate a haunting, choose whether to settle or cultivate it, and build rituals or machinery around that decision. The primal and scientific paths can reach similar outcomes through different costs and tools.

The five best starting candidates are:
1. A Spectral Analogue that makes personal and local Haunt understandable.
2. A small family of offering-based grave rituals.
3. Spirit Villager commissions and bargains.
4. Recoverable, clearly signaled failures in haunted processing.
5. A limited spirit caretaker that maintains a graveyard or ritual site.

These connect existing systems, give players reasons to explore and build, and can begin without a new dimension or a general-purpose spell engine.

## Current foundation and overlap

The reviewed baseline is the Haint/Spectralite Haint milestone on main/dev, commit fa9f898. Mortal Expansion already has separate personal and chunk Haunt, escalating supernatural activity, ectoplasm processing, haunted furnaces/generators, cleansing and containment, named Graves, Spirit Journey with corpse and Spiritwell recovery, Spirit Villager trades, garb phasing and wall breathing, and spectral equipment.

Important distinction: personal Haunt controls ambient event eligibility; chunk Haunt supports world simulation and normal spawning. Proposed local rituals and machines should specify which value they affect.

Some overview and historical roadmap paragraphs still describe implemented features as future work. The recent feature documents and dated notes establish the current garb, journey, and merchant behavior. This report does not recommend adding those features a second time.

Several suggestions below expand existing backlog concepts: detection, grave consecration, Blood Blooms, ritual/industrial purification, fluid infrastructure, boss souls, and portals. Others introduce new gameplay around those foundations. Scope ratings are relative design judgments, not estimates in days.

## Lessons from the reference mods

| Reference | What its own project material emphasizes | Useful lesson for Mortal Expansion |
|---|---|---|
| [Thaumcraft](https://www.curseforge.com/minecraft/mc-mods/thaumcraft), [author API](https://github.com/Azanor/thaumcraft-api) | Extracting Essentia from objects; an in-game guide; API systems for aspects, aura, research, crafting, and golems. | Make supernatural phenomena observable and usable. Connect discoveries, equipment, and processing. |
| [Witchery](https://www.curseforge.com/minecraft/mc-mods/witchery) | Gardens, brews, ritual circles, poppets, spirit-world travel, familiars, covens, necromancy, and dangerous bargains. | Give preparations, places, offerings, and relationships a role in magic. |
| [Ars Magica 2](https://www.curseforge.com/minecraft/mc-mods/ars-magica-2) | Player-created spells, bosses, and an in-world start to obtaining its guide. | Let players customize a few useful abilities and earn powers through meaningful encounters. |
| [Ars Nouveau](https://www.curseforge.com/minecraft/mc-mods/ars-nouveau) | Custom spells, artifacts, rituals, magical machinery, and creature-based automation. | Use recognizable supernatural helpers for small automation tasks. |
| [Blood Magic](https://www.curseforge.com/minecraft/mc-mods/blood-magic) | Powerful magic and technological advancement with dangerous resource costs. | Make greater power involve an understandable sacrifice or exposure. |
| [Occultism](https://www.curseforge.com/minecraft/mc-mods/occultism) | Summoned spirits, ritual preparation, hidden otherworld groves, storage, familiars, and task automation. | Spirits can be actors in the economy, workforce, and exploration systems. |
| [Botania](https://botaniamod.net/) | Natural magic used as technology, visible resource transport, renewable resources, and designed automation challenges. | Make builds readable and make automation depend on arranging interacting systems. |
| [Mana and Artifice](https://www.curseforge.com/minecraft/mc-mods/mana-and-artifice) | Modular spells, prepared rituals, factions, guided tiers, and modular worker constructs. | Offer clear milestones and specialized helpers; give each capability a cost. |
| [Hexerei](https://www.curseforge.com/minecraft/mc-mods/hexerei) | Mixing cauldrons, herb storage, candles that melt, and distinctive vegetation and structures. | Useful magic can also furnish a believable lived-in ritual space. |
| [Malum](https://www.curseforge.com/minecraft/mc-mods/malum) | Soul-derived arcana, spirit infusion, rune totems, and magical natural materials. | Tie material processing and area effects to spirits and place. |
| [EvilCraft](https://www.curseforge.com/minecraft/mc-mods/evilcraft) | Blood-powered machinery, infusion and repair, collectible Vengeance Spirits, spirit processing, tanks, and fluid tools. | Death-related resources can support a coherent industrial chain. |

These are thematic references, not a list of dependencies to install. The source descriptions span different Minecraft versions and sometimes contain older introductory text. Historical Thaumcraft and Witchery are useful references without being assumed available for Mortal Expansion's current Minecraft/NeoForge version. Specific integrations would need a separate version and API check.

## Feature candidates

### A. Investigation and discovery

**1. Spectral Analogue and field readings — High priority; small to medium scope; existing backlog expansion.**
A handheld instrument shows personal Haunt separately from local Haunt, with a clear rising/falling indication. A placed version outputs a redstone signal for a selected local threshold. The field instrument could hint at nearby activity without giving perfect long-range tracking. This makes the mod's central resource understandable and supports automated safety systems.

First version: two readings and trend arrows. Later: a graph, alarms, or recorded site history. Do not make detection a prerequisite for ordinary item recovery.

**2. Case files in the Book of Ectcetera — High priority; medium scope; new proposal.**
Record discoveries by doing things: observe a Spirit, test a salt boundary, recover a corpse, purify ectoplasm, or complete a grave ritual. Entries explain what was learned and suggest the next experiment. Reward knowledge with optional recipes or cosmetic records rather than hiding essential mechanics behind repetitive scanning.

This takes inspiration from guided magical discovery while using existing content. Multiplayer discoveries should be shareable; practical safety and recovery instructions should always be available.

**3. Haunted-site investigations — Medium priority; medium scope; new proposal.**
A graveyard, abandoned workshop, or mausoleum contains a short, generated story: an inscription, a disturbed object, a spectral trace, and a final spirit interaction. Players can resolve the haunting peacefully or exploit the site's Haunt for materials. Start with several reusable scenario templates instead of unrestricted procedural storytelling.

**4. Spirit-visible secrets — Medium priority; medium scope; new proposal.**
During Spirit Journey or through a charged spectral lens, reveal hidden inscriptions, tracks, doors, and clues. Give Spiritwood forests and graveyards a second layer of exploration. Living players need an alternate access route through the lens so deliberately dying is not the optimal discovery strategy.

### B. Rituals, graves, and spirit relationships

**5. Grave offerings and consecration — High priority; medium scope; existing backlog expansion.**
Use a Grave, offering tray, candles, and selected ingredients for a small ritual family: calm a local haunting, consecrate an anchor, call a willing Spirit, or bless a graveyard. Show duration, remaining uses, and maintenance needs. Different outcomes create uses for both raw and purified ectoplasm.

Start with cleansing and one protective blessing. Stronger revival benefits should stay limited and should not replace free corpse recovery.

**6. Spirit Villager commissions — High priority; medium scope; new proposal.**
Add a few services alongside ordinary trades: identify a haunted relic, request a flower offering, repair an inscription, or help settle a local haunting. An experienced merchant can reward completed work with ritual knowledge or a one-use charm.

Keep the current recipe-balanced trades useful. Begin with two or three repeatable services and visible payment/reward; do not require a separate full quest framework.

**7. Spirit bargains — Medium priority; medium scope; new proposal.**
A player chooses a benefit with a stated obligation: a spirit improves a furnace for a night in exchange for offerings, grants a temporary spectral skill in exchange for personal Haunt, or guards a site while its grave is maintained. Terms, expiry, cancellation, and failure consequences must be visible.

Prefer finite, reversible bargains to permanent hidden penalties. Contract drawbacks should be tracked separately from ordinary negative haunt events so garb cannot silently erase the agreed cost.

**8. Graveyard harmony and unrest — Medium priority; medium to large scope; new proposal.**
A maintained graveyard gains a limited local benefit from appropriate graves, Spiritwood, offerings, and calm spirits. Neglected or exploited sites develop unrest. Represent maintenance through simple capacities and visible states rather than constant gardening chores.

Keep this separate from the existing chunk Haunt number: a high-Haunt site might still be orderly if properly tended. Start with one site controller and one benefit.

**9. Protective effigies — Medium priority; small to medium scope; new proposal.**
Craft an owner-bound ectothread effigy with a narrow purpose: absorb one haunt disturbance, mark a safe return point, or protect against a specific spectral attack. Charges and the binding owner are visible. This supplies accessible early protection before full garb.

Avoid unconditional death prevention and unrestricted remote player targeting. Specialized protection should complement the existing recovery choices.

**10. Blood Blooms and ritual gardens — Medium priority; medium scope; existing backlog expansion.**
Grow mod-specific plants in different Haunt conditions. One plant thrives in calm soil, another in a maintained haunted graveyard, and another near a Spiritwell. Their products feed offerings, incense, purification, and equipment recipes.

Allow cultivated production after discovery. Keep ingredients understandable and avoid several nearly interchangeable new fluids or flowers.

### C. Spirit labor and haunted industry

**11. Spirit caretaker — High priority; medium scope; new proposal.**
A willing spirit performs one job within a small marked area: refill offering trays, collect Grave Moss, supply a furnace, or carry items between two inventories. A grave or shrine is its anchor; offerings maintain the agreement.

Start with one job and visible work boundaries. Respect barriers and permissions, suspend work in unloaded areas, and avoid giving helpers unrestricted access to nearby inventories.

**12. Spiritwood constructs — Later priority; large scope; new proposal.**
Build a physical worker from Spiritwood and spectral materials, with one replaceable module for hauling, harvesting, or guarding. It offers a more robust scientific counterpart to a ritual spirit helper.

This overlaps the caretaker's role, so establish the simple helper first. Modular limb systems and generalized programming can wait.

**13. Ectoplasm infrastructure — High priority; medium scope; existing backlog expansion.**
Complete the Ecto Pipe, Ectank, and Liquefactor as a small visible processing chain. Add pipe direction indicators, tank gauges, and a way to stop a process automatically. The first meaningful build could turn renewable ingredients into purified processing supplies.

Review the historical leaking-tank idea before committing to it. If leakage remains, provide a clear indicator, manageable consequences, and an upgrade that contains it.

**14. Containment and recoverable processing failures — High priority; medium scope; new proposal.**
High-Haunt processing offers better output but requires containment. Poor containment produces detectable spectral pressure, visible warnings, and eventually a manageable event such as a released hostile spirit or temporarily unstable machinery.

Use existing Haunt, ectoplasm, and defenses before adding a second generic pollution system. Let players pause production, repair containment, or perform a cleansing ritual. Reserve major threats for deliberate high-tier operation.

**15. The Possessor as a spirit-infused machine — Medium priority; medium scope; existing backlog expansion.**
A machine uses ectoplasm and a spirit agreement or captured non-player residue to grant a selected spectral property. Begin with the existing Demi-Tangible role. Later upgrades could add controlled spectral utilities without duplicating vanilla enchantments.

The ritual path might perform the same transformation with offerings and preparation; the machine path spends power and processing resources. Keep recipe availability and cost explicit.

**16. Paired ritual and industrial purification — High priority; medium scope; existing backlog expansion.**
Offer an inexpensive hands-on ritual purifier and a more expensive automatic industrial purifier. Both operate on the same underlying Haunt rules, with clearly different throughput, ingredient cost, and setup.

First establish their purposes relative to the existing Purification Station, which produces purified material. Area cleansing and material purification should remain understandable separate operations. Display how much personal or local Haunt a process changes.

**17. Redstone spectral controls — Medium priority; small to medium scope; new proposal.**
Expose local Haunt thresholds, containment warnings, ward charge, tank levels, and spirit-helper activity through comparators or configurable signals. Players can design a shutdown, alarm, or replenishment system using ordinary redstone.

This is a strong way to add magitech depth without immediately introducing a custom network, programmer, or storage terminal.

### D. Equipment, encounters, and travel

**18. A focused spectral skill set — Medium priority; medium to large scope; new proposal.**
Give an ectoplasmic focus a short selection of thematic powers: reveal spirits, tether one ghost briefly, illuminate hidden traces, or launch a demi-tangible projectile. Let players select a delivery mode and one effect, with charges or ectoplasm cost.

Keep early scope to a handful of combinations. The full spell-builder breadth of the Ars mods would be a much larger independent system and could dilute Mortal Expansion's identity.

**19. Boss soul imprints — Medium priority; large scope; existing backlog expansion.**
Boss encounters yield identity-linked imprints for equipment sidegrades: a Pharaohl imprint might briefly manipulate a site's Haunt, a Haint imprint might frighten nearby enemies, or an Elemental imprint might add a particular spectral attack.

Use a small number of slots and mutually exclusive choices. Existing Spectralite Haint armor is already powerful; new effects should have costs or conditions rather than endlessly stacking permanent bonuses. Do not require collectible player souls.

**20. Cleansing and binding encounters — Medium priority; large scope; new proposal.**
A major haunting can be ended through combat, a prepared binding ritual, or a negotiated resolution. For example, a haunted tomb boss is temporarily made tangible by activating ritual anchors while teammates manage escaping spirits.

Begin with one encounter that proves spectral combat and rituals can cooperate. Distinct phases and clear counterplay matter more than adding several bosses at once.

**21. Linked grave or Spiritwell travel — Later priority; large scope; existing portal backlog expansion.**
An optional ritual links discovered sites and permits travel at an explicit ectoplasm/Haunt cost. Start with same-dimension travel and require destination validation. Travel could favor spirit-state access while retaining a costly route for living players.

Keep this independent of Spirit Journey's existing rescue menu: the proposed network is planned transportation, not a new fee or restriction on recovering death items. Never generate terrain merely to locate an unknown well.

**22. Spirit-side expeditions — Long-term priority; very large scope; new proposal.**
A deliberately entered spirit realm or special expedition area could hold unresolved memories, unusual plants, and late-game encounters. Players enter alive through a ritual or travel system and can leave safely.

Prototype spirit-visible secrets in existing terrain first. A complete dimension adds terrain, structures, progression, mobs, client presentation, and substantial save/version maintenance.

## Suggested progression

1. **Observe:** Book guidance, field instrument, first graveyard investigation.
2. **Protect and tend:** salt/torches, offerings, basic effigies, cultivated ritual ingredients.
3. **Cooperate:** merchant commissions, limited spirit caretaker, simple bargains.
4. **Process:** fluid chain, paired purification, spectral machinery, redstone controls.
5. **Choose greater risk:** deliberately cultivate a contained haunted site, undertake a binding encounter, and choose a boss imprint.
6. **Travel and explore:** a linked-site network, then optional spirit-side expeditions much later.

A player should be able to choose a quieter ritual lifestyle or a more dangerous industrial approach. Both should use the same world and materials, with tradeoffs that remain readable.

## Recommended first design batch

| Candidate | Small first deliverable | Decision to make before implementation |
|---|---|---|
| Spectral Analogue | Show personal and local Haunt independently | Exact readings immediately, or tier readings with an advanced upgrade? |
| Grave rituals | Offering tray plus cleansing/blessing recipes | Ritual radius, costs, duration, and maintenance burden |
| Spirit commissions | Two services offered by experienced merchants | Rewards, repeatability, and whether work is personal or shared |
| Haunted processing | One warning state and one recoverable failure | Which existing machine benefits and what containment prevents |
| Spirit caretaker | Refill nearby offering trays | Binding method, payment, range, and behavior when the anchor disappears |

These five form a connected loop: measure a site, investigate it, negotiate with its inhabitants, maintain it, and decide how much power to draw from it.

## Scope and migration guidance

Use existing Haunt values before adding another player resource bar. Define personal versus local effects explicitly. Preserve the current protections and item-recovery rules. Set abilities should describe conditions and exceptions clearly.

For eventual implementation, put ritual recipes, offering ingredients, machine recipes, and membership categories in data-driven definitions and generated resources. Keep ritual logic separate from registry/network/rendering APIs. Stable IDs and minimal persisted state will help migration between Minecraft versions. New helpers, bargains, and site controllers need deliberate owner, dimension, unload, restart, and cancellation behavior.

Finish and balance the existing journey, combat, merchant, and garb foundations alongside new features. Several old checklist entries need reconciliation before treating the roadmap as an implementation queue.

The linked project pages and author materials support the reference-mod summaries. The priorities, proposed mechanics, progression order, and scope ratings are this report's design recommendations, not claims that those exact systems exist in the reference mods.
