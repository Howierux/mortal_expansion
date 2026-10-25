# Fabric content catalog

The Fabric port registers all **97 original item IDs and 60 original block IDs**, plus six source/flowing fluid IDs. The Mortal Expansion creative tab exposes the catalog and all five Book of Ectcetera tiers. The Darkhold uses its distinct original art. Matching IDs do not establish NeoForge save compatibility: use fresh Fabric worlds.

## Available now

- Existing item pictures, block geometry, textures, animated texture metadata and translations are reused. No replacement wool/leather placeholder art is introduced.
- Native wood building blocks provide ordinary stairs, slabs, fences, gates, doors, trapdoors, buttons and pressure plates. Logs, leaves, moss, ores and basic stone blocks use native block types.
- Native tools retain the original baseline mining tiers, attack attributes, durability and repair ingredients. Native armor retains defense, toughness, knockback resistance, durability and repair ingredients. Fractional Spectralite sword damage is preserved without NeoForge's float overload.
- Five worn armor skins: Bloodstone, Spectralite, Ghost Garb, Haint Garb and the distinct Spectralite-Haint weave. Native armor trims and armor enchantment tags are enabled.
- Machine and specialty blocks expose their visual states, including furnace eyes/lights, grave direction/depth/naturalization, golem flesh stages, torches and pipe arms. Pipes now transfer fluids; both generators have functional inventories, power output and screens. See [Fabric machinery](FABRIC_MACHINES.md).
- Three native bucket fluids can be placed and picked up. Fabric fluid handlers and bucket color providers retain their color families. Bucket models use native water/bucket artwork with separately tinted liquid geometry, without NeoForge model loaders.
- Native datagen supplies block/item tags and loot tables. The Fancifier composite model is converted to native JSON; Fabric supplies its translucent render layer.

## Still pending

This is a catalog/appearance implementation, not a port of every item's behavior. Specialized entries show a `Fabric preview` tooltip, and their source contains `Missing function` comments. Pending systems include:

- Book screens, upgrading, secret knowledge, recipe restrictions, research and crafting progression. Creative book tier previews do not unlock knowledge.
- EMF live readings and the precise numeric display; previews display the unlit reader model.
- Other machinery inventories/screens, furnaces/cooking, altars and the remaining energy consumers. Generators, tanks and pipes are implemented; see [Fabric machinery](FABRIC_MACHINES.md).
- Fancifier opacity application and functional fading blocks. Its legacy item/block currently displays Gravestone as a visible catalog preview.
- Equipment powers, Haunt scaling, tree felling, area mining, repair ticks, garb intangibility, jump and protections; scepter use and ritual/boon consumption.
- Grave text/naturalization/porting, dreamcatcher moon symbols/protection, Spiritwell travel, Wisp transformations/spawning and golem creation.
- Spiritwood tree growth and world generation; dynamic leaf opacity. Saplings are placeable previews and do not grow oak trees.
- Holy Salt signals/barriers, Spirit Torch behavior, fluid blessings/viscosity/cauldron interactions and Holy Water potion/brewing registration.

Specialty blocks use ordinary catalog collision where their detailed interaction/shape port is pending. Grave thickness, sapling outline, pipe arms and the Fancifier base have dedicated shapes. No original recipes are blindly enabled, so locked-knowledge crafting cannot be bypassed by copied recipe JSON. Native drops are provisional until each specialty mechanic is ported.

## Regeneration and validation

From `fabric/`:

~~~text
python tools/import_catalog_assets.py
python tools/validate_catalog_assets.py
gradlew.bat runDatagen
gradlew.bat build
~~~

The importer copies original textures byte-for-byte and converts loader-specific model schemas; it is explicit and reproducible. Framework-independent artwork remains at the repository root as the NeoForge reference. Native tag and loot providers remain Java Fabric data generators. CI checks imported assets and generated data for drift.

Validation: nine native headless Fabric GameTests pass, including ordinary equipment stats, pipe neighbor updates and native bucket placement/pickup. A local client smoke check loaded all 97 item models and 1,138 non-fluid block states, five armor texture pairs and the Darkhold, without missing-model/texture warnings, at 30% master volume. The temporary probe was removed from production sources. Gameplay behavior listed as pending has not been claimed as tested.

The machinery update additionally verifies rollback, pipe budgets/directions, portable fluid contents, fuel/Haunt accounting, enchantment conversion, automation and exact menu values. Both generator screens and tank synchronization were checked in a real integrated client/server session at 30% volume.
