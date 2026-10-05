package com.howierux.mortalexpansion.item;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Reusable guide with per-stack chapter progression and legacy full-content support. */
public final class BookOfEctceteraItem extends Item {
    public BookOfEctceteraItem(Properties properties) { super(properties); }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) com.howierux.mortalexpansion.client.EctceteraBookScreen.open(com.howierux.mortalexpansion.guide.EctceteraProgression.tier(stack));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        int tier = com.howierux.mortalexpansion.guide.EctceteraProgression.tier(stack);
        tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.mortal_expansion.ectcetera.tier." + tier)
                .withStyle(net.minecraft.ChatFormatting.AQUA));
        if (tier < 3) tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.mortal_expansion.ectcetera.upgrade",
                com.howierux.mortalexpansion.guide.EctceteraProgression.upgradeIngredient(tier).getDescription())
                .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    @Override
    public boolean isFoil(ItemStack stack) { return true; }
}
