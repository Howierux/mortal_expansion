# Fabric formation and encounters

## Wisp o' Lantern and Mortal Golem

Loaded Carved Pumpkins in Supernatural-or-higher chunks receive a 2% conversion roll per distributed minute-long pass during night (13000–23000). Conversion costs one chunk Haunt and preserves facing. Wisp o' Lanterns faintly emit light level four, drop a Carved Pumpkin and raw ectoplasm normally, and drop themselves with Silk Touch. A loaded lantern attempts a nearby Ghost spawn every ten minutes at night, requiring a player within 32 blocks, mob spawning enabled, non-Peaceful difficulty, valid space and fewer than four nearby Ghosts.

Each Rotten Flesh use on a Bone Block adds exactly one of 64 flesh points, with appearance changing every 16. Sixteen or more flesh and a Wisp head form a Mortal Golem; adding flesh below an existing Wisp also checks formation. A splash of Regeneration can form the same frame with a Carved Pumpkin head. The golem needs its full bounding volume free of other collision, liquids and living occupants except the two construction blocks. Failed formation preserves materials; successful creation consumes the frame and head. Existing flesh-tier health, damage, repair and AI remain in the combat slice.

## Encounters and native population

Entering a loaded vanilla Desert Pyramid triggers its Pharaohl encounter at the blue terracotta center once per player/pyramid; saved player keys and a loaded active-boss check prevent duplicates. Abominable-or-higher Overworld Haunt enables the reference 1% Haint encounter check every ten seconds near a loaded Witch Hut, with nearby-encounter and safe-space checks. Neither locator generates or force-loads distant structures.

Natural Overworld zombie-family, skeleton-family, Creeper, Witch, Wither Skeleton, Ghast and Enderman acceptance follows the reference tier rules and reduced vanilla weights. These filter successful vanilla placement checks, never relax vanilla checks or alter command/spawner summons. Naturally spawned non-Wither skeletons at exactly Supernatural lose their bows after native initialization and fight in melee. ME natural spawn rules from the combat slice remain in force.

Validation: native tests cover material thresholds, one-flesh cost, blocked formation, existing Wisp formation, Regeneration construction, day/night conversion, Haunt cost and ordinary loot. Dedicated server loading verifies the potion and post-finalize skeleton hooks. Long stochastic encounter/population observations and protection-mod compatibility remain playtest work.

Migration: no Fabric event exposes thrown-potion impact or post-finalize skeleton equipment, so two focused mixins attach those rules. Loaded-chunk section palette checks skip sections without pumpkins; scans are distributed across the minute rather than scanning every block each tick.
