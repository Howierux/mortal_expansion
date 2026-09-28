package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModEnchantments;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class EctchantmentMachineBlockEntity extends BlockEntity {
    public static final int RAW_ECTOPLASM_CHARGE = 1_000;
    public static final int PURE_ECTOPLASM_CHARGE = 4_000;
    public static final int ENCHANTMENT_COST = 4_000;
    public static final int MAX_CHARGE = 16_000;

    private static final String CHARGE_TAG = "EctoplasmicCharge";

    private int ectoplasmicCharge;

    public EctchantmentMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ECTCHANTMENT_MACHINE.get(), pos, state);
    }

    public int getEctoplasmicCharge() {
        return ectoplasmicCharge;
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

        if (stack.getEnchantmentLevel(enchantment) > 0) {
            return EnchantResult.ALREADY_PRESENT;
        }

        if (ectoplasmicCharge < ENCHANTMENT_COST) {
            return EnchantResult.NEEDS_FUEL;
        }

        stack.enchant(enchantment, 1);
        ectoplasmicCharge -= ENCHANTMENT_COST;
        setChanged();
        return EnchantResult.SUCCESS;
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
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(CHARGE_TAG, ectoplasmicCharge);
    }

    public enum EnchantResult {
        SUCCESS,
        NOT_SUPPORTED,
        ALREADY_PRESENT,
        NEEDS_FUEL
    }
}
