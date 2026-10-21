# Demon Sin Affinities

Each Demon has one of seven equally likely Sin Affinities, chosen on creation and saved by a stable lowercase name. Affinities currently affect appearance only; all share Demon health, damage, movement, hitbox, intangibility, loot, spawn rules, and the Darkhold revelation requirement. No sin-specific abilities are implied by these models.

| Affinity | Color | Model details |
| --- | --- | --- |
| Pride | Indigo | Three crown-like horns, forehead crest, slightly raised chin |
| Envy | Green | Uneven horns and narrow, slanted brows |
| Gluttony | Orange | Fuller jaw, small teeth, rounded projecting belly |
| Wrath | Red | Heavy outward horns, angry brows and small shoulder spikes |
| Sloth | Blue | Forward-drooping horns, lowered eyelids and slouched head/shoulders |
| Greed | Yellow | Short upright horns, ear ornaments, coin-like collar and buckle |
| Lust | Violet | Swept-back horns, forehead gem and a small collar accent |

The seven pre-baked native humanoid layers share one neutral Demon skin atlas. Native tint supplies the affinity color, while geometry and pose distinguish the silhouettes and faces. Models are cached by affinity; they are not rebuilt per frame. The spectral opacity remains 20%. The native humanoid animation, shared hitbox and secret-based per-viewer culling remain in effect, including outlines, nameplates, shadow and fire presentation. Dropping the Darkhold or lowering Haunt does not undo previously learned sight.

The revealed Demon entry in the Darkhold now includes a third page explaining the seven colors and identifying details. Affinities have localized display names. For testing, summon a specific affinity with `/summon mortal_expansion:demon ~ ~ ~ {SinAffinity:"pride"}` (replace `pride` with another name above). The viewer still needs the Demon revelation to see it.

## Persistence and migration

`SinAffinity` is saved as a lowercase string, independent of enum declaration order. The existing synchronized entity-data slot now carries an explicit 0-6 index. Existing Demons with the old `DemonVariant` integer retain their color identity: 0/red becomes Wrath, 1/violet becomes Lust, 2/blue becomes Sloth, and 3/orange becomes Gluttony. The next save writes the stable named affinity. Unknown named affinities or invalid network indices fall back safely to Pride. Registry entity ID and ordinary Demon saved state are unchanged.

Client-only model and renderer classes are separate from the server-side affinity identity. Native layer builders and the existing humanoid model API supply geometry; no new mixin, shader, renderer dependency, or data schema is added beyond the named saved affinity. Future migration should verify model-layer registration, humanoid part names/UVs, translucent rendering and viewer-knowledge culling.

## Art files and provenance

The built-in OpenAI ImageGen tool produced the shared Demon atlas from this mod's original Ghost texture as a UV-layout reference. The accepted master is `exec-adacbc42-8775-4c71-9399-15d28862a42c.png` under `C:/Users/scott/.codex/generated_images/01a10808-7c57-7922-a619-5f19e36de671/`. Export uses nearest-neighbor sampling to 64x64, native UV occupancy/opaque surface texels, and packs the four generated material swatches into the model's exact UV slots. The renderer supplies translucency. No outside game's Demon or character artwork was used.

Final workspace asset: `src/main/resources/assets/mortal_expansion/textures/entity/demon.png`.

Native models: `src/main/java/com/howierux/mortalexpansion/client/DemonModel.java` and `DemonRenderer.java`.

Final accepted generation prompt:

Precise texture edit: keep this EXACT 1024x1024 square canvas and ALL existing UV island positions. It is a technical Minecraft skin atlas on a 64x64 logical grid enlarged16x, NOT an illustration. Do not enlarge or rearrange the head or torso, do not crop unused transparent lower half. Face remains at image x128..255 y128..255. Existing body/limbs remain image y256..511. Repaint the existing used pixels as a neutral grayscale Demon with pale ash-gray skin, black square eyes, two tiny ivory fangs, charcoal tattered vest. Completely remove sage/green colors. Flat simple Minecraft pixels, 2-3 shades, no antialiasing, gradients, shadows or glow. No new left arm or left leg islands needed; mirrored native limbs reuse the upper half. In the transparent upper right, add exactly four flat8x8 logical material squares: logical x56..63 y0..7 charcoal, x56..63 y8..15 ivory, x56..63 y16..23 black, x56..63 y24..31 palegray. These are technical UV swatches for model horns and decorations. Preserve transparent background everywhere else. Preserve existing head and body scale, matching original reference exactly.

## Validation

The Gradle build and all 212 required server GameTests passed. The new affinity test checks all seven named save/load roundtrips, synchronized index/color identity, old four-color migration and safe unknown-ID handling. Existing Darkhold tests continue to cover private vision and progression. Guide and generated-resource checks pass. The exported atlas was inspected for native UV coverage; actual model animation, lighting and silhouette readability remain client playtest checks.

## Daylight behavior

Every affinity is intrinsically sunlight-immune. Natural daytime spawns become possible in Abominable/Eldritch chunks (50+ Haunt) after world awakening; Demonic chunks (40-49) retain normal darkness requirements. See [Darkhold spawning rules](DARKHOLD.md).

## Long-range perception

All affinities share a 96-block base follow/detection range. Survival players can be acquired through ordinary walls of any thickness. Normal combat target filters still exclude Creative and Spectator players (including Spirit Journey). Allied Demons retain their prohibition on attacking players. The Darkhold secret gates the player's ability to see Demons, not a Demon's ability to sense a vulnerable player.

Blocks in `mortal_expansion:intangible_solid`, including Gravestone and Spiritwood, block the eye-to-eye sight ray; Holy Salt and Spirit Torch barrier columns still block it too. Inserting a screen drops the normal player target immediately and stops direct spectral pursuit. Removing it permits a later reacquisition. Other spectral mobs retain their existing four-layer ordinary-wall tracking limit. This changes sight and pursuit, not phasing through protected materials.

The Demon-specific ray uses Mojang's voxel traversal once per visibility check instead of quarter-block sampling and a wall-cell set. It rejects unloaded terrain without requesting chunks and remains bounded by the current follow-range attribute. Native target search scheduling remains in use; there is no new world scan or chunk loader.

`DemonPerceptionVersion` is saved as 1. On reading an unversioned Demon whose saved follow-range base is still the old default 32, the base upgrades to 96. Other customized saved ranges and subsequent versioned tuning are preserved. Target goal registration uses a focused protected hook in the shared monster class; no mixin or new packet is needed.

The Darkhold's revealed Demon entry includes this behavior on its fourth page.

Perception validation: the build and all 219 required server tests passed. Tests exercise the actual target selector with a real mode-switching ServerPlayer at 80 blocks behind eight stone layers, immediate Gravestone screening, reopening/reacquisition, Creative/Spectator rejection, range bounds, allied restrictions, old/custom saved ranges, and missing-terrain rejection. Other spectral mobs retain their prior wall-layer limit.
