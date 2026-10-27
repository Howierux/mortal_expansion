# Cemetery playtest fixes — 2026-10-27

Natural grave shimmer models contain transparent pixels. Native Fabric assigned the entire grave to the solid render layer after dropping NeoForge's per-model render type, so the overlay hid the stone behind black faces. Registering the grave in the translucent layer preserves both the stone and faint shimmer. An integrated client check at 30% volume exercised all twelve facing/depth combinations; the screenshot confirms visible stone rather than opaque black overlays.

Native player destruction now adds one personal Haunt when the grave's persisted natural state is true. Naturally generated and flower-naturalized graves share that state. Plain player-placed graves give no breaking Haunt. Fabric FakePlayer automation adds one chunk Haunt instead. The hook runs only on the server after native event/claim cancellation; cancelled breaks grant nothing. Removing graves for respawn/porting does not invoke this player-breaking hook.

Two new native tests cover real Survival/Creative breaks, placed graves, claim cancellation, fake-player automation and stone-fence neighbor updates. The Fabric build passes 81 required GameTests. The stone-fence tag already existed in Fabric; NeoForge's corresponding native block/item fence tags were missing and are now generated there, with a 239-test passing build.

Existing disconnected NeoForge fence states may need a neighbor update (for example replacing an adjacent fence). Fresh placement/generation uses the corrected tag. No per-tick world scan is introduced.
