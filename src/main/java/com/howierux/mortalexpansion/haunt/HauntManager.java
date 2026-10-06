package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

/** Authoritative API for fine-grained chunk haunt state. */
public final class HauntManager {
    private HauntManager() {}

    public static HauntTier getTier(ServerLevel level, BlockPos pos) {
        return getTier(level, new ChunkPos(pos));
    }

    public static HauntTier getTier(ServerLevel level, ChunkPos pos) {
        return HauntTier.fromValue(getValue(level, pos));
    }

    public static int getValue(ServerLevel level, BlockPos pos) {
        return getValue(level, new ChunkPos(pos));
    }

    public static int getValue(ServerLevel level, ChunkPos pos) {
        LevelChunk chunk = level.getChunk(pos.x, pos.z);
        HauntTier tier = HauntTier.fromId(chunk.getData(ModAttachments.HAUNT_TIER));
        int progress = chunk.getData(ModAttachments.HAUNT_PROGRESS);

        if (progress < 0) {
            progress = tier == HauntTier.VOID ? 0 : HauntTier.DEFAULT_PROGRESS;
            chunk.setData(ModAttachments.HAUNT_PROGRESS, progress);
        }

        return tier.id() * HauntTier.POINTS_PER_TIER + progress;
    }

    public static HauntTier setTier(ServerLevel level, BlockPos pos, HauntTier tier) {
        return setTier(level, new ChunkPos(pos), tier);
    }

    public static HauntTier setTier(ServerLevel level, ChunkPos pos, HauntTier tier) {
        setValue(level, pos, tier.midpointValue());
        return tier;
    }

    public static int setValue(ServerLevel level, BlockPos pos, int value) {
        return setValue(level, new ChunkPos(pos), value);
    }

    public static int setValue(ServerLevel level, ChunkPos pos, int value) {
        level.getChunk(pos.x,pos.z).setData(ModAttachments.HAUNT_FRACTION,0);
        return storeWholeValue(level,pos,value);
    }
    private static int storeWholeValue(ServerLevel level,ChunkPos pos,int value) {
        int clamped = HauntTier.clampValue(value);
        HauntTier tier = HauntTier.fromValue(clamped);
        LevelChunk chunk = level.getChunk(pos.x, pos.z);
        HauntTier previousTier = HauntTier.fromId(chunk.getData(ModAttachments.HAUNT_TIER));

        chunk.setData(ModAttachments.HAUNT_TIER, tier.id());
        chunk.setData(
                ModAttachments.HAUNT_PROGRESS,
                HauntTier.progressFromValue(clamped)
        );
        if (previousTier != tier) {
            com.howierux.mortalexpansion.blockentity.HauntFurnaceBlockEntity.onChunkTierChanged(chunk, tier);
        }
        return clamped;
    }

    public static int addPoints(ServerLevel level, BlockPos pos, int amount) {
        return addPoints(level, new ChunkPos(pos), amount);
    }

    public static int addPoints(ServerLevel level, ChunkPos pos, int amount) {
        addHundredths(level,pos,(long)amount*FractionalHaunt.UNITS_PER_POINT);
        return getValue(level,pos);
    }

    public static int getHundredths(ServerLevel level,BlockPos pos){return getHundredths(level,new ChunkPos(pos));}
    public static int getHundredths(ServerLevel level,ChunkPos pos){
        int units=getValue(level,pos)*FractionalHaunt.UNITS_PER_POINT+level.getChunk(pos.x,pos.z).getData(ModAttachments.HAUNT_FRACTION);
        return FractionalHaunt.add(units,0);
    }
    public static double getPreciseValue(ServerLevel level,BlockPos pos){return getHundredths(level,pos)/(double)FractionalHaunt.UNITS_PER_POINT;}
    public static void addHundredths(ServerLevel level,BlockPos pos,long amount){addHundredths(level,new ChunkPos(pos),amount);}
    public static void addHundredths(ServerLevel level,ChunkPos pos,long amount){
        int result=FractionalHaunt.add(getHundredths(level,pos),amount);
        storeWholeValue(level,pos,result/FractionalHaunt.UNITS_PER_POINT);
        level.getChunk(pos.x,pos.z).setData(ModAttachments.HAUNT_FRACTION,result%FractionalHaunt.UNITS_PER_POINT);
    }

    /** Whole-tier adjustment retained for systems that truly mean a full tier. */
    public static HauntTier addLevels(ServerLevel level, BlockPos pos, int amount) {
        addPoints(level, pos, amount * HauntTier.POINTS_PER_TIER);
        return getTier(level, pos);
    }

    public static int raisePoint(ServerLevel level, BlockPos pos) {
        return addPoints(level, pos, 1);
    }

    public static int lowerPoint(ServerLevel level, BlockPos pos) {
        return addPoints(level, pos, -1);
    }

    public static boolean isAtLeast(ServerLevel level, BlockPos pos, HauntTier minimum) {
        return getValue(level, pos) >= minimum.minimumValue();
    }
}
