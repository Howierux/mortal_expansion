# Woven garb and Spectralite Haint art

Ghost Garb now depicts weathered green Ectothread cloth with dark seams and pale ectoplasm stitches. Its recipe is string around raw ectoplasm, then thread armor shapes. Haint Garb depicts cleaner blue-white purified thread with braided pale seams and blue knots. Spectralite Haint visibly combines those pale cloth panels with dark Spectralite plating, cyan seams and the Bloodstone red retained through its Netherite upgrade lineage.

All twelve inventory icons are distinct. Ghost/Haint use their own native armor-material layers. Spectralite Haint retains the existing Spectralite material, statistics and effects, selecting separate worn textures through NeoForge's supported per-item getArmorTexture hook on GarbArmorItem. Existing native trim rendering and generated hybrid inventory trim overlays remain intact. No new registry IDs, packets, renderers or mixins are required. Equipment item IDs, benefits, repair ingredients, durability, smithing and saved components remain unchanged.

The fixed green/blue leather-placeholder tint handler is removed, and the new material layers are not dyeable. Their intended recipe colors are baked into the artwork. Previously saved DYED_COLOR components remain stored but do not tint this fixed artwork. These garb sets did not have a new dyeing feature added by this change.

## Saved assets

Under src/main/resources/assets/mortal_expansion/textures/item/:
- ghost_garb_{helmet,chestplate,leggings,boots}_woven.png
- haint_garb_{helmet,chestplate,leggings,boots}_woven.png
- spectralite_haint_{helmet,chestplate,leggings,boots}_woven.png

Under textures/models/armor/:
- ghost_garb_woven_layer_{1,2}.png
- haint_garb_woven_layer_{1,2}.png
- spectralite_haint_woven_layer_{1,2}.png

Inventory sprites are 16x16 RGBA; worn layers are 64x32 RGBA with exact native UV alpha coverage. Generated atlas tiles were cropped to opaque bounds and baked into those masks to prevent gutter artifacts. New filenames preserve prior textures. Visual appearance while worn still needs an in-game check.

## Generation prompts

Art was produced with the built-in image-generation tool, one reference-edit call for each set. No CLI/API fallback was used. References were compact inventory-and-worn sheets assembled from the existing native leather layout and the current Spectralite art.

### ghost_garb

Use case: precise-object-edit. Asset: technical Minecraft texture atlas, not a presentation or character illustration. Edit the reference sheet in place, preserving its exact 1024x640 aspect ratio, pixel grid, ALL UV island positions and transparent silhouette boundaries. Top row: four 16x16 inventory sprites, helmet/chestplate/leggings/boots, positioned in 32-pixel cells. Lower row starts at y=32 on the original 128x80 grid: two 64x32 unfolded armor UV layouts, left layer 1, right layer 2. Keep this grid exactly. Transparent background and gutters, no text, labels, shadows outside islands, extra assets or layout changes. Cohesive readable pixel art at native Minecraft resolution, strong enough material separation to read in game. Replace leather with GHOST GARB woven from Ectothread (eight string around raw ectoplasm). Weathered pale moss/lime green woven fabric, dark olive seams, visibly stitched thread crosses and frayed-looking surface detail kept within silhouettes. A soft hood, cloth tunic, trousers and wrapped boots instead of metal plates. Muted ghostly green, sparse brighter ectoplasm stitches. Apply consistent weave to inventory icons and both worn UV layouts.

### haint_garb

Use case: precise-object-edit. Asset: technical Minecraft texture atlas, not a presentation or character illustration. Edit the reference sheet in place, preserving its exact 1024x640 aspect ratio, pixel grid, ALL UV island positions and transparent silhouette boundaries. Top row: four 16x16 inventory sprites, helmet/chestplate/leggings/boots, positioned in 32-pixel cells. Lower row starts at y=32 on the original 128x80 grid: two 64x32 unfolded armor UV layouts, left layer 1, right layer 2. Keep this grid exactly. Transparent background and gutters, no text, labels, shadows outside islands, extra assets or layout changes. Cohesive readable pixel art at native Minecraft resolution, strong enough material separation to read in game. Replace leather with HAINT GARB woven from purified Ectothread (string and purified ectoplasm). Clean pale blue-white woven fabric, light icy blue shading, silver-white braided seams and small blue ectoplasmic knots. Soft hood, cloth tunic, trousers, wrapped boots. More refined and luminous-looking than rough green Ghost Garb, but no real light-emission effect. Apply matching fabric to four icons and both worn UV layouts.

### spectralite_haint

Use case: precise-object-edit. Asset: technical Minecraft texture atlas, not a presentation or character illustration. Edit the reference sheet in place, preserving its exact 1024x640 aspect ratio, pixel grid, ALL UV island positions and transparent silhouette boundaries. Top row: four 16x16 inventory sprites, helmet/chestplate/leggings/boots, positioned in 32-pixel cells. Lower row starts at y=32 on the original 128x80 grid: two 64x32 unfolded armor UV layouts, left layer 1, right layer 2. Keep this grid exactly. Transparent background and gutters, no text, labels, shadows outside islands, extra assets or layout changes. Cohesive readable pixel art at native Minecraft resolution, strong enough material separation to read in game. Create SPECTRALITE HAINT armor by visibly combining the existing dark Spectralite Netherite-over-crimson-Bloodstone plating with Haint Garb's pale blue-white purified woven cloth. Large pale cloth panels at hood lining/collar, torso center, elbows and leg joints, dark angular plates at brow, shoulders, chest edges, forearms and knees; pale cyan stitched borders bind cloth to metal; retain restrained crimson seams from the base metal. It must look clearly different from plain dark Spectralite, approximately half pale purified fabric and half dark plating. Keep surfaces clear enough for vanilla armor trims. Apply consistent material placement to four inventory icons and both worn UV layouts.
