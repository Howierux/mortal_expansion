package com.howierux.mortalexpansion.haunt;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Tag membership is extensible; durations are isolated gameplay balance. */
public final class PurityOfferings {
    public static final TagKey<Item> TAG=TagKey.create(Registries.ITEM,ResourceLocation.fromNamespaceAndPath("mortal_expansion","purity_offerings"));
    private PurityOfferings(){}
    public static int burnTicks(ItemStack stack){
        if(!stack.is(TAG))return 0;
        return (stack.is(ModItems.GRAVE_MOSS.get()) || stack.is(ModItems.SPIRITWOOD_LOG.get()))?600:stack.is(ModItems.PURE_ECTOPLASM.get())?2400:1200;
    }
}
