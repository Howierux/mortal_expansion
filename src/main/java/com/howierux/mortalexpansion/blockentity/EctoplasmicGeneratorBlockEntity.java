package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.haunt.HauntTier;
import com.howierux.mortalexpansion.registry.ModAttachments;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class EctoplasmicGeneratorBlockEntity extends BlockEntity {
    public static final int ENERGY_CAPACITY = 200_000;
    public static final int MAX_OUTPUT_PER_TICK = 2_000;

    public static final int RAW_BURN_TICKS = 1_000;
    public static final int PURE_BURN_TICKS = 2_000;
    public static final int RAW_BASE_FE_PER_TICK = 20;
    public static final int PURE_BASE_FE_PER_TICK = 40;

    private static final String ENERGY_TAG = "Energy";
    private static final String BURN_TICKS_TAG = "BurnTicks";
    private static final String BASE_GENERATION_TAG = "BaseGeneration";

    private final GeneratorEnergyStorage energyStorage =
            new GeneratorEnergyStorage(ENERGY_CAPACITY, MAX_OUTPUT_PER_TICK);

    private int burnTicksRemaining;
    private int baseGenerationPerTick;

    public EctoplasmicGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ECTOPLASMIC_GENERATOR.get(), pos, state);
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getBurnTicksRemaining() {
        return burnTicksRemaining;
    }

    public boolean isBurning() {
        return burnTicksRemaining > 0;
    }

    public boolean canAcceptFuel(ItemStack stack) {
        return !isBurning() && getBurnTicks(stack) > 0;
    }

    public boolean addFuel(ItemStack stack) {
        if (!canAcceptFuel(stack)) {
            return false;
        }

        burnTicksRemaining = getBurnTicks(stack);
        baseGenerationPerTick = getBaseGeneration(stack);
        setChanged();
        return true;
    }

    public HauntTier getLocalHauntTier() {
        if (level == null) {
            return HauntTier.VOID;
        }

        return HauntTier.fromId(
                level.getChunkAt(worldPosition).getData(ModAttachments.HAUNT_TIER)
        );
    }

    public double getHauntMultiplier() {
        return 1.0D + (getLocalHauntTier().id() * 0.25D);
    }

    public int getCurrentGenerationPerTick() {
        return (int) Math.floor(baseGenerationPerTick * getHauntMultiplier());
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            EctoplasmicGeneratorBlockEntity blockEntity
    ) {
        if (blockEntity.burnTicksRemaining > 0) {
            int generation = blockEntity.getCurrentGenerationPerTick();
            int accepted = blockEntity.energyStorage.generate(generation);

            if (accepted > 0) {
                blockEntity.burnTicksRemaining--;
                if (blockEntity.burnTicksRemaining <= 0) {
                    blockEntity.baseGenerationPerTick = 0;
                }
                blockEntity.setChanged();
            }
        }

        blockEntity.pushEnergy(level, pos);
    }

    private void pushEnergy(Level level, BlockPos pos) {
        int remainingOutput = Math.min(
                MAX_OUTPUT_PER_TICK,
                energyStorage.getEnergyStored()
        );

        if (remainingOutput <= 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (remainingOutput <= 0) {
                break;
            }

            BlockPos targetPos = pos.relative(direction);
            IEnergyStorage target = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK,
                    targetPos,
                    direction.getOpposite()
            );

            if (target == null || !target.canReceive()) {
                continue;
            }

            int available = energyStorage.extractEnergy(remainingOutput, true);
            if (available <= 0) {
                break;
            }

            int accepted = target.receiveEnergy(available, false);
            if (accepted > 0) {
                energyStorage.extractEnergy(accepted, false);
                remainingOutput -= accepted;
            }
        }
    }

    private static int getBurnTicks(ItemStack stack) {
        if (stack.is(ModItems.PURE_ECTOPLASM.get())) {
            return PURE_BURN_TICKS;
        }
        if (stack.is(ModItems.RAW_ECTOPLASM.get())) {
            return RAW_BURN_TICKS;
        }
        return 0;
    }

    private static int getBaseGeneration(ItemStack stack) {
        if (stack.is(ModItems.PURE_ECTOPLASM.get())) {
            return PURE_BASE_FE_PER_TICK;
        }
        if (stack.is(ModItems.RAW_ECTOPLASM.get())) {
            return RAW_BASE_FE_PER_TICK;
        }
        return 0;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(ENERGY_TAG)) {
            energyStorage.deserializeNBT(registries, tag.get(ENERGY_TAG));
        }
        burnTicksRemaining = Math.max(0, tag.getInt(BURN_TICKS_TAG));
        baseGenerationPerTick = Math.max(0, tag.getInt(BASE_GENERATION_TAG));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(ENERGY_TAG, energyStorage.serializeNBT(registries));
        tag.putInt(BURN_TICKS_TAG, burnTicksRemaining);
        tag.putInt(BASE_GENERATION_TAG, baseGenerationPerTick);
    }

    private final class GeneratorEnergyStorage extends EnergyStorage {
        private GeneratorEnergyStorage(int capacity, int maxExtract) {
            super(capacity, 0, maxExtract);
        }

        private int generate(int amount) {
            int accepted = Math.min(capacity - energy, Math.max(0, amount));
            if (accepted > 0) {
                energy += accepted;
            }
            return accepted;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (extracted > 0 && !simulate) {
                EctoplasmicGeneratorBlockEntity.this.setChanged();
            }
            return extracted;
        }
    }
}
