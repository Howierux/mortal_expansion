package com.howierux.mortalexpansion.worldgen;

import com.howierux.mortalexpansion.catalog.Catalog;
import com.howierux.mortalexpansion.worldgen.structure.*;
import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import terrablender.api.*;

/** Static registration is separate from generated dynamic registries and TerraBlender climate insertion. */
public final class TerrainRuntime implements TerraBlenderApi {
 public static final Feature<?> SPIRITWELL=Registry.register(BuiltInRegistries.FEATURE,Catalog.id("spiritwell"),new SpiritwellFeature());
 public static final StructureType<GraveyardCemeteryStructure> CEMETERY_TYPE=Registry.register(BuiltInRegistries.STRUCTURE_TYPE,Catalog.id("graveyard_cemetery"),()->GraveyardCemeteryStructure.CODEC);
 public static final StructurePieceType CEMETERY_PIECE=Registry.register(BuiltInRegistries.STRUCTURE_PIECE,Catalog.id("graveyard_cemetery"),(StructurePieceType)GraveyardCemeteryPiece::new);
 public static ResourceKey<PlacedFeature> placed(String name){return ResourceKey.create(Registries.PLACED_FEATURE,Catalog.id(name));}
 public static void initialize(){
  BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),GenerationStep.Decoration.UNDERGROUND_ORES,placed("bloodstone_ore"));
  BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),GenerationStep.Decoration.TOP_LAYER_MODIFICATION,placed("spiritwell"));
 }
 @Override public void onTerraBlenderInitialized(){
  Regions.register(new MortalExpansionOverworldRegion(Catalog.id("graveyard_overworld"),1));
  Regions.register(new SpiritwoodOverworldRegion(Catalog.id("spiritwood_overworld"),1));
 }
}
