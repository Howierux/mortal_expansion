# Temporary ingredient-informed inventory art

Ten generic block/borrowed plant item icons are replaced with original temporary sprites. The designs were generated with the built-in imagegen tool, then exported as 64x64 transparent PNGs using nearest-neighbor sampling. Original generated masters are retained separately. `_temporary` filenames make later replacement explicit without overwriting earlier authored artwork.

The two thread icons show string coils infused with raw green or purified blue ectoplasm. Generator icons reflect gold, iron, furnaces, grindstone and redstone; the Ectchantment Machine reflects purified stone, enchanting book/table and purified ectoplasm. The research station reflects its Obsidian base, book and Holy Salt. The paired altars reflect Beacon/Nether Star over dark or icy-blue furnaces. Grave Moss and the sapling reflect their natural haunted-plant sources rather than invented crafting recipes.

These are inventory/held-item sprites. Matching placed-block art is documented in [TEMPORARY_BLOCK_ART.md](TEMPORARY_BLOCK_ART.md). Recipes, live EMF model/readings, registered IDs and gameplay are unchanged. Full-color Grave Moss and sapling sprites use neutral inventory tint; their matching world textures also use neutral tint. Java data generation owns all affected item models. No new shaders, model loaders or networking are introduced.

## Saved asset paths

| Item | Asset |
| --- | --- |
| ectothread | `src/main/resources/assets/mortal_expansion/textures/item/ectothread_temporary.png` |
| purified_ectothread | `src/main/resources/assets/mortal_expansion/textures/item/purified_ectothread_temporary.png` |
| ectoplasmic_generator | `src/main/resources/assets/mortal_expansion/textures/item/ectoplasmic_generator_temporary.png` |
| enchantment_generator | `src/main/resources/assets/mortal_expansion/textures/item/enchantment_generator_temporary.png` |
| ectchantment_machine | `src/main/resources/assets/mortal_expansion/textures/item/ectchantment_machine_temporary.png` |
| mortality_research_station | `src/main/resources/assets/mortal_expansion/textures/item/mortality_research_station_temporary.png` |
| altar_of_purity | `src/main/resources/assets/mortal_expansion/textures/item/altar_of_purity_temporary.png` |
| altar_of_impurity | `src/main/resources/assets/mortal_expansion/textures/item/altar_of_impurity_temporary.png` |
| grave_moss | `src/main/resources/assets/mortal_expansion/textures/item/grave_moss_temporary.png` |
| spiritwood_sapling | `src/main/resources/assets/mortal_expansion/textures/item/spiritwood_sapling_temporary.png` |

## Prompt set

Each request generated one distinct asset on a transparent background. Final generation prompts:

### ectothread

Use case: stylized-concept. Asset type: transparent Minecraft inventory item sprite. Create one original temporary Ectothread icon: a compact coiled skein made from eight strands of pale string soaked in raw lime-green ectoplasm, loose curling thread end and small green spectral droplet. Consider the crafting recipe of eight string surrounding one raw ectoplasm. Cohesive classic pixel-art game sprite, crisp chunky square pixels on a 32-by-32 logical grid, limited lime/cream/dark-green palette, no gradients, no text, no frame, no scene, no extra objects. Single centered item fills roughly 80 percent of a square canvas; genuinely transparent background. Readable at inventory size; no wool block or cubic silhouette.

### purified_ectothread

Use case: stylized-concept. Asset type: original temporary Minecraft inventory sprite. One centered item on a genuinely transparent square background, crisp chunky pixel art on a 32-by-32 logical grid, limited palette, dark outline, simple readable silhouette, item occupies about 80% of canvas. No text, labels, frame, scene, floor or extra items. Consider the actual crafting ingredients in the design. A coiled skein of eight pale cream string strands infused with purified ectoplasm; icy light-blue luminous thread loops, a loose curling end and a tiny cyan droplet. Recipe is eight String around Purified Ectoplasm. Match a ghostly thread icon, no wool cube or wooden spool.

### ectoplasmic_generator

Use case: stylized-concept. Asset type: original temporary Minecraft inventory sprite. One centered item on a genuinely transparent square background, crisp chunky pixel art on a 32-by-32 logical grid, limited palette, dark outline, simple readable silhouette, item occupies about 80% of canvas. No text, labels, frame, scene, floor or extra items. Consider the actual crafting ingredients in the design. A compact gold-framed generator with a charcoal-gray Haunt Furnace base and a bright lime raw-ectoplasm reservoir above its furnace opening. Recipe is seven gold ingots surrounding Raw Ectoplasm above a Haunt Furnace. Recognizable gold housing, green fuel and furnace vent, no crying obsidian block stand-in.

### enchantment_generator

Use case: stylized-concept. Asset type: original temporary Minecraft inventory sprite. One centered item on a genuinely transparent square background, crisp chunky pixel art on a 32-by-32 logical grid, limited palette, dark outline, simple readable silhouette, item occupies about 80% of canvas. No text, labels, frame, scene, floor or extra items. Consider the actual crafting ingredients in the design. A compact silver iron generator with a clear gray grindstone wheel at top, redstone-red conductor/core in its center and a dark Haunt Furnace vent in the base. Recipe is six iron ingots, one grindstone, one redstone and one Haunt Furnace. Mechanical, tangible ingredient shapes, not an enchanting-table cube.

### ectchantment_machine

Use case: stylized-concept. Asset type: original temporary Minecraft inventory sprite. One centered item on a genuinely transparent square background; crisp chunky pixel art on a 32-by-32 logical grid, dark outline, limited colors, readable at tiny size. About 80% canvas coverage. No text, frame, scenery, floor or extra objects. Compact arcane machine made of pale icy-blue Purified Gravestone, incorporating a small red-covered enchanting book/table and a prominent glowing cyan purified-ectoplasm core. Recipe combines Enchanting Table plus Purified Ectoplasm surrounded by seven Purified Gravestone blocks. Distinct curved/stepped machine silhouette, show the stone, book and blue core; no generic iron block.

### mortality_research_station

Use case: stylized-concept. Asset type: original temporary Minecraft inventory sprite. One centered item on a genuinely transparent square background; crisp chunky pixel art on a 32-by-32 logical grid, dark outline, limited colors, readable at tiny size. About 80% canvas coverage. No text, frame, scenery, floor or extra objects. A small obsidian research desk with an open brown Book of Ectcetera, two bright white Holy Salt crystal piles at its sides, and pale parchment pages. Recipe matches enchanting table: four Obsidian base blocks, Holy Salt replacing the two Diamonds, and the Book on top as a preserved catalyst. Show book, white crystals and dark obsidian base; no diamonds, no enchanting-table cube.

### altar_of_purity

Use case: stylized-concept. Asset type: original temporary Minecraft inventory sprite. One centered item on a genuinely transparent square background; crisp chunky pixel art on a 32-by-32 logical grid, dark outline, limited colors, readable at tiny size. About 80% canvas coverage. No text, frame, scenery, floor or extra objects. An original purity altar inventory icon: an icy-blue Haint Furnace pedestal with a furnace opening and a small Beacon crystal/glass lantern mounted above, a bright white four-point Nether Star visible inside. Recipe is Beacon directly above Haint Furnace. Pale blue stone, glass, white/cyan purity light, clear altar silhouette; no gold/glowstone cube stand-in.

### altar_of_impurity

Use case: stylized-concept. Asset type: original temporary Minecraft inventory sprite. One centered item on a genuinely transparent square background; crisp chunky pixel art on a 32-by-32 logical grid, dark outline, limited colors, readable at tiny size. About 80% canvas coverage. No text, frame, scenery, ground or extra objects. A dark impurity altar: a charcoal-gray Haunt Furnace pedestal with a visible furnace opening, supporting a small Beacon crystal/glass lantern, four-point Nether Star visible inside with violet spectral light. Recipe is Beacon directly above Haunt Furnace. Emphasize actual dark furnace, glass and star, smoky-purple accents and a stepped altar silhouette. Paired style with an icy-blue purity altar. Not a magma/block cube stand-in.

### grave_moss

Use case: stylized-concept. Asset type: original temporary Minecraft inventory sprite. One centered item on a genuinely transparent square background; crisp chunky pixel art on a 32-by-32 logical grid, dark outline, limited colors, readable at tiny size. About 80% canvas coverage. No text, frame, scenery, ground or extra objects. A harvested tuft of Grave Moss: tangled ash-green moss fronds with a few pale cyan spectral tips, an organic trailing sprig and dark green outline. It naturally grows on haunted Spiritwood foliage and graveyard surfaces, so show living moss fibers and faint ghostly highlights. Compact loose plant clump, not a glow-lichen block face, not a potted plant.

### spiritwood_sapling

Use case: stylized-concept. Asset type: original temporary Minecraft inventory sprite. One centered item on a genuinely transparent square background; crisp chunky pixel art on a 32-by-32 logical grid, dark outline, limited colors, readable at tiny size. About 80% canvas coverage. No text, frame, scenery, ground or extra objects. A young Spiritwood sapling: pale gray spectral bark, a small branching trunk, soft moss-green leaf clusters touched by cyan ghost-light and tiny exposed roots. It comes from Spiritwood tree leaves, so show a recognizable young tree from haunted woodland. Small asymmetrical tree silhouette, no oak-sapling texture copy, no cube, no flowerpot.

## Verification

The sprite contact sheet was inspected at enlarged and 16-pixel inventory sizes. Exported PNGs are 64x64 with transparent corner pixels, and each generated model points to its matching sprite. Data generation and build pass; guide/generated-resource checks pass. No new gameplay tests were added for these art-only changes. In-game readability, atlas loading and hand appearance still require client playtesting.
