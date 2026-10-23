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

Natural Demon spawning requires chunk Haunt **Demonic (40) or above** and world awakening. Demonic chunks retain ordinary Monster light requirements. At **Abominable (50) or Eldritch (60)** chunk Haunt, natural daytime placement uses Mojang's any-light Monster rule, so exposed daylight no longer prevents spawning. Nighttime retains ordinary darkness requirements. Difficulty, valid supporting ground, collision, mob caps, biome eligibility and other normal spawning restrictions still apply. **Demons are immune to sunlight at every Haunt level**, including Void; they neither seek daytime shelter nor restrict their movement to sustaining chunks. This is not general fire immunity. Generated biome modifiers add single Demons at weight 12 to Overworld, Nether, and End biome tags. Normal monster caps and other spawn restrictions still apply. Explicit command/spawner sources remain available for development testing.

Demons are stronger Ghost-like intangible hunters: 40 health, 8 base attack damage, movement speed 0.30. Seven synchronized, saved [Sin Affinities](DEMON_AFFINITIES.md) have matching colors and distinct native Demon model details. They use shared spectral movement, defenses, barriers, sanctification, and Smite sensitivity. Their native loot table drops one **Demonic Ash**.

Demons are invisible to each viewer until that player has revealed the Demon secret in a Darkhold. Merely crafting the book, reaching 70 without a revelation, or another player learning it does not grant sight. Revelation is permanent: removing the book or lowering Haunt does not hide them again. Only the visual presentation is gated; unseen Demons still exist, move, and fight normally. The renderer suppresses the model, outline, fire display, shadow and nameplate for uninitiated viewers. Private knowledge synchronization refreshes on revelation, login, respawn, and dimension changes; no global invisibility flag or new mixin is used. Further uses for Demonic Ash remain future content. The seven sin abilities are described below.

## Seven sin secrets

The unlock sequence reverses the terraces of Dante's *Purgatorio*: **Lust, Gluttony, Greed, Sloth, Wrath, Envy, Pride**. Reveal each band while carrying/reading a Darkhold. Revelations are permanent and the book records them; abilities remain usable without holding the book. Demon sight still reveals at 70 independently of these seven secrets. Each revelation and successful activation has a thematic message.

| Personal Haunt | Secret | Ability |
| --- | --- | --- |
| 70 | Lust | Crouch three times within three seconds. Adult animals within eight blocks enter native love mode; compatible pairs breed through normal AI. Ten-second cooldown. Babies and breeding cooldowns are respected. |
| 80 | Gluttony | Eat at full hunger. Saturation has no gameplay upper cap; the hunger bar stays at 20. Native exhaustion/healing still consume the reserve. Food is consumed normally, including its normal effects and returned containers. |
| 90 | Greed | Hold use with a diamond for two seconds. Find one diamond/deepslate diamond ore in loaded terrain within 64 blocks and convert it into one diamond in inventory, dropping it at the player if full. No available ore means no diamond. |
| 100 | Sloth | Start sleeping in a safe Overworld bed during the day to call a sleep vote. Others may join without the secret. A strict majority of all online nonspectators must sleep long enough; then time advances to 13000 (night). Vote counts are announced. |
| 110 | Wrath | Taking actual health damage or killing a living entity grants Strength I for ten seconds. Another trigger raises this to Strength II and refreshes the duration. Further triggers refresh at II; existing stronger/longer potion effects are preserved. |
| 120 | Envy | Hold the player-list key (Tab by default), click another online player's name, and inspect a read-only snapshot of all 36 inventory slots, four armor slots, and offhand. Release Tab to dismiss the list; Escape closes the inventory vision. |
| 130 | Pride | Gain one experience point every ten seconds while alive and nonspectating. No offline gain. |

Greed uses the server's normal `ServerPlayerGameMode.destroyBlock` path, preserving spawn protection, adventure restrictions, cancellable NeoForge block-break events, and block destruction hooks. Cancelled ore is left intact; the search may continue to other ore. Native mining loot is suppressed only during this synchronous secret operation to deliver exactly one diamond, independent of Fortune, Silk Touch, or the tool in the other hand. Searches inspect section palettes first, scan at most 4,096 block positions per tick, never load chunks, stop on dimension changes or moving more than 16 blocks from the search origin, and have a five-second retry interval. Claim mods must expose their normal NeoForge mining protection hooks; private protection systems may require an adapter.

Sloth retains bed range, obstruction, dimension and nearby-monster restrictions. `doDaylightCycle=false` prevents the time skip. The daytime vote overrides the normal `playersSleepingPercentage` threshold with a strict majority; ordinary nighttime sleep remains unchanged. Players in other dimensions count as online but cannot vote from that dimension. A normal safe-bed interaction is the vote; there is no separate confirmation dialog.

Envy requests are authorized on the server against the viewer's persistent secret, limited to one per ten ticks, and sent only to that viewer. Offline or self targets are rejected. The screen has no container menu, writable slots, transfer actions or inventory mutation packets. Snapshots remain a view of the moment selected; reopen to refresh. Ender chests, external storage and independently stored backpack contents are not enumerated.

### Ability hooks and migration

Gameplay and thresholds live in `DamnedSecret`/`DamnedAbilities`; packets and client screens are separate. Timers/search state are transient and reset across player replacement/logout; learned secrets retain death-copying persistence. Existing ordinary recipe bits and Demon bit are preserved.

The framework has no general event for permitting full-hunger food use or changing saturation's clamp. `DamnedPlayerFoodMixin` permits eating and associates native `FoodData` with its owner; `DamnedFoodDataMixin` removes only saturation's upper clamp for an unlocked living player. This covers native food APIs (including cake) while retaining hunger, exhaustion, regeneration, saves and health packets. `DamnedDiamondUseMixin` gives diamonds a native hold duration only for Greed readers. `DamnedDaySleepMixin` suppresses vanilla's automatic morning skip only during an active daytime Sloth vote: the sleep-finished event cannot cancel vanilla's subsequent wake-up. Recheck these narrow signatures when porting versions.

## Persistence and migration

Existing registry IDs and normal secret bits 1-64 are unchanged. The book-tier codec now accepts 0-4; legacy unmarked books remain tier 3, never automatically Darkholds. New `damned_secrets` book memory and `damned_knowledge` player memory retain bit 1 for Demons; bits 2 through 128 store the seven sin secrets. The widened 0-255 codecs read existing 0/1 saves unchanged. The research map combines masks only for display; ordinary recipe knowledge remains mask 127.

World SavedData `mortal_expansion_darkhold` stores `Awakened`. Player `player_haunt_excess` supplements the existing tier/progress fields and survives death. Exposure timing resets on login and never pays offline time. The final crafting recipe is generated through the existing native recipe codec. Demon biome modifiers, biome/entity tags, models, and loot use data generators. Native item post-crafting and player events supply Darkhold activation. Focused ability mixins are documented below.

## Temporary art provenance

Original Darkhold and Demonic Ash inventory art was generated with OpenAI ImageGen, then sized and centered on transparent 32px canvases. No third-party book or game textures were copied. Demons now use an original neutral Demon atlas with seven native model variations; see [Demon art provenance](DEMON_AFFINITIES.md).

Darkhold prompt: "Original Minecraft inventory sprite of a forbidden sentient spellbook called The Darkhold. Closed charcoal-black leather book with deep crimson bindings, bloodstone-red corner studs, a small pale bone sigil resembling an open eye on the front, page edges old ivory, subtle dark purple clasp. Flat isometric Minecraft item pixel art on a 32x32 logical grid enlarged nearest-neighbor, centered clear silhouette and transparent margins. No readable lettering, no title text, no realistic lighting, no background, no glow or cast shadows. Genuine transparent background."

Demonic Ash prompt: "Original Minecraft inventory sprite of Demonic Ash: small irregular mound of charcoal-gray and deep purple ash, with a few dull crimson ember flecks and pale gray powder edges. It is dropped by spectral Demons. Flat readable pixel art on 32x32 logical grid enlarged nearest-neighbor, centered with transparent margins, limited palette, no container, no text, no realistic lighting, no background, no bloom. Genuine transparent background."

## Validation

The sin update passes the production build and all **235 required GameTests**. Five added gameplay checks cover reverse-order unlock boundaries and persistence, native survival food consumption/saturation saves, repeated-crouch courtship, Wrath stacking, Pride timing, diamond held-use tracking and protected ore extraction, Sloth joining/majority rules, and private inventory snapshot serialization. The Envy screen was rendered in a development client at 30% volume and inspected for readable inventory, armor/offhand and read-only presentation. The temporary probe is excluded from the production jar; its reusable source is in `tools/client_checks/EnvyScreenCheck.java`. Full multiplayer input/claim-plugin combinations still need playtesting. Earlier full-suite runs showed intermittent failures in existing Ghost targeting/encounter spawning checks; the final run passed them as well.

Gameplay checks cover real result pickup/input consumption, preview purity, permanent normal knowledge, the 70 threshold, legacy migration, nested exposure, nonstacking copies, continuation beyond 79, player save/load, native automated-craft activation, persistent world gating, Demon attributes/tags/variant saving, and extended EMF/book network synchronization. Visual placement, backpack-specific integrations, spawn frequency, and balance still need playtesting.

Development checks can use `/give @s mortal_expansion:book_of_ectcetera[mortal_expansion:ectcetera_tier=4]`, then `/haunt player get` and repeated `/haunt player add <amount>` to reach 70. `/summon mortal_expansion:demon` bypasses natural spawning requirements for direct combat testing.

Daylight validation covers actual registered spawn placement at values 39, 40, 49, 50, 59, 60 and 69, awakening gating, ground requirements, and ten seconds of sun exposure in a Void chunk. The native placement hook skips persistent-Haunt queries for world-generation and explicit spawn reasons.

Demons now have long-range, thick-wall perception with demi-tangible screening. See [Demon perception and save migration](DEMON_AFFINITIES.md#long-range-perception).
