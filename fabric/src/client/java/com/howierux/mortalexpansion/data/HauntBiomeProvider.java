package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.haunt.HauntBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import java.util.concurrent.CompletableFuture;
/** Optional references allow Haunt foundation to load before the custom biome registry is ported. */
public final class HauntBiomeProvider extends FabricTagProvider<Biome> {
 public HauntBiomeProvider(FabricDataOutput output,CompletableFuture<HolderLookup.Provider> lookup){super(output,Registries.BIOME,lookup);}
 @Override protected void addTags(HolderLookup.Provider lookup){
  tag(HauntBiomeTags.NATURAL_HAUNT);
  tag(HauntBiomeTags.SUPERNATURAL_HAUNT).addOptional(ResourceLocation.fromNamespaceAndPath("mortal_expansion","spiritwood_forest"));
  tag(HauntBiomeTags.DEAD_RISING_HAUNT).addOptional(ResourceLocation.fromNamespaceAndPath("mortal_expansion","graveyard"));
 }
}
