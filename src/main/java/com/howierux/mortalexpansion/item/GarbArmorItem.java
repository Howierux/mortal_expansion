package com.howierux.mortalexpansion.item;

import com.howierux.mortalexpansion.registry.ModArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import java.util.List;

/** Shared benefit summaries; the equipment tags decide which full-set effects apply. */
public final class GarbArmorItem extends SpectralArmorItem {
    private final boolean haint;
    private final boolean spectralite;

    public GarbArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        haint = !material.equals(ModArmorMaterials.GHOST_GARB);
        spectralite = material.equals(ModArmorMaterials.SPECTRALITE);
    }

    /** Supported NeoForge per-item texture hook keeps the hybrid's material and stats intact. */
    @Override public net.minecraft.resources.ResourceLocation getArmorTexture(ItemStack stack,
            net.minecraft.world.entity.Entity entity,net.minecraft.world.entity.EquipmentSlot slot,
            ArmorMaterial.Layer layer,boolean innerModel){
        return spectralite?net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                com.howierux.mortalexpansion.MortalExpansion.MOD_ID,
                "textures/models/armor/spectralite_haint_woven_layer_"+(innerModel?2:1)+".png"):null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // Plain garb does not have Spectralite's protection against intangible attackers.
        if (spectralite) super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.mortal_expansion.garb." + (spectralite ? "spectralite" : "stats"))
                .withStyle(ChatFormatting.GRAY));
        for (String benefit : new String[]{"intangible", "haunt", "breathing", "vulnerable"}) {
            tooltip.add(Component.translatable("tooltip.mortal_expansion.garb." + benefit).withStyle(ChatFormatting.AQUA));
        }
        if (haint) tooltip.add(Component.translatable("tooltip.mortal_expansion.garb.jump").withStyle(ChatFormatting.AQUA));
    }
}
