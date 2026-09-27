package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Single authoritative API for chunk haunt state.
 *
 * Future machines, rituals, spawning, equipment bonuses, purification and
 * spreading should call this class rather than accessing the attachment directly.
 */
public final class HauntManager {
    private HauntManager() {}

    public static HauntTier getTier(ServerLevel level, BlockPos pos) {
        return getTier(level, new ChunkPos(pos));
    }

    public static HauntTier getTier(ServerLevel level, ChunkPos pos) {
        LevelChunk chunk = level.getChunk(pos.x, pos.z);
        return HauntTier.fromId(chunk.getData(ModAttachments.HAUNT_TIER));
    }

    public static HauntTier setTier(ServerLevel level, BlockPos pos, HauntTier tier) {
        return setTier(level, new ChunkPos(pos), tier);
    }

    public static HauntTier setTier(ServerLevel level, ChunkPos pos, HauntTier tier) {
        LevelChunk chunk = level.getChunk(pos.x, pos.z);
        chunk.setData(ModAttachments.HAUNT_TIER, tier.id());
        return tier;
    }

    public static HauntTier addLevels(ServerLevel level, BlockPos pos, int amount) {
        ChunkPos chunkPos = new ChunkPos(pos);
        HauntTier current = getTier(level, chunkPos);
        return setTier(level, chunkPos, HauntTier.fromId(current.id() + amount));
    }

    public static HauntTier raise(ServerLevel level, BlockPos pos) {
        return addLevels(level, pos, 1);
    }

    public static HauntTier lower(ServerLevel level, BlockPos pos) {
        return addLevels(level, pos, -1);
    }

    public static boolean isAtLeast(ServerLevel level, BlockPos pos, HauntTier minimum) {
        return getTier(level, pos).id() >= minimum.id();
    }
}
