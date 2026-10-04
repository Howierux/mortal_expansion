package com.howierux.mortalexpansion.spiritwell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import com.howierux.mortalexpansion.registry.ModBlocks;
import java.util.HashSet;
import java.util.Comparator;

/** Per-dimension persistent positions, populated when existing or generated wells load. */
public final class SpiritwellIndex extends SavedData {
    private final HashSet<BlockPos> positions = new HashSet<>();
    public static SpiritwellIndex get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(SpiritwellIndex::new, SpiritwellIndex::load, null), "mortal_expansion_spiritwells");
    }
    private static SpiritwellIndex load(CompoundTag tag, HolderLookup.Provider registries) {
        var data = new SpiritwellIndex();
        for (long packed : tag.getLongArray("Positions")) data.positions.add(BlockPos.of(packed));
        return data;
    }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLongArray("Positions", positions.stream().mapToLong(BlockPos::asLong).toArray());
        return tag;
    }
    public void add(BlockPos pos) { if (positions.add(pos.immutable())) setDirty(); }
    public BlockPos nearest(ServerLevel level, net.minecraft.world.phys.Vec3 from) {
        var ordered = positions.stream().sorted(Comparator.comparingDouble(pos -> pos.getCenter().distanceToSqr(from))).toList();
        for (var pos : ordered) {
            if (!level.getWorldBorder().isWithinBounds(pos)) continue;
            // Load only indexed candidates, never generate arbitrary terrain to search.
            level.getChunkAt(pos);
            if (level.getBlockState(pos).is(ModBlocks.SPIRITWELL.get())) return pos;
            positions.remove(pos);
            setDirty();
        }
        return null;
    }
}
