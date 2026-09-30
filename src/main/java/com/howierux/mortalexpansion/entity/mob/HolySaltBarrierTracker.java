package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Caches whether an X/Z column contains Holy Salt.
 *
 * A single Holy Salt dust position projects a spectral barrier through the
 * entire vertical build height at that X/Z coordinate. Ordinary entities are
 * unaffected; this cache is consulted only by intangible phasing/tracking.
 */
public final class HolySaltBarrierTracker {
    private HolySaltBarrierTracker() {}

    private static final Map<Level, Map<Long, Boolean>> COLUMN_CACHE =
            new WeakHashMap<>();

    public static void markPresent(Level level, BlockPos pos) {
        cache(level).put(columnKey(pos.getX(), pos.getZ()), true);
    }

    public static void invalidate(Level level, BlockPos pos) {
        Map<Long, Boolean> cache = COLUMN_CACHE.get(level);
        if (cache != null) {
            cache.remove(columnKey(pos.getX(), pos.getZ()));
        }
    }

    public static boolean hasSaltColumn(Level level, int x, int z) {
        long key = columnKey(x, z);
        Map<Long, Boolean> cache = cache(level);
        Boolean known = cache.get(key);

        if (known != null) {
            return known;
        }

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
            cursor.set(x, y, z);
            if (level.getBlockState(cursor).is(ModBlocks.HOLY_SALT.get())) {
                cache.put(key, true);
                return true;
            }
        }

        cache.put(key, false);
        return false;
    }

    private static Map<Long, Boolean> cache(Level level) {
        return COLUMN_CACHE.computeIfAbsent(level, ignored -> new HashMap<>());
    }

    private static long columnKey(int x, int z) {
        return ((long)x & 0xFFFFFFFFL) << 32
                | ((long)z & 0xFFFFFFFFL);
    }
}
