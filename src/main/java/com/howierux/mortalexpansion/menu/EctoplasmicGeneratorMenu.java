package com.howierux.mortalexpansion.menu;

import com.howierux.mortalexpansion.blockentity.EctoplasmicGeneratorBlockEntity;
import com.howierux.mortalexpansion.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class EctoplasmicGeneratorMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 6;
    private static final int MACHINE_SLOTS = 1;

    private final ContainerData data;

    public EctoplasmicGeneratorMenu(
            int containerId,
            Inventory inventory,
            FriendlyByteBuf extraData
    ) {
        this(
                containerId,
                inventory,
                getGenerator(inventory, extraData.readBlockPos()),
                new SimpleContainerData(DATA_COUNT)
        );
    }

    private static EctoplasmicGeneratorBlockEntity getGenerator(
            Inventory inventory,
            BlockPos pos
    ) {
        if (inventory.player.level().getBlockEntity(pos)
                instanceof EctoplasmicGeneratorBlockEntity generator) {
            return generator;
        }

        throw new IllegalStateException("Ectoplasmic Generator block entity missing");
    }

    public EctoplasmicGeneratorMenu(
            int containerId,
            Inventory inventory,
            EctoplasmicGeneratorBlockEntity generator
    ) {
        this(containerId, inventory, generator, generator.createMenuData());
    }

    private EctoplasmicGeneratorMenu(
            int containerId,
            Inventory inventory,
            EctoplasmicGeneratorBlockEntity generator,
            ContainerData data
    ) {
        super(ModMenus.ECTOPLASMIC_GENERATOR.get(), containerId);

        this.data = data;
        checkContainerDataCount(data, DATA_COUNT);
        addDataSlots(data);

        addSlot(new SlotItemHandler(
                generator.getItems(),
                EctoplasmicGeneratorBlockEntity.SLOT_ECTO,
                80,
                58
        ));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(
                        inventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        125 + row * 18
                ));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(
                    inventory,
                    col,
                    8 + col * 18,
                    183
            ));
        }
    }

    public int getEnergyStored() { return data.get(0) * 10; }
    public int getEnergyCapacity() { return data.get(1) * 10; }
    public int getBurnTicksRemaining() { return data.get(2); }
    public int getHauntTierId() { return data.get(3); }
    public int getHauntMultiplierPercent() { return data.get(4); }
    public int getGenerationPerTick() { return data.get(5); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < MACHINE_SLOTS) {
            if (!moveItemStackTo(stack, MACHINE_SLOTS, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(stack, 0, MACHINE_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
