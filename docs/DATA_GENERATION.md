# Data generation

Blocks and items are registered in Java in `ModBlocks` and `ModItems`. Data generation
is a separate development step: it writes the JSON Minecraft consumes without
running generators during normal gameplay.

`ModDataGenerators` registers NeoForge/Mojang providers under
`src/main/java/com/howierux/mortalexpansion/data`:

- `ModBlockModels`: block geometry, textures, and block states, using framework
  shape helpers where they preserve the existing visuals.
- `ModItemModels`: item models and typed fluid-container loaders.
- `ModPostEffects`: Spirit Journey grayscale post-chain, using vanilla shader programs.
- `ModRecipes`: crafting, cooking, and smithing recipes with the existing unlock
  policy. The Book's custom advancement remains authored separately.
- `ModBlockLoot` and `ModLootTables`: validated loot builders serialized with
  Mojang's codec, retaining the existing named-random-sequence policy.
- `DatapackBuiltinEntriesProvider`: demi-tangible melee damage types, serialized with Mojang registry codecs.
- `ModTags`: block, item, entity, damage-type, and biome tags, including empty
  extension tags and additions to vanilla tags.

Generate after editing a provider:

```text
gradlew.bat runData
python tools/check_datagen.py
python tools/check_ectcetera.py
gradlew.bat build
```

Commit both Java providers and their output in `src/generated/resources` so a fresh
checkout can build and launch without first running data generation. Never edit
those generated JSON files directly. Their `.cache` directory is local metadata
and is ignored. Textures, translations, custom advancement content, and worldgen
definitions remain in `src/main/resources`.

The migration preserved all 344 existing resource definitions. The semantic
snapshot accepts equivalent serializer defaults, namespace spellings, and float
formatting; it detects changed recipes, drops, geometry, mappings, and tag entries.
When changing gameplay content deliberately, review the generated diff and run
`python tools/check_datagen.py --update-baseline`, then commit the updated snapshot.
CI regenerates output, checks this snapshot and references, and rejects stale
tracked generated files.

For a Minecraft/NeoForge version migration, adapt provider API calls and imports
to the target version, regenerate, and inspect any semantic differences. The
framework owns JSON schema serialization; custom model geometry and intentional
legacy stair rotations remain mod-owned content.

The provider setup follows the [NeoForge 1.21.1 model data-generation API](https://docs.neoforged.net/docs/1.21.1/resources/client/models/datagen/).
