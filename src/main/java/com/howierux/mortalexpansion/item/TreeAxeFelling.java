package com.howierux.mortalexpansion.item;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.level.*;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import java.util.*;

/** Loaded, bounded tree discovery followed by ordinary permission-aware player harvesting. */
public final class TreeAxeFelling {
    public static final int MAX_LOGS=512,HORIZONTAL_RADIUS=16,VERTICAL_RADIUS=64;
    private static final ThreadLocal<Boolean> FELLING=ThreadLocal.withInitial(()->false);
    private static final Map<ServerLevel,List<Job>> QUEUED=new WeakHashMap<>();
    private static final class Job {
        final ServerPlayer player;final BlockPos origin;final Block block;final ItemStack tool;final Set<BlockPos> logs;
        boolean confirmed;
        Job(ServerPlayer p,BlockPos pos,BlockState state,Set<BlockPos> logs){player=p;origin=pos.immutable();block=state.getBlock();tool=p.getMainHandItem();this.logs=logs;confirmed=p.gameMode.isCreative();}
    }
    private TreeAxeFelling(){}
    public static boolean treeTool(ItemStack stack){return stack.is(ModItems.BLOODSTONE_AXE.get()) || stack.is(ModItems.SPECTRALITE_AXE.get());}
    public static boolean allowed(ServerPlayer p,BlockPos pos,ItemStack tool){
        var level=p.serverLevel();if(!p.isAlive() || p.gameMode.getGameModeForPlayer()==net.minecraft.world.level.GameType.SPECTATOR
                || tool.isEmpty() || !treeTool(tool) || !level.hasChunkAt(pos) || !level.getWorldBorder().isWithinBounds(pos)
                || pos.getY()<level.getMinBuildHeight() || pos.getY()>=level.getMaxBuildHeight() || !level.mayInteract(p,pos))return false;
        var state=level.getBlockState(pos);return state.is(BlockTags.LOGS) && state.getDestroySpeed(level,pos)>=0
                && (!state.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(state));
    }
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
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void mined(BlockEvent.BreakEvent event){
        if(FELLING.get() || !(event.getPlayer() instanceof ServerPlayer p) || p.isShiftKeyDown() || !allowed(p,event.getPos(),p.getMainHandItem()))return;
        var logs=discover(p.serverLevel(),event.getPos());if(logs.size()<2)return;
        QUEUED.computeIfAbsent(p.serverLevel(),unused->new ArrayList<>()).add(new Job(p,event.getPos(),event.getState(),logs));
    }
    @SubscribeEvent(priority=EventPriority.LOWEST,receiveCanceled=true) public static void drops(BlockDropsEvent event){
        if(FELLING.get() || !(event.getBreaker() instanceof ServerPlayer p))return;var jobs=QUEUED.get(event.getLevel());if(jobs==null)return;
        for(var job:jobs)if(job.player==p && job.origin.equals(event.getPos()) && event.getTool().getItem()==job.tool.getItem())job.confirmed=true;
    }
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel level)QUEUED.remove(level);}
    @SubscribeEvent public static void tick(LevelTickEvent.Post event){
        if(!(event.getLevel() instanceof ServerLevel level))return;var jobs=QUEUED.remove(level);if(jobs==null)return;
        for(var job:jobs){var p=job.player;
            if(!job.confirmed || !p.isAlive() || p.serverLevel()!=level || p.isShiftKeyDown() || p.getMainHandItem()!=job.tool || job.tool.isEmpty()
                    || !level.hasChunkAt(job.origin) || level.getBlockState(job.origin).is(job.block)
                    || job.origin.getCenter().distanceToSqr(p.getEyePosition())>Math.pow(p.blockInteractionRange()+3,2))continue;
            fell(p,job.origin,job.logs,job.tool);
        }
    }
    public static int fell(ServerPlayer p,BlockPos origin,Set<BlockPos> logs,ItemStack tool){
        var queue=new ArrayDeque<BlockPos>();var seen=new HashSet<BlockPos>();queue.add(origin);seen.add(origin);int count=0;
        boolean prior=FELLING.get();FELLING.set(true);
        try{while(!queue.isEmpty() && count<MAX_LOGS && p.getMainHandItem()==tool && !tool.isEmpty()){
            var pos=queue.removeFirst();
            if(!pos.equals(origin)){
                if(!allowed(p,pos,tool))continue;var before=p.serverLevel().getBlockState(pos);
                if(!p.gameMode.destroyBlock(pos) || p.serverLevel().getBlockState(pos).is(before.getBlock()))continue;count++;
            }
            for(int dx=-1;dx<=1;dx++)for(int dy=-1;dy<=1;dy++)for(int dz=-1;dz<=1;dz++){
                if(dx==0 && dy==0 && dz==0)continue;var next=pos.offset(dx,dy,dz);
                if(logs.contains(next) && seen.add(next))queue.addLast(next);
            }
        }}finally{FELLING.set(prior);}return count;
    }
}
