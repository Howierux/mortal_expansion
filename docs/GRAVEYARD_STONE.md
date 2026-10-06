# Graveyard Stone weathering

Ordinary `minecraft:stone` slowly becomes `mortal_expansion:gravestone` when the block's own biome is Graveyard. Cemetery structures are irrelevant: the process works inside and outside them, on naturally generated or player-placed Stone, at any Haunt tier. Cobblestone, Smooth Stone, Deepslate, Purified Gravestone and other blocks are not converted.

The existing loaded-chunk daily simulation samples at most 64 positions per chunk per Minecraft day, alternating top-surface and full-height samples, and converts at most four eligible blocks. Actual conversions depend on Stone availability and the biome at each sampled position. Only loaded tracked chunks are checked; no terrain is loaded or generated. Stone below ground can also weather where its actual biome remains Graveyard rather than a cave biome. These are starting balance limits.

The conversion helper does not read or write chunk/personal Haunt, and there is no resource cost or minimum tier. Existing daily Haunt decay, spread and Ward cleansing remain independent. Weathering uses a separate seeded random source so adding it does not consume the old Haunt random rolls. The cadence follows existing day-time transitions, including sleep, and pauses with a frozen day cycle; it does not catch up unloaded days.

Implementation uses the existing chunk-load/unload tracking and level tick daily simulation, native biome registry keys, block checks and setBlock. No new saved fields, generated resources, packets or mixins. Migration checks: loaded-chunk access, heightmap semantics, native block updates and biome lookup at the exact candidate position.

Validation covers conversion in Graveyard without any structure lookup, unchanged fractional Haunt, other biomes/materials, repeated conversion rejection, unloaded positions, sampled Stone conversion and the four-block per-pass cap. Client appearance and long-term weathering balance remain playtest checks.
