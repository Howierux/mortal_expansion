# Spectral tracks and named site discovery

Implemented 2026-10-05. Discoveries are personal. Passing within 20 blocks of a Spiritwell anchor or the boundary of a generated Cemetery registers that site, with a chat announcement of its name. Discovery checks run every server tick against the recorded sites. Distance includes elevation relative to the site's anchor/surface. Cemeteries are actual Graveyard Cemetery structures, not every Graveyard biome or arbitrary group of placed graves.

Names are deterministic from world seed, dimension and site identity, then persisted in the site registry. Spiritwells use names such as Whispering Hollow Spiritwell; Cemeteries draw from Blackwell, Ravenwood, Hollow Creek, Grim Hollow, Ashcroft, Dreadmoor and other horror-themed names. Duplicate display names are possible; stable IDs, dimensions and coordinates distinguish destinations.

Use `/spectralsites` to list your registered names, dimensions and anchor coordinates. This command needs no operator permission. The operator-only `/spectralsites trail` starts footprints for development testing.

## Footprints

Second Sight is now an available beneficial Haunt effect at Supernatural or higher. It displays full-bright glowing green square footsteps toward the nearest recorded site that the player has not discovered, for up to 30 seconds. Guidance updates once per second as the player moves, and stops on discovery of its target, dimension change or expiry. A successful trigger has a localized hint. If there is no recorded undiscovered target or no client channel, it does not claim success or spend an effect cooldown.

The client draws the nearby ten-step portion of the trail on loaded solid surfaces. The direction is guidance, not a guaranteed navigable path through obstacles; no footprints are deliberately drawn over fluids or without a nearby supporting surface. Geometry is procedural, with no entities or raster texture dependency. Disconnect, death and dimension changes cannot retain a client trail from an old world.

Only recorded, generated sites are candidates. Wells reuse the existing persistent position index and migrate into named sites. Existing Cemeteries are indexed from loaded structure starts near players once their centre chunk is available. Registry records remain available after chunks unload, so another player's undiscovered sites can guide them later. No terrain is generated or force-loaded to locate a site. Loaded removed wells are pruned from both indexes. Finding sites in never-generated terrain remains an explicit missing function; hidden inscription revealing also remains pending.

## Future teleport hook and migration

`SpectralDiscovery.registeredDestination(player, id)` exposes only sites in that player's discovery records. Each record includes kind, stable identity, name, dimension, anchor and Cemetery bounds. Actual teleportation, costs, permissions and safe landing are marked `missing function`; raw registry coordinates are not yet validated travel destinations.

A new codec-backed player attachment copies discoveries on death and saves them across logout. Existing worlds default to an empty personal discovery list and acquire site records when the existing anchors/structures are observed. A per-dimension codec-backed SavedData index stores known sites. The new clientbound footprint payload contains only a target and visible flag; discovery and nearest-site decisions stay server-authoritative.

Build and server tests cover exact twenty-block boundaries, per-player isolation, dimension separation, stable names, actual loaded Cemetery structure metadata, player/index save/load, nearest-undiscovered filtering, blocked unvisited travel records, existing-well indexing/removal and payload roundtrips. In-game footprint visibility, Cemetery terrain elevation and extended restart/death discovery behavior still require playtesting.


## Footprint visibility correction

Footsteps now use the native unlit position/color translucent quad renderer on the main world target after translucent blocks, with an explicit buffer flush. The former lightning renderer used the weather target and could hide the trail with graphics/compositing settings. The trail ends after ten alternating steps, with its final three squares progressively fading. A bright green 0.32-block square and a wider soft green halo make each alternating step visible at night while retaining terrain occlusion. Downward collision rays place the marks just above the actual surface, including slabs and uneven ground; fluid surfaces and missing ground remain excluded. Packet refresh has a three-second grace interval to prevent brief network delays from flickering the trail. Server duration, discovery, target selection and stop conditions are unchanged.

No texture assets, client entities, new payloads or mixins are added. Preserve main-target rendering, explicit flush, unlit color and surface placement during version migrations. In-game appearance remains a client playtest check.
