package com.howierux.mortalexpansion.haunt;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

/** Loaded-chunk-only authoritative API. Reading a sensor/command never generates adjacent terrain. */
public final class HauntManager {
    private HauntManager(){}
    public static void initialize(ServerLevel level,LevelChunk chunk){
        var target=(AttachmentTarget)chunk;
        if(!target.hasAttached(FabricHauntData.CHUNK))target.setAttached(FabricHauntData.CHUNK,
                (NaturalHauntGenerator.supportsDimension(level)?NaturalHauntGenerator.determineInitialTier(level,chunk):HauntTier.VOID).midpointValue()*100);
    }
    private static LevelChunk loaded(ServerLevel level,ChunkPos pos){
        var chunk=level.getChunkSource().getChunkNow(pos.x,pos.z);
        if(chunk==null)throw new IllegalArgumentException("Haunt access requires a loaded chunk: "+pos);
        initialize(level,chunk);return chunk;
    }
    public static int getHundredths(ServerLevel level,ChunkPos pos){return ((AttachmentTarget)loaded(level,pos)).getAttachedOrThrow(FabricHauntData.CHUNK);}
    public static int getHundredths(ServerLevel level,BlockPos pos){return getHundredths(level,new ChunkPos(pos));}
    public static int getValue(ServerLevel level,ChunkPos pos){return getHundredths(level,pos)/100;}
    public static int getValue(ServerLevel level,BlockPos pos){return getValue(level,new ChunkPos(pos));}
    public static double getPreciseValue(ServerLevel level,BlockPos pos){return getHundredths(level,pos)/100.0;}
    public static HauntTier getTier(ServerLevel level,ChunkPos pos){return HauntTier.fromValue(getValue(level,pos));}
    public static HauntTier getTier(ServerLevel level,BlockPos pos){return getTier(level,new ChunkPos(pos));}
    public static int setValue(ServerLevel level,ChunkPos pos,int value){
        int clamped=HauntTier.clampValue(value);((AttachmentTarget)loaded(level,pos)).setAttached(FabricHauntData.CHUNK,clamped*100);return clamped;
    }
    public static int setValue(ServerLevel level,BlockPos pos,int value){return setValue(level,new ChunkPos(pos),value);}
    public static HauntTier setTier(ServerLevel level,ChunkPos pos,HauntTier tier){setValue(level,pos,tier.midpointValue());return tier;}
    public static HauntTier setTier(ServerLevel level,BlockPos pos,HauntTier tier){return setTier(level,new ChunkPos(pos),tier);}
    public static void addHundredths(ServerLevel level,ChunkPos pos,long amount){
        var chunk=loaded(level,pos);int current=((AttachmentTarget)chunk).getAttachedOrThrow(FabricHauntData.CHUNK);
        ((AttachmentTarget)chunk).setAttached(FabricHauntData.CHUNK,FractionalHaunt.add(current,amount));
    }
    public static void addHundredths(ServerLevel level,BlockPos pos,long amount){addHundredths(level,new ChunkPos(pos),amount);}
    public static int addPoints(ServerLevel level,ChunkPos pos,int amount){addHundredths(level,pos,(long)amount*100);return getValue(level,pos);}
    public static int addPoints(ServerLevel level,BlockPos pos,int amount){return addPoints(level,new ChunkPos(pos),amount);}
}
