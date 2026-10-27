# Fabric Secrets of the Damned

The reversed Purgatorio order is implemented: Lust at 70, Gluttony 80, Greed 90, Sloth 100, Wrath 110, Envy 120 and Pride 130 personal Haunt. Bit zero remains the existing Demon revelation; the seven further bits preserve the existing item/player codec range. Revelation is permanent, copied on death and privately synchronized. The existing atlas now reveals each sin using this state.

- Lust: three crouch starts within three seconds start nearby adult animal courtship, with a ten-second cooldown. Native mating handles compatibility, children and breeding cooldowns.
- Gluttony: native food can be eaten at full hunger and its saturation upper clamp is removed. Ordinary players retain vanilla limits; hunger/exhaustion/healing/save behavior remains native.
- Greed: hold use with a Diamond for two seconds. A bounded search of ore-containing sections in already-loaded chunks within 64 blocks finds a real Diamond Ore to mine. Native server mining preserves Fabric claim cancellation; successful use consumes ore and pays exactly one Diamond, with no duplicate native drops. Search cancels on dimension/movement/held-item changes and clears on logout/server stop.
- Sloth: daytime bed use starts an Overworld sleep vote. A strict majority of all online nonspectators must sleep long enough; success sets the time to 13000 and wakes sleepers. Native bed/monster checks remain, with Dreamcatchers able to waive the monster check. Fabric sleep events prevent the usual morning reset during this vote.
- Wrath: damage taken or a kill grants Strength for ten seconds, stacking to Strength II.
- Envy: Tab opens selectable online names. Selection requests a private, read-only server snapshot of all 41 inventory/armor/offhand slots. Permission, target and request rate are checked; copied stacks cannot mutate the target.
- Pride: one XP every ten seconds, with feedback.

Darkhold exposure remains one +0.01 pulse every ten seconds per carrier regardless of copy count. Bounded read-only traversal now supports native containers/bundles and backpacks exposing Fabric ItemStorage. Existing nested-book sin knowledge can be read without mutating opaque inventories. Backpacks exposing neither interface need their own adapter; arbitrary private mod inventory formats cannot be discovered safely.

Validation covers all bands, Gluttony's native cap changes, Lust courtship, Wrath limits, Pride timing, read-only Envy copies, real ore consumption and claim rejection, daytime bed voting and Fabric backpack exposure. Client checks at 30% volume verified private knowledge, unlocked atlas and actual Tab input; restart retained all revealed bits. Focused native food/use/drop hooks are isolated mixins because Fabric has no equivalent saturation-clamp/zero-duration-diamond events.
