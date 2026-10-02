package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/** Persistent, per-ward ticking tickets for the same 3x3 area the Ward cleanses. */
public final class PurificationWardChunkLoader {
    private PurificationWardChunkLoader() {}

    private static final Map<ServerLevel, Set<BlockPos>> PENDING = new WeakHashMap<>();
    private static final TicketController CONTROLLER = new TicketController(
            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purification_wards"),
            (level, helper) -> {
                // NeoForge calls this before restoring persisted tickets. The owner chunk
                // is intentionally read here to discard tickets whose Ward no longer exists.
                for (BlockPos owner : helper.getBlockTickets().keySet()) {
                    if (!level.getBlockState(owner).is(ModBlocks.PURIFICATION_WARD.get())) {
                        helper.removeAllTickets(owner);
                    }
                }
            });

    public static void register(IEventBus modBus) {
        modBus.addListener((RegisterTicketControllersEvent event) -> event.register(CONTROLLER));
        NeoForge.EVENT_BUS.register(PurificationWardChunkLoader.class);
    }

    public static void requestLoad(ServerLevel level, BlockPos pos) {
        PENDING.computeIfAbsent(level, unused -> new HashSet<>()).add(pos.immutable());
    }

    public static void release(ServerLevel level, BlockPos pos) {
        Set<BlockPos> pending = PENDING.get(level);
        if (pending != null) pending.remove(pos);
        updateTickets(level, pos, false);
    }

    private static void updateTickets(ServerLevel level, BlockPos owner, boolean add) {
        ChunkPos center = new ChunkPos(owner);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                CONTROLLER.forceChunk(level, owner, center.x + dx, center.z + dz, add, true);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        // Take the batch out before loading: newly loaded neighbor Wards queue for
        // the next tick rather than mutating the collection we're traversing.
        Set<BlockPos> pending = PENDING.remove(level);
        if (pending == null) return;
        for (BlockPos pos : pending) {
            var chunk = level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
            if (chunk != null && chunk.getBlockState(pos).is(ModBlocks.PURIFICATION_WARD.get())) {
                updateTickets(level, pos, true);
            }
        }
    }
}
