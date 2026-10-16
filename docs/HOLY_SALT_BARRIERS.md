# Holy Salt barrier cleanup and active-salt work

## Findings

The old collision cache recorded true/false for every queried X/Z column. A cache miss scanned the entire vertical build height through Level.getBlockState, with no explicit loaded-chunk guard. Both positive and negative entries lived until explicit invalidation or level garbage collection; chunks did not evict them.

Salt placement/removal callbacks updated that cache on the server, but ordinary client chunk writes skip those callbacks. The existing client transition mixin observed Spirit Torches only. As a result, removing client-visible salt could leave a positive collision entry behind. This is runtime collision state, not a saved invisible barrier block.

Separately, any indexed salt anywhere enabled enclosure checks for every loaded mob. A cold enclosure check could visit up to 4,096 planar cells. Multiple mobs in uncached regions or after cache invalidation could trigger these searches together. The playtest log showed repeated multi-second server catch-up warnings. That confirms delayed ticks, but does not by itself attribute their full cost to salt; no live before/after profile was captured while the world was paused.

## Changes

HolySaltBarrierTracker now indexes actual salt positions by loaded chunk and maintains positive column reference counts. Collision/visibility queries are constant-time map lookups with no block reads, height scans, negative-result entries or chunk loading. Chunk load reads the supplied section palettes directly, scanning only sections that can contain salt. Chunk unload removes its positions/counts; level unload removes the index. Shared level maps use concurrent access so an integrated client and server cannot race while looking up their separate indexes. The associated torch and sanctification indexes also have explicit level-unload cleanup. Multiple salt heights retain the boundary until the final salt position is removed.

The existing native client block-transition hook now observes Holy Salt as well as Spirit Torches. Confirmed updates, client predictions and server corrections are idempotent, including same-block power changes. Server removal callbacks still update immediately. Chunk reload replaces stale entries with actual blocks.

Sanctification now skips flood fills for mobs far from any loaded salt or torch-link barrier. A barrier-free 65x65 square exceeds the current 4,096-cell area limit, so a mob without a barrier in that nearby square cannot lie inside a supported enclosure. Conservative chunk-based spatial checks may include slightly more cells, never exclude a valid nearby enclosure. Fresh enclosure searches are capped at two per level tick; excess cold requests retry on the normal next mob poll. Cached enclosure effects retain their normal refresh cadence. This bounds flood-fill spikes; it does not turn salt circuits off.

## Validation and compatibility

Dedicated gameplay coverage checks stacked salt removal and support loss, immediate phasing-boundary cleanup, repeated packet/prediction corrections, chunk unload/reload and stale-entry replacement, unloaded-space queries without forced loads, and active enclosure sanctification stopping after a gap is opened.

Existing item/block IDs, saved salt states, circuit power and vertical projection semantics are unchanged. No saved-world cleanup or barrier-block deletion is required. Restarting with the fixed version rebuilds indexes from actual loaded salt. Native circuit propagation remains unchanged; loaded-world lag reduction still needs a live playtest/profile. The existing client mixin is extended because client writes bypass block placement/removal callbacks; no additional mixin is introduced.
