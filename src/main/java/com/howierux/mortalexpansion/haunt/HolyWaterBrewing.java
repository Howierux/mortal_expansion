package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.registry.ModPotions;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

public final class HolyWaterBrewing {
    private HolyWaterBrewing(){}
    public static ItemStack bottle(){return PotionContents.createItemStack(Items.POTION,ModPotions.HOLY_WATER);}
    public static ItemStack splash(){return PotionContents.createItemStack(Items.SPLASH_POTION,ModPotions.HOLY_WATER);}
    @SubscribeEvent public static void tooltip(net.neoforged.neoforge.event.entity.player.ItemTooltipEvent event){
        var contents=event.getItemStack().getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS,PotionContents.EMPTY);
        if(contents.is(ModPotions.HOLY_WATER)){
            event.getToolTip().add(net.minecraft.network.chat.Component.translatable("tooltip.mortal_expansion.consecration").withStyle(net.minecraft.ChatFormatting.AQUA));
            event.getToolTip().add(net.minecraft.network.chat.Component.translatable("tooltip.mortal_expansion.consecration_cooldown").withStyle(net.minecraft.ChatFormatting.GRAY));
        }
    }
    @SubscribeEvent public static void recipes(RegisterBrewingRecipesEvent event){
        // The standard BrewingRecipe is discoverable by JEI without a second recipe implementation.
        event.getBuilder().addRecipe(new net.neoforged.neoforge.common.brewing.BrewingRecipe(
                Ingredient.of(Items.GLASS_BOTTLE),Ingredient.of(ModItems.HOLY_WATER_BUCKET.get()),bottle()){
            @Override public boolean isInput(ItemStack input){
                // Native brewing slots hold one bottle. Refuse oversized direct/automated inputs rather than deleting extras.
                return input.getCount()==1 && super.isInput(input);
            }
        });
        // Vanilla's gunpowder container conversion preserves the native Holy Water potion contents.
        // BrewingStandBlockEntity already returns the bucket through the ingredient's crafting remainder.
    }
}
