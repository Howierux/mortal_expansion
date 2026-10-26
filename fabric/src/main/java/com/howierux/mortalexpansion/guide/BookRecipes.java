package com.howierux.mortalexpansion.guide;
import com.howierux.mortalexpansion.catalog.Catalog;
import com.howierux.mortalexpansion.recipe.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
public final class BookRecipes {
 public static final RecipeSerializer<EctceteraUpgradeRecipe> UPGRADE=Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,Catalog.id("ectcetera_upgrade"),new EctceteraUpgradeRecipe.Serializer());
 public static final RecipeSerializer<MortalityResearchStationRecipe> STATION=Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,Catalog.id("mortality_research_station"),new MortalityResearchStationRecipe.Serializer());
 public static final RecipeSerializer<TemplateFreeSmithingRecipe> SMITHING=Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,Catalog.id("template_free_smithing"),new TemplateFreeSmithingRecipe.Serializer());
 public static void initialize(){}
}
