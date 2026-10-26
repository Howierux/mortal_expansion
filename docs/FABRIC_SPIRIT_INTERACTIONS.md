# Fabric Spirit interactions and graves

Right-click an adult Spirit to choose Trade or Unfinished Business. The latter offers a kill objective with a 75% preference for hostile targets. Native generated entity-type tags define easy, standard and dangerous targets; objective count and rewards follow the NeoForge reference. Killing the required mobs pays emeralds, ectoplasm and XP immediately without returning to a persistent Spirit. Temporary haunting-owned mobs cannot farm objectives. Spirit Commission now uses this system.

Offers, active progress and cooldowns use a persistent, death-copied Fabric player attachment. Offers expire after five minutes; accepted objectives remain until completed or abandoned. `/spiritquest status`, `accept`, `decline` and `abandon` are available. Server-issued UI tokens are bound to the player, Spirit, dimension, distance, action mode and expiry, and consumed before dispatch.

An adult jobless vanilla villager within four blocks of a grave engraved `Villager` becomes a Spirit. The binding and original villager metadata are saved. Breaking or invalidating the grave restores a jobless villager, preserving inventory, name, age and health where native conversion permits. Unloaded graves are unknown rather than destroyed, and validation never loads terrain.

Graves check once on each newly observed day: flowers within three horizontal blocks and two vertical blocks permit a 20% naturalization chance at Supernatural Haunt or above, costing one chunk Haunt. First load establishes the day marker without replaying missed days. Generated grave identities and Grave Porting remain separate pending work.

Validation: native tests cover issued-offer acceptance, wrong-target kills, automatic single reward delivery, stale/replayed/distant UI actions, grave conversion and inventory-preserving reversion. Integrated client checks opened the actual options screen and retained active quest progress across restart.

Migration: inscription-dependent workstation identity cannot use a vanilla block-state-only POI, so the grave ticker and native villager conversion own the binding. Fabric attachments are fresh-world state; NeoForge saved-state migration remains unimplemented.
