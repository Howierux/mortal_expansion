package com.howierux.mortalexpansion.menu;

import com.howierux.mortalexpansion.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public final class EnchantmentGeneratorMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 5;

    private final ContainerData data;

    public EnchantmentGeneratorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainerData(DATA_COUNT));
    }

    public EnchantmentGeneratorMenu(
            int containerId,
            Inventory inventory,
            ContainerData data
    ) {
        super(ModMenus.ENCHANTMENT_GENERATOR.get(), containerId);
        checkContainerDataCount(data, DATA_COUNT);
        this.data = data;
        addDataSlots(data);
    }

    public int getEnergyStored() {
        return data.get(0) * 10;
    }

    public int getEnergyCapacity() {
        return data.get(1) * 10;
    }

    public int getRemainingEnergyToGenerate() {
        return data.get(2) * 10;
    }

    public int getGenerationPerTick() {
        return data.get(3);
    }

    public boolean hasFinishedItem() {
        return data.get(4) != 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
