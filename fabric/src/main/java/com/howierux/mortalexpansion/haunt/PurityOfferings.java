package com.howierux.mortalexpansion.haunt;
import com.howierux.mortalexpansion.catalog.CatalogItems;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Tag membership is extensible; durations are isolated gameplay balance. */
public final class PurityOfferings {
    public static final TagKey<Item> TAG=TagKey.create(Registries.ITEM,ResourceLocation.fromNamespaceAndPath("mortal_expansion","purity_offerings"));
    public static final TagKey<Item> IMPURITY=TagKey.create(Registries.ITEM,ResourceLocation.fromNamespaceAndPath("mortal_expansion","impurity_offerings"));
    private PurityOfferings(){}
    public static int burnTicks(ItemStack stack){return burnTicks(stack,false);}
    public static int burnTicks(ItemStack stack,boolean impure){
        if(!stack.is(impure?IMPURITY:TAG))return 0;
        if(stack.is(CatalogItems.SPIRIT_BOON))return 1200;
        return (stack.is(CatalogItems.GRAVE_MOSS) || stack.is(CatalogItems.SPIRITWOOD_LOG))?600:stack.is(CatalogItems.PURE_ECTOPLASM)?2400:1200;
    }
}
