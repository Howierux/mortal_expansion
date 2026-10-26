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
  tag(net.minecraft.tags.BiomeTags.IS_OVERWORLD).add(com.howierux.mortalexpansion.worldgen.ModBiomes.GRAVEYARD,com.howierux.mortalexpansion.worldgen.ModBiomes.SPIRITWOOD_FOREST);
  tag(net.minecraft.tags.BiomeTags.IS_FOREST).add(com.howierux.mortalexpansion.worldgen.ModBiomes.SPIRITWOOD_FOREST);
  tag(net.minecraft.tags.TagKey.create(Registries.BIOME,com.howierux.mortalexpansion.catalog.Catalog.id("has_structure/graveyard_cemetery"))).add(com.howierux.mortalexpansion.worldgen.ModBiomes.GRAVEYARD);
  tag(HauntBiomeTags.NATURAL_HAUNT);
  tag(HauntBiomeTags.SUPERNATURAL_HAUNT).add(com.howierux.mortalexpansion.worldgen.ModBiomes.SPIRITWOOD_FOREST);
  tag(HauntBiomeTags.DEAD_RISING_HAUNT).add(com.howierux.mortalexpansion.worldgen.ModBiomes.GRAVEYARD);
 }
}
