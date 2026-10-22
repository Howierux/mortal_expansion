package com.howierux.mortalexpansion.fancifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import java.util.*;
@EventBusSubscriber(modid="mortal_expansion")
public final class FadingPistons {
    private record Snapshot(Map<BlockPos,Integer> destinations,int base){}
    private static final Map<Level,LinkedHashMap<BlockPos,Snapshot>> PENDING=new WeakHashMap<>();
    @SubscribeEvent(priority=EventPriority.LOWEST) public static void before(PistonEvent.Pre event){
        if(!(event.getLevel() instanceof Level level) || level.isClientSide)return;
        var target=new HashMap<BlockPos,Integer>();
        var queue=PENDING.computeIfAbsent(level,ignored->new LinkedHashMap<>());if(queue.size()>=64)queue.remove(queue.keySet().iterator().next());
        queue.put(event.getPos().immutable(),new Snapshot(target,FadingWorld.opacity(level,event.getPos())));
    }
    /** Use the actual native resolution after head removal, especially for sticky pulls. */
    public static void resolved(Level level,BlockPos piston,net.minecraft.world.level.block.piston.PistonStructureResolver resolver){
        if(level.isClientSide)return;var queue=PENDING.get(level);var snapshot=queue==null?null:queue.get(piston);if(snapshot==null)return;
        var destinations=new HashMap<BlockPos,Integer>();for(var source:resolver.getToPush())destinations.put(source.relative(resolver.getPushDirection()),FadingWorld.opacity(level,source));
        queue.put(piston.immutable(),new Snapshot(destinations,snapshot.base));
    }
    @SubscribeEvent public static void after(PistonEvent.Post event){
        if(!(event.getLevel() instanceof Level level) || level.isClientSide)return;
        var queue=PENDING.get(level);var snapshot=queue==null?null:queue.remove(event.getPos());if(snapshot==null)return;
        snapshot.destinations.forEach((pos,alpha)->{if(!level.getBlockState(pos).isAir())FadingWorld.set(level,pos,alpha);});
        FadingWorld.set(level,event.getPos(),snapshot.base);
        if(event.getPistonMoveType().isExtend)FadingWorld.set(level,event.getFaceOffsetPos(),snapshot.base);
    }
}
