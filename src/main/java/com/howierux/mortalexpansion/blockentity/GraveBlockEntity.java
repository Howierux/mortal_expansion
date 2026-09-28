package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stores persistent provenance for a Grave.
 *
 * Graves default to placed/non-natural. World generation must explicitly mark
 * them natural after placement.
 */
public final class GraveBlockEntity extends BlockEntity {
    private static final String NATURALLY_GENERATED_TAG = "NaturallyGenerated";

    private boolean naturallyGenerated;

    public GraveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRAVE.get(), pos, state);
    }

    public boolean isNaturallyGenerated() {
        return naturallyGenerated;
    }

    public void setNaturallyGenerated(boolean naturallyGenerated) {
        if (this.naturallyGenerated == naturallyGenerated) {
            return;
        }

        this.naturallyGenerated = naturallyGenerated;
        setChanged();
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
