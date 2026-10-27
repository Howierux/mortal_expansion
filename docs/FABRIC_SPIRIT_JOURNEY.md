# Fabric Spirit Journey

## Survival flow

Craft three Graves from six Gravestone in a 2x3 rectangle. Place a grave, right-click it, and engrave your own player name to bind it. Naming is limited to the placing owner, once, and the server rechecks reach/permission/ownership. Inscriptions render on the actual facing/depth surface. Flower naturalization, grave Spirit workstations and loaded cross-dimensional Grave Porting are now implemented. Generated unowned graves store the natural block-state and do not assign a player respawn owner.

A dead, non-hardcore player with a valid bound self-grave can choose **Spirit Journey** on the death screen. Their player-shaped corpse remains at the original death position and they respawn at the grave as a spectator. The journey applies the generated greyscale post-chain and reveals Spiritwell beacon beams only to that player.

- Approach your own corpse within two blocks for a free return with held items. Obstructed bodies remain recoverable until a nearby vertical landing is safe.
- Press **J** anywhere during the journey to open its return GUI. It can return through the nearest indexed Spiritwell, recover held items and add ten points to personal Haunt and that well's chunk, subject to existing Haunt caps. A failed search/safe-landing check spends nothing. Right-clicking a well can also open the GUI when the interaction reaches the server.
- Returning restores the prior non-spectator mode, health and movement state, removes the corpse link, and restores normal color. Normal survival deaths therefore return to survival.
- If killed during a journey, the next respawn is normal survival at the vanilla bed/anchor/world spawn. It does not redirect to a grave or corpse. The original held drops are released at the original death site, and an abandoned corpse removes itself when its owner is available.

## Item and save safety

Vanilla still decides keepInventory, Vanishing and the actual item drops. A focused death-equipment scope captures only the dying player's resulting item entities **before** they enter world-spawn or automatic-pickup callbacks. Native item components, count, position, dimension and UUID are retained. Normal respawn releases pending drops at death; journey selection moves them into held escrow; recovery detaches escrow before adding items. Overflow uses native inventory/drop behavior. Duplicate return packets cannot recover or charge twice. keepInventory retains its native inventory without creating a second escrow copy.

State persists in player attachments `spirit_journey` and `deferred_death_items`, copied on death, plus the native persistent `player_corpse` entity. Reconnecting restores the active journey and held escrow. Unloaded death locations/corpses are handled through recorded positions and UUIDs; normal discovery scans do not generate terrain.

Fabric's attachment API copies through AFTER_RESPAWN. A narrowly isolated completed-respawn wrapper resolves journeys **after all those callbacks**, and points the connection at the replacement player before teleporting. A separate spawn-choice hook selects the grave before vanilla can consume an unrelated Respawn Anchor charge; failed journeys retain vanilla anchor usage. Both ordering rules must survive version migration. Networking accepts only named actions; clients cannot choose arbitrary teleport destinations, corpse owners or inventory contents.

Client screens, corpse/inscription/beam rendering and the private post-chain loader stay in client sources. The post-chain is generated with the same vanilla grayscale programs as NeoForge. Shader-pack interoperability remains separate playtesting.

## Verification

Native tests perform actual deaths and replacement-player respawns, check grave arrival, native item-component retention, Vanishing, absence of transient drop-spawn callbacks, one-time corpse/well recovery, exact Haunt charges, failed-journey vanilla Nether-anchor spawn/charge, avoiding anchor charges for grave journeys, normal-choice drop release, immutable engraving and keepInventory safety.

A two-launch integrated client check at 30% volume selected the death-screen button, saved an active journey, restarted with its corpse/escrow intact, opened the real J-key GUI and returned through its button. It recovered exactly seven Diamonds, added ten personal Haunt, and removed greyscale and the private beam. Screenshots verified footprints, the greyscale beam and the return screen. Temporary probes were removed from production sources.

Dedicated multiplayer, third-party death/inventory/protection mods and NeoForge world migration are not yet release-validated.

## Grave consumption

Entering Spirit Journey preserves non-natural and naturalized self-named graves, their binding, and personal Haunt. Only ordinary Respawn at a non-natural grave consumes it and adds one personal Haunt point. Naturalized graves remain intact for both choices without that respawn cost. Failed journey deaths continue to use normal bed/anchor/world spawn and never consume the grave. Native regression tests exercise both grave states and both choices.
