package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
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
    @SubscribeEvent public static void load(ChunkEvent.Load event) {
        if(event.getLevel() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk)
            LOADED.computeIfAbsent(level, ignored -> new HashSet<>()).add(chunk.getPos());
    }
    @SubscribeEvent public static void unload(ChunkEvent.Unload event) {
        if(event.getLevel() instanceof ServerLevel level) {
            var chunks=LOADED.get(level);
            if(chunks!=null) {chunks.remove(event.getChunk().getPos());if(chunks.isEmpty())LOADED.remove(level);}
        }
    }
    public static boolean convert(ServerLevel level, BlockPos pos) {
        if(!nighttime(level) || level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null
                || !level.getWorldBorder().isWithinBounds(pos)
                || HauntManager.getTier(level,pos).id()<HauntTier.SUPERNATURAL.id())return false;
        var state=level.getBlockState(pos);
        if(!state.is(Blocks.CARVED_PUMPKIN))return false;
        var converted=ModBlocks.WISP_O_LANTERN.get().defaultBlockState()
                .setValue(CarvedPumpkinBlock.FACING,state.getValue(CarvedPumpkinBlock.FACING));
        if(!level.setBlock(pos,converted,3))return false;
        HauntManager.addPoints(level,pos,-1);
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
    @SubscribeEvent public static void tick(LevelTickEvent.Post event) {
        if(!(event.getLevel() instanceof ServerLevel level) || !nighttime(level) || level.getGameTime()%20!=0)return;
        var chunks=LOADED.get(level);if(chunks==null)return;
        int phase=(int)((level.getGameTime()/20)%60);
        for(var pos:chunks) {
            if(Math.floorMod(pos.hashCode(),60)!=phase)continue;
            var chunk=level.getChunkSource().getChunkNow(pos.x,pos.z);
            if(chunk!=null)scan(level,chunk);
        }
    }
}
