# Bloodstone and Spectralite area shovels

Updated 2026-10-06. Both shovels mine a 3x3x1 plane centred on a normally mined shovel-mineable block and perpendicular to the targeted block face. East/west faces give a Y/Z plane, north/south faces give an X/Y plane, and top/bottom faces give a horizontal X/Z plane. Player facing does not choose the plane. Air, unbreakable blocks, non-shovel materials, unloaded positions and blocks the tool cannot harvest are skipped.

Sneak while mining to break only the selected block. Sneak right-click retains normal shovel path-making.

## Spectralite charged dig

Use the Spectralite shovel in the main hand. Hold right-click in air or on a shovel-mineable block for 40 game ticks (nominally two seconds). A short sound and action-bar hint signal readiness. Release while aiming at a shovel-mineable block within normal reach to dig up to 64 connected blocks.

Connectivity is six-way face adjacency, allowing mixed shovel-mineable materials. Disconnected material is untouched. Protected or invalid blocks are not bridges through which the search can reach more material. Early release, missing/invalid targets or changed/broken tools do not produce a charged dig. Sneaking disables charging so ordinary path-making remains accessible. The charge grants no shield protection and consumes no Haunt or extra resource beyond normal per-block durability.

Both modes stop when the held tool breaks or changes. Creative mode retains its normal drop/durability rules. Silk Touch, Fortune, block drops, XP and break-event protection use normal ServerPlayerGameMode destruction for each block. Native spawn protection, world borders and loaded-chunk checks apply. No terrain is loaded or generated for the search. Further block movement caused by ordinary physics remains ordinary gameplay.

## Implementation and migration

The server LeftClickBlock START event records the targeted face. STOP does not overwrite it; ABORT clears it. Records expire after 60 seconds and require the same level, position and held stack. Server-driven breaks without a record use a matching current ray hit; an unknown face leaves only normal single-block mining. Normal BreakEvent selection queues a one-tick follow-up. Survival BlockDropsEvent confirms real player harvesting, and the follow-up checks the centre was actually removed and the original tool remains held. Creative breaks verify removal without depending on drops. Drop-event cancellation still retains its ordinary loot meaning. A scoped recursion guard prevents extra breaks from starting their own 3x3 jobs and prevents charged breaks from scheduling additional planes.

The charged action uses supported item use/release callbacks and the player's current server eye/aim. Its breadth-first search counts at most 64 actual successful removals. Transient jobs are cleared on level unload and are not persisted. Stable item IDs, attributes, tiers, fire resistance, models and crafting recipes are unchanged. No new mixins, registry entries, saved fields or packets are introduced.

On version migration, verify the break/drop event ordering, tool-reference/durability lifecycle, server ray pick and native destroyBlock permissions/drop semantics. All gameplay thresholds and limits are isolated in AreaShovelItem/AreaShovelMining.

## Verification

Build and 88 server GameTests pass. New tests cover both shovels and both plane orientations, one-block depth/no recursion, normal wear, sneak/path behavior, real charge timing/release, exact 64-of-80 connected limit, disconnected blocks, per-block vetoes and protected bridges, broken/swapped tools and Silk Touch loot. Guide and generated-resource checks pass. In-game aiming, charge feedback and large-world excavation still require playtesting.
