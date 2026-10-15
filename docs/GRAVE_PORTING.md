# Grave Porting

A named Grave belonging to the player must be naturalized and currently loaded. A Bone Block with exactly 64 Rotten Flesh applied must be within three blocks of the Grave, measured as three-dimensional distance between block centers (inclusive). Use the existing flesh application mechanic; no lantern is required. Multiple eligible Graves can exist without changing the selected respawn anchor. A frame shared by nearby Graves can enable more than one; consuming it disables those destinations until another full frame is supplied.

Right-click a Spiritwell to open a paginated list of your eligible loaded Graves across all server dimensions. Each destination shows dimension and coordinates. The menu displays the +1 personal Haunt cost and full-frame consumption. Source Spiritwells remain Overworld-only; destination Graves can be in any dimension. Spiritwells have a small clickable outline but no physical collision. This is travel for living non-spectator players, separate from Spirit Journey recovery.

Confirmation rechecks the player, short-lived one-use token, range to the source well, its existence, Grave ownership/name/naturalization, chunk loading and the full frame. It finds a clear, supported, non-fluid landing within two blocks horizontally and up to two vertically of the Grave, inside world limits. Unloaded/broken/missing destinations, changed resources, obstructed landings and canceled dimension travel spend nothing. Successful arrival consumes one qualifying full frame without drops, preserves the Grave, clears falling momentum and adds exactly one personal Haunt point (normal clamping applies). No chunk Haunt is added. Duplicate selections cannot repeat the payment or consumption.

Only Grave block entities observed through native onLoad/setRemoved lifecycle hooks are indexed; the index is transient, server-only and separated by dimension. All neighboring frame and landing queries check existing loaded chunks first. Lookup never generates or force-loads terrain, and no persistent travel registry is added. Existing Grave/frame save formats are unchanged. Networking uses native codecs and player-bound transient sessions; client screen handlers are deferred until a client payload is handled. New travel payload IDs are grave_port_open and grave_port_choose.

The Book of Ectcetera has a three-page Grave Porting branch from Spiritwells in the Bones/Spirits tab. Preserve loaded-only lookup, resource consumption, safe landing and replay protection when migrating versions.

Void damage explicitly bypasses intangibility protection for spectral mobs and garb wearers; native void damage timing and creative/spectator rules remain in place.

## Flower-tended naturalization

At Supernatural or higher chunk Haunt, a placed Grave with a flower within three blocks horizontally and two blocks vertically has a 20% chance to naturalize on each new Minecraft day while loaded (previously 5%). Success spends one local Haunt point. More flowers do not stack the chance. Loading a Grave records the day without a roll, and missed unloaded days are not replayed.
