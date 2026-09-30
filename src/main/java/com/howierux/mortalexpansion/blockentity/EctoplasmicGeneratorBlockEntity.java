package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.haunt.HauntTier;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.Containers;

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

    public static final int SLOT_ECTO = 0;

    private final GeneratorEnergyStorage energyStorage =
            new GeneratorEnergyStorage(ENERGY_CAPACITY, MAX_OUTPUT_PER_TICK);

    private final ItemStackHandler items = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == SLOT_ECTO && getBurnTicks(stack) > 0;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final IItemHandler automationItems = new IItemHandler() {
        @Override public int getSlots() { return items.getSlots(); }
        @Override public ItemStack getStackInSlot(int slot) { return items.getStackInSlot(slot); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return items.insertItem(slot, stack, simulate);
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
        @Override public int getSlotLimit(int slot) { return items.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            return items.isItemValid(slot, stack);
        }
    };

    private int burnTicksRemaining;
    private int baseGenerationPerTick;

    public EctoplasmicGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ECTOPLASMIC_GENERATOR.get(), pos, state);
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public IItemHandler getAutomationItems() {
        return automationItems;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getBurnTicksRemaining() {
        return burnTicksRemaining;
    }

    public ContainerData createMenuData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> energyStorage.getEnergyStored() / 10;
                    case 1 -> ENERGY_CAPACITY / 10;
                    case 2 -> burnTicksRemaining;
                    case 3 -> getLocalHauntTier().id();
                    case 4 -> (int) Math.round(getHauntMultiplier() * 100.0D);
                    case 5 -> getCurrentGenerationPerTick();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                // Read-only generator status data.
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
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

        if (level instanceof ServerLevel serverLevel) {
            return HauntManager.getTier(serverLevel, worldPosition);
        }

        return HauntTier.VOID;
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
        blockEntity.tryConsumeEctoSlot();

        // Push only energy that was already buffered at the beginning of the
        // tick. Newly generated FE remains visible in the internal buffer for
        // at least one tick instead of being generated and immediately erased
        // from the display by same-tick output.
        int bufferedBeforeGeneration = blockEntity.energyStorage.getEnergyStored();
        blockEntity.pushEnergy(level, pos, bufferedBeforeGeneration);

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
    }

    private void tryConsumeEctoSlot() {
        if (isBurning()) {
            return;
        }

        ItemStack ecto = items.getStackInSlot(SLOT_ECTO);
        if (getBurnTicks(ecto) <= 0) {
            return;
        }

        burnTicksRemaining = getBurnTicks(ecto);
        baseGenerationPerTick = getBaseGeneration(ecto);
        items.extractItem(SLOT_ECTO, 1, false);
        setChanged();
    }

    private void pushEnergy(Level level, BlockPos pos, int availableAtTickStart) {
        int remainingOutput = Math.min(
                MAX_OUTPUT_PER_TICK,
                Math.min(availableAtTickStart, energyStorage.getEnergyStored())
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

            if (target == null) {
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

    public void dropInventory(ServerLevel level) {
        ItemStack ecto = items.getStackInSlot(SLOT_ECTO);
        if (!ecto.isEmpty()) {
            Containers.dropItemStack(
                    level,
                    worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D,
                    ecto.copy()
            );
            items.setStackInSlot(SLOT_ECTO, ItemStack.EMPTY);
        }
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
        if (tag.contains("Items")) {
            items.deserializeNBT(registries, tag.getCompound("Items"));
        }
        burnTicksRemaining = Math.max(0, tag.getInt(BURN_TICKS_TAG));
        baseGenerationPerTick = Math.max(0, tag.getInt(BASE_GENERATION_TAG));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(ENERGY_TAG, energyStorage.serializeNBT(registries));
        tag.put("Items", items.serializeNBT(registries));
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
                EctoplasmicGeneratorBlockEntity.this.setChanged();
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
