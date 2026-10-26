package com.howierux.mortalexpansion.item;

import com.howierux.mortalexpansion.catalog.CatalogArmor;
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
        haint = !material.equals(CatalogArmor.GHOST_GARB);
        spectralite = (material.equals(CatalogArmor.SPECTRALITE) || material.equals(CatalogArmor.SPECTRALITE_HAINT));
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
