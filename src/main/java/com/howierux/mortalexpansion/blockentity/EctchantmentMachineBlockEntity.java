package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModEnchantments;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.registry.ModDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class EctchantmentMachineBlockEntity extends BlockEntity {
    public static final int RAW_ECTOPLASM_CHARGE = 1_000;
    public static final int PURE_ECTOPLASM_CHARGE = 4_000;
    public static final int ENCHANTMENT_COST = 4_000;
    public static final int MAX_CHARGE = 16_000;
    public static final int FE_ENCHANTMENT_COST = 40_000;
    public static final int FE_CAPACITY = 160_000;
    public static final int FE_MAX_RECEIVE = 4_000;

    private static final String CHARGE_TAG = "EctoplasmicCharge";
    private static final String ENERGY_TAG = "Energy";

    private int ectoplasmicCharge;
    private final MachineEnergyStorage energyStorage =
            new MachineEnergyStorage(FE_CAPACITY, FE_MAX_RECEIVE);

    public EctchantmentMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ECTCHANTMENT_MACHINE.get(), pos, state);
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

    public ContainerData createMenuData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ectoplasmicCharge;
                    case 1 -> MAX_CHARGE;
                    case 2 -> energyStorage.getEnergyStored() / 10;
                    case 3 -> FE_CAPACITY / 10;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                // Read-only machine status data.
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public boolean canAcceptFuel(ItemStack stack) {
        int value = getFuelValue(stack);
        return value > 0 && ectoplasmicCharge + value <= MAX_CHARGE;
    }

    public int addFuel(ItemStack stack) {
        int value = getFuelValue(stack);
        if (value <= 0 || ectoplasmicCharge + value > MAX_CHARGE) {
            return 0;
        }

        ectoplasmicCharge += value;
        setChanged();
        return value;
    }

    public EnchantResult tryApplyDemiTangible(ItemStack stack, ServerLevel level) {
        Holder<Enchantment> enchantment = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(ModEnchantments.DEMI_TANGIBLE);

        if (!stack.supportsEnchantment(enchantment)) {
            return EnchantResult.NOT_SUPPORTED;
        }

        if (Boolean.TRUE.equals(stack.get(ModDataComponents.DEMI_TANGIBLE.get()))
                || stack.getEnchantmentLevel(enchantment) > 0) {
            return EnchantResult.ALREADY_PRESENT;
        }

        if (ectoplasmicCharge >= ENCHANTMENT_COST) {
            stack.set(ModDataComponents.DEMI_TANGIBLE.get(), true);
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            ectoplasmicCharge -= ENCHANTMENT_COST;
            setChanged();
            return EnchantResult.SUCCESS;
        }

        if (energyStorage.getEnergyStored() >= FE_ENCHANTMENT_COST) {
            stack.set(ModDataComponents.DEMI_TANGIBLE.get(), true);
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            energyStorage.extractEnergy(FE_ENCHANTMENT_COST, false);
            setChanged();
            return EnchantResult.SUCCESS;
        }

        return EnchantResult.NEEDS_FUEL;
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

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ectoplasmicCharge = Math.max(0, Math.min(MAX_CHARGE, tag.getInt(CHARGE_TAG)));
        if (tag.contains(ENERGY_TAG)) {
            energyStorage.deserializeNBT(registries, tag.get(ENERGY_TAG));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(CHARGE_TAG, ectoplasmicCharge);
        tag.put(ENERGY_TAG, energyStorage.serializeNBT(registries));
    }

    private final class MachineEnergyStorage extends EnergyStorage {
        private MachineEnergyStorage(int capacity, int maxReceive) {
            super(capacity, maxReceive, FE_ENCHANTMENT_COST);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                EctchantmentMachineBlockEntity.this.setChanged();
            }
            return received;
        }

        @Override
        public boolean canExtract() {
            return false;
        }
    }

    public enum EnchantResult {
        SUCCESS,
        NOT_SUPPORTED,
        ALREADY_PRESENT,
        NEEDS_FUEL
    }
}
