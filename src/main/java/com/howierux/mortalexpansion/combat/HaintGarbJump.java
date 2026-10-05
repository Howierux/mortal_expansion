package com.howierux.mortalexpansion.combat;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Transient equipment bonus, additive with native jump effects and never saved on the player. */
public final class HaintGarbJump {
    private HaintGarbJump() {}
    public static final double BONUS = 0.3D;
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "haint_garb_jump");
    private static final TagKey<Item> HAINT_GARB = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "haints_garb"));

    public static boolean fullSet(Player player) {
        if (player.isSpectator()) return false;
        for (var stack : player.getArmorSlots()) if (stack.isEmpty() || !stack.is(HAINT_GARB)) return false;
        return true;
    }

    @SubscribeEvent
    public static void tick(PlayerTickEvent.Pre event) {
        // Both sides need the attribute before the client predicts a jump.
        var player = event.getEntity();
        var jump = player.getAttribute(Attributes.JUMP_STRENGTH);
        if (jump == null) return;
        if (fullSet(player)) {
            if (!jump.hasModifier(ID)) jump.addTransientModifier(new AttributeModifier(ID, BONUS, AttributeModifier.Operation.ADD_VALUE));
        } else jump.removeModifier(ID);
    }
}
