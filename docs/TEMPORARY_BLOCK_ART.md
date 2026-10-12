# Temporary placed-block art

The six machinery/research/altar blocks now use original ingredient-informed face textures matching their inventory sprites, generated with the built-in imagegen tool. Each atlas was exported into 64×64 opaque PNG faces with nearest-neighbor sampling. Existing block shapes, IDs, gameplay and placement behavior are preserved; machines use the existing fixed north front. The research station remains 12 pixels tall, with its book and salt on the top. The altars have separate brighter front textures for their existing lit states. Glow is painted color, not an emissive shader.

All final face assets are in `src/main/resources/assets/mortal_expansion/textures/block/`, named `<block>_<front|side|top>_temporary.png`; altars additionally have `<block>_front_lit_temporary.png`. Grave Moss and Spiritwood Sapling reuse the transparent inventory art as `grave_moss_temporary.png` and `spiritwood_sapling_temporary.png`, with their directional/cross geometry retained and neutral tint. Original masters remain at the paths below. Extra unused atlas quadrants are not exported.

Models and states are generated through ModBlockModels. No new renderers, loaders or saved-data fields are required. Live in-game appearance still needs playtesting.

## Prompt set

### ectoplasmic_generator

Use case: stylized-concept. Asset type: Minecraft block texture atlas. Input image is visual design reference only. Create flat orthographic pixel art texture faces matching gold frame around charcoal furnace, green ectoplasm tank and green furnace opening. EXACT layout: square canvas divided into four equal square quadrants with no gaps, borders, text or margins: upper left is FRONT face, upper right SIDE face, lower left TOP face, lower right active glowing FRONT face. Each quadrant fills its entire area with opaque material texture, crisp limited-palette pixels comparable to a 32x32 Minecraft texture enlarged. All views are FLAT texture maps, NO perspective, NO isometric objects, no background, no shadows outside materials. Front incorporates the reference furnace/core; side conveys the same casing; top conveys the reservoir, grindstone, book, or beacon as applicable. Lower-right identical front but stronger luminous core/furnace fire. Avoid letters or labels. Keep ingredient colors of reference.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-fca203a6-3577-44fe-9cf6-b05b18cbd4c3.png`.

### enchantment_generator

Use case: stylized-concept. Asset type: Minecraft block texture atlas. Input image is visual design reference only. Create flat orthographic pixel art texture faces matching iron casing and charcoal furnace, grindstone circular detail with small redstone red core. EXACT layout: square canvas divided into four equal square quadrants with no gaps, borders, text or margins: upper left is FRONT face, upper right SIDE face, lower left TOP face, lower right active glowing FRONT face. Each quadrant fills its entire area with opaque material texture, crisp limited-palette pixels comparable to a 32x32 Minecraft texture enlarged. All views are FLAT texture maps, NO perspective, NO isometric objects, no background, no shadows outside materials. Front incorporates the reference furnace/core; side conveys the same casing; top conveys the reservoir, grindstone, book, or beacon as applicable. Lower-right identical front but stronger luminous core/furnace fire. Avoid letters or labels. Keep ingredient colors of reference.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-a5bd7411-d110-4931-9f09-92c5e856835d.png`.

### ectchantment_machine

Use case: stylized-concept. Asset type: Minecraft block texture atlas. Input image is visual design reference only. Create flat orthographic pixel art texture faces matching pale blue purified stone casing with cyan spectral core and enchanting book detail. EXACT layout: square canvas divided into four equal square quadrants with no gaps, borders, text or margins: upper left is FRONT face, upper right SIDE face, lower left TOP face, lower right active glowing FRONT face. Each quadrant fills its entire area with opaque material texture, crisp limited-palette pixels comparable to a 32x32 Minecraft texture enlarged. All views are FLAT texture maps, NO perspective, NO isometric objects, no background, no shadows outside materials. Front incorporates the reference furnace/core; side conveys the same casing; top conveys the reservoir, grindstone, book, or beacon as applicable. Lower-right identical front but stronger luminous core/furnace fire. Avoid letters or labels. Keep ingredient colors of reference.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-92c363ec-f6c6-4ac9-b3f5-2d161648629e.png`.

### mortality_research_station

Use case: stylized-concept. Asset type: Minecraft block texture atlas. Input image is visual design reference only. Create flat orthographic pixel art texture faces matching dark purple obsidian desk, brown open book and white holy salt atop. EXACT layout: square canvas divided into four equal square quadrants with no gaps, borders, text or margins: upper left is FRONT face, upper right SIDE face, lower left TOP face, lower right active glowing FRONT face. Each quadrant fills its entire area with opaque material texture, crisp limited-palette pixels comparable to a 32x32 Minecraft texture enlarged. All views are FLAT texture maps, NO perspective, NO isometric objects, no background, no shadows outside materials. Front incorporates the reference furnace/core; side conveys the same casing; top conveys the reservoir, grindstone, book, or beacon as applicable. Lower-right identical front but stronger luminous core/furnace fire. Avoid letters or labels. Keep ingredient colors of reference.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-8c66f1d0-552e-41e7-b56f-bf5a67e19b6b.png`.

### altar_of_purity

Use case: stylized-concept. Asset type: Minecraft block texture atlas. Input image is visual design reference only. Create flat orthographic pixel art texture faces matching ice blue purified stone furnace pedestal, glass beacon and white cyan star light. EXACT layout: square canvas divided into four equal square quadrants with no gaps, borders, text or margins: upper left is FRONT face, upper right SIDE face, lower left TOP face, lower right active glowing FRONT face. Each quadrant fills its entire area with opaque material texture, crisp limited-palette pixels comparable to a 32x32 Minecraft texture enlarged. All views are FLAT texture maps, NO perspective, NO isometric objects, no background, no shadows outside materials. Front incorporates the reference furnace/core; side conveys the same casing; top conveys the reservoir, grindstone, book, or beacon as applicable. Lower-right identical front but stronger luminous core/furnace fire. Avoid letters or labels. Keep ingredient colors of reference.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-669b7c7d-d76a-4f3b-88fe-47f20a89d18d.png`.

### altar_of_impurity

Use case: stylized-concept. Asset type: Minecraft block texture atlas. Input image is visual design reference only. Create flat orthographic pixel art texture faces matching charcoal violet stone furnace pedestal, glass beacon and violet star light. EXACT layout: square canvas divided into four equal square quadrants with no gaps, borders, text or margins: upper left is FRONT face, upper right SIDE face, lower left TOP face, lower right active glowing FRONT face. Each quadrant fills its entire area with opaque material texture, crisp limited-palette pixels comparable to a 32x32 Minecraft texture enlarged. All views are FLAT texture maps, NO perspective, NO isometric objects, no background, no shadows outside materials. Front incorporates the reference furnace/core; side conveys the same casing; top conveys the reservoir, grindstone, book, or beacon as applicable. Lower-right identical front but stronger luminous core/furnace fire. Avoid letters or labels. Keep ingredient colors of reference.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-5d6fc5cf-1720-4372-af44-7a6969a5bc1e.png`.

