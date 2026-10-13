package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.blockentity.DreamcatcherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import java.util.*;

/** Server-only index of loaded decorations. It never keeps chunks loaded or persists cache entries. */
public final class Dreamcatchers {
    private static final Map<ServerLevel, Map<BlockPos, UUID>> LOADED = new WeakHashMap<>();
    private static final Set<ServerPlayer> PREVENTED = Collections.newSetFromMap(new WeakHashMap<>());
    private Dreamcatchers() {}

    public static void add(ServerLevel level, BlockPos pos, UUID owner) {
        if (owner != null) LOADED.computeIfAbsent(level, ignored -> new HashMap<>()).put(pos.immutable(), owner);
    }
    public static void remove(ServerLevel level, BlockPos pos) {
        var entries = LOADED.get(level);
        if (entries != null) {
            entries.remove(pos);
            if (entries.isEmpty()) LOADED.remove(level);
        }
    }
    public static boolean active(ServerPlayer player) { return protectionPercent(player) > 0; }
    public static int protectionPercent(ServerPlayer player) {
        int protection = 0;
        for (ServerLevel level : player.getServer().getAllLevels()) {
            var entries = LOADED.get(level);
            if (entries == null) continue;
            for (var entry : entries.entrySet()) {
                if (!player.getUUID().equals(entry.getValue())
                        || level.getChunkSource().getChunkNow(entry.getKey().getX() >> 4, entry.getKey().getZ() >> 4) == null) continue;
                if (level.getBlockEntity(entry.getKey()) instanceof DreamcatcherBlockEntity catcher
                        && player.getUUID().equals(catcher.owner()) && !catcher.isRemoved()) {
                    if(catcher.getBlockState().is(com.howierux.mortalexpansion.registry.ModBlocks.PURIFIED_DREAMCATCHER.get())) return 100;
                    protection = 50;
                }
            }
        }
        return protection;
    }
    /** Strongest loaded decoration wins; no stacking. Regular protection uses one coin flip per selected event, regardless of decoration count. A caught event counts as handled. */
    public static boolean prevent(ServerPlayer player) {
        PREVENTED.remove(player);
        int protection = protectionPercent(player);
        if (protection == 0 || protection == 50 && !player.getRandom().nextBoolean()) return false;
        PREVENTED.add(player);
        player.displayClientMessage(Component.translatable("message.mortal_expansion.dreamcatcher.caught"), true);
        return true;
    }
    public static boolean consumeFeedback(ServerPlayer player) { return PREVENTED.remove(player); }

    @SubscribeEvent
    public static void sleep(CanPlayerSleepEvent event) {
        if (event.getProblem() == Player.BedSleepingProblem.NOT_SAFE && active(event.getEntity()))
            event.setProblem(null);
    }
}
