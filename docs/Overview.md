# Mortal Expansion overview

Updated 2026-10-16 for consolidated `main`. See the [documentation index](README.md), [release status](RELEASE_STATUS.md), and [roadmap](ROADMAP.md).

Mortal Expansion connects death, spirits, magic, and machinery through Haunt. Players can cultivate haunted locations or cleanse them using offerings and spiritual defenses. The original design notebook remains a source of ideas; historical proposals do not establish current behavior.

## Current systems

| Area | Implemented behavior | Details |
| --- | --- | --- |
| Haunt | Separate persistent personal and chunk values, fractional points, named tiers from Void to Eldritch, spread and cleansing. New players start at Natural (15.00); saved values are preserved. Personal Haunt drives independent benefit/risk events. | [Personal effects](PERSONAL_HAUNT_EFFECTS.md) |
| Exploration | Spiritwood Forest, Graveyard biome and Cemetery structures; Overworld-only Spiritwells; named discovery within 20 blocks; short glowing green footprint trails toward recorded undiscovered sites. | [Discovery](SPECTRAL_DISCOVERY.md) |
| Death and travel | Self-named Grave anchors, grayscale Spirit Journey and corpse recovery, anywhere-accessible journey recovery menu, and resource-consuming Grave Porting to eligible loaded Graves across dimensions. | [Journey](SPIRIT_JOURNEY.md), [porting](GRAVE_PORTING.md) |
| Knowledge | Four book tiers, research map, gradual secret revelations, enforced recipe locks, and research contributions. The station is the first whispered secret, available with a starter book. | [Book](BOOK_OF_ECTCETERA.md), [Insight](SPECTRAL_INSIGHT.md), [research](MORTALITY_RESEARCH_STATION.md) |
| Creatures | Spectral enemies and transformations; peaceful Spirit/Elemental ranged retaliation and sunlight immunity; Spirit Villager trades and kill quests with immediate rewards; four Mortal Golem flesh tiers. | [Spirits](SPIRIT_VILLAGERS.md), [quests](SPIRIT_QUESTS.md), [golems](MORTAL_GOLEM.md), [retaliation](SPECTRAL_RETALIATION.md) |
| Equipment | Bloodstone/Spectralite tool abilities, Ghost/Haint intangibility and haunt protection, Haint jumps, Spectralite Haint combinations, and mineral armor trims. Drowning and void damage bypass garb protection. | [Garb](GHOST_GARB.md), [trims](ARMOR_TRIMS.md) |
| Processing | Heated cauldrons, Ectoplasmic/Enchantment Generators, Ectchantment Machine, Haunt/Haint Furnaces, and Purification Station accepting Grave Moss or Holy Salt. Ectoplasm generator fuel emits chunk Haunt. | [Recipes](MACHINE_RECIPES.md), [purification](PURIFICATION_STATION.md) |
| Fluids | Ectanks hold 16,000/64,000 mB; pipes hold 2,000/8,000 mB and transfer at 500/2,000 mB per tick. Configurable sides, bucket interaction, retained contents, tank fill rendering, and loaded-only transfer. | [Infrastructure](ECTO_INFRASTRUCTURE.md) |
| Defenses | Basic/Precise EMF Readers, salt/torch barriers, sanctification, Wards, opposing offering altars, and 50%/100% Dreamcatcher interception of good and bad events. | [EMF](EMF_READER.md), [salt](HOLY_SALT_BARRIERS.md), [altars](ALTARS_AND_SECRETS.md), [Dreamcatchers](DREAMCATCHER.md) |

Purification Wards retain their documented chunk tickets. Altars, Dreamcatchers, fluid networks, and Grave Porting have separate loaded-area rules; do not assume every system loads distant terrain.

## Runtime and development

The build targets Minecraft 1.21.1, NeoForge 21.1.219, JDK 21, and TerraBlender 1.21.1-4.1.0.8. The declared mod version is `1.0.0`; historical milestone suffixes are not release versions.

Java source lives under `src/main/java/com/howierux/mortalexpansion/`. Gameplay rules, registration, persistence, networking, and client rendering have focused packages. Generated resources live in `src/generated/resources`; textures, translations, and other authored resources live in `src/main/resources`. Follow [data generation](DATA_GENERATION.md) when changing resource definitions.

Development normally targets `dev`; `main` receives requested consolidations. Local playtest copies and launches are updated on request. [Notes](Notes.md) preserves dated decisions; the roadmap retains historical proposals with current-status corrections. Automated validation does not imply every visual, balance, multiplayer, or long-running save scenario has been manually verified.
