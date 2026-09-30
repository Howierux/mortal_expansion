package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModDataComponents;
import com.howierux.mortalexpansion.registry.ModEnchantments;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class EctchantmentMachineBlockEntity extends BlockEntity {
    public static final int RAW_ECTOPLASM_CHARGE = 1_000;
    public static final int PURE_ECTOPLASM_CHARGE = 4_000;
    public static final int ENCHANTMENT_COST = 4_000;
    public static final int MAX_CHARGE = 16_000;
    public static final int FE_ENCHANTMENT_COST = 40_000;
    public static final int FE_CAPACITY = 160_000;
    public static final int FE_MAX_RECEIVE = 4_000;

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;

    public static final int STATUS_IDLE = 0;
    public static final int STATUS_NEEDS_RESOURCE = 1;
    public static final int STATUS_INVALID_INPUT = 2;
    public static final int STATUS_OUTPUT_READY = 3;

    private static final String CHARGE_TAG = "EctoplasmicCharge";
    private static final String ENERGY_TAG = "Energy";
    private static final String ITEMS_TAG = "Items";

    private int ectoplasmicCharge;
    private int recentFeInput;
    private long recentFeInputTick = Long.MIN_VALUE;

    private final ItemStackHandler items = new ItemStackHandler(3) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case SLOT_INPUT -> !stack.isEmpty();
                case SLOT_FUEL -> getFuelValue(stack) > 0;
                default -> false;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

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
            if (slot != SLOT_OUTPUT) {
                return ItemStack.EMPTY;
            }
            return items.extractItem(slot, amount, simulate);
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

    private final MachineEnergyStorage energyStorage =
            new MachineEnergyStorage(FE_CAPACITY, FE_MAX_RECEIVE);

    public EctchantmentMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ECTCHANTMENT_MACHINE.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public IItemHandler getAutomationItems() {
        return automationItems;
    }

    public int getEctoplasmicCharge() {
        return ectoplasmicCharge;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getRecentFeInput() {
        if (level == null || recentFeInputTick == Long.MIN_VALUE) {
            return 0;
        }
        return level.getGameTime() - recentFeInputTick <= 2L
                ? recentFeInput
                : 0;
    }

    public ContainerData createMenuData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ectoplasmicCharge;
                    case 1 -> MAX_CHARGE;
                    case 2 -> energyStorage.getEnergyStored() / 10;
                    case 3 -> FE_CAPACITY / 10;
                    case 4 -> getRecentFeInput();
                    case 5 -> getStatus();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                // Read-only machine status data.
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            EctchantmentMachineBlockEntity machine
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        machine.consumeFuelSlot();
        machine.tryProcessInput(serverLevel);
    }

    private void consumeFuelSlot() {
        ItemStack fuel = items.getStackInSlot(SLOT_FUEL);
        int value = getFuelValue(fuel);

        if (value <= 0 || ectoplasmicCharge + value > MAX_CHARGE) {
            return;
        }

        ectoplasmicCharge += value;
        items.extractItem(SLOT_FUEL, 1, false);
        setChanged();
    }

    private void tryProcessInput(ServerLevel level) {
        if (!items.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
            return;
        }

        ItemStack input = items.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) {
            return;
        }

        ItemStack result = createOperationResult(input, level);
        if (result.isEmpty() || !hasEctchantmentCost()) {
            return;
        }

        consumeEctchantmentCost();
        items.extractItem(SLOT_INPUT, 1, false);
        items.setStackInSlot(SLOT_OUTPUT, result);
        setChanged();
    }

    private ItemStack createOperationResult(ItemStack input, ServerLevel level) {
        ItemStack transmuted = getTransmutationResult(input);
        if (!transmuted.isEmpty()) {
            return transmuted;
        }

        Holder<Enchantment> enchantment = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(ModEnchantments.DEMI_TANGIBLE);

        if (!input.supportsEnchantment(enchantment)
                || Boolean.TRUE.equals(input.get(ModDataComponents.DEMI_TANGIBLE.get()))
                || input.getEnchantmentLevel(enchantment) > 0) {
            return ItemStack.EMPTY;
        }

        ItemStack result = input.copyWithCount(1);
        result.set(ModDataComponents.DEMI_TANGIBLE.get(), true);
        result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return result;
    }

    private boolean hasEctchantmentCost() {
        return ectoplasmicCharge >= ENCHANTMENT_COST
                || energyStorage.getEnergyStored() >= FE_ENCHANTMENT_COST;
    }

    private void consumeEctchantmentCost() {
        if (ectoplasmicCharge >= ENCHANTMENT_COST) {
            ectoplasmicCharge -= ENCHANTMENT_COST;
        } else {
            energyStorage.consumeInternal(FE_ENCHANTMENT_COST);
        }
        setChanged();
    }

    private int getStatus() {
        if (!items.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
            return STATUS_OUTPUT_READY;
        }

        ItemStack input = items.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) {
            return STATUS_IDLE;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return hasEctchantmentCost()
                    ? STATUS_IDLE
                    : STATUS_NEEDS_RESOURCE;
        }

        if (createOperationResult(input, serverLevel).isEmpty()) {
            return STATUS_INVALID_INPUT;
        }

        return hasEctchantmentCost()
                ? STATUS_IDLE
                : STATUS_NEEDS_RESOURCE;
    }

    public ItemStack getTransmutationResult(ItemStack stack) {
        if (stack.is(Items.DIAMOND)) {
            return new ItemStack(ModItems.BLOODSTONE.get());
        }

        if (stack.is(Items.STONE)) {
            return new ItemStack(ModItems.GRAVESTONE.get());
        }

        if (stack.is(ItemTags.LOGS)
                && !stack.is(ModItems.SPIRITWOOD_LOG.get())) {
            return new ItemStack(ModItems.SPIRITWOOD_LOG.get());
        }

        if (stack.is(ItemTags.SAPLINGS)
                && !stack.is(ModItems.SPIRITWOOD_SAPLING.get())) {
            return new ItemStack(ModItems.SPIRITWOOD_SAPLING.get());
        }

        if (stack.is(Items.VINE)
                || stack.is(Items.MOSS_BLOCK)
                || stack.is(Items.GLOW_LICHEN)) {
            return new ItemStack(ModItems.GRAVE_MOSS.get());
        }

        return ItemStack.EMPTY;
    }

    public static int getFuelValue(ItemStack stack) {
        if (stack.is(ModItems.PURE_ECTOPLASM.get())) {
            return PURE_ECTOPLASM_CHARGE;
        }

        if (stack.is(ModItems.RAW_ECTOPLASM.get())) {
            return RAW_ECTOPLASM_CHARGE;
        }

        return 0;
    }

    public void dropInventory(ServerLevel level) {
        for (int slot = 0; slot < items.getSlots(); slot++) {
            ItemStack stack = items.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(
                        level,
                        worldPosition.getX() + 0.5D,
                        worldPosition.getY() + 0.5D,
                        worldPosition.getZ() + 0.5D,
                        stack.copy()
                );
                items.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ectoplasmicCharge = Math.max(0, Math.min(MAX_CHARGE, tag.getInt(CHARGE_TAG)));
        items.deserializeNBT(registries, tag.getCompound(ITEMS_TAG));

        if (tag.contains(ENERGY_TAG)) {
            energyStorage.deserializeNBT(registries, tag.get(ENERGY_TAG));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(CHARGE_TAG, ectoplasmicCharge);
        tag.put(ITEMS_TAG, items.serializeNBT(registries));
        tag.put(ENERGY_TAG, energyStorage.serializeNBT(registries));
    }

    private final class MachineEnergyStorage extends EnergyStorage {
        private MachineEnergyStorage(int capacity, int maxReceive) {
            super(capacity, maxReceive, FE_ENCHANTMENT_COST);
        }

        int consumeInternal(int amount) {
            int consumed = Math.min(
                    Math.max(0, amount),
                    this.energy
            );

            if (consumed > 0) {
                this.energy -= consumed;
                EctchantmentMachineBlockEntity.this.setChanged();
            }

            return consumed;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                EctchantmentMachineBlockEntity.this.recentFeInput = received;
                if (EctchantmentMachineBlockEntity.this.level != null) {
                    EctchantmentMachineBlockEntity.this.recentFeInputTick =
                            EctchantmentMachineBlockEntity.this.level.getGameTime();
                }
                EctchantmentMachineBlockEntity.this.setChanged();
            }
            return received;
        }

        @Override
        public boolean canExtract() {
            return false;
        }
    }
}
