package com.howierux.mortalexpansion.mixin;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Advance exactly one vanilla operation, leaving inventory and XP handling vanilla. */
@Mixin(AbstractFurnaceBlockEntity.class)
public interface HauntFurnaceAccessor {
    @Accessor("litTime") int mortalExpansion$litTime();
    @Accessor("cookingProgress") int mortalExpansion$progress();
    @Accessor("cookingProgress") void mortalExpansion$progress(int value);
    @Accessor("cookingTotalTime") int mortalExpansion$total();
}
