package com.howierux.mortalexpansion.recipe;

import com.howierux.mortalexpansion.registry.ModRecipeSerializers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

/** Vanilla transformation and component preservation, with an intentionally empty template slot.
 * Vanilla isIncomplete rejects Ingredient.EMPTY, so this supported recipe extension checks only real inputs.
 */
public final class TemplateFreeSmithingRecipe extends SmithingTransformRecipe {
    private final Ingredient baseIngredient;
    private final Ingredient additionIngredient;
    private final ItemStack output;

    public TemplateFreeSmithingRecipe(Ingredient base, Ingredient addition, ItemStack result) {
        super(Ingredient.EMPTY, base, addition, result);
        baseIngredient = base; additionIngredient = addition; output = result;
    }

    @Override public boolean isIncomplete() {
        return baseIngredient.hasNoItems() || additionIngredient.hasNoItems();
    }
    @Override public RecipeSerializer<?> getSerializer() { return ModRecipeSerializers.TEMPLATE_FREE_SMITHING.get(); }

    public static final class Serializer implements RecipeSerializer<TemplateFreeSmithingRecipe> {
        private static final MapCodec<TemplateFreeSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(recipe -> recipe.baseIngredient),
                Ingredient.CODEC_NONEMPTY.fieldOf("addition").forGetter(recipe -> recipe.additionIngredient),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output)
        ).apply(instance, TemplateFreeSmithingRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, TemplateFreeSmithingRecipe> STREAM = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.baseIngredient,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.additionIngredient,
                ItemStack.STREAM_CODEC, recipe -> recipe.output,
                TemplateFreeSmithingRecipe::new);
        @Override public MapCodec<TemplateFreeSmithingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, TemplateFreeSmithingRecipe> streamCodec() { return STREAM; }
    }
}
