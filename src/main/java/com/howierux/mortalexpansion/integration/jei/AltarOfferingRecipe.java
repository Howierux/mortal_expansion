package com.howierux.mortalexpansion.integration.jei;
import com.howierux.mortalexpansion.haunt.PurityOfferings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import java.util.*;
/** JEI-independent display data, derived from the same loaded tags and duration rules as the altars. */
public record AltarOfferingRecipe(ItemStack offering,int burnTicks) {
    public AltarOfferingRecipe { offering=offering.copyWithCount(1); }
    public ItemStack remainder(){return offering.copyWithCount(1).getCraftingRemainingItem();}
    public static List<AltarOfferingRecipe> forAltar(boolean impure){
        var recipes=new ArrayList<AltarOfferingRecipe>();
        BuiltInRegistries.ITEM.getTag(impure?PurityOfferings.IMPURITY:PurityOfferings.TAG).ifPresent(items->{
            for(var item:items){var stack=item.value().getDefaultInstance();int ticks=PurityOfferings.burnTicks(stack,impure);
                if(ticks>0)recipes.add(new AltarOfferingRecipe(stack,ticks));}
        });
        return List.copyOf(recipes);
    }
}
