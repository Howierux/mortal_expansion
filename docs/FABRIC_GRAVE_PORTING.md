# Fabric Grave Porting

Right-click a Spiritwell to list eligible loaded graves across every dimension. A destination must be naturalized, engraved with the player's name and bound to their UUID, with a Bone Block frame containing exactly 64 flesh within three blocks. The loaded-only grave index never creates destination chunks or retains tickets. Existing discovery names remain separate future destination hooks.

Server-issued menu tokens bind the player to the source well, source dimension, exact offered destination list and expiry. Selection consumes the token, rechecks source distance/alive/riding state, destination ownership/naturalization, full anchor and safe landing. Unloaded or changed destinations fail without spending materials. A valid native cross-dimensional teleport consumes the entire fleshed bone frame without drops and adds one personal Haunt. Position, velocity and fall state are checked/reset at arrival. Replayed selections cannot teleport or spend again.

A valid landing requires loaded, dry, supported space within two horizontal blocks and two blocks above the grave, inside world limits and free of collisions or occupants. No alternative corpse recovery is introduced: a failed Spirit Journey still uses ordinary bed/spawn behavior.

Validation includes native same- and cross-dimensional travel, owned naturalized destination filtering, consumed anchors/Haunt and replay rejection. A two-launch integrated-client check at 30% volume restored the destination, opened the real menu and ported through its button. Dedicated multiplayer, third-party teleport cancellation and death-mod interaction remain release checks. The teleport is revalidated before the destination frame is consumed.
