package com.howierux.mortalexpansion.fancifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.*;
public final class FadingPistons {
    private record Snapshot(Map<BlockPos,Integer> destinations,int base){}
    private static final Map<Level,LinkedHashMap<BlockPos,Snapshot>> PENDING=new WeakHashMap<>();
    public static void before(Level level,BlockPos pos){if(level.isClientSide)return;var queue=PENDING.computeIfAbsent(level,ignored->new LinkedHashMap<>());if(queue.size()>=64)queue.remove(queue.keySet().iterator().next());queue.put(pos.immutable(),new Snapshot(new HashMap<>(),FadingWorld.opacity(level,pos)));}
    /** Use the actual native resolution after head removal, especially for sticky pulls. */
    public static void resolved(Level level,BlockPos piston,net.minecraft.world.level.block.piston.PistonStructureResolver resolver){
        if(level.isClientSide)return;var queue=PENDING.get(level);var snapshot=queue==null?null:queue.get(piston);if(snapshot==null)return;
        var destinations=new HashMap<BlockPos,Integer>();for(var source:resolver.getToPush())destinations.put(source.relative(resolver.getPushDirection()),FadingWorld.opacity(level,source));
        queue.put(piston.immutable(),new Snapshot(destinations,snapshot.base));
    }
    public static void after(Level level,BlockPos pos,net.minecraft.core.Direction direction,boolean extending){
        if(level.isClientSide)return;
        var queue=PENDING.get(level);var snapshot=queue==null?null:queue.remove(pos);if(snapshot==null)return;
        snapshot.destinations.forEach((target,alpha)->{if(!level.getBlockState(target).isAir())FadingWorld.set(level,target,alpha);});
        FadingWorld.set(level,pos,snapshot.base);
        if(extending)FadingWorld.set(level,pos.relative(direction),snapshot.base);
    }
}
