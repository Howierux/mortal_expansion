package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import java.util.*;

/** Loaded-only salt index. Collision queries do no block reads, scans or chunk loading. */
public final class HolySaltBarrierTracker {
    private HolySaltBarrierTracker() {}
    private static final Map<Level,Index> LEVELS=new java.util.concurrent.ConcurrentHashMap<>();
    private static final class Index {
        final Map<Long,Set<BlockPos>> chunks=new HashMap<>();
        final Map<Long,Integer> columns=new HashMap<>();
        void add(BlockPos pos){
            if(chunks.computeIfAbsent(chunkKey(pos),k->new HashSet<>()).add(pos.immutable()))
                columns.merge(columnKey(pos.getX(),pos.getZ()),1,Integer::sum);
        }
        void decrement(BlockPos pos){columns.computeIfPresent(columnKey(pos.getX(),pos.getZ()),(k,count)->count==1?null:count-1);}
        void remove(BlockPos pos){var positions=chunks.get(chunkKey(pos));if(positions!=null && positions.remove(pos))decrement(pos);}
        void forget(long chunk){var positions=chunks.remove(chunk);if(positions!=null)positions.forEach(this::decrement);}
    }
    private static long chunkKey(BlockPos pos){return ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4);}
    private static long columnKey(int x,int z){return ((long)x<<32)|(z&0xffffffffL);}
    public static void markPresent(Level level,BlockPos pos){LEVELS.computeIfAbsent(level,k->new Index()).add(pos);}
    /** Removes this salt position only: another salt height in the column still blocks. */
    public static void invalidate(Level level,BlockPos pos){var index=LEVELS.get(level);if(index!=null)index.remove(pos);}
    /** Client packet writes skip onPlace/onRemove; the existing transition hook calls this too. */
    public static void onBlockChanged(Level level,BlockPos pos,BlockState before,BlockState after){
        if(after.is(CatalogBlocks.HOLY_SALT))markPresent(level,pos);
        else if(before.is(CatalogBlocks.HOLY_SALT))invalidate(level,pos);
    }
    public static boolean hasSaltColumn(Level level,int x,int z){
        var index=LEVELS.get(level);return index!=null && index.columns.containsKey(columnKey(x,z));
    }
    /** Conservative spatial filter: only loaded chunks intersecting the nearby square. */
    public static boolean hasSaltNear(Level level,BlockPos pos,int radius){
        var index=LEVELS.get(level);if(index==null || index.columns.isEmpty())return false;
        for(int x=(pos.getX()-radius)>>4;x<=(pos.getX()+radius)>>4;x++)
            for(int z=(pos.getZ()-radius)>>4;z<=(pos.getZ()+radius)>>4;z++){
                var positions=index.chunks.get(ChunkPos.asLong(x,z));if(positions!=null && !positions.isEmpty())return true;
            }
        return false;
    }
    public static void load(Level level,LevelChunk chunk){
        var index=LEVELS.computeIfAbsent(level,k->new Index());
        index.forget(chunk.getPos().toLong());
        index.chunks.put(chunk.getPos().toLong(),new HashSet<>());
        var sections=chunk.getSections();int baseX=chunk.getPos().getMinBlockX(),baseZ=chunk.getPos().getMinBlockZ();
        // Load may fire before FULL; inspect the supplied sections, never query the level.
        for(int i=0;i<sections.length;i++){
            var section=sections[i];if(!section.getStates().maybeHas(s->s.is(CatalogBlocks.HOLY_SALT)))continue;
            int baseY=chunk.getMinBuildHeight()+i*16;
            for(int y=0;y<16;y++)for(int z=0;z<16;z++)for(int x=0;x<16;x++)
                if(section.getBlockState(x,y,z).is(CatalogBlocks.HOLY_SALT))index.add(new BlockPos(baseX+x,baseY+y,baseZ+z));
        }
    }
    public static void unload(Level level,LevelChunk chunk){var index=LEVELS.get(level);if(index!=null){index.forget(chunk.getPos().toLong());}}
    public static void clear(Level level){LEVELS.remove(level);}
}
