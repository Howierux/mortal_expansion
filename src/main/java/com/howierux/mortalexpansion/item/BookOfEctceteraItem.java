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
        if (level.isClientSide) com.howierux.mortalexpansion.client.EctceteraBookScreen.open(hand);
        if(player instanceof net.minecraft.server.level.ServerPlayer server)com.howierux.mortalexpansion.guide.SpectralInsight.absorb(server,stack);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        int tier = com.howierux.mortalexpansion.guide.EctceteraProgression.tier(stack);
        tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.mortal_expansion.ectcetera.tier." + tier)
                .withStyle(net.minecraft.ChatFormatting.AQUA));
        if(tier>=2)tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.mortal_expansion.ectcetera.aware",Integer.bitCount(com.howierux.mortalexpansion.guide.SpectralInsight.secrets(stack))).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        if (tier < 3) tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.mortal_expansion.ectcetera.upgrade",
                com.howierux.mortalexpansion.guide.EctceteraProgression.upgradeIngredient(tier).getDescription())
                .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    @Override public void inventoryTick(ItemStack stack,Level level,net.minecraft.world.entity.Entity entity,int slot,boolean selected){
        if(entity instanceof net.minecraft.server.level.ServerPlayer p)com.howierux.mortalexpansion.guide.SpectralInsight.absorb(p,stack);
    }
    @Override
    public boolean isFoil(ItemStack stack) { return true; }
}
