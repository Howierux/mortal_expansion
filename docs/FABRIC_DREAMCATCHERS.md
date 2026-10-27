# Fabric Dreamcatchers

Both wall-mounted Dreamcatchers now use persistent owner-bearing block entities. A player's strongest loaded owned catcher, in any loaded dimension, determines protection: regular 50%, purified 100%, without stacking. One coin flip is made per selected event. Caught positive or negative events count as handled and display catcher feedback rather than the effect's hint. Deferred Ghost deliveries use the same protection.

Fabric sleep events allow an owner to sleep despite nearby monsters. They do not bypass distance, dimension, ordinary sleep time or invalid-bed checks. Owner assignment comes from native placement; chunk load/unload and removal maintain a loaded-only index without chunk tickets. Removing a catcher immediately ends its protection. Native block-entity NBT preserves ownership across restart.

The client block-entity renderer shows the symbolic current moon phase, using small untextured bands centered in the wooden hoop. Both variants retain their original art and knowledge-gated recipes/atlas branches.

Validation covers owner and strongest protection, intercepted rewards, removal, NBT and actual integrated-client glyph loading. A two-launch client check at 30% volume verified restored protection. Existing active haunt ownership is handled by the Haunt subsystem; catchers prevent new selections rather than retroactively deleting external effects.
