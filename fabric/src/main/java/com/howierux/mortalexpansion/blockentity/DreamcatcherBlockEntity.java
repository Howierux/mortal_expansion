package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.haunt.Dreamcatchers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;

public final class DreamcatcherBlockEntity extends BlockEntity {
    private UUID owner;
    public DreamcatcherBlockEntity(BlockPos pos, BlockState state) {
        super(com.howierux.mortalexpansion.content.RemainingRuntime.DREAM, pos, state);
    }
    public UUID owner() { return owner; }
    public void setOwner(UUID owner) {
        if (level instanceof ServerLevel server) Dreamcatchers.remove(server, worldPosition);
        this.owner = owner;
        setChanged();
        if (level instanceof ServerLevel server && !isRemoved()) Dreamcatchers.add(server, worldPosition, owner);
    }
    @Override public void setRemoved() {
        if (level instanceof ServerLevel server) Dreamcatchers.remove(server, worldPosition);
        super.setRemoved();
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (owner != null) tag.putUUID("Owner", owner);
    }
    @Override public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        setOwner(tag.hasUUID("Owner") ? tag.getUUID("Owner") : null);
    }
}
