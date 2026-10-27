package com.howierux.mortalexpansion.integration.jei;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.chat.Component;

/** Display data only. This addon never executes a machine operation. */
public record ProcessRecipe(List<Ingredient> inputs, ItemStack output, List<Component> notes) {
    public ProcessRecipe { inputs = List.copyOf(inputs); output = output.copy(); notes = List.copyOf(notes); }
}
