# Fabric named discovery and spectral tracks

Spiritwells and cemeteries are discovered within a 20-block distance. Each player keeps persistent discoveries, including name, dimension, anchor and bounds; records survive death. Well distance is measured from its center, and cemetery distance from its horizontal bounds at surface height. The exact 20-block boundary is included.

Names are deterministic from world seed, dimension and site position. Wells use combinations such as Whispering Hollow; cemeteries draw from horror-themed names including Blackwell and Ravenwood. Duplicate visits do not rename or duplicate a record. `registeredDestination` exposes discovered metadata for future travel systems; this slice does not implement named-destination teleportation or Grave Porting.

A per-dimension SavedData index records observed generated sites. Spiritwell block placement/load/removal maintains its index; refresh also prunes removed loaded wells from the site cache. Nearby loaded cemetery structure starts supply surface anchors. Refresh/discovery runs once per second and never generates terrain to locate sites. Explicit journey recovery may load an already indexed well destination.

## Tracks and commands

Second Sight selects the nearest indexed, undiscovered well/cemetery in the current dimension. It sends only that target to the affected player. The client draws alternating, bright green square steps on actual ground surfaces. Ten steps are visible ahead, fading near the end; the trail expires after 30 seconds and receives short refresh packets while active. Reaching the destination, changing dimension or exhausting targets clears it. Unloaded terrain and water are skipped when drawing.

```text
/spectralsites
/spectralsites trail
/mortalexpansion haunt @s second_sight
```

The list shows the caller's own named discoveries. The latter two are operator testing commands. A trail cannot start without a known undiscovered target. Saved/generated terrain must first be observed by the index; locating never-generated sites without forced world generation remains an explicit missing function.

Tests cover boundary distance, stable names, duplicate visits, native index persistence and stale-well removal. A real client at 30% volume visually verified green ground footprints. The same client also verified the journey-only beacon beam; see [Spirit Journey](FABRIC_SPIRIT_JOURNEY.md).
