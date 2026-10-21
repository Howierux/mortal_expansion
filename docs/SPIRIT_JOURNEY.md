# Spirit Journey recovery

During an active Spirit Journey, press J (rebindable in Controls) to open the menu
from anywhere. Select Return at nearest spiritwell to revive in the current
dimension, recover held death items, and gain 10 personal haunt points plus 10
haunt points in the destination chunk, capped by the existing haunt maximum.
The menu shows the cost and destination before selection. Walking back to the
body remains free. Recovery consumes the journey and prevents repeated requests
from duplicating items or charging haunt again. Full inventories drop overflow
at the return location.

Wells register in a per-dimension SavedData index when their block entities load.
Recorded wells remain available after their chunks unload and across restarts.
Older worlds populate this index as existing chunks load; unloaded legacy chunks
are not scanned. No terrain is generated merely to find a destination. Missing
recorded wells are removed during validation. With no recorded well in the
current dimension, the button is disabled and the journey can continue.

The server revalidates active journey state, nearest well, world border, and
collision-free space on confirmation. An obstructed destination does not consume
items, end the journey, or charge haunt. Loaded old bodies are removed immediately;
unloaded bodies remove themselves on a later tick when their owner is online and
has finished the journey.

Version migration: SpiritwellIndex owns SavedData serialization, the payload class
owns wire codecs, and client key/menu registration stays client-only. The new
return-mode attachment preserves the pre-death mode for remote recovery. Existing
journeys without that attachment default to Survival. Existing escrow and corpse
UUID save formats are retained.

The world renders in grayscale during an active journey. The filter is client-only,
uses the vanilla color-convolution program at zero saturation, and is generated
by ModPostEffects. SpiritJourneyVisuals contains the renderer integration.
Journey recovery/disconnection restores normal camera effects; resource reloads
and spectator camera changes reapply grayscale while the journey remains active.
HUD and menu rendering retain their normal colors.

## Death during Spirit Journey

Dying during an active Spirit Journey ends that journey. Ordinary Respawn returns the player in Survival mode to Minecraft's normal respawn point (a valid bed/anchor, otherwise world spawn), bypassing registered graves and the old corpse. Another Spirit Journey cannot be selected for that death. The registered grave is not consumed or permanently disabled by this fallback.

The abandoned corpse is no longer recoverable and removes itself when loaded with its owner online. Items held by the failed journey drop at their original death location, rather than being duplicated or left inaccessible in escrow. Keep Inventory retains its normal behavior.

Implementation uses the existing serialized, death-copied corpse link and one-shot journey choice to distinguish initial journey entry from a subsequent death. No new save format or mixin is required. Native respawn-position and respawn events preserve Minecraft's bed/anchor validity and fallback behavior; corpse and well recovery reject dead players.
