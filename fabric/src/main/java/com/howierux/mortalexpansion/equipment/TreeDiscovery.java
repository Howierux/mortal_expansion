package com.howierux.mortalexpansion.equipment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;
/** Bounded, loaded-only tree discovery; execution goes through EquipmentMining's player break pipeline. */
public final class TreeDiscovery {
 public static final int MAX_LOGS=512,HORIZONTAL_RADIUS=16,VERTICAL_RADIUS=64;
    private static boolean family(BlockState state,Block root,List<TagKey<Block>> families){
        return state.is(BlockTags.LOGS) && (state.is(root) || families.stream().anyMatch(state::is));
    }
    private static boolean foliage(ServerLevel level,BlockPos log){
        for(var pos:BlockPos.betweenClosed(log.offset(-2,-2,-2),log.offset(2,2,2))){
            if(!level.hasChunkAt(pos))continue;var state=level.getBlockState(pos);
            if(state.is(BlockTags.WART_BLOCKS))return true;
            if(state.is(BlockTags.LEAVES) && (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.PERSISTENT)
                    || !state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.PERSISTENT)))return true;
        }return false;
    }
    public static Set<BlockPos> discover(ServerLevel level,BlockPos origin){
        if(!level.hasChunkAt(origin))return Set.of();var root=level.getBlockState(origin);if(!root.is(BlockTags.LOGS))return Set.of();
        var families=List.of(BlockTags.OAK_LOGS,BlockTags.SPRUCE_LOGS,BlockTags.BIRCH_LOGS,BlockTags.JUNGLE_LOGS,BlockTags.ACACIA_LOGS,BlockTags.DARK_OAK_LOGS,BlockTags.MANGROVE_LOGS,BlockTags.CHERRY_LOGS,BlockTags.CRIMSON_STEMS,BlockTags.WARPED_STEMS).stream().filter(root::is).toList();
        var queue=new ArrayDeque<BlockPos>();var seen=new HashSet<BlockPos>();var logs=new LinkedHashSet<BlockPos>();queue.add(origin.immutable());seen.add(origin.immutable());boolean canopy=false;
        while(!queue.isEmpty()){
            var pos=queue.removeFirst();if(!level.hasChunkAt(pos) || !family(level.getBlockState(pos),root.getBlock(),families))continue;
            if(logs.size()>=MAX_LOGS)return Set.of();logs.add(pos);if(!canopy)canopy=foliage(level,pos);
            for(int dx=-1;dx<=1;dx++)for(int dy=-1;dy<=1;dy++)for(int dz=-1;dz<=1;dz++){
                if(dx==0 && dy==0 && dz==0)continue;var next=pos.offset(dx,dy,dz);
                if(Math.abs(next.getX()-origin.getX())>HORIZONTAL_RADIUS || Math.abs(next.getZ()-origin.getZ())>HORIZONTAL_RADIUS
                        || Math.abs(next.getY()-origin.getY())>VERTICAL_RADIUS)continue;
                if(seen.add(next) && level.hasChunkAt(next) && family(level.getBlockState(next),root.getBlock(),families))queue.addLast(next);
            }
        }
        return canopy?Set.copyOf(logs):Set.of();
    }
}
