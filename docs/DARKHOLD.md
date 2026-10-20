# The Darkhold and Secrets of the Damned

The final Book of Ectcetera upgrade combines the **purified book (tier 3)** with **one Ritual Star** in a crafting grid. The resulting **Darkhold (tier 4)** has original black/crimson book art and immediately teaches every Secrets of the Dead recipe to its carrier. Names and other book components survive upgrading. Earlier tiers retain gradual Insight progression.

## Exposure and revelations

The Secrets of the Damned research-map tab is invisible before tier 4. Its introduction is immediately readable; the Demon entry remains unknown until the carrier reaches **70 personal Haunt**, the first band above Eldritch. A thematic message announces revelation. Knowledge survives lowering Haunt, death, and logout; like ordinary learned books, a Darkhold can carry revealed knowledge to another reader.

Each living, nonspectating carrier receives **+0.01 personal Haunt every 200 ticks (10 seconds at 20 TPS)**. Extra copies do not stack. There is no offline catch-up. Removing all carried copies stops exposure; accrued Haunt remains. Exposure continues beyond 79. Existing Haunt effects and cleansing can still independently change the total.

Nested vanilla containers/bundles and backpacks exposing NeoForge's item-handler capability are recognized without opening them. Traversal is read-only and bounded to eight nesting levels and 4,096 stacks per root item, with identity-cycle protection. Backpacks using private storage without the standard capability need a future explicit integration; remote storage is not treated as carried. Nested books teach normal knowledge to their carrier; their individual revelation component refreshes when taken out/read.

## Reader display

The basic reader keeps its in-hand bulb display without a new HUD. Both readers still measure chunk Haunt normally; holding use reads personal Haunt. The precise reader retains decimals above the old maximum.

| Personal Haunt | Overflow display |
| --- | --- |
| 70-79 | First bulb becomes red |
| 80-89 | First two red |
| 90-99 | First three red |
| 100-109 | Fourth becomes dark purple |
| 110-119 | Fifth becomes dark purple |
| 120 and higher | Three red, three dark purple |

Higher readings remain numeric on the precise reader; lamps saturate after the sixth overflow band. Existing named-tier gameplay rules saturate at Eldritch. Chunk Haunt, ritual clipping, and natural recovery baselines retain their existing limits. Personal Haunt uses a separate overflow attachment after world awakening; its technical fixed-point safety ceiling is 20,000,000 points, rather than 79.

## Demons

Crafting the first Darkhold permanently awakens Demons for the save, across dimensions. Crafting previews do not activate it. Player crafting and vanilla automated crafting both activate it; carrying a Darkhold obtained through commands/integrations also reconciles the flag.

Natural Demon spawning requires chunk Haunt **Demonic (40) or above**, plus ordinary Monster placement/darkness rules. Generated biome modifiers add single Demons at weight 4 to Overworld, Nether, and End biome tags. Normal monster caps and other spawn restrictions still apply. Explicit command/spawner sources remain available for development testing.

Demons are stronger Ghost-like intangible hunters: 40 health, 8 base attack damage, movement speed 0.30. Four synchronized, saved color variants tint the Ghost appearance. They use shared spectral movement, defenses, barriers, sanctification, and Smite sensitivity. Their native loot table drops one **Demonic Ash**.

Demons are invisible to each viewer until that player has revealed the Demon secret in a Darkhold. Merely crafting the book, reaching 70 without a revelation, or another player learning it does not grant sight. Revelation is permanent: removing the book or lowering Haunt does not hide them again. Only the visual presentation is gated; unseen Demons still exist, move, and fight normally. The renderer suppresses the model, outline, fire display, shadow and nameplate for uninitiated viewers. Private knowledge synchronization refreshes on revelation, login, respawn, and dimension changes; no global invisibility flag or new mixin is used. Further uses for Demonic Ash and later Secrets of the Damned remain future content.

## Persistence and migration

Existing registry IDs and normal secret bits 1-64 are unchanged. The book-tier codec now accepts 0-4; legacy unmarked books remain tier 3, never automatically Darkholds. New `damned_secrets` book memory and `damned_knowledge` player memory currently use bit 1 for Demons. The research map combines masks only for display; ordinary recipe knowledge remains mask 127.

World SavedData `mortal_expansion_darkhold` stores `Awakened`. Player `player_haunt_excess` supplements the existing tier/progress fields and survives death. Exposure timing resets on login and never pays offline time. The final crafting recipe is generated through the existing native recipe codec. Demon biome modifiers, biome/entity tags, models, and loot use data generators. No new mixins were needed: native item post-crafting and player events supply activation.

## Temporary art provenance

Original Darkhold and Demonic Ash inventory art was generated with OpenAI ImageGen, then sized and centered on transparent 32px canvases. No third-party book or game textures were copied. Demons reuse this mod's original Ghost art with native render tinting.

Darkhold prompt: "Original Minecraft inventory sprite of a forbidden sentient spellbook called The Darkhold. Closed charcoal-black leather book with deep crimson bindings, bloodstone-red corner studs, a small pale bone sigil resembling an open eye on the front, page edges old ivory, subtle dark purple clasp. Flat isometric Minecraft item pixel art on a 32x32 logical grid enlarged nearest-neighbor, centered clear silhouette and transparent margins. No readable lettering, no title text, no realistic lighting, no background, no glow or cast shadows. Genuine transparent background."

Demonic Ash prompt: "Original Minecraft inventory sprite of Demonic Ash: small irregular mound of charcoal-gray and deep purple ash, with a few dull crimson ember flecks and pale gray powder edges. It is dropped by spectral Demons. Flat readable pixel art on 32x32 logical grid enlarged nearest-neighbor, centered with transparent margins, limited palette, no container, no text, no realistic lighting, no background, no bloom. Genuine transparent background."

## Validation

Gameplay checks cover real result pickup/input consumption, preview purity, permanent normal knowledge, the 70 threshold, legacy migration, nested exposure, nonstacking copies, continuation beyond 79, player save/load, native automated-craft activation, persistent world gating, Demon attributes/tags/variant saving, and extended EMF/book network synchronization. Visual placement, backpack-specific integrations, spawn frequency, and balance still need playtesting.

Development checks can use `/give @s mortal_expansion:book_of_ectcetera[mortal_expansion:ectcetera_tier=4]`, then `/haunt player get` and repeated `/haunt player add <amount>` to reach 70. `/summon mortal_expansion:demon` bypasses natural spawning requirements for direct combat testing.
