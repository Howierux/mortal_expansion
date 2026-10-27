package com.howierux.mortalexpansion.haunt;
import net.minecraft.world.item.*;import net.minecraft.world.item.alchemy.*;
public final class HolyWaterBrewing {
 public static ItemStack bottle(){return PotionContents.createItemStack(Items.POTION,com.howierux.mortalexpansion.content.RemainingRuntime.HOLY_POTION);}
 public static ItemStack splash(){return PotionContents.createItemStack(Items.SPLASH_POTION,com.howierux.mortalexpansion.content.RemainingRuntime.HOLY_POTION);}
 public static boolean recipe(ItemStack input,ItemStack ingredient){return input.is(Items.GLASS_BOTTLE)&&input.getCount()==1&&ingredient.is(com.howierux.mortalexpansion.catalog.CatalogItems.HOLY_WATER_BUCKET);}
}
