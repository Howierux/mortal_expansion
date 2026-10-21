package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.worldgen.ModBiomes;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.*;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.*;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.List;
/** Native spawn-list codecs retain vanilla biome variation and stable modifier IDs. */
public final class ModSpawnModifiers {
    private ModSpawnModifiers(){}
    private static MobSpawnSettings.SpawnerData spawn(EntityType<?> type,int weight,int min,int max){return new MobSpawnSettings.SpawnerData(type,weight,min,max);}
    private static void add(BootstrapContext<BiomeModifier> context,String id,HolderSet<net.minecraft.world.level.biome.Biome> biomes,List<MobSpawnSettings.SpawnerData> entries){
        context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,id)),
                new BiomeModifiers.AddSpawnsBiomeModifier(biomes,entries));
    }
    public static void bootstrap(BootstrapContext<BiomeModifier> context){
        var biomes=context.lookup(Registries.BIOME);
        add(context,"add_haunt_spawns",biomes.getOrThrow(BiomeTags.IS_OVERWORLD),List.of(
                spawn(ModEntities.SPIRIT.get(),24,1,2),spawn(ModEntities.GHOST.get(),36,1,2),
                spawn(ModEntities.PHANTOM.get(),14,1,1),spawn(ModEntities.HAUNT.get(),8,1,1),
                spawn(EntityType.WITHER_SKELETON,5,1,2),spawn(EntityType.GHAST,2,1,1)));
        add(context,"add_demon_spawns",biomes.getOrThrow(net.minecraft.tags.TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"demon_spawn_biomes"))),List.of(spawn(ModEntities.DEMON.get(),12,1,1)));
        add(context,"add_elemental_spawns",HolderSet.direct(biomes.getOrThrow(ModBiomes.SPIRITWOOD_FOREST)),List.of(spawn(ModEntities.ELEMENTAL.get(),12,1,2)));
    }
}
