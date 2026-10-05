# Spectral tracks and named site discovery

Implemented 2026-10-05. Discoveries are personal. Passing within 20 blocks of a Spiritwell anchor or the boundary of a generated Cemetery registers that site, with a chat announcement of its name. Discovery checks run every server tick against the recorded sites. Distance includes elevation relative to the site's anchor/surface. Cemeteries are actual Graveyard Cemetery structures, not every Graveyard biome or arbitrary group of placed graves.

Names are deterministic from world seed, dimension and site identity, then persisted in the site registry. Spiritwells use names such as Whispering Hollow Spiritwell; Cemeteries draw from Blackwell, Ravenwood, Hollow Creek, Grim Hollow, Ashcroft, Dreadmoor and other horror-themed names. Duplicate display names are possible; stable IDs, dimensions and coordinates distinguish destinations.

Use `/spectralsites` to list your registered names, dimensions and anchor coordinates. This command needs no operator permission. The operator-only `/spectralsites trail` starts footprints for development testing.

## Footprints

Second Sight is now an available beneficial Haunt effect at Supernatural or higher. It displays cyan translucent heel/toe bootprints toward the nearest recorded site that the player has not discovered, for up to 30 seconds. Guidance updates once per second as the player moves, and stops on discovery of its target, dimension change or expiry. A successful trigger has a localized hint. If there is no recorded undiscovered target or no client channel, it does not claim success or spend an effect cooldown.

The client draws the nearby twelve-block portion of the trail on loaded solid surfaces. The direction is guidance, not a guaranteed navigable path through obstacles; no footprints are deliberately drawn over fluids or without a nearby supporting surface. Geometry is procedural, with no entities or raster texture dependency. Disconnect, death and dimension changes cannot retain a client trail from an old world.

Only recorded, generated sites are candidates. Wells reuse the existing persistent position index and migrate into named sites. Existing Cemeteries are indexed from loaded structure starts near players once their centre chunk is available. Registry records remain available after chunks unload, so another player's undiscovered sites can guide them later. No terrain is generated or force-loaded to locate a site. Loaded removed wells are pruned from both indexes. Finding sites in never-generated terrain remains an explicit missing function; hidden inscription revealing also remains pending.

## Future teleport hook and migration

`SpectralDiscovery.registeredDestination(player, id)` exposes only sites in that player's discovery records. Each record includes kind, stable identity, name, dimension, anchor and Cemetery bounds. Actual teleportation, costs, permissions and safe landing are marked `missing function`; raw registry coordinates are not yet validated travel destinations.

A new codec-backed player attachment copies discoveries on death and saves them across logout. Existing worlds default to an empty personal discovery list and acquire site records when the existing anchors/structures are observed. A per-dimension codec-backed SavedData index stores known sites. The new clientbound footprint payload contains only a target and visible flag; discovery and nearest-site decisions stay server-authoritative.

Build and server tests cover exact twenty-block boundaries, per-player isolation, dimension separation, stable names, actual loaded Cemetery structure metadata, player/index save/load, nearest-undiscovered filtering, blocked unvisited travel records, existing-well indexing/removal and payload roundtrips. In-game footprint visibility, Cemetery terrain elevation and extended restart/death discovery behavior still require playtesting.
