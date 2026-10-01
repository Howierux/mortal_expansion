package com.howierux.mortalexpansion.item;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Reusable guide; existing books always use the current mod's documentation. */
public final class BookOfEctceteraItem extends Item {
    public BookOfEctceteraItem(Properties properties) { super(properties); }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) com.howierux.mortalexpansion.client.EctceteraBookScreen.open();
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    @Override
    public boolean isFoil(ItemStack stack) { return true; }
}
