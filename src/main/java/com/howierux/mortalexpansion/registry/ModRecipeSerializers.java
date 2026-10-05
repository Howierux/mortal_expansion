package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModRecipeSerializers {
    private ModRecipeSerializers() {}
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MortalExpansion.MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, TemplateFreeSmithingRecipe.Serializer> TEMPLATE_FREE_SMITHING =
            SERIALIZERS.register("template_free_smithing", TemplateFreeSmithingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, com.howierux.mortalexpansion.recipe.EctceteraUpgradeRecipe.Serializer> ECTCETERA_UPGRADE =
            SERIALIZERS.register("ectcetera_upgrade", com.howierux.mortalexpansion.recipe.EctceteraUpgradeRecipe.Serializer::new);
    public static void register(IEventBus bus) { SERIALIZERS.register(bus); }
}
