package com.howierux.mortalexpansion.gametest;
import net.minecraft.core.*;import net.minecraft.core.registries.Registries;import net.minecraft.server.level.ServerLevel;import net.minecraft.resources.ResourceKey;import net.minecraft.world.level.biome.Biome;import net.minecraft.world.level.chunk.LevelChunk;import java.util.*;
/** BiomeZoomer samples neighboring quart cells. Isolate and restore a full local test neighborhood. */
final class BiomeTestArea implements AutoCloseable {
 private final ServerLevel level;private final Map<LevelChunk,Map<BlockPos,Holder<Biome>>> saved=new HashMap<>();
 static BiomeTestArea force(ServerLevel level,BlockPos center,ResourceKey<Biome> key){return new BiomeTestArea(level,center,key);}
 private BiomeTestArea(ServerLevel level,BlockPos center,ResourceKey<Biome> key){this.level=level;var value=level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(key);for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){var chunk=level.getChunk((center.getX()>>4)+dx,(center.getZ()>>4)+dz);var cells=new HashMap<BlockPos,Holder<Biome>>();for(int x=chunk.getPos().x*4;x<chunk.getPos().x*4+4;x++)for(int z=chunk.getPos().z*4;z<chunk.getPos().z*4+4;z++)for(int y=level.getMinBuildHeight()>>2;y<level.getMaxBuildHeight()>>2;y++)cells.put(new BlockPos(x,y,z),chunk.getNoiseBiome(x,y,z));saved.put(chunk,cells);chunk.fillBiomesFromNoise((x,y,z,c)->value,level.getChunkSource().randomState().sampler());}}
 public void close(){saved.forEach((chunk,cells)->chunk.fillBiomesFromNoise((x,y,z,c)->cells.get(new BlockPos(x,y,z)),level.getChunkSource().randomState().sampler()));}
}
