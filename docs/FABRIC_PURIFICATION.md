# Fabric furnaces and purification

Haunt Furnace, Haint Furnace, Purification Station and Purification Ward now have functional Fabric implementations. Their original block IDs, models and textures remain. Block crafting and secret gates are now supplied by the [knowledge slice](FABRIC_KNOWLEDGE.md).

## Haunt and Haint furnaces

Both inherit native furnace smelting, inventory, hopper access, recipe experience, recipe lookup, persistence and the vanilla furnace screen. Smelting speed follows the existing rule:

`speed = 1 + 9 Ã— chunk tier ID / 6`

| Haunt tier | Speed | Standard 200-tick recipe |
| --- | ---: | ---: |
| Void | 1Ã— | 200 ticks |
| Natural | 2.5Ã— | 80 ticks |
| Supernatural | 4Ã— | 50 ticks |
| Dead Rising | 5.5Ã— | 37 ticks |
| Demonic | 7Ã— | 29 ticks |
| Abominable | 8.5Ã— | 24 ticks |
| Eldritch | 10Ã— | 20 ticks |

Recipe duration rounds up, with a minimum of one tick. Tier crossings notify furnaces in the affected loaded chunk and rescale active progress proportionally. No recurring world scan is needed.

The Haunt Furnace burns fuel normally. The Haint Furnace spends existing burn time only when cooking advances. At Demonic or higher it preserves burn time even while cooking, after an initial fuel charge. Its open ghast eyes indicate actual Haunt-powered cooking; idle and lower-tier states use closed eyes. Native light/facing states and the existing furnace tints remain.

Three public smelting recipes are generated: Holy Water Bucket â†’ Holy Salt, Liquid Ectoplasm Bucket â†’ Raw Ectoplasm, Purified Ectoplasm Bucket â†’ Purified Ectoplasm. Each takes 200 base ticks and returns its bucket only after a successful smelt. Blocked output does not consume fluid or return a premature bucket. This also works in vanilla furnaces.

Native cooking lacks an input-remainder callback, so one focused `FluidBucketCookingMixin` observes actual consumption during `AbstractFurnaceBlockEntity.serverTick`. It covers native and custom furnaces using that path, including unusual stacked inputs by dropping the extra vessel. Future Minecraft updates should verify the method signature and successful-consumption condition. Spectral Interference pauses remain a marked missing function until Haunt events are ported.

## Purification Station

The existing screen and four slots are functional: Raw Ectoplasm, reagent, output, and Water Bucket. The `mortal_expansion:purification_reagents` item tag initially includes Grave Moss and Holy Salt and is generated programmatically.

Each craft reserves one raw ectoplasm, one reagent and 1,000 mB of water, then uses exactly 100,000 energy across the existing 60-tick process to produce one purified ectoplasm. Partial energy is accounted for without restarting or double-charging. Insufficient energy pauses progress. Completion adds one point of chunk Haunt. Output capacity is checked before starting and while finishing.

- Water capacity: 10,000 mB. Only ordinary water is accepted; fluid access is input-only.
- Energy capacity: 400,000, with up to 10,000 accepted per API operation; no external extraction.
- Fabric fluid pipes can supply water, and the existing generators can supply adjacent energy.
- Hoppers/Fabric item automation can insert ingredients/water buckets and extract output/returned buckets, but cannot steal recipe inputs.
- Bucket input returns an empty Bucket. Paired native menu-data fields preserve exact energy values.

World saves retain inventory, water, energy, partial progress and energy spent. Breaking drops the unprocessed inventory separately and retains water/energy on the station item through `mortal_expansion:purification_station_data`. Replacing restores only those resources, avoiding duplicated inventory or active-craft credit. As in the reference implementation, ingredients reserved by an interrupted craft are not refunded on breaking.

## Purification Ward

The Ward has no Beacon block entity and produces no beam. It protects its own chunk and eight neighbors. At the daily simulation boundary it removes one Haunt point per protected loaded chunk, with overlapping Wards not stacking that daily reduction. Protected chunks neither send nor receive simulated spread, skip natural recovery, and block personal-Haunt leakage. Haunt-event/mob protections will connect when those dependent systems are ported.

Each Ward owns native region tickets for that 3Ã—3 area. Owner positions persist per dimension in native SavedData under `mortal_expansion_purification_wards`, as packed BlockPos longs. On restart, tickets are restored and owner blocks validated after their chunks load. Invalid owners are discarded. Physical removal releases only that Ward's tickets and saved owner; chunk unload alone does not discard its persistent ownership. Overlaps retain independent tickets, and vanilla `/forceload` tickets are untouched.

Version-sensitive ticket and SavedData calls are contained in `PurificationWards`; the protection tracker and daily Haunt planner stay separate. No unrelated pipe or generator begins force-loading chunks.

## Validation and remaining scope

Thirteen native Fabric GameTests pass. Four new checks cover tier rescaling, Haint fuel/eye states, all fluid-cooking bucket returns and blocked output, station energy/persistence/automation and existing generator/pipe integration, portable resource accounting, Ward coverage, daily cleansing, owner serialization, and preserving vanilla forceload ownership.

Real client checks opened the station and native furnace screens, verified live values and Haint-eye synchronization, and visually checked the layouts. A second client launch verified exact station resources after restart and restored an active Ward 640 blocks away from spawn, outside player view distance, before removing it and confirming owner cleanup. Both launches used 30% master volume. Temporary probes, worlds and screenshots are excluded from the published jar.

Altars and world rituals are now implemented in the [altar slice](FABRIC_ALTARS.md). Research and book progression are now implemented. Ectchantment Machine, Fancifier behavior, other Haunt effects and NeoForge save migration remain pending ports. These checks do not establish full mod parity or third-party modpack compatibility.
