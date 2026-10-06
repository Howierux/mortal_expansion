package com.howierux.mortalexpansion.item;

import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import java.util.*;

/** Bounded player mining through the normal permission/drop/durability pipeline. */
public final class AreaShovelMining {
    public static final int CONNECTED_LIMIT=64;
    private static final ThreadLocal<Boolean> BREAKING=ThreadLocal.withInitial(()->false);
    private static final Map<ServerLevel,List<Job>> QUEUED=new WeakHashMap<>();
    private static final class Job {
        final ServerPlayer player;final BlockPos origin;final Block block;final ItemStack tool;final Direction.Axis facing;
        boolean confirmed;
        Job(ServerPlayer p,BlockPos pos,Block block){player=p;origin=pos.immutable();this.block=block;tool=p.getMainHandItem();facing=p.getDirection().getAxis();confirmed=p.gameMode.isCreative();}
    }
    private AreaShovelMining(){}
    public static boolean areaTool(ItemStack stack){return stack.is(ModItems.BLOODSTONE_SHOVEL.get()) || stack.is(ModItems.SPECTRALITE_SHOVEL.get());}
    public static boolean allowed(ServerPlayer p,BlockPos pos,ItemStack tool){
        var level=p.serverLevel();
        if(!p.isAlive() || p.gameMode.getGameModeForPlayer()==GameType.SPECTATOR || tool.isEmpty() || !areaTool(tool) || !level.hasChunkAt(pos) || !level.getWorldBorder().isWithinBounds(pos)
                || pos.getY()<level.getMinBuildHeight() || pos.getY()>=level.getMaxBuildHeight() || !level.mayInteract(p,pos))return false;
        var state=level.getBlockState(pos);
        return state.is(BlockTags.MINEABLE_WITH_SHOVEL) && state.getDestroySpeed(level,pos)>=0
                && (!state.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(state));
    }
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void mined(BlockEvent.BreakEvent event){
        if(BREAKING.get() || !(event.getPlayer() instanceof ServerPlayer p) || p.isShiftKeyDown()
                || !allowed(p,event.getPos(),p.getMainHandItem()))return;
        QUEUED.computeIfAbsent(p.serverLevel(),unused->new ArrayList<>()).add(new Job(p,event.getPos(),event.getState().getBlock()));
    }
    @SubscribeEvent(priority=EventPriority.LOWEST,receiveCanceled=true) public static void drops(BlockDropsEvent event){
        if(BREAKING.get() || !(event.getBreaker() instanceof ServerPlayer p))return;
        var jobs=QUEUED.get(event.getLevel());if(jobs==null)return;
        for(var job:jobs)if(job.player==p && job.origin.equals(event.getPos()) && event.getTool().getItem()==job.tool.getItem())job.confirmed=true;
    }
    @SubscribeEvent public static void unload(net.neoforged.neoforge.event.level.LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel level)QUEUED.remove(level);}
    @SubscribeEvent public static void tick(LevelTickEvent.Post event){
        if(!(event.getLevel() instanceof ServerLevel level))return;
        var jobs=QUEUED.remove(level);if(jobs==null)return;
        for(var job:jobs){var p=job.player;
            if(!job.confirmed || !p.isAlive() || p.serverLevel()!=level || p.gameMode.getGameModeForPlayer()==GameType.SPECTATOR
                    || p.isShiftKeyDown() || p.getMainHandItem()!=job.tool || job.tool.isEmpty()
                    || !level.hasChunkAt(job.origin) || level.getBlockState(job.origin).is(job.block)
                    || job.origin.getCenter().distanceToSqr(p.getEyePosition())>Math.pow(p.blockInteractionRange()+3,2))continue;
            plane(p,job.origin,job.facing,job.tool);
        }
    }
    private static boolean destroy(ServerPlayer p,BlockPos pos,ItemStack original){
        if(p.getMainHandItem()!=original || !allowed(p,pos,original))return false;
        var before=p.serverLevel().getBlockState(pos);
        if(!p.gameMode.destroyBlock(pos))return false;
        return !p.serverLevel().getBlockState(pos).is(before.getBlock());
    }
    public static void plane(ServerPlayer p,BlockPos origin,Direction.Axis facing,ItemStack tool){
        boolean prior=BREAKING.get();BREAKING.set(true);
        try{for(int y=-1;y<=1;y++)for(int side=-1;side<=1;side++){
            if(y==0 && side==0)continue;
            var pos=facing==Direction.Axis.X?origin.offset(0,y,side):origin.offset(side,y,0);
            destroy(p,pos,tool);
        }}finally{BREAKING.set(prior);}
    }
    public static int connected(ServerPlayer p,BlockPos origin){
        var tool=p.getMainHandItem();if(!tool.is(ModItems.SPECTRALITE_SHOVEL.get()) || !allowed(p,origin,tool))return 0;
        var queue=new ArrayDeque<BlockPos>();var seen=new HashSet<BlockPos>();queue.add(origin.immutable());seen.add(origin.immutable());int count=0;
        boolean prior=BREAKING.get();BREAKING.set(true);
        try{while(!queue.isEmpty() && count<CONNECTED_LIMIT && p.getMainHandItem()==tool && !tool.isEmpty()){
            var pos=queue.removeFirst();if(!destroy(p,pos,tool))continue;count++;
            // A protected/unmineable cell is not a bridge to further cells.
            for(var direction:Direction.values()){
                var next=pos.relative(direction);if(seen.add(next) && allowed(p,next,tool))queue.addLast(next);
            }
        }}finally{BREAKING.set(prior);}
        return count;
    }
}
