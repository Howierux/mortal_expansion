# Natural spawn frequency update

The balance pass favors Mortal Expansion mobs while slightly reducing ordinary Zombies, Skeletons and Creepers. Haunt thresholds, daytime/darkness requirements, Demon world awakening and secret visibility, native monster/creature caps, and supporting-ground/collision rules still apply. Weights express relative selection within a spawn category, not a per-tick spawn probability or a guaranteed population multiplier.

| Mob | Previous weight | New weight | Habitat |
| --- | --- | --- | --- |
| Spirit | 12 | 24 | Existing Overworld biome pool |
| Ghost | 18 | 36 | Existing Overworld biome pool |
| Empowered Phantom | 7 | 14 | Existing Overworld biome pool |
| Haunt | 4 | 8 | Existing Overworld biome pool |
| Demon | 4 | 12 | Existing Overworld, Nether and End biome tags |
| Elemental | No natural entry | 12 | Spiritwood Forest only, groups of 1-2 |

Elementals use supported on-ground creature placement. Their natural runtime spawns require Supernatural Haunt (20+) and, like Spirits, do not require darkness or suffer sunlight damage. Vanilla creature caps and other normal restrictions still apply. Existing world-generation spawn handling remains separate to avoid recursive persistent-chunk requests; Spiritwood Forest has a Supernatural baseline.

Eligible **Zombie-family, basic Skeleton-family, and Creeper natural spawn attempts in the Overworld retain 85% acceptance**. This is a 15% reduction applied after the native spawn-table choice, preserving each biome's original weights and other mods' spawn tables. Natural-tier Zombies retain their existing scarcity multiplied by 0.85 (20% becomes 17%). Below-threshold undead remain blocked. Wither Skeletons and Ghasts keep their existing weights. Explicit commands, spawners and other non-natural reasons are unaffected by this reduction.

Haint appearance rolls near qualifying witch huts rise from 0.5% to **1% per existing ten-second check**, retaining Abominable Haunt and active-Haint limits. Wisp o' Lantern Ghost attempts now occur every **12,000 loaded nighttime ticks (10 minutes)** instead of 24,000, retaining the nearby-player requirement and four-Ghost limit. Daylight/unloading still pauses the timer. Longer old saved delays clamp to the new interval on loading.

Mummies and Pharaohls retain their summon/structure encounters, and Mortal Golems retain player construction. This pass does not turn those encounters into ordinary biome spawns or change boss combat summon rates.

## Generation and compatibility

`ModSpawnModifiers` bootstraps native AddSpawns biome modifiers through the existing registry provider. The stable `add_haunt_spawns` modifier moves from authored resources to generated output; the authored file is removed to prevent duplicates. Demon and Elemental modifiers are generated alongside it. Common-hostile rejection extends the existing supported FinalizeSpawn event handler; no biome replacement, mixin or new saved entity format is needed.

All changes live on dev until main promotion is requested. Existing local playtest installations need an explicit update/restart to use the new values.

## Validation

Native data generation, resource/guide checks, the Gradle build, and all **216 required server GameTests** passed. Integration tests read the modified live biome spawn lists to verify weights and no duplicate entries, restrict Elementals to Spiritwood Forest, exercise Supernatural gating, sample deterministic Zombie/Skeleton/Creeper rejection, and confirm explicit summons are unaffected. Existing lantern timer and nearby-limit tests pass with the shorter interval. Long-term population balance remains a playtest concern.
