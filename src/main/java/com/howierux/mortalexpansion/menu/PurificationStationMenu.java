package com.howierux.mortalexpansion.menu;

import com.howierux.mortalexpansion.blockentity.PurificationStationBlockEntity;
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

public final class PurificationStationMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 8;
    private static final int MACHINE_SLOTS = 4;

    private final ContainerData data;

    public PurificationStationMenu(
            int containerId,
            Inventory inventory,
            FriendlyByteBuf extraData
    ) {
        this(
                containerId,
                inventory,
                getStation(inventory, extraData.readBlockPos()),
                new SimpleContainerData(DATA_COUNT)
        );
    }

    private static PurificationStationBlockEntity getStation(
            Inventory inventory,
            BlockPos pos
    ) {
        if (inventory.player.level().getBlockEntity(pos)
                instanceof PurificationStationBlockEntity station) {
            return station;
        }

        throw new IllegalStateException("Purification Station block entity missing");
    }

    public PurificationStationMenu(
            int containerId,
            Inventory inventory,
            PurificationStationBlockEntity station
    ) {
        this(containerId, inventory, station, station.createMenuData());
    }

    private PurificationStationMenu(
            int containerId,
            Inventory inventory,
            PurificationStationBlockEntity station,
            ContainerData data
    ) {
        super(ModMenus.PURIFICATION_STATION.get(), containerId);

        this.data = data;
        checkContainerDataCount(data, DATA_COUNT);
        addDataSlots(data);

        addSlot(new SlotItemHandler(
                station.getItems(),
                PurificationStationBlockEntity.SLOT_RAW_ECTOPLASM,
                44,
                50
        ));

        addSlot(new SlotItemHandler(
                station.getItems(),
                PurificationStationBlockEntity.SLOT_GRAVE_MOSS,
                44,
                74
        ));

        addSlot(new SlotItemHandler(
                station.getItems(),
                PurificationStationBlockEntity.SLOT_OUTPUT,
                116,
                62
        ) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addSlot(new SlotItemHandler(
                station.getItems(),
                PurificationStationBlockEntity.SLOT_WATER_BUCKET,
                68,
                44
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

    public int getWater() { return data.get(0); }
    public int getWaterCapacity() { return data.get(1); }
    public int getEnergy() { return data.get(2) * 10; }
    public int getEnergyCapacity() { return data.get(3) * 10; }
    public int getProgress() { return data.get(4); }
    public int getMaxProgress() { return data.get(5); }
    public int getFeUsed() { return data.get(6) * 10; }
    public int getRecentFeInput() { return data.get(7); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < MACHINE_SLOTS) {
            if (!moveItemStackTo(
                    stack,
                    MACHINE_SLOTS,
                    slots.size(),
                    true
            )) {
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
