package com.howierux.mortalexpansion.menu;

import com.howierux.mortalexpansion.blockentity.EctchantmentMachineBlockEntity;
import com.howierux.mortalexpansion.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class EctchantmentMachineMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 6;
    private static final int MACHINE_SLOTS = 3;

    private final ContainerData data;

    public EctchantmentMachineMenu(
            int containerId,
            Inventory inventory,
            FriendlyByteBuf extraData
    ) {
        this(
                containerId,
                inventory,
                getMachine(inventory, extraData.readBlockPos())
        );
    }

    private static EctchantmentMachineBlockEntity getMachine(
            Inventory inventory,
            BlockPos pos
    ) {
        if (inventory.player.level().getBlockEntity(pos)
                instanceof EctchantmentMachineBlockEntity machine) {
            return machine;
        }

        throw new IllegalStateException("Ectchantment Machine block entity missing");
    }

    public EctchantmentMachineMenu(
            int containerId,
            Inventory inventory,
            EctchantmentMachineBlockEntity machine
    ) {
        super(ModMenus.ECTCHANTMENT_MACHINE.get(), containerId);

        this.data = machine.createMenuData();
        checkContainerDataCount(data, DATA_COUNT);
        addDataSlots(data);

        addSlot(new SlotItemHandler(
                machine.getItems(),
                EctchantmentMachineBlockEntity.SLOT_INPUT,
                44,
                43
        ));

        addSlot(new SlotItemHandler(
                machine.getItems(),
                EctchantmentMachineBlockEntity.SLOT_FUEL,
                80,
                43
        ));

        addSlot(new SlotItemHandler(
                machine.getItems(),
                EctchantmentMachineBlockEntity.SLOT_OUTPUT,
                116,
                43
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(
                        inventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        96 + row * 18
                ));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(
                    inventory,
                    col,
                    8 + col * 18,
                    154
            ));
        }
    }

    public int getEctoplasmicCharge() { return data.get(0); }
    public int getMaxEctoplasmicCharge() { return data.get(1); }
    public int getEnergyStored() { return data.get(2) * 10; }
    public int getEnergyCapacity() { return data.get(3) * 10; }
    public int getRecentFeInput() { return data.get(4); }
    public int getStatus() { return data.get(5); }

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
            if (!moveItemStackTo(stack, 0, 2, false)) {
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
