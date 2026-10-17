# Current status and publication preparation

Updated 2026-10-16. The gameplay baseline promoted to `main` is `f6e36d817a88972118fd59078811f2a034eb33bf`. Its Gradle build and all **194 required server GameTests** passed. This documentation revision adds no gameplay and does not change that count. Earlier per-feature counts are historical.

## Implemented, with further validation remaining

The [overview](Overview.md) lists current systems. Recent work includes fluid infrastructure, spectral retaliation, armor art/trims, Ghost targeting of Spirits/Elementals, and Holy Salt cleanup/workload bounds. The salt changes address stale client barriers and expensive enclosure searches; updated-build performance has not yet been measured against the earlier live sample.

Before publishing, playtest book navigation/knowledge locks, machine screens, fluid connections, garb movement/damage exceptions, Spirit Journey/recovery, Grave Porting, and Golem clearance/combat. Exercise multiplayer, reconnect/restart, and older-save behavior where relevant. Automated checks cover many server rules; they do not establish visual quality or long-term balance. Startup fluid-model warnings remain a documented investigation item.

## Partially implemented or deferred

- Some proposed Haunt effects lack supporting systems: hidden inscription revelations, spirit workers/services, recognition, and veil revelations. See [personal effects](PERSONAL_HAUNT_EFFECTS.md) and command feedback; an autocomplete entry alone does not establish that an effect can run.
- Spirit Villagers have expertise-based trades and kill quests. Expertise-gated quests and additional services remain future work.
- Named discovery and Grave Porting exist. A general linked Spiritwell/portal network, never-generated-site locating, and wider expedition systems remain proposals.
- Pipes and tanks store/transfer fluids. The Liquefactor, tank leakage, specialized containment, and broader processing integrations remain unfinished.
- Major Haint/End encounters and temporary post-encounter Haunt caps remain deferred. A general mob balance/spawn review is still needed.
- Historical Spiritweave, boss-soul upgrades, creative flight, Blood Blooms, Possessor/Ectocleanser machines, and Duplicator/Triplicator concepts are distinct from shipped garb and machines.
- Vanilla Crafters support learned secret recipes; integrations with other mods' automated crafting remain unfinished.

Grave Moss fire cleansing is closed in favor of altars. Nether/End Spiritwell generation is intentionally out of scope.

## Distribution checks

Align version metadata, changelog, dependencies, and artifact before tagging a release; the declared version remains `1.0.0`. Temporary art provenance is documented. Review third-party redistribution permissions, especially the [Warframe tapping recording](THIRD_PARTY_AUDIO.md), before distributing a public build. This source update does not create a release or certify asset permissions.
