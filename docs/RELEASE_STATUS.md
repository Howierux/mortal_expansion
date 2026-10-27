# Current status and publication preparation

## 1.1.0-beta.1 candidate — 2026-10-27

Prepared for dev-to-main promotion. The versioned build and all **239 required server GameTests** pass after isolating shared-cap/autonomous-target test fixtures; gameplay is unchanged by that isolation. Main has not been merged by this preparation. See [candidate changelog and release gate](RELEASE_1.1.0-beta.1.md). Earlier dated validation counts below are historical. The cemetery playtest follow-up adds Stone Fence to the native fence tags, so placed/generated connections survive neighbor updates; existing disconnected saved states may need a neighbor update.

Updated 2026-10-16. The gameplay baseline promoted to `main` is `f6e36d817a88972118fd59078811f2a034eb33bf`. Its Gradle build and all **194 required server GameTests** passed. This documentation revision adds no gameplay and does not change that count. Earlier per-feature counts are historical.

## Current dev validation (2026-10-20)

[The Darkhold](DARKHOLD.md) adds the Ritual Star book upgrade, hidden Damned chapter, personal Haunt overflow/exposure, extended EMF readings, and gated Demon spawning/Demonic Ash. The dev build and all **219 required server GameTests** passed; generated-resource and book checks also passed (659 generated resources). Five Darkhold tests exercise these connected rules, including private per-player Demon vision. Demons remain visually hidden until their secret is revealed; that learned sight persists after removing the book or lowering Haunt. The original Darkhold suite encountered the existing intermittent Ghost-target/retaliation test before confirmation; the subsequent visibility update passed all 211 tests on its first run. Seven saved Demon Sin Affinities now supply distinct native model details and the requested color mapping, with legacy color migration; the follow-up build and all 212 tests passed. See [Demon affinities](DEMON_AFFINITIES.md). Visuals, backpack-specific interoperability, and spawn balance remain playtest items. The Demon daylight follow-up permits exposed daytime natural spawning at Abominable/Eldritch Haunt and makes Demons sunlight-immune at every tier; its build and all 214 required tests passed. Main has not been promoted; the local playtest copy was last updated to the affinity revision, before this daylight change.

The [spawn balance follow-up](SPAWN_BALANCE.md) doubles existing mod spawn weights, triples Demon weight, adds Supernatural+ Spiritwood Elementals, and reduces natural Overworld Zombie/Skeleton-family and Creeper acceptance by 15%. Rare Haint and lantern Ghost sources are faster. Its generated resources, build, and all 216 tests passed. The local playtest copy has not received this follow-up.

Raw Ectoplasm is now a shared offering for both altars, adding sixty seconds per item. Generated-tag checks, the build and all 216 tests passed; the existing offering test now checks Raw Ectoplasm consumption in both altar types.

On 2026-10-21, both altars gained separate optional JEI offering categories driven by their loaded tags and real burn durations. The build and all 217 required server tests passed without JEI; the JEI-enabled build/data-loading check, 661-resource check and book validation also passed. Live JEI navigation/layout remains a client playtest item. This follow-up is on dev; the local playtest copy is unchanged.

The Demon perception follow-up sets a 96-block base detection range, ignores ordinary wall thickness, and immediately loses normal player tracking behind demi-tangible screens/barriers. The build and all 219 required tests passed, including actual Survival/Creative/Spectator targeting, old-range migration and unloaded-terrain safety. The Darkhold documents the behavior. This update is on dev; the local playtest copy is unchanged.

## Implemented, with further validation remaining

The [overview](Overview.md) lists current systems. Recent work includes fluid infrastructure, spectral retaliation, armor art/trims, Ghost targeting of Spirits/Elementals, and Holy Salt cleanup/workload bounds. The salt changes address stale client barriers and expensive enclosure searches; updated-build performance has not yet been measured against the earlier live sample.

Before publishing, playtest book navigation/knowledge locks, machine screens, fluid connections, garb movement/damage exceptions, Spirit Journey/recovery, Grave Porting, and Golem clearance/combat. Exercise multiplayer, reconnect/restart, and older-save behavior where relevant. Automated checks cover many server rules; they do not establish visual quality or long-term balance. Startup fluid-model warnings remain a documented investigation item.

## Partially implemented or deferred

- Remaining Haunt gaps include hidden inscription revelations, Spirit Recognition services and Veil Revelation encounters. Helpful Visitation now grants a Spirit Boon; worker jobs remain a separate proposal. See [personal effects](PERSONAL_HAUNT_EFFECTS.md) and command feedback; an autocomplete entry alone does not establish that an effect can run.
- Spirit Villagers have expertise-based trades and kill quests. Expertise-gated quests and additional services remain future work.
- Named discovery and Grave Porting exist. A general linked Spiritwell/portal network, never-generated-site locating, and wider expedition systems remain proposals.
- Pipes and tanks store/transfer fluids. The Liquefactor, tank leakage, specialized containment, and broader processing integrations remain unfinished.
- Major Haint/End encounters and temporary post-encounter Haunt caps remain deferred. A general mob balance/spawn review is still needed.
- Historical Spiritweave, boss-soul upgrades, creative flight, Blood Blooms, Possessor/Ectocleanser machines, and Duplicator/Triplicator concepts are distinct from shipped garb and machines.
- Vanilla Crafters support learned secret recipes; integrations with other mods' automated crafting remain unfinished.

Grave Moss fire cleansing is closed in favor of altars. Nether/End Spiritwell generation is intentionally out of scope.

## Distribution checks

Align version metadata, changelog, dependencies, and artifact before tagging a release; the candidate declares `1.1.0-beta.1`. Temporary art provenance is documented. The previously bundled Warframe tapping recording has been removed; current tapping uses vanilla wood impacts. See [audio provenance](THIRD_PARTY_AUDIO.md). Review other asset provenance before distribution. This source update does not create a release or certify asset permissions.
