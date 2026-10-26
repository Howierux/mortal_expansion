package com.howierux.mortalexpansion.item;

import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

public final class AreaShovelItem extends ShovelItem {
    public static final int CHARGE_TICKS=40;
    public AreaShovelItem(Tier tier,Properties properties){super(tier,properties);}
    private boolean charging(UseOnContext context){return context.getHand()==InteractionHand.MAIN_HAND
            && context.getPlayer()!=null && !context.getPlayer().isShiftKeyDown()
            && context.getItemInHand().is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_SHOVEL)
            && context.getLevel().getBlockState(context.getClickedPos()).is(net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL);}
    @Override public InteractionResult useOn(UseOnContext context){
        if(charging(context)){context.getPlayer().startUsingItem(context.getHand());return InteractionResult.CONSUME;}return super.useOn(context);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(hand!=InteractionHand.MAIN_HAND || !stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_SHOVEL) || player.isShiftKeyDown())return InteractionResultHolder.pass(stack);
        player.startUsingItem(hand);return InteractionResultHolder.consume(stack);
    }
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 72000;}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.NONE;}
    @Override public void onUseTick(Level level,LivingEntity entity,ItemStack stack,int remaining){
        if(entity instanceof net.minecraft.server.level.ServerPlayer p && stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_SHOVEL)
                && getUseDuration(stack,entity)-remaining==CHARGE_TICKS){
            p.displayClientMessage(Component.translatable("message.mortal_expansion.shovel_charged"),true);
            p.playNotifySound(net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,net.minecraft.sounds.SoundSource.PLAYERS,0.6F,1.3F);
        }
    }
    @Override public void releaseUsing(ItemStack stack,Level level,LivingEntity entity,int remaining){
        if(!(entity instanceof net.minecraft.server.level.ServerPlayer p) || p.getMainHandItem()!=stack
                || !stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_SHOVEL) || p.isShiftKeyDown()
                || getUseDuration(stack,entity)-remaining<CHARGE_TICKS)return;
        var hit=p.pick(p.blockInteractionRange(),1,false);
        if(hit instanceof net.minecraft.world.phys.BlockHitResult block && hit.getType()==net.minecraft.world.phys.HitResult.Type.BLOCK)
            com.howierux.mortalexpansion.equipment.EquipmentMining.connected(p,block.getBlockPos());
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<Component> tooltip,TooltipFlag flag){
        super.appendHoverText(stack,context,tooltip,flag);
        tooltip.add(Component.translatable("tooltip.mortal_expansion.shovel.area").withStyle(net.minecraft.ChatFormatting.GRAY));
        if(stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_SHOVEL))tooltip.add(Component.translatable("tooltip.mortal_expansion.shovel.charge",CHARGE_TICKS/20,64).withStyle(net.minecraft.ChatFormatting.AQUA));
    }
}
