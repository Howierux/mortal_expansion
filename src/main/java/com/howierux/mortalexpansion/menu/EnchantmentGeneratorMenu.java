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

    private final com.howierux.mortalexpansion.blockentity.EnchantmentGeneratorBlockEntity generator;
    public EnchantmentGeneratorMenu(int id, Inventory inventory) {
        this(id, inventory, null, new net.minecraft.world.SimpleContainer(2), new SimpleContainerData(DATA_COUNT));
    }
    public EnchantmentGeneratorMenu(int id, Inventory inventory,
            com.howierux.mortalexpansion.blockentity.EnchantmentGeneratorBlockEntity generator) {
        this(id, inventory, generator, generator.getMenuItems(), generator.createMenuData());
    }
    private EnchantmentGeneratorMenu(int id, Inventory inventory,
            com.howierux.mortalexpansion.blockentity.EnchantmentGeneratorBlockEntity generator,
            net.minecraft.world.Container items, ContainerData data) {
        super(ModMenus.ENCHANTMENT_GENERATOR.get(), id);
        this.generator = generator;
        this.data = data;
        checkContainerDataCount(data, DATA_COUNT);
        addDataSlots(data);
        addSlot(new net.minecraft.world.inventory.Slot(items, 0, 44, 52) {
            @Override public boolean mayPlace(ItemStack stack) {
                return generator == null ? !net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentsForCrafting(stack).isEmpty()
                        : generator.canAccept(stack);
            }
            @Override public int getMaxStackSize() { return 1; }
        });
        addSlot(new net.minecraft.world.inventory.Slot(items, 1, 116, 52) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new net.minecraft.world.inventory.Slot(inventory, col + row * 9 + 9, 8 + col * 18, 125 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new net.minecraft.world.inventory.Slot(inventory, col, 8 + col * 18, 183));
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
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem(); var original = stack.copy();
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return generator == null || (!generator.isRemoved() && player.level().getBlockEntity(generator.getBlockPos()) == generator
                && player.distanceToSqr(generator.getBlockPos().getCenter()) <= 64);
    }
}
