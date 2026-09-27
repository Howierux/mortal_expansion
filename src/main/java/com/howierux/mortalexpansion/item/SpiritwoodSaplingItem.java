package com.howierux.mortalexpansion.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Sapling item that always carries Minecraft's foil/glint rendering.
 */
public final class SpiritwoodSaplingItem extends BlockItem {
    public SpiritwoodSaplingItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
