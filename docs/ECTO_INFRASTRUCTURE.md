# Ecto Pipes and Ectanks

| Block | Stored fluid | Transfer limit |
| --- | ---: | ---: |
| Ectank | 16,000 mB | No additional tank-side limit |
| Purified Ectank | 64,000 mB | No additional tank-side limit |
| Ecto Pipe | 2,000 mB | 500 mB/t |
| Purified Ecto Pipe | 8,000 mB | 2,000 mB/t |

At 20 ticks per second, pipe limits are 10 and 40 buckets per second. Each pipe shares its limit across its faces and branches; mixed routes are limited by their slowest pipe. Machines can impose their own limits. Each block holds one fluid, including its components, and incompatible fluids cannot mix.

## Using the blocks

Right-click with a filled or empty bucket to insert or withdraw a whole bucket. Manual bucket transfers bypass the pipe automation rate, allowing a 1,000 mB bucket to work on basic pipes. Rejected bucket interactions do not spill fluid on top of the block. Empty-hand right-click on a tank shows fluid and amount; its glass body displays its fill level.

Empty-hand right-click on an extended arm selects that arm's direction, even when clicking its top or side alongside an attached tank. Click the central cube's corresponding face to configure or re-enable a retracted connection. Right-click cycles its mode: **Normal**, **Input**, **Output**, **Disabled**. Normal connects pipes and sends fluid into adjacent tanks/machines. Set the source-facing face to Input to extract from an adjacent tank or machine. Output permits flow out through that face only. Disabled prevents both directions and removes that visual connection. Pipe-to-pipe direction settings are also respected. For a tank-to-tank line, set Input on the first pipe's face touching the source tank; leave the rest Normal.

Mined tanks and pipes retain their fluid and pipe face settings in the dropped block item. Block destruction without an item drop can destroy stored contents. There is no leakage, power requirement or separate GUI.

## Recipes and models

Ectank: eight Gravestone around Glass. Purified Ectank: eight Purified Gravestone around Glass. Each yields one tank.

Ecto Pipe: `SSS / G G / SSS`, S = Gravestone, G = Glass, center empty; yields 16. Use Purified Gravestone for 16 Purified Ecto Pipes. There is no recipe consuming filled pipes or tanks.

Original native models use stone rims/collars and transparent glass, with Purified Gravestone for upgraded blocks. Tank fluid is rendered with the fluid's native texture and tint. Pipe glass uses full-face UVs so its border remains visible between stone pieces. Glass is inset from the stone and tube end caps are omitted to avoid coplanar surfaces. Pipes connect to supported fluid handlers in all six directions. There is no animated fluid inside pipe models.

## Persistence, compatibility and performance

All four blocks expose NeoForge's native block fluid capability, so supported modded fluids and machines can interact without individual integrations. Existing decorative block registry IDs and directional properties are preserved; newly created block entities start empty. Saved block/item data uses native FluidTank NBT and BLOCK_ENTITY_DATA, including fluid components and six validated face modes. Registration, rendering, storage, network coordination and data generation remain separate; no custom packets or mixins are needed.

One server coordinator processes cached connected networks, tracks nonempty buffers and caches routes. Topology changes invalidate the cache; a periodic refresh also detects capability providers appearing without state changes. Only loaded chunks are queried: pipes do not force-load a source, destination or route. Each pipe retains its own actual buffer so splitting a network does not discard pooled fluid. Loops are bounded by visited-node traversal. A route containing another fluid is blocked rather than mixing contents; separate incompatible-fluid networks for predictable routing.

The historical Liquefactor and other processing proposals remain separate unfinished features.
