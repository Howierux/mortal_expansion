package com.howierux.mortalexpansion.item;

import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import java.util.List;

/** Read-only sensor: the server samples both sources, and held use selects the personal display. */
public final class EmfReaderItem extends Item {
    public EmfReaderItem(Properties properties) { super(properties); }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof ServerPlayer player)) return;
        if (stack == player.getMainHandItem() || stack == player.getOffhandItem()) sample(stack, player);
        else stack.remove(ModDataComponents.EMF_READING.get());
    }
    public static void sample(ItemStack stack, ServerPlayer player) {
        var pos = player.chunkPosition();
        // A player's current chunk is already loaded; never scan adjacent or unloaded chunks.
        if (!player.serverLevel().hasChunk(pos.x, pos.z)) return;
        var sample = new EmfReading(player.level().dimension().location(), pos.x, pos.z, player.getUUID(),
                HauntManager.getValue(player.serverLevel(), pos), PlayerHauntManager.getHundredths(player));
        if (!sample.equals(stack.get(ModDataComponents.EMF_READING.get()))) stack.set(ModDataComponents.EMF_READING.get(), sample);
    }
    public static EmfReading reading(ItemStack stack, Player player) {
        var sample = stack.get(ModDataComponents.EMF_READING.get());
        return sample != null && sample.isCurrentFor(player) ? sample : null;
    }
    public static boolean personal(ItemStack stack, LivingEntity holder) {
        return holder != null && holder.isUsingItem() && holder.getUseItem() == stack;
    }
    public static int value(ItemStack stack, Player player) {
        var sample = reading(stack, player);
        if (sample == null) return -1;
        return personal(stack, player) ? sample.personalHaunt() : sample.chunkHaunt();
    }
    public static int hundredths(ItemStack stack, Player player) {
        var sample = reading(stack, player);
        return sample == null ? -1 : personal(stack, player) ? sample.personalHundredths()
                : sample.chunkHaunt() * FractionalHaunt.UNITS_PER_POINT;
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) sample(stack, serverPlayer);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    @Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return context.getPlayer() == null ? InteractionResult.PASS
                : use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.NONE; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mortal_expansion.emf_reader.chunk").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.mortal_expansion.emf_reader.personal").withStyle(ChatFormatting.AQUA));
        if (stack.is(com.howierux.mortalexpansion.registry.ModItems.PRECISE_EMF_READER.get()))
            tooltip.add(Component.translatable("tooltip.mortal_expansion.emf_reader.precise").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.mortal_expansion.emf_reader.scale").withStyle(ChatFormatting.GRAY));
    }
}
