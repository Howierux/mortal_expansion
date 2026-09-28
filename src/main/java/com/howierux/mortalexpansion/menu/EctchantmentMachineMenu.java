package com.howierux.mortalexpansion.menu;

import com.howierux.mortalexpansion.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public final class EctchantmentMachineMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 4;

    private final ContainerData data;

    public EctchantmentMachineMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainerData(DATA_COUNT));
    }

    public EctchantmentMachineMenu(
            int containerId,
            Inventory inventory,
            ContainerData data
    ) {
        super(ModMenus.ECTCHANTMENT_MACHINE.get(), containerId);
        checkContainerDataCount(data, DATA_COUNT);
        this.data = data;
        addDataSlots(data);
    }

    public int getEctoplasmicCharge() {
        return data.get(0);
    }

    public int getMaxEctoplasmicCharge() {
        return data.get(1);
    }

    public int getEnergyStored() {
        return data.get(2);
    }

    public int getEnergyCapacity() {
        return data.get(3);
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
