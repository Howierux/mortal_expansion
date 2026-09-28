package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.haunt.HauntTier;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stores persistent provenance for a Grave.
 *
 * Graves default to placed/non-natural. World generation explicitly marks its
 * Graves natural. A placed Grave can also naturalize during a new-day check
 * when local Haunt and nearby flowers meet the required conditions.
 */
public final class GraveBlockEntity extends BlockEntity {
    private static final String NATURALLY_GENERATED_TAG = "NaturallyGenerated";

    /** Low daily chance for a qualifying placed Grave to become natural. */
    private static final float NATURALIZATION_CHANCE = 0.05F;

    private boolean naturallyGenerated;

    /**
     * Intentionally not persisted. A newly loaded Grave records the current
     * day without rolling, so Graves that were unloaded at dawn do not receive
     * a retroactive naturalization attempt.
     */
    private long lastObservedDay = Long.MIN_VALUE;

    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRAVE.get(), pos, state);
    }

    public boolean isNaturallyGenerated() {
        return naturallyGenerated;
    }

    /**
     * Runtime provenance change, used when a placed Grave naturalizes.
     * This happens during normal server ticking, never during world generation.
     */
    public void setNaturallyGenerated(boolean naturallyGenerated) {
        this.naturallyGenerated = naturallyGenerated;
        setChanged();

        if (level != null) {
            BlockState state = getBlockState();
            if (state.hasProperty(GraveBlock.NATURAL)
                    && state.getValue(GraveBlock.NATURAL) != naturallyGenerated) {
                level.setBlock(
                        worldPosition,
                        state.setValue(GraveBlock.NATURAL, naturallyGenerated),
                        2
                );
            }
        }
    }

    /**
     * World-generation-safe provenance update. The generator places the block
     * with NATURAL=true up front, so this method must not rewrite the block.
     */
    public void markNaturallyGeneratedFromWorldgen() {
        naturallyGenerated = true;
        setChanged();
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            GraveBlockEntity grave
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Repair visual state from older saves only after the chunk is fully
        // loaded and ticking, never from onLoad/world generation.
        if (state.hasProperty(GraveBlock.NATURAL)
                && state.getValue(GraveBlock.NATURAL) != grave.naturallyGenerated) {
            level.setBlock(
                    pos,
                    state.setValue(GraveBlock.NATURAL, grave.naturallyGenerated),
                    2
            );
            return;
        }

        long currentDay = Math.floorDiv(serverLevel.getDayTime(), 24_000L);

        if (grave.lastObservedDay == Long.MIN_VALUE) {
            grave.lastObservedDay = currentDay;
            return;
        }

        if (grave.lastObservedDay == currentDay) {
            return;
        }

        grave.lastObservedDay = currentDay;

        if (grave.naturallyGenerated) {
            return;
        }

        if (!HauntManager.isAtLeast(serverLevel, pos, HauntTier.SUPERNATURAL)) {
            return;
        }

        if (!hasNearbyFlowers(serverLevel, pos)) {
            return;
        }

        if (serverLevel.getRandom().nextFloat() >= NATURALIZATION_CHANCE) {
            return;
        }

        // Naturalization consumes one point of local Haunt, not an entire tier.
        HauntManager.addPoints(serverLevel, pos, -1);
        grave.setNaturallyGenerated(true);
    }

    private static boolean hasNearbyFlowers(ServerLevel level, BlockPos gravePos) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    if (dx * dx + dz * dz > 9) {
                        continue;
                    }

                    cursor.set(
                            gravePos.getX() + dx,
                            gravePos.getY() + dy,
                            gravePos.getZ() + dz
                    );

                    if (level.getBlockState(cursor).is(BlockTags.FLOWERS)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        naturallyGenerated = tag.getBoolean(NATURALLY_GENERATED_TAG);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean(NATURALLY_GENERATED_TAG, naturallyGenerated);
    }
}
