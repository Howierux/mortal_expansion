package com.howierux.mortalexpansion.haunt;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.*;

/** Loaded chunks are distributed across minute-long passes; vanilla blocks need no mixin. */
public final class WispLanternConversion {
    public static final float CONVERSION_CHANCE = 0.02F;
    private static final Map<ServerLevel, Set<ChunkPos>> LOADED = new WeakHashMap<>();
    private WispLanternConversion() {}
    public static boolean nighttime(ServerLevel level) {
        long time = Math.floorMod(level.getDayTime(), 24000L);
        return level.dimensionType().hasSkyLight() && time >= 13000 && time < 23000;
    }
    public static void load(ServerLevel level,LevelChunk chunk){LOADED.computeIfAbsent(level,unused->new HashSet<>()).add(chunk.getPos());}
    public static void unload(ServerLevel level,LevelChunk chunk){var loaded=LOADED.get(level);if(loaded!=null)loaded.remove(chunk.getPos());}
    public static void clear(ServerLevel level){LOADED.remove(level);}
    public static boolean convert(ServerLevel level, BlockPos pos) {
        if(!nighttime(level) || level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null
                || !level.getWorldBorder().isWithinBounds(pos)
                || HauntManager.getTier(level,pos).id()<HauntTier.SUPERNATURAL.id())return false;
        var state=level.getBlockState(pos);
        if(!state.is(Blocks.CARVED_PUMPKIN))return false;
        var converted=com.howierux.mortalexpansion.catalog.CatalogBlocks.WISP_O_LANTERN.defaultBlockState()
                .setValue(CarvedPumpkinBlock.FACING,state.getValue(CarvedPumpkinBlock.FACING));
        if(!level.setBlock(pos,converted,3))return false;
        HauntManager.addPoints(level,pos,-1);
        MortalGolemFormation.tryForm(level,pos);
        return true;
    }
    public static int scan(ServerLevel level, LevelChunk chunk) {
        if(level.getChunkSource().getChunkNow(chunk.getPos().x,chunk.getPos().z)!=chunk
                || !nighttime(level) || HauntManager.getTier(level,chunk.getPos()).id()<HauntTier.SUPERNATURAL.id())return 0;
        int converted=0;
        var sections=chunk.getSections();
        for(int sectionIndex=0;sectionIndex<sections.length;sectionIndex++) {
            var section=sections[sectionIndex];
            if(!section.maybeHas(state->state.is(Blocks.CARVED_PUMPKIN)))continue;
            int baseY=(level.getMinSection()+sectionIndex)*16;
            for(int y=0;y<16;y++)for(int z=0;z<16;z++)for(int x=0;x<16;x++) {
                if(section.getBlockState(x,y,z).is(Blocks.CARVED_PUMPKIN)
                        && level.random.nextFloat()<CONVERSION_CHANCE
                        && convert(level,new BlockPos(chunk.getPos().getMinBlockX()+x,baseY+y,chunk.getPos().getMinBlockZ()+z)))
                    converted++;
            }
        }
        return converted;
    }
    public static void tick(ServerLevel level) {
        if(!nighttime(level)||level.getGameTime()%20!=0)return;
        var chunks=LOADED.get(level);if(chunks==null)return;
        int phase=(int)((level.getGameTime()/20)%60);
        for(var pos:chunks) {
            if(Math.floorMod(pos.hashCode(),60)!=phase)continue;
            var chunk=level.getChunkSource().getChunkNow(pos.x,pos.z);
            if(chunk!=null)scan(level,chunk);
        }
    }
}
