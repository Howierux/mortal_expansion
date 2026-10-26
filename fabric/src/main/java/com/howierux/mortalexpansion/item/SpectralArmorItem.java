package com.howierux.mortalexpansion.item;


import com.howierux.mortalexpansion.catalog.CatalogArmor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SpectralArmorItem extends ArmorItem {
    private final boolean fireProofItem;

    public SpectralArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        fireProofItem = (material.equals(CatalogArmor.SPECTRALITE) || material.equals(CatalogArmor.SPECTRALITE_HAINT));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
            List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int reduction = Math.round(0.05F * 100.0F);
        tooltip.add(Component.translatable("tooltip.mortal_expansion.armor.spectral_protection", reduction)
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.mortal_expansion.armor.spectral_set",
                reduction * 4)
                .withStyle(ChatFormatting.GRAY));
        if (fireProofItem) {
            tooltip.add(Component.translatable("tooltip.mortal_expansion.armor.fireproof_item")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
