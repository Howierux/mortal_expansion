# Fancifier

Place decorative blocks in the input, choose 20%, 40%, 60% or 80% opacity, then Apply. One input becomes one output. Process up to a full stack, take only part of the result, or shift-click it into inventory. Put faded blocks back in and choose 100% to recover the original items and their components. Opacity changes do not consume fuel or ectoplasm. Closing or losing access to the workstation returns unclaimed input/output to the player, with normal overflow drops.

## Crafting

```
Iron        Raw Ecto       Iron
Raw Ecto    Stonecutter    Raw Ecto
Iron        Redstone      Iron
```

The recipe, models, tag, loot and advancement use native data generators. The Fancifier is in the creative tab and has a two-page branch from Ectoplasm in the Book of Ectcetera. Its iron/stone body, ectoplasm insets and redstone control follow the recipe. The saw uses a genuinely translucent mint texture.

## Supported blocks

The generated `mortal_expansion:fancifiable` block tag initially contains ordinary static building blocks, logs/pillars, glass, slabs and stairs. Exact safe native block classes, a BlockItem, no block entity and no random ticks determine default membership. Machines, containers, crops, falling blocks, doors, fences/walls and other active or connection-dependent types are excluded in this first version. Datapacks may extend the tag for suitable static decorative blocks; block entities remain rejected. This is not a generic machine/behavior-copying system.

Placed blocks retain the original placement state, collision/selection geometry, mining speed/tool eligibility, waterlogging where supported and emitted light. Original spectral-solid material still blocks intangible movement and Demon perception. They transmit light rather than occluding neighboring faces, like transparent decorations. Original random ticks, interactions, shape reconnection, stripping and machine capabilities are outside this decorative-only version. Pistons cannot move the decorated block entity.

Breaking, pick-block, save/load and client synchronization preserve source item data and opacity. Drops are the decorated item rather than the underlying block's ordinary loot. Restoration preserves original item components and counts. Removing a material from the eligibility tag does not destroy already-placed drops or prevent restoring existing items at 100%.

## Implementation and validation

One stable `fancified_block` block/item with a non-ticking block entity stores a count-one original ItemStack, its native BlockState and selected opacity. Item data uses native `minecraft:block_entity_data`; native ItemStack, BlockState and block-entity packets handle persistence/networking. Server menu buttons validate range, workstation distance, input, output compatibility and capacity before processing. Dedicated client-only renderers multiply the original baked model's vertex alpha for world, inventory, hand and dropped-item appearances; there are no render mixins or registrations per source block.

The per-block renderer favors reliable support and persistence over giant decorative installations: every visible decorated block has a renderer, although it has no server ticker or scanning work. Native transparency can show normal ordering artifacts with overlapping transparent surfaces. In-game appearance/layout still needs manual playtesting.

Server regressions cover all opacity levels, stacks/partial results, original custom names, restoration, invalid controls, unsupported inputs, incompatible outputs, closing, real slab placement/collision/waterlogging, emitted light, spirit barriers, drops, pick-block, reload, the requested loaded recipe and Ectoplasm book gating. Build and resource/guide checks accompany those tests.

## Artwork provenance

Generated with the built-in imagegen tool, master `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-97b72a3a-1b42-4da8-9093-f306a442202a.png`. Four equal quadrants were exported nearest-neighbor to 32x32 native PNGs: `textures/block/fancifier_top.png`, `fancifier_side.png`, `fancifier_bottom.png`, `fancifier_saw.png`. Original saw transparency is retained and multiplied by 0.4 for the intangible blade. The block-derived inventory model uses those same assets.

Prompt: Create a production Minecraft block texture atlas, square image split into four EXACT equal quadrants, pixel art at a logical 32 by 32 pixel grid, each quadrant one 16x16 texture enlarged with nearest-neighbor crisp edges. No labels, no text, no borders or gaps. Top-left quadrant: top surface of a magical stonecutter workstation, cool grey stone and iron rim, small lime green ectoplasm pools, subtle redstone red control inset; flat orthographic tiling texture. Top-right quadrant: side surface of same block, iron corner reinforcement, grey rough stone center, a small redstone conductor and mint ectoplasm accents; flat orthographic texture. Bottom-left quadrant: bottom face rough grey stone with iron bolts; flat orthographic texture. Bottom-right quadrant: a single circular serrated saw blade seen straight-on side elevation, centered in its 16x16 tile, pale spectral mint green, some see-through ghostly holes and genuine alpha transparency outside the saw, flat pixel art silhouette. All other quadrants opaque. Original asset for Mortal Expansion Fancifier, vanilla-compatible restrained 16-pixel game textures, no perspective cube, no mockup, no realistic lighting.
