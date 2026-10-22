# Fancifier

Put any placeable block item into the Fancifier, drag the bar or type any whole opacity percentage from 1% to 100%, inspect the live preview, and Apply. Process a full stack or take part of the output. Put faded items back in and select 100% to restore their original appearance. No fuel or additional ectoplasm is consumed. Closing or losing access to the workstation returns unclaimed items, with normal overflow drops.

## Crafting

```
Iron        Raw Ecto       Iron
Raw Ecto    Stonecutter    Raw Ecto
Iron        Redstone      Iron
```

The generated recipe, iron/stone/ectoplasm/redstone art, translucent saw, creative entry and Ectoplasm book branch are unchanged.

## Any placeable block

New faded outputs retain their original block item and native placed block. Chests keep their inventories; furnaces cook; machines retain their block entities, fluid/item/energy capabilities and tickers. Doors/beds get their normal parts, fences connect, crops grow, redstone works and falling blocks use normal physics. Shape, waterlogging, light blocking/emission, tool requirements and spectral barriers follow the original block. Native mining/drop rules apply, including Silk Touch requirements and separate container contents.

Opacity is a native item component plus synchronized chunk metadata, rather than a proxy replacing the functional block. Native state changes retain fading. Piston pushes and falling blocks transfer it to the new position; cauldron filling and copper ageing/waxing retain it. True replacement/removal clears old metadata, preventing new blocks from inheriting stale fading. Block-item drops, falling-block drops and pick-block retain opacity. Native placement and its cancellation/protection transaction are preserved. Original item components survive processing and restoration; once placed, each block's normal component and inventory persistence rules apply.

All nonempty BlockItems are accepted, including planting items and custom modded BlockItem subclasses. Items that do not place blocks, and world-only states with no item, are outside the input system. Standard baked models and block-entity renderers using native buffer APIs are supported. Third-party renderers that issue their own GPU draws or use custom shaders ignoring alpha may need a dedicated adapter; they still retain their native gameplay behavior. Other rendering-engine replacements need compatibility playtesting.

Existing decorative `fancified_block` instances, their saved block entities and old wrapped items remain valid. Mining/pick-block/restoration yields the new native item format; old wrapped items now place native blocks. No world reset, forced chunk loading, global scan or server ticker is introduced. The former decorative eligibility tag remains for saved legacy decorations, but does not restrict new inputs.

## Rendering and validation

The original-material face rules still hide equal-opacity shared glass/solid faces, including the 80% Magenta Glass fix. Normal chunk geometry is suppressed only for positions carrying fading; a bounded client index renders those models translucently. Native block entities and items use their original models, vertex formats, shaders and textures with added alpha blending. Neighbor faces and section visibility remain visible behind faded blocks. Position-based model offsets, seeds, model data and per-quad tints are preserved. Client chunk/dimension/logout cleanup prevents stale visual data.

Server coverage includes stack/partial output/restoration, invalid controls, closing, native slab shape and drops, save/network codecs, inventories, furnace cooking, doors, fences, crops, falling sand, pistons, placement cancellation, mod tank capabilities, cauldrons, copper and legacy shared glass faces. A separate self-closing development client rendered a comparison grid of native and 20/40/60/80% block, chest, sign, bed, banner and machine items successfully. In-world client synchronization and third-party mod renderers remain playtest checks. See [native fading implementation](FANCIFIER_NATIVE_FADING.md) for API/migration details.

## Artwork provenance

Generated with the built-in imagegen tool, master `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-97b72a3a-1b42-4da8-9093-f306a442202a.png`. Four equal quadrants were exported nearest-neighbor to 32x32 native PNGs: `textures/block/fancifier_top.png`, `fancifier_side.png`, `fancifier_bottom.png`, `fancifier_saw.png`. Original saw transparency is retained and multiplied by 0.4 for the intangible blade. The block-derived inventory model uses those same assets.

Prompt: Create a production Minecraft block texture atlas, square image split into four EXACT equal quadrants, pixel art at a logical 32 by 32 pixel grid, each quadrant one 16x16 texture enlarged with nearest-neighbor crisp edges. No labels, no text, no borders or gaps. Top-left quadrant: top surface of a magical stonecutter workstation, cool grey stone and iron rim, small lime green ectoplasm pools, subtle redstone red control inset; flat orthographic tiling texture. Top-right quadrant: side surface of same block, iron corner reinforcement, grey rough stone center, a small redstone conductor and mint ectoplasm accents; flat orthographic texture. Bottom-left quadrant: bottom face rough grey stone with iron bolts; flat orthographic texture. Bottom-right quadrant: a single circular serrated saw blade seen straight-on side elevation, centered in its 16x16 tile, pale spectral mint green, some see-through ghostly holes and genuine alpha transparency outside the saw, flat pixel art silhouette. All other quadrants opaque. Original asset for Mortal Expansion Fancifier, vanilla-compatible restrained 16-pixel game textures, no perspective cube, no mockup, no realistic lighting.


The workstation uses a framework composite model with a solid body and a separate translucent blade in both world and item rendering. The opaque body occludes the buried lower blade; only the blade uses its texture alpha (approximately 40%). Keep these material layers separate when migrating model providers.

The draggable bar and numeric field stay synchronized. The field rejects letters, decimals, signs, zero and values above 100. It can be temporarily empty while editing; Apply is disabled until a valid value is present, and leaving an empty field restores the last selection. Changing the preview does not process items; Apply commits the chosen opacity.
