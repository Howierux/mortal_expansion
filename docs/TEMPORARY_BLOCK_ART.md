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


## Sapling bark revision

The Spiritwood Sapling item and placed sprites now share darker oak-like brown bark and muted cool green leaves, matching the current placed log and foliage. Both 64×64 transparent assets retain their existing filenames, cross geometry and neutral tint. Generated using the built-in imagegen tool and exported with nearest-neighbor sampling; original master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-7a50a81b-9615-464c-be8f-95b87d26a7cf.png`.

Prompt: Use case: precise-object-edit. Asset type: Minecraft transparent pixel-art sapling sprite, used identically for inventory and placed crossed-plane block. First image is edit target, second is current Spiritwood leaf texture reference. Replace the pale white trunk, branches and roots with darker oak-like medium/dark brown bark, vertically streaked with muted warm brown highlights and very dark brown grooves. Adjust foliage to muted oak green with subtle cool blue-green tint matching the second reference; keep tiny pale cool accents sparingly, not glowing cyan ornamentation. Keep the existing sapling silhouette, branching placement, roots, leaf clusters and crisp low-resolution pixel-art aesthetic. One centered upright tree sapling, fully visible with small clear margins, no ground plane, no shadow, no backdrop, no lettering. Genuine transparent background. It must look like a young oak-barked Spiritwood tree rather than white deadwood.



## Purification Ward and Station

Both item sprites and placed-block faces now use ingredient-informed temporary art. The Station shows its six iron ingots as riveted casing, its two redstone pieces as circuitry, and Purified Spiritwood as pale blue plank panels. The Ward shows its five Purified Spiritwood planks as a wooden pedestal and carving with Grave Moss in the four corners. Its cosmetic model replaces the generic Beacon appearance; the existing collision, cleansing, chunk tickets, recipe, light properties and interaction behavior are retained.

Final sprites: `src/main/resources/assets/mortal_expansion/textures/item/purification_station_temporary.png` and `purification_ward_temporary.png`. Final block faces: `src/main/resources/assets/mortal_expansion/textures/block/<purification_station|purification_ward>_<front|side|top|bottom>_temporary.png`. All are 64×64 PNGs; item sprites have genuine transparency, block faces are opaque. Built-in imagegen generated all art; nearest-neighbor sampling exported it. Java model generation owns both item and block references. Live appearance still needs playtesting.

### Purification prompt set

**purification_station item:** Use case: stylized-concept. Asset type: transparent Minecraft inventory sprite. One compact cubical Purification Station machine, silver iron casing with dark iron rivets, small red redstone circuitry/details at left and right sides, pale icy blue Purified Spiritwood plank lower band and top work surface. Recipe has six iron ingots surrounding two redstone and one Purified Spiritwood plank; convey those materials. Reference image is the exact pale-blue plank color and grain. Cohesive crisp pixel art matching vanilla inventory icons, simple 32x32-style enlarged pixels. Isometric three-quarter cubical view centered, small margins, entire object visible. No glass orb, beacon, fire, letters, ground shadow, backdrop, or separate floating ingredients. Genuine transparent background.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-bca3bdc0-68c9-4781-99e3-481d833628e3.png`.

**purification_station block atlas:** Use case: stylized-concept. Asset type: Minecraft block texture atlas. Input image is visual material/design reference. Create flat orthographic pixel-art texture faces matching silver iron riveted casing, dark joints, small red redstone circuitry/core on sides, pale icy-blue Purified Spiritwood plank panels in lower band and top surface. EXACT layout: square canvas split into four equal square quadrants touching without borders/gaps/margins: upper-left FRONT face, upper-right SIDE face, lower-left TOP face, lower-right BOTTOM face (simple matching base material). Each quadrant fills its whole square with fully opaque material texture. Crisp limited-palette pixels comparable to a 32x32 Minecraft texture enlarged. Strictly flat texture maps, no perspective, no isometric objects, no background outside material, no labels/text. Preserve ingredient colors of reference. Front and side have redstone traces in iron above a wooden lower band, top a pale wood surface framed in iron.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-93a32c46-d3ab-4533-9e96-7cc033fd27c0.png`.

**purification_ward item:** Use case: stylized-concept. Asset type: transparent Minecraft inventory sprite. One compact wooden Purification Ward: pale icy-blue Purified Spiritwood plank square pedestal with a smaller square wooden raised center, visible plank grain and joints, ash-green Grave Moss framing its four corners, a small subtle pale teal carved ward marking on the central face. First reference is the exact purified plank color and grain; second reference is Grave Moss colors and tendrils. Recipe uses five Purified Spiritwood planks in a cross and four Grave Moss in corners, convey wood and organic moss only. Crisp pixel art matching vanilla inventory icons, simple 32x32-style enlarged pixels. Isometric three-quarter view centered small margins entire object visible. No iron/gold, crystal, glass beacon, orb, fire, text, ground shadow or backdrop. Genuine transparent background.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-7ea1da9f-3da7-4e1d-9ca5-7b0c3245abf9.png`.

**purification_ward block atlas:** Use case: stylized-concept. Asset type: Minecraft block texture atlas. Input image is visual material/design reference. Create flat orthographic pixel-art texture faces matching pale icy-blue Purified Spiritwood plank grain and joints, ash-green Grave Moss clusters at four corners and trailing edges, subtle teal ward carving on TOP face only. Wood and moss only, no metal or glass. EXACT layout: square canvas split into four equal square quadrants touching without borders/gaps/margins: upper-left FRONT face, upper-right SIDE face, lower-left TOP face, lower-right BOTTOM face (simple matching base material). Each quadrant fills its whole square with fully opaque material texture. Crisp limited-palette pixels comparable to a 32x32 Minecraft texture enlarged. Strictly flat texture maps, no perspective, no isometric objects, no background outside material, no labels/text. Preserve ingredient colors of reference. Front and side show planks with moss at upper corners, top shows a wood cross grain arrangement with four moss corners.

Master: `C:\Users\scott\.codex\generated_images\01a10808-7c57-7922-a619-5f19e36de671\exec-04deeedf-4ccb-44b4-8975-493f20b81979.png`.


## Purification Station screen spacing

The machine panel is 24 GUI pixels taller. Its labels render at 90% size (fitting further only if translated text is too wide), with separate water and energy rows and a dedicated lower progress row/bar. Inventory slots and background move together through the shared menu MACHINE_HEIGHT constant; machine slot positions and indices, recipe behavior, and saved data are unchanged.
