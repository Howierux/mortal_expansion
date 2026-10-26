package com.howierux.mortalexpansion.data;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import java.util.concurrent.CompletableFuture;
public final class TerrainProvider extends FabricDynamicRegistryProvider {
 public TerrainProvider(FabricDataOutput output,CompletableFuture<HolderLookup.Provider> lookup){super(output,lookup);}
 @Override protected void configure(HolderLookup.Provider lookup,Entries entries){entries.addAll(lookup.lookupOrThrow(Registries.BIOME));entries.addAll(lookup.lookupOrThrow(Registries.CONFIGURED_FEATURE));entries.addAll(lookup.lookupOrThrow(Registries.PLACED_FEATURE));entries.addAll(lookup.lookupOrThrow(Registries.STRUCTURE));entries.addAll(lookup.lookupOrThrow(Registries.STRUCTURE_SET));}
 @Override public String getName(){return "Mortal terrain registries";}
}
