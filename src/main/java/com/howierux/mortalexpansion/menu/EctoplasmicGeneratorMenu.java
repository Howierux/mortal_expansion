package com.howierux.mortalexpansion.menu;

import com.howierux.mortalexpansion.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public final class EctoplasmicGeneratorMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 6;

    private final ContainerData data;

    public EctoplasmicGeneratorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainerData(DATA_COUNT));
    }

    public EctoplasmicGeneratorMenu(
            int containerId,
            Inventory inventory,
            ContainerData data
    ) {
        super(ModMenus.ECTOPLASMIC_GENERATOR.get(), containerId);
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

    public int getBurnTicksRemaining() {
        return data.get(2);
    }

    public int getHauntTierId() {
        return data.get(3);
    }

    public int getHauntMultiplierPercent() {
        return data.get(4);
    }

    public int getGenerationPerTick() {
        return data.get(5);
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
