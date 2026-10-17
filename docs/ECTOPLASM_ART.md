# Ectoplasm item art

Raw and Purified Ectoplasm use original 64x64 RGBA inventory/held sprites generated with the built-in image-generation tool and exported using nearest-neighbor sampling. Their item IDs, recipes, drops, tint rules, and gameplay remain unchanged. Raw also appears on existing spectral projectiles that render this item.

- Raw: spectral residue from ghosts/spirits, irregular folded slime and drifting tendrils; lime/emerald palette.
- Purified: cleansed and cooked condensed essence, denser pearlescent core and cleaner lobes; icy blue/cyan/white palette.

Asset paths:
- `src/main/resources/assets/mortal_expansion/textures/item/raw_ectoplasm.png`
- `src/main/resources/assets/mortal_expansion/textures/item/pure_ectoplasm.png`

The generated item models now use the new base art alone. Old animated shimmer layers were tailored to the former silhouettes and are no longer referenced; their source assets are retained for history. The new art is static and does not imply world-light emission.

Generated masters:
- `C:/Users/scott/.codex/generated_images/01a10808-7c57-7922-a619-5f19e36de671/exec-f09fc0a4-b4a3-4b37-9c0f-d704c6b2a3a4.png`
- `C:/Users/scott/.codex/generated_images/01a10808-7c57-7922-a619-5f19e36de671/exec-8964e29c-87cf-4aef-83da-e704691438dd.png`

## Generation prompts

### Raw

Use case: stylized-concept. Asset type: Minecraft transparent inventory item sprite, original pixel art. Create ONE Raw Ectoplasm item, a cohesive irregular dollop of supernatural residue shed by ghosts and spirits, with a curling wispy upper tendril, two small suspended globules attached visually to the main silhouette, gooey folded body and drooping lower edge. Keep classic bright lime and emerald GREEN color theme, deep forest-green shadow outline and a few pale green luminous highlights. It should read as haunted spectral slime or spirit essence, not a plant, flame, crystal, face, bottle or ordinary slimeball. Crisp chunky square pixels on a 32x32 logical grid, limited palette, no smooth gradients or blur, no text, no frame, no scene, no cast shadow. One centered isolated complete object occupies 80 percent of square. Genuinely transparent background with clean alpha, no checkerboard. Production game sprite readable at tiny inventory scale.

### Purified

Use case: stylized-concept. Asset type: Minecraft transparent inventory item sprite, original pixel art. Create ONE Purified Ectoplasm item. This substance is ghost residue cleansed with Holy Water and Grave Moss, then condensed into solid ectoplasm by cooking. A compact clean pearlescent clump of spirit essence with smooth folded droplet lobes, small upward curling spectral wisp, a bright dense central core, smooth rounded gel lobes without crystals, shards, gemstones or faceted surfaces. Keep its existing icy LIGHT BLUE, CYAN and WHITE color theme, pale silver-blue highlights, deep muted teal-blue outline. Related in spirit to green raw ectoplasm, but noticeably cleaner, more stable and condensed, without green contamination. No container, tools, plant, flame, face or text. Crisp chunky square pixel-art on a 32x32 logical grid, limited palette, clean silhouette readable at tiny Minecraft inventory scale. No smooth gradients, blur, halo or floor shadow. One centered complete isolated object occupies 80 percent of square. Genuinely transparent background and clean alpha, no checkerboard.

## Validation

Both exported sprites were visually inspected at 64x64 and retain transparent backgrounds. Regenerate models through ModItemModels and validate resources/build after changes. In-game held-item appearance remains a playtest check.
## Purified revision prompt

Use case: precise-object-edit. Edit target: attached Purified Ectoplasm Minecraft sprite. Remove ALL crystalline parts: the two angular diamond-shaped crystal shards embedded at lower left and lower right, their pointed tips, hard faceted edges, and any gemstone-like interior surfaces. Replace those exact regions with smooth rounded folded ectoplasm gel, blending into the surrounding lumpy pearlescent spirit essence. Keep the existing overall droplet mound silhouette, curling upper spectral wisp, smaller wisps, dense glowing core, icy light-blue/cyan/white palette, crisp chunky pixel-art style, approximate composition and transparent background. It must read purely as soft purified spectral goo or condensed spirit residue, with rounded lobes and curved highlights, no crystals, no shards, no facets, no gemstones anywhere. No text, extra objects, background, cast shadow or halo. Preserve genuine alpha transparency.
