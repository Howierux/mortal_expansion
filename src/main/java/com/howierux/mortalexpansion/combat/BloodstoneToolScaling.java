package com.howierux.mortalexpansion.combat;

import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Personal Haunt scales held Bloodstone tools; worn-pickaxe and chunk repairs stay separate. */
public final class BloodstoneToolScaling {
    public static final ResourceLocation DAMAGE = ResourceLocation.parse("mortal_expansion:bloodstone_power");
    public static final ResourceLocation SPEED = ResourceLocation.parse("mortal_expansion:bloodstone_speed");
    private BloodstoneToolScaling() {}
    public static double multiplier(int tier) { return 1.0 + 0.1 * (Math.clamp(tier,0,6)-1); }
    public static boolean tool(ItemStack stack) {
        return stack.is(ModItems.BLOODSTONE_SWORD.get()) || stack.is(ModItems.BLOODSTONE_PICKAXE.get())
                || stack.is(ModItems.BLOODSTONE_AXE.get()) || stack.is(ModItems.BLOODSTONE_SHOVEL.get()) || stack.is(ModItems.BLOODSTONE_HOE.get());
    }
    public static void update(Player player) {
        double bonus = tool(player.getMainHandItem()) && !player.isSpectator() ? multiplier(PlayerHauntManager.getTier(player).id())-1 : 0;
        updateAttribute(player.getAttribute(Attributes.ATTACK_DAMAGE), DAMAGE, bonus);
        updateAttribute(player.getAttribute(Attributes.ATTACK_SPEED), SPEED, bonus);
    }
    private static void updateAttribute(AttributeInstance attribute, ResourceLocation id, double bonus) {
        if (attribute == null) return;
        var current = attribute.getModifier(id);
        if (current != null && current.amount() == bonus) return;
        attribute.removeModifier(id);
        if (bonus != 0) attribute.addTransientModifier(new AttributeModifier(id,bonus,AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
    @SubscribeEvent public static void tick(PlayerTickEvent.Pre event) {
        if (!event.getEntity().level().isClientSide) update(event.getEntity());
    }
    @SubscribeEvent public static void mining(PlayerEvent.BreakSpeed event) {
        var player = event.getEntity();
        if (!tool(player.getMainHandItem()) || player.isSpectator()) return;
        double factor;
        if (!player.level().isClientSide) factor = multiplier(PlayerHauntManager.getTier(player).id());
        else {
            // Attack-speed attributes are natively synchronized; personal
            // attachments need no additional client packet just for mining.
            var attribute = player.getAttribute(Attributes.ATTACK_SPEED);
            var modifier = attribute == null ? null : attribute.getModifier(SPEED);
            factor = modifier == null ? 1 : 1 + modifier.amount();
        }
        event.setNewSpeed((float)(event.getNewSpeed()*factor));
    }
}
