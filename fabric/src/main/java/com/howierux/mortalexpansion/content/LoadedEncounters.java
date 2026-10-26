package com.howierux.mortalexpansion.content;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.*;
public final class LoadedEncounters {
 public static StructureStart find(ServerLevel level,BlockPos pos,ResourceKey<Structure> key,int radius,boolean inside){var type=level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(key).value();for(int x=(pos.getX()-radius)>>4;x<=(pos.getX()+radius)>>4;x++)for(int z=(pos.getZ()-radius)>>4;z<=(pos.getZ()+radius)>>4;z++){var chunk=level.getChunkSource().getChunkNow(x,z);if(chunk==null)continue;for(var start:chunk.getAllStarts().values())if(start.isValid()&&start.getStructure()==type){var box=start.getBoundingBox();if(inside?box.isInside(pos):pos.distSqr(new BlockPos((box.minX()+box.maxX())/2,pos.getY(),(box.minZ()+box.maxZ())/2))<=radius*radius)return start;}}return null;}
}
