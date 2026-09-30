package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class PurificationStationBlockEntity extends BlockEntity {
    public static final int WATER_PER_CRAFT = 1000;
    public static final int WATER_CAPACITY = WATER_PER_CRAFT * 10;
    public static final int FE_PER_CRAFT = 100_000;
    public static final int PROCESS_TICKS = 60;
    public static final int FE_CAPACITY = 400_000;
    public static final int FE_MAX_RECEIVE = 10_000;

    public static final int SLOT_RAW_ECTOPLASM = 0;
    public static final int SLOT_GRAVE_MOSS = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_WATER_BUCKET = 3;

    private static final String ITEMS_TAG = "Items";
    private static final String WATER_TAG = "Water";
    private static final String ENERGY_TAG = "Energy";
    private static final String PROGRESS_TAG = "Progress";
    private static final String FE_USED_TAG = "FeUsed";

    private final ItemStackHandler items = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case SLOT_RAW_ECTOPLASM -> stack.is(ModItems.RAW_ECTOPLASM.get());
                case SLOT_GRAVE_MOSS -> stack.is(ModItems.GRAVE_MOSS.get());
                case SLOT_WATER_BUCKET -> stack.is(Items.WATER_BUCKET);
                default -> false;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final FluidTank waterTank = new FluidTank(
            WATER_CAPACITY,
            stack -> stack.getFluid() == Fluids.WATER
    ) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private final StationEnergyStorage energy =
            new StationEnergyStorage(FE_CAPACITY, FE_MAX_RECEIVE);

    private final IItemHandler automationItems = new IItemHandler() {
        @Override
        public int getSlots() {
            return items.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return items.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == SLOT_OUTPUT) {
                return stack;
            }
            return items.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == SLOT_OUTPUT) {
                return items.extractItem(slot, amount, simulate);
            }

            if (slot == SLOT_WATER_BUCKET
                    && items.getStackInSlot(slot).is(Items.BUCKET)) {
                return items.extractItem(slot, amount, simulate);
            }

            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return items.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot != SLOT_OUTPUT && items.isItemValid(slot, stack);
        }
    };

    private int progress;
    private int feUsed;
    private int recentFeInput;
    private long recentFeInputTick = Long.MIN_VALUE;

    public PurificationStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PURIFICATION_STATION.get(), pos, state);
    }

    public ItemStackHandler getItems() { return items; }
    public IItemHandler getAutomationItems() { return automationItems; }
    public FluidTank getWaterTank() { return waterTank; }
    public IEnergyStorage getEnergyStorage() { return energy; }
    public int getProgress() { return progress; }
    public int getFeUsed() { return feUsed; }
    public int getRecentFeInput() {
        if (level == null || recentFeInputTick == Long.MIN_VALUE) {
            return 0;
        }
        return level.getGameTime() - recentFeInputTick <= 2L
                ? recentFeInput
                : 0;
    }
    public boolean isProcessing() { return progress > 0; }

    private boolean hasCompleteRecipeReady() {
        ItemStack raw = items.getStackInSlot(SLOT_RAW_ECTOPLASM);
        ItemStack moss = items.getStackInSlot(SLOT_GRAVE_MOSS);
        ItemStack output = items.getStackInSlot(SLOT_OUTPUT);

        boolean outputSpace = output.isEmpty()
                || (output.is(ModItems.PURE_ECTOPLASM.get())
                && output.getCount() < output.getMaxStackSize());

        return raw.is(ModItems.RAW_ECTOPLASM.get())
                && moss.is(ModItems.GRAVE_MOSS.get())
                && waterTank.getFluidAmount() >= WATER_PER_CRAFT
                && outputSpace;
    }

    public ContainerData createMenuData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> waterTank.getFluidAmount();
                    case 1 -> WATER_CAPACITY;
                    case 2 -> energy.getEnergyStored() / 10;
                    case 3 -> FE_CAPACITY / 10;
                    case 4 -> progress;
                    case 5 -> PROCESS_TICKS;
                    case 6 -> feUsed / 10;
                    case 7 -> getRecentFeInput();
                    default -> 0;
                };
            }

            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 8; }
        };
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            PurificationStationBlockEntity station
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        station.tryDrainWaterBucket();

        if (station.progress == 0) {
            if (station.tryStartCraft()) {
                return;
            }
        }

        if (station.progress <= 0) {
            return;
        }

        int targetUsed = (int)Math.ceil(
                (double)FE_PER_CRAFT * (station.progress + 1) / PROCESS_TICKS
        );
        int neededThisTick = Math.max(0, targetUsed - station.feUsed);

        if (neededThisTick > 0) {
            int extracted = station.energy.extractEnergy(neededThisTick, false);
            station.feUsed += extracted;

            if (extracted < neededThisTick) {
                station.setChanged();
                return;
            }
        }

        station.progress++;

        if (station.progress >= PROCESS_TICKS) {
            ItemStack output = station.items.getStackInSlot(SLOT_OUTPUT);
            if (output.isEmpty()) {
                station.items.setStackInSlot(
                        SLOT_OUTPUT,
                        new ItemStack(ModItems.PURE_ECTOPLASM.get())
                );
            } else {
                output.grow(1);
                station.items.setStackInSlot(SLOT_OUTPUT, output);
            }

            HauntManager.addPoints(serverLevel, pos, 1);
            station.progress = 0;
            station.feUsed = 0;
            station.setChanged();
        }
    }

    private void tryDrainWaterBucket() {
        ItemStack bucket = items.getStackInSlot(SLOT_WATER_BUCKET);

        if (!bucket.is(Items.WATER_BUCKET)
                || waterTank.getFluidAmount() > WATER_CAPACITY - WATER_PER_CRAFT) {
            return;
        }

        waterTank.fill(
                new FluidStack(Fluids.WATER, WATER_PER_CRAFT),
                net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE
        );
        items.setStackInSlot(SLOT_WATER_BUCKET, new ItemStack(Items.BUCKET));
        setChanged();
    }

    private boolean tryStartCraft() {
        if (!hasCompleteRecipeReady()) {
            return false;
        }

        items.extractItem(SLOT_RAW_ECTOPLASM, 1, false);
        items.extractItem(SLOT_GRAVE_MOSS, 1, false);
        waterTank.drain(WATER_PER_CRAFT, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);

        progress = 1;
        feUsed = 0;
        setChanged();
        return true;
    }

    public void dropUnprocessedInventory(ServerLevel level) {
        for (int slot : new int[]{
                SLOT_RAW_ECTOPLASM,
                SLOT_GRAVE_MOSS,
                SLOT_OUTPUT,
                SLOT_WATER_BUCKET
        }) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                net.minecraft.world.Containers.dropItemStack(
                        level,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5,
                        stack.copy()
                );
                items.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    public CompoundTag savePortableData(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("StoredWater", waterTank.getFluidAmount());
        tag.putInt("StoredEnergy", energy.getEnergyStored());
        return tag;
    }

    public void loadPortableData(CompoundTag tag) {
        int water = Math.max(0, Math.min(WATER_CAPACITY, tag.getInt("StoredWater")));
        if (water > 0) {
            waterTank.setFluid(new FluidStack(Fluids.WATER, water));
        }

        int storedEnergy = Math.max(0, Math.min(FE_CAPACITY, tag.getInt("StoredEnergy")));
        energy.setEnergy(storedEnergy);
        setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.deserializeNBT(registries, tag.getCompound(ITEMS_TAG));
        waterTank.readFromNBT(registries, tag.getCompound(WATER_TAG));
        if (tag.contains(ENERGY_TAG)) {
            energy.deserializeNBT(registries, tag.get(ENERGY_TAG));
        }
        progress = Math.max(0, Math.min(PROCESS_TICKS, tag.getInt(PROGRESS_TAG)));
        feUsed = Math.max(0, Math.min(FE_PER_CRAFT, tag.getInt(FE_USED_TAG)));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(ITEMS_TAG, items.serializeNBT(registries));
        CompoundTag waterTag = new CompoundTag();
        waterTank.writeToNBT(registries, waterTag);
        tag.put(WATER_TAG, waterTag);
        tag.put(ENERGY_TAG, energy.serializeNBT(registries));
        tag.putInt(PROGRESS_TAG, progress);
        tag.putInt(FE_USED_TAG, feUsed);
    }

    private final class StationEnergyStorage extends EnergyStorage {
        StationEnergyStorage(int capacity, int maxReceive) {
            super(capacity, maxReceive, FE_PER_CRAFT);
        }

        void setEnergy(int amount) {
            this.energy = Math.max(0, Math.min(capacity, amount));
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                PurificationStationBlockEntity.this.recentFeInput = received;
                if (PurificationStationBlockEntity.this.level != null) {
                    PurificationStationBlockEntity.this.recentFeInputTick =
                            PurificationStationBlockEntity.this.level.getGameTime();
                }
                PurificationStationBlockEntity.this.setChanged();
            }
            return received;
        }

        @Override
        public boolean canExtract() {
            return false;
        }
    }
}
