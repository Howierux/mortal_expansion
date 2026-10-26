package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.worldgen.*;
import com.howierux.mortalexpansion.worldgen.structure.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.*;
import net.minecraft.resources.*;
import net.minecraft.tags.*;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.*;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import java.util.*;

/** Native dynamic-registry bootstraps are the source of world-generation data. */
public final class TerrainBootstrap {
 public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry,String name){return ResourceKey.create(registry,Catalog.id(name));}
 public static ResourceKey<ConfiguredFeature<?,?>> configured(String name){return key(Registries.CONFIGURED_FEATURE,name);}
 public static void configured(BootstrapContext<ConfiguredFeature<?,?>> c){
  c.register(ModTreeGrowers.SPIRITWOOD_TREE,new ConfiguredFeature<>(Feature.TREE,new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(CatalogBlocks.SPIRITWOOD_LOG),new StraightTrunkPlacer(4,2,0),BlockStateProvider.simple(CatalogBlocks.SPIRITWOOD_LEAVES),new BlobFoliagePlacer(ConstantInt.of(2),ConstantInt.of(0),3),new TwoLayersFeatureSize(1,0,1)).ignoreVines().build()));
  c.register(configured("graveyard_trees"),new ConfiguredFeature<>(Feature.RANDOM_SELECTOR,new RandomFeatureConfiguration(List.of(new WeightedPlacedFeature(Holder.direct(new PlacedFeature(c.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModTreeGrowers.SPIRITWOOD_TREE),List.of())),.8F)),c.lookup(Registries.PLACED_FEATURE).getOrThrow(VegetationPlacements.TREES_PLAINS))));
  c.register(configured("bloodstone_ore"),new ConfiguredFeature<>(Feature.ORE,new OreConfiguration(List.of(OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),CatalogBlocks.BLOODSTONE_ORE_STONE.defaultBlockState()),OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES),CatalogBlocks.BLOODSTONE_ORE.defaultBlockState())),4,.5F)));
  c.register(configured("spiritwell"),new ConfiguredFeature<>(newFeature(),NoneFeatureConfiguration.INSTANCE));
 }
 private static SpiritwellFeature newFeature(){return (SpiritwellFeature)TerrainRuntime.SPIRITWELL;}
 public static void placed(BootstrapContext<PlacedFeature> c){
  var lookup=c.lookup(Registries.CONFIGURED_FEATURE);
  c.register(TerrainRuntime.placed("spiritwood_trees"),new PlacedFeature(lookup.getOrThrow(ModTreeGrowers.SPIRITWOOD_TREE),VegetationPlacements.treePlacement(PlacementUtils.countExtra(10,.1F,1),CatalogBlocks.SPIRITWOOD_SAPLING)));
  c.register(TerrainRuntime.placed("graveyard_trees"),new PlacedFeature(lookup.getOrThrow(configured("graveyard_trees")),VegetationPlacements.treePlacement(PlacementUtils.countExtra(0,.05F,1),Blocks.OAK_SAPLING)));
  c.register(TerrainRuntime.placed("bloodstone_ore"),new PlacedFeature(lookup.getOrThrow(configured("bloodstone_ore")),List.of(CountPlacement.of(1),InSquarePlacement.spread(),HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80),VerticalAnchor.aboveBottom(80)),BiomeFilter.biome())));
  c.register(TerrainRuntime.placed("spiritwell"),new PlacedFeature(lookup.getOrThrow(configured("spiritwell")),List.of(RarityFilter.onAverageOnceEvery(256),InSquarePlacement.spread(),HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),BiomeFilter.biome())));
 }
 public static void structures(BootstrapContext<Structure> c){c.register(key(Registries.STRUCTURE,"graveyard_cemetery"),new GraveyardCemeteryStructure(new Structure.StructureSettings(c.lookup(Registries.BIOME).getOrThrow(TagKey.create(Registries.BIOME,Catalog.id("has_structure/graveyard_cemetery"))),Map.of(),GenerationStep.Decoration.SURFACE_STRUCTURES,TerrainAdjustment.NONE)));}
 public static void sets(BootstrapContext<StructureSet> c){c.register(key(Registries.STRUCTURE_SET,"graveyard_cemetery"),new StructureSet(c.lookup(Registries.STRUCTURE).getOrThrow(key(Registries.STRUCTURE,"graveyard_cemetery")),new RandomSpreadStructurePlacement(9,7,RandomSpreadType.LINEAR,18470631)));}
 public static void biomes(BootstrapContext<Biome> c){
  {
   var generation=new BiomeGenerationSettings.Builder(c.lookup(Registries.PLACED_FEATURE),c.lookup(Registries.CONFIGURED_CARVER));
   generation.addCarver(GenerationStep.Carving.AIR,ResourceKey.create(Registries.CONFIGURED_CARVER,ResourceLocation.parse("minecraft:cave")));
   generation.addCarver(GenerationStep.Carving.AIR,ResourceKey.create(Registries.CONFIGURED_CARVER,ResourceLocation.parse("minecraft:cave_extra_underground")));
   generation.addCarver(GenerationStep.Carving.AIR,ResourceKey.create(Registries.CONFIGURED_CARVER,ResourceLocation.parse("minecraft:canyon")));
   generation.addFeature(GenerationStep.Decoration.values()[1],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:lake_lava_underground")));
   generation.addFeature(GenerationStep.Decoration.values()[1],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:lake_lava_surface")));
   generation.addFeature(GenerationStep.Decoration.values()[2],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:amethyst_geode")));
   generation.addFeature(GenerationStep.Decoration.values()[3],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:monster_room")));
   generation.addFeature(GenerationStep.Decoration.values()[3],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:monster_room_deep")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_dirt")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_gravel")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_granite_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_granite_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diorite_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diorite_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_andesite_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_andesite_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_tuff")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_coal_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_coal_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_iron_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_iron_middle")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_iron_small")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_gold")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_gold_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_redstone")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_redstone_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diamond")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diamond_medium")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diamond_large")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diamond_buried")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_lapis")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_lapis_buried")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_copper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:underwater_magma")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:disk_sand")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:disk_clay")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:disk_gravel")));
   generation.addFeature(GenerationStep.Decoration.values()[8],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:spring_water")));
   generation.addFeature(GenerationStep.Decoration.values()[8],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:spring_lava")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:glow_lichen")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:patch_tall_grass_2")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("mortal_expansion:graveyard_trees")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:patch_grass_plain")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:brown_mushroom_normal")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:red_mushroom_normal")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:patch_sugar_cane")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:patch_pumpkin")));
   generation.addFeature(GenerationStep.Decoration.values()[10],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:freeze_top_layer")));
   var spawns=new MobSpawnSettings.Builder();
   spawns.addSpawn(MobCategory.AMBIENT,new MobSpawnSettings.SpawnerData(EntityType.BAT,10,8,8));
   spawns.addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(EntityType.SHEEP,12,4,4));
   spawns.addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(EntityType.PIG,10,4,4));
   spawns.addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(EntityType.CHICKEN,10,4,4));
   spawns.addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(EntityType.COW,8,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.SPIDER,100,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE,95,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE_VILLAGER,5,1,1));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.SKELETON,100,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.CREEPER,100,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.SLIME,100,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN,10,1,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.WITCH,5,1,1));
   spawns.addSpawn(MobCategory.UNDERGROUND_WATER_CREATURE,new MobSpawnSettings.SpawnerData(EntityType.GLOW_SQUID,10,4,6));
   var effects=new BiomeSpecialEffects.Builder().fogColor(11184810).skyColor(7236751).waterColor(3362130).waterFogColor(329011).grassColorOverride(7305831).foliageColorOverride(6587485);
   effects.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS);
   c.register(ModBiomes.GRAVEYARD,new Biome.BiomeBuilder().hasPrecipitation(true).temperature(.7F).downfall(0.5F).specialEffects(effects.build()).mobSpawnSettings(spawns.build()).generationSettings(generation.build()).build());
  }
  {
   var generation=new BiomeGenerationSettings.Builder(c.lookup(Registries.PLACED_FEATURE),c.lookup(Registries.CONFIGURED_CARVER));
   generation.addCarver(GenerationStep.Carving.AIR,ResourceKey.create(Registries.CONFIGURED_CARVER,ResourceLocation.parse("minecraft:cave")));
   generation.addCarver(GenerationStep.Carving.AIR,ResourceKey.create(Registries.CONFIGURED_CARVER,ResourceLocation.parse("minecraft:cave_extra_underground")));
   generation.addCarver(GenerationStep.Carving.AIR,ResourceKey.create(Registries.CONFIGURED_CARVER,ResourceLocation.parse("minecraft:canyon")));
   generation.addFeature(GenerationStep.Decoration.values()[1],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:lake_lava_underground")));
   generation.addFeature(GenerationStep.Decoration.values()[1],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:lake_lava_surface")));
   generation.addFeature(GenerationStep.Decoration.values()[2],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:amethyst_geode")));
   generation.addFeature(GenerationStep.Decoration.values()[3],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:monster_room")));
   generation.addFeature(GenerationStep.Decoration.values()[3],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:monster_room_deep")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_dirt")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_gravel")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_granite_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_granite_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diorite_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diorite_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_andesite_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_andesite_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_tuff")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_coal_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_coal_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_iron_upper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_iron_middle")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_iron_small")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_gold")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_gold_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_redstone")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_redstone_lower")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diamond")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diamond_medium")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diamond_large")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_diamond_buried")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_lapis")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_lapis_buried")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:ore_copper")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:underwater_magma")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:disk_sand")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:disk_clay")));
   generation.addFeature(GenerationStep.Decoration.values()[6],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:disk_gravel")));
   generation.addFeature(GenerationStep.Decoration.values()[8],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:spring_water")));
   generation.addFeature(GenerationStep.Decoration.values()[8],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:spring_lava")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:glow_lichen")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:forest_flowers")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("mortal_expansion:spiritwood_trees")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:flower_default")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:patch_grass_forest")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:brown_mushroom_normal")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:red_mushroom_normal")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:patch_sugar_cane")));
   generation.addFeature(GenerationStep.Decoration.values()[9],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:patch_pumpkin")));
   generation.addFeature(GenerationStep.Decoration.values()[10],ResourceKey.create(Registries.PLACED_FEATURE,ResourceLocation.parse("minecraft:freeze_top_layer")));
   var spawns=new MobSpawnSettings.Builder();
   spawns.addSpawn(MobCategory.AMBIENT,new MobSpawnSettings.SpawnerData(EntityType.BAT,10,8,8));
   spawns.addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(EntityType.SHEEP,12,4,4));
   spawns.addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(EntityType.PIG,10,4,4));
   spawns.addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(EntityType.CHICKEN,10,4,4));
   spawns.addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(EntityType.COW,8,4,4));
   spawns.addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(EntityType.WOLF,5,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.SPIDER,100,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE,95,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE_VILLAGER,5,1,1));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.SKELETON,100,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.CREEPER,100,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.SLIME,100,4,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN,10,1,4));
   spawns.addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(EntityType.WITCH,5,1,1));
   spawns.addSpawn(MobCategory.UNDERGROUND_WATER_CREATURE,new MobSpawnSettings.SpawnerData(EntityType.GLOW_SQUID,10,4,6));
   var effects=new BiomeSpecialEffects.Builder().fogColor(11184810).skyColor(7186907).waterColor(3175807).waterFogColor(329011).grassColorOverride(7838058).foliageColorOverride(43520);
   c.register(ModBiomes.SPIRITWOOD_FOREST,new Biome.BiomeBuilder().hasPrecipitation(true).temperature(.7F).downfall(0.85F).specialEffects(effects.build()).mobSpawnSettings(spawns.build()).generationSettings(generation.build()).build());
  }
 }
}
