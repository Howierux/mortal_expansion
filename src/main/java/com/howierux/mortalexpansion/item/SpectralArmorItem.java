package com.howierux.mortalexpansion.item;

import com.howierux.mortalexpansion.combat.IntangibleCombatHandler;
import com.howierux.mortalexpansion.registry.ModArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class SpectralArmorItem extends ArmorItem {
    private final boolean fireProofItem;

    public SpectralArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        fireProofItem = material.equals(ModArmorMaterials.SPECTRALITE);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
            List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int reduction = Math.round(IntangibleCombatHandler.ARMOR_REDUCTION_PER_PIECE * 100.0F);
        tooltip.add(Component.translatable("tooltip.mortal_expansion.armor.spectral_protection", reduction)
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.mortal_expansion.armor.spectral_set",
                reduction * IntangibleCombatHandler.MAX_PROTECTIVE_ARMOR_PIECES)
                .withStyle(ChatFormatting.GRAY));
        if (fireProofItem) {
            tooltip.add(Component.translatable("tooltip.mortal_expansion.armor.fireproof_item")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
