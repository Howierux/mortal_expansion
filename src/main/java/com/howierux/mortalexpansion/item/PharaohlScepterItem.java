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

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide && level instanceof ServerLevel server) {
            summonMummy(server, player, 1.5D);
            summonMummy(server, player, -1.5D);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private void summonMummy(ServerLevel level, Player owner, double sideOffset) {
        MummyEntity mummy = ModEntities.MUMMY.get().create(level);
        if (mummy == null) {
            return;
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
        level.addFreshEntity(mummy);
    }
}
