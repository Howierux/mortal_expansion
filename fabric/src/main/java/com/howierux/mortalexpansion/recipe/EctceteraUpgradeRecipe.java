package com.howierux.mortalexpansion.recipe;

import com.howierux.mortalexpansion.guide.EctceteraProgression;
import com.howierux.mortalexpansion.catalog.Catalog;
import com.howierux.mortalexpansion.guide.KnowledgeData;
import com.howierux.mortalexpansion.catalog.CatalogItems;
import com.howierux.mortalexpansion.guide.BookRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/** Supported shapeless recipe extension for a sequential, component-preserving book upgrade. */
public final class EctceteraUpgradeRecipe extends ShapelessRecipe {
    private final int fromTier;
    public EctceteraUpgradeRecipe(int fromTier) {
        super("ectcetera_upgrades", CraftingBookCategory.MISC, EctceteraProgression.book(fromTier + 1),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(CatalogItems.BOOK_OF_ECTCETERA),
                        Ingredient.of(EctceteraProgression.upgradeIngredient(fromTier))));
        this.fromTier = fromTier;
    }
    private static ItemStack inputBook(CraftingInput input) {
        return input.items().stream().filter(stack -> stack.is(CatalogItems.BOOK_OF_ECTCETERA))
                .findFirst().orElse(ItemStack.EMPTY);
    }
    @Override public boolean matches(CraftingInput input, Level level) {
        return super.matches(input, level) && EctceteraProgression.tier(inputBook(input)) == fromTier;
    }
    @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        if (input.ingredientCount() != 2 || inputBook(input).isEmpty()
                || EctceteraProgression.tier(inputBook(input)) != fromTier
                || input.items().stream().noneMatch(stack -> stack.is(EctceteraProgression.upgradeIngredient(fromTier)))) return ItemStack.EMPTY;
        var result = inputBook(input).copyWithCount(1);
        result.set(Catalog.BOOK_TIER, fromTier + 1);
        if(fromTier==3)result.set(KnowledgeData.BOOK_SECRETS,com.howierux.mortalexpansion.guide.SpectralInsight.ALL);
        return result;
    }
    @Override public RecipeSerializer<?> getSerializer() { return BookRecipes.UPGRADE; }

    public static final class Serializer implements RecipeSerializer<EctceteraUpgradeRecipe> {
        private static final MapCodec<EctceteraUpgradeRecipe> CODEC = Codec.intRange(0, 3)
                .fieldOf("from_tier").xmap(EctceteraUpgradeRecipe::new, recipe -> recipe.fromTier);
        private static final StreamCodec<RegistryFriendlyByteBuf, EctceteraUpgradeRecipe> STREAM = StreamCodec.of(
                (buffer, recipe) -> buffer.writeVarInt(recipe.fromTier), buffer -> new EctceteraUpgradeRecipe(buffer.readVarInt()));
        @Override public MapCodec<EctceteraUpgradeRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, EctceteraUpgradeRecipe> streamCodec() { return STREAM; }
    }
}
