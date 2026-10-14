package com.howierux.mortalexpansion.item;

import com.howierux.mortalexpansion.entity.mob.MummyEntity;
import com.howierux.mortalexpansion.registry.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class PharaohlScepterItem extends Item {
    public static final int CAPACITY=12;
    public static int charge(ItemStack stack){return Math.clamp(stack.getOrDefault(
            com.howierux.mortalexpansion.registry.ModDataComponents.SCEPTER_CHARGE.get(),0),0,CAPACITY);}
    public static boolean recharge(ItemStack scepter,ItemStack ammo){
        int amount=ammo.is(com.howierux.mortalexpansion.registry.ModItems.RAW_ECTOPLASM.get())?4:
                ammo.is(com.howierux.mortalexpansion.registry.ModItems.PURE_ECTOPLASM.get())?12:0;
        if(amount==0 || charge(scepter)>=CAPACITY)return false;
        scepter.set(com.howierux.mortalexpansion.registry.ModDataComponents.SCEPTER_CHARGE.get(),
                Math.min(CAPACITY,charge(scepter)+amount));ammo.shrink(1);return true;
    }
    @Override public boolean overrideOtherStackedOnMe(ItemStack stack,ItemStack cursor,
            net.minecraft.world.inventory.Slot slot,net.minecraft.world.inventory.ClickAction action,
            Player player,net.minecraft.world.entity.SlotAccess cursorAccess){
        if(action!=net.minecraft.world.inventory.ClickAction.SECONDARY || !slot.allowModification(player)
                || !(cursor.is(com.howierux.mortalexpansion.registry.ModItems.RAW_ECTOPLASM.get())
                || cursor.is(com.howierux.mortalexpansion.registry.ModItems.PURE_ECTOPLASM.get())))return false;
        recharge(stack,cursor);return true;
    }
    @Override public boolean isBarVisible(ItemStack stack){return true;}
    @Override public int getBarWidth(ItemStack stack){return Math.round(13.0F*charge(stack)/CAPACITY);}
    @Override public int getBarColor(ItemStack stack){return 0x85E8DF;}
    @Override public void appendHoverText(ItemStack stack,Item.TooltipContext context,
            java.util.List<net.minecraft.network.chat.Component> lines,net.minecraft.world.item.TooltipFlag flag){
        super.appendHoverText(stack,context,lines,flag);
        lines.add(net.minecraft.network.chat.Component.translatable("tooltip.mortal_expansion.scepter.charge",charge(stack),CAPACITY));
        lines.add(net.minecraft.network.chat.Component.translatable("tooltip.mortal_expansion.scepter.reload"));
    }
    private static final int COOLDOWN_TICKS = 15 * 20;

    public PharaohlScepterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (charge(stack)==0) {
            if(!level.isClientSide)player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "message.mortal_expansion.scepter.empty"),true);
            return InteractionResultHolder.fail(stack);
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide && level instanceof ServerLevel server) {
            boolean first=summonMummy(server, player, 1.5D);
            boolean second=summonMummy(server, player, -1.5D);
            if(!first && !second)return InteractionResultHolder.fail(stack);
            stack.set(com.howierux.mortalexpansion.registry.ModDataComponents.SCEPTER_CHARGE.get(),charge(stack)-1);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private boolean summonMummy(ServerLevel level, Player owner, double sideOffset) {
        MummyEntity mummy = ModEntities.MUMMY.get().create(level);
        if (mummy == null) {
            return false;
        }

        double yawRadians = Math.toRadians(owner.getYRot());
        double sideX = Math.cos(yawRadians) * sideOffset;
        double sideZ = Math.sin(yawRadians) * sideOffset;

        mummy.moveTo(
                owner.getX() + sideX,
                owner.getY(),
                owner.getZ() + sideZ,
                owner.getYRot(),
                0.0F
        );
        mummy.setAlliedOwner(owner.getUUID());
        return level.addFreshEntity(mummy);
    }
}
