package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.gametest.*;
import java.util.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BloodstoneDiamondGameTests {
    private static ItemStack craft(GameTestHelper h, ItemStack... stacks) {
        var input=CraftingInput.of(3,3,Arrays.asList(stacks));
        return h.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING,input,h.getLevel())
                .orElseThrow().value().assemble(input,h.getLevel().registryAccess());
    }
    @GameTest(template="deferred_items_test")
    public static void bloodstone_replaces_diamond_but_preserves_dedicated_gear(GameTestHelper h) {
        var blood = new ItemStack(ModItems.BLOODSTONE.get());
        h.assertTrue(blood.is(Tags.Items.GEMS_DIAMOND) && blood.is(Tags.Items.GEMS),"Bloodstone belongs to shared diamond/gem tags");
        var empty = ItemStack.EMPTY;
        h.assertTrue(craft(h,empty,new ItemStack(Items.BOOK),empty,blood,new ItemStack(Items.OBSIDIAN),blood,
                new ItemStack(Items.OBSIDIAN),new ItemStack(Items.OBSIDIAN),new ItemStack(Items.OBSIDIAN)).is(Items.ENCHANTING_TABLE),
                "Native tagged diamond recipe accepts Bloodstone");
        h.assertTrue(craft(h,blood,blood,blood,blood,blood,blood,blood,blood,blood).is(Items.DIAMOND_BLOCK),"Nine Bloodstone compress into a diamond block");
        h.assertTrue(craft(h,blood,empty,empty,empty,empty,empty,empty,empty,empty).is(Items.DIAMOND),"One Bloodstone converts to one diamond for gear");
        var input=CraftingInput.of(3,3,List.of(blood,blood,blood,empty,new ItemStack(Items.STICK),empty,empty,new ItemStack(Items.STICK),empty));
        var matches=h.getLevel().getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(recipe->recipe.value().matches(input,h.getLevel())).toList();
        h.assertTrue(matches.size()==1 && matches.getFirst().value().assemble(input,h.getLevel().registryAccess()).is(ModItems.BLOODSTONE_PICKAXE.get()),
                "All-Bloodstone gear pattern retains Bloodstone gear; matches: "+matches.stream().map(r->r.id().toString()).toList());
        for(var holder:h.getLevel().getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            var recipe=holder.value();
            // Every ingredient that accepts vanilla diamond should accept
            // Bloodstone too, except the nine deliberately protected gear recipes.
            var result=recipe.getResultItem(h.getLevel().registryAccess());
            boolean gear=Set.of(Items.DIAMOND_HELMET,Items.DIAMOND_CHESTPLATE,Items.DIAMOND_LEGGINGS,Items.DIAMOND_BOOTS,
                    Items.DIAMOND_SWORD,Items.DIAMOND_PICKAXE,Items.DIAMOND_AXE,Items.DIAMOND_SHOVEL,Items.DIAMOND_HOE).contains(result.getItem());
            for(var ingredient:recipe.getIngredients()) if(ingredient.test(new ItemStack(Items.DIAMOND)) && !gear)
                h.assertTrue(ingredient.test(blood),"Diamond ingredient accepts Bloodstone: "+holder.id());
        }
        h.succeed();
    }
}
