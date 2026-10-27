# Ritual Star: Overworld Haunt rituals

Craft one Nether Star surrounded by eight Bloodstone to make one Ritual Star. Place it in the offering slot of an Overworld altar:

- Altar of Impurity consumes it to add 10 current Haunt points to every Overworld chunk.
- Altar of Purity consumes it to subtract 10 current Haunt points from every Overworld chunk.

Both rituals are repeatable. Normal 0–69 limits apply, and uncapped fractional values are retained. Personal Haunt, natural generation classification and natural recovery baselines stay unchanged. Existing recovery, spread, Wards and ordinary altar pulses continue after the shift. The Nether, End and custom dimensions are unaffected, and their altars reject Ritual Stars without consuming them.

The star is a special sacrifice, rather than burn-time fuel: one star triggers one world ritual and grants no fuel. The altar uses its normal offering cadence (up to 12.5 seconds if a prior offering is cooling down) and does not need existing burn time. An already burning altar retains its normal fuel and subsequent local pulses. Purity's offering-fuel blessing does not amplify the world shift. Stars stack to one; automated insertion uses the same dimension checks and consumption cadence. Current Overworld players receive a ritual message and the altar emits particles.

## Existing, unloaded and future chunks

Activation visits only the tracked, already loaded Overworld chunks and applies the shift immediately. Saved chunks that are unloaded receive any missed shifts on their next load or authoritative Haunt read. Future generated Overworld chunks initialize normally, then receive the world's recorded rituals. No region-file sweep, terrain generation or forced chunk loading is used to deliver the world effect.

Ritual order matters at the bounds. A chunk at 65 receiving +10 then -10 becomes 69 then 59, including if it was unloaded for both rituals. A new 15-point chunk receiving those same two rituals returns to 15. Neither case changes its natural recovery target.

## Persistence and migration

`mortal_expansion_world_haunt_rituals` is Overworld SavedData containing the ordered ±10-point journal. `haunt_ritual_revision` is a persisted integer chunk attachment identifying the last applied entry. Existing saves default to an empty journal and revision zero. Chunk reconciliation writes the existing tier/progress/fraction fields and invokes the existing furnace tier-change hook. Native chunk load events reconcile after natural initialization; newly initialized chunks reset their revision before applying history. Authoritative setters reconcile pending rituals before setting their requested value, avoiding a later duplicate shift.

The journal grows by one integer per sacrifice. Loaded chunks normally process only the newly added entry; chunks absent for many rituals replay the missed entries once. This preserves clamping order without scanning unloaded chunks. The ritual does not modify the daily simulation's natural-floor calculation. No custom packets or mixins are added.

Tests cover the crafting pattern, actual altar consumption, loaded neighbor updates, repeatability, fraction/cap behavior, journal and per-chunk save state, delayed/new chunk delivery, unchanged recovery targets and Nether rejection. Real-world restart and multiplayer presentation remain playtest checks.

## Item art

The generated 32x32 RGBA sprite is `src/main/resources/assets/mortal_expansion/textures/item/ritual_star.png`, exported nearest-neighbor from the original transparent built-in imagegen master: `C:/Users/scott/.codex/generated_images/01a10808-7c57-7922-a619-5f19e36de671/exec-e13db282-a218-412d-ac8f-6d5bb2ed0e46.png`.

Final prompt: Original Minecraft inventory item sprite for a Ritual Star: a luminous ivory four-point Nether-Star-like core held inside a dark crimson bloodstone ring, eight small deep red gem studs spaced evenly around it. Bold clean pixel art, 32x32 logical grid enlarged nearest-neighbor, limited palette white ivory gold and dark blood red. Symmetric centered readable silhouette with transparent margins. Flat front view, no text, no realistic shading, no bloom, no background or cast shadow. Genuine transparent background. This item is crafted from a Nether Star surrounded by eight Bloodstone, and sacrificed in either a holy or unholy altar.
