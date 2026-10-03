package com.howierux.mortalexpansion.spiritwell;

import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.PistonEvent;

/** Local lookup only: no chunk tickets, global scans, or per-tick maintenance. */
public final class SpiritwellProtection {
    public static final int RADIUS = 2;
    private SpiritwellProtection() {}

    public static boolean isProtected(LevelAccessor level, BlockPos target) {
        for (BlockPos candidate : BlockPos.betweenClosed(target.offset(-RADIUS, -RADIUS, -RADIUS),
                target.offset(RADIUS, RADIUS, RADIUS))) {
            if (candidate.distSqr(target) <= RADIUS * RADIUS
                    && level.hasChunk(candidate.getX() >> 4, candidate.getZ() >> 4)
                    && level.getBlockState(candidate).is(ModBlocks.SPIRITWELL.get())) return true;
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (event instanceof BlockEvent.EntityMultiPlaceEvent multi) {
            for (var snapshot : multi.getReplacedBlockSnapshots()) {
                if (isProtected(event.getLevel(), snapshot.getPos())
                        || snapshot.getState().is(ModBlocks.SPIRITWELL.get())) {
                    event.setCanceled(true);
                    return;
                }
            }
        } else if (isProtected(event.getLevel(), event.getPos())
                || event.getBlockSnapshot().getState().is(ModBlocks.SPIRITWELL.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onFluidSolidification(BlockEvent.FluidPlaceBlockEvent event) {
        if (isProtected(event.getLevel(), event.getPos())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getState().is(ModBlocks.SPIRITWELL.get())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPiston(PistonEvent.Pre event) {
        boolean extending = event.getPistonMoveType() == PistonEvent.PistonMoveType.EXTEND;
        if (extending && isProtected(event.getLevel(), event.getFaceOffsetPos())) {
            event.setCanceled(true);
            return;
        }
        var resolver = event.getStructureHelper();
        if (resolver == null || !resolver.resolve()) return;
        Direction movement = extending ? event.getDirection() : event.getDirection().getOpposite();
        for (BlockPos from : resolver.getToPush()) {
            if (isProtected(event.getLevel(), from.relative(movement))) {
                event.setCanceled(true);
                return;
            }
        }
    }
}
