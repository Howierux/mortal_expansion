# Fabric spectral combat and creatures

This slice ports shared intangible combat and all ten Mortal Expansion creature types. It uses native entities, attributes, goals, projectiles, merchant menus and models, with Fabric registrations, attachments and events. Generated data supplies damage types, the Demi-Tangible enchantment, combat tags, spawn-egg models and entity loot. NeoForge main/dev are unchanged.

## Damage and movement

Ordinary physical and environmental damage does not harm intangible entities. Magic/magical damage tags are accepted across mod namespaces by exact final tag names (`magic`, `magical`, `is_magic`, `is_magical`). Explosions, invulnerability-bypassing sources, large Ghast fireballs, spectral arrows, intangible attackers, tagged Bloodstone/Spectralite tools and Demi-Tangible enchanted items can affect them. Void damage always applies.

Native melee factories assign magic-tagged damage to demi-tangible weapons and Mortal Golem attacks. Purified Gravestone Sword deals 50% additional damage to intangible targets. Demons take half damage from demi-tangible weapon attacks and marked projectiles, but do not halve unrelated magic spells. Each Spectralite/combined armor piece reduces damage from an intangible attacker by 5%, up to 20% before ordinary armor calculations.

The anvil accepts a Demi-Tangible book on otherwise unenchantable items. It only bypasses support checks for this enchantment; other book enchantments still need native-valid results. Projectile permission is captured from native arrow/thrown metadata and the actual launching item-use context, stored on the projectile and saved in native NBT. Equipping another weapon after launch does not change the shot. Asynchronous third-party launchers must preserve firing-stack metadata or use the exposed launch-context helper; permission is never inferred from an unrelated currently held item.

Spectral ME mobs pass ordinary walls and rise through ceilings while retaining floors. Spectral-solid tags, salt columns and Spirit Torch links block passage and tracking; unloaded terrain is opaque. Ordinary tracking tolerates four occupied cells; Demons use loaded-only tracking up to 96 blocks. Vanilla Phantoms also phase, and vanilla Phantoms/Ghasts receive intangible defenses and translucent appearances. Ghasts retain their ordinary horizontal movement.

## Creature roster

| Creature | Health | Combat and special behavior |
| --- | ---: | --- |
| Ghost | 24 | Intangible melee hunter; also targets Spirits/Elementals |
| ME Phantom | 34 | Tangible; faster when unobserved; leaves a Ghost on death |
| Spirit | 18 | Peaceful merchant; retaliates with magic bolts |
| Elemental | 36 | Peaceful until damaged; retaliates with magic bolts |
| Haunt | 60 | Strong intangible melee hunter |
| Haint | 120 | Strong intangible hunter; releases three Haunts on death |
| Mummy | 40 | Tangible melee; releases a Ghost on death |
| Pharaohl | 220 | Boss bar, eight-second physical/spectral phases, summons Mummies |
| Demon | 40 | Seven saved/synchronized sin variants; long-range tracking and weapon resistance |
| Mortal Golem | 25–100 | Smite/spectral hunter; flesh tiers, repair/upgrade feeding and Haunt regeneration |

Existing textures/models are reused, including the flat-faced golem and seven Demon model variants. Spirits, Elementals and Demons are sunlight-immune. Other relevant mobs retain Haunt-dependent daylight shelter/movement rules. Golems cannot be targeted by spiders, creepers or Endermen. Each tier heals the same proportion per rotten flesh, upgrades only when fully repaired, and regenerates at Dead Rising or higher. All ten types have creative spawn eggs and `/summon mortal_expansion:<id>` support.

Spirits use the native merchant screen with five expertise tiers, persistent demand/stock/XP and varied offers. The future interaction-choice/quest screen and engraved-grave villager conversion are explicitly deferred.

## Natural spawning and drops

The Overworld receives Spirits (weight 24, groups 1–2), Ghosts (36, 1–2), ME Phantoms (14, 1) and Haunts (8, 1). Elementals appear in Spiritwood Forests (12, 1–2). Minimum chunk tiers are Natural, Dead Rising, Demonic, Demonic and Supernatural respectively. Hostile placement uses native light checks except sustaining high-Haunt daytime placement. Demons have weight 12 in vanilla-dimension biome sets after world awakening; they need Demonic chunk Haunt and may spawn in daylight above Demonic. Creature world generation never queries saved Haunt during the generation pipeline.

Haints, Mummies, Pharaohls and Golems retain explicit/summoned encounter roles. Their future encounter triggers are not replaced with unrestricted natural spawning. Vanilla population rebalance and Overworld Ghast/Wither Skeleton additions remain separate work.

Ghosts, Spirits, Elementals, Haunts and Haints drop raw ectoplasm. ME Phantoms/Mummies drop rotten flesh, Pharaohls also drop their Scepter, and Demons drop Demonic Ash. Golem loot remains empty.

## Demon revelation

The first crafted/carried Darkhold permanently awakens the world for Demon spawning. Carrying one adds 0.01 personal Haunt every ten seconds, with no bonus from additional copies. Traversal is bounded and supports native container/bundle contents; third-party backpack capabilities need integration. Awakening permits personal Haunt above 69 while chunk Haunt remains capped.

At 70 personal Haunt with a Darkhold, the Demon secret enters the book and permanent owner-only player knowledge. Demons remain visually hidden until that knowledge is learned; sight persists after putting the book away and after death. Sin affinities determine appearance, not yet separate sin powers. The seven sin abilities/unlocks remain deferred.

## Barrier lifecycle and boundaries

Salt and torch indexes scan only matching section palettes on chunk load. Successful block transitions update indexes immediately, including commands, actual breaks and client corrections. Chunk unload removes contributions; breaking one of multiple salt blocks in a column preserves the remaining block's protection. Torch links rebuild only after relevant changes and stay within loaded chunks.

Garb movement/breathing/jump/protection-from-events, Holy Water/consecrated-area gameplay, Fancifier spectral-solid resolution, Spirit quests/workstations, golem construction and Spirit Journey are still separate ports. Golems can currently be created with eggs/commands and have their entity interactions. Scepter ammunition/summoning remains unported. No claim of full gameplay or NeoForge save compatibility is made.

## Validation

Thirty native server GameTests include actual damage and source tagging, Demon resistance, boss phases, ordinary/upward/floor collision, anvil output with a test-only empty supported-items tag (forcing the unsupported-item path), projectile launch/NBT provenance, real barrier creation/removal, ten entity factories, death offspring, retaliation bolts, golem stats/repair persistence, Demon variants, Darkhold awakening and owner knowledge. The prior machinery, terrain and knowledge tests remain passing. Client checks exercise real renderers, owner-only Demon visibility and native Spirit merchant menus at 30% master volume; temporary probes are excluded from production.
