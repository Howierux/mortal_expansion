package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.haunt.HauntBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import java.util.concurrent.CompletableFuture;
/** Generated biome membership drives native Haunt baselines and Overworld feature selection. */
public final class HauntBiomeProvider extends FabricTagProvider<Biome> {
 public HauntBiomeProvider(FabricDataOutput output,CompletableFuture<HolderLookup.Provider> lookup){super(output,Registries.BIOME,lookup);}
 @Override protected void addTags(HolderLookup.Provider lookup){
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("minecraft:is_nether"))).addOptionalTag(net.minecraft.resources.ResourceLocation.parse("c:is_nether"));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("minecraft:is_end"))).addOptionalTag(net.minecraft.resources.ResourceLocation.parse("c:is_end"));

   // NeoForge dev f080fdc: preserve public tag contracts with native Fabric builders.
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("minecraft:is_forest"))).add(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:spiritwood_forest")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("minecraft:is_overworld"))).add(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:spiritwood_forest")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("minecraft:is_overworld"))).add(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:graveyard")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:dead_rising_haunt"))).add(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:graveyard")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:demon_spawn_biomes"))).addTag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("minecraft:is_overworld")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:demon_spawn_biomes"))).addTag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("minecraft:is_nether")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:demon_spawn_biomes"))).addTag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("minecraft:is_end")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:has_structure/graveyard_cemetery"))).add(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:graveyard")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:supernatural_haunt"))).add(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BIOME,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:spiritwood_forest")));

  tag(net.minecraft.tags.BiomeTags.IS_OVERWORLD).add(com.howierux.mortalexpansion.worldgen.ModBiomes.GRAVEYARD,com.howierux.mortalexpansion.worldgen.ModBiomes.SPIRITWOOD_FOREST);
  tag(net.minecraft.tags.BiomeTags.IS_FOREST).add(com.howierux.mortalexpansion.worldgen.ModBiomes.SPIRITWOOD_FOREST);
  tag(net.minecraft.tags.TagKey.create(Registries.BIOME,com.howierux.mortalexpansion.catalog.Catalog.id("has_structure/graveyard_cemetery"))).add(com.howierux.mortalexpansion.worldgen.ModBiomes.GRAVEYARD);
  tag(HauntBiomeTags.NATURAL_HAUNT);
  tag(HauntBiomeTags.SUPERNATURAL_HAUNT).add(com.howierux.mortalexpansion.worldgen.ModBiomes.SPIRITWOOD_FOREST);
  tag(HauntBiomeTags.DEAD_RISING_HAUNT).add(com.howierux.mortalexpansion.worldgen.ModBiomes.GRAVEYARD);
 }
}
