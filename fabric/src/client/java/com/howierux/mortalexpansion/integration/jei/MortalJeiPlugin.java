package com.howierux.mortalexpansion.integration.jei;

import com.howierux.mortalexpansion.machine.EctchantmentEntity;
import com.howierux.mortalexpansion.machine.PurificationStationEntity;
import com.howierux.mortalexpansion.guide.EctceteraProgression;
import com.howierux.mortalexpansion.catalog.*;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public final class MortalJeiPlugin implements IModPlugin {
    public static final RecipeType<ProcessRecipe> ECTCHANTMENT = type("ectchantment");
    public static final RecipeType<ProcessRecipe> PURIFICATION = type("purification");
    public static final RecipeType<ProcessRecipe> CAULDRON = type("heated_cauldron");
    public static final RecipeType<AltarOfferingRecipe> PURITY_OFFERINGS = RecipeType.create("mortal_expansion","purity_offerings",AltarOfferingRecipe.class);
    public static final RecipeType<AltarOfferingRecipe> IMPURITY_OFFERINGS = RecipeType.create("mortal_expansion","impurity_offerings",AltarOfferingRecipe.class);
    private static RecipeType<ProcessRecipe> type(String path) {
        return RecipeType.create("mortal_expansion", path, ProcessRecipe.class);
    }
    @Override public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("mortal_expansion", "jei");
    }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            new ProcessCategory(gui, ECTCHANTMENT, "jei.mortal_expansion.ectchantment", new ItemStack(com.howierux.mortalexpansion.catalog.CatalogItems.ECTCHANTMENT_MACHINE)),
            new ProcessCategory(gui, PURIFICATION, "jei.mortal_expansion.purification", new ItemStack(com.howierux.mortalexpansion.catalog.CatalogItems.PURIFICATION_STATION)),
            new ProcessCategory(gui, CAULDRON, "jei.mortal_expansion.cauldron", new ItemStack(Items.CAULDRON)),
            new AltarOfferingCategory(gui,PURITY_OFFERINGS,false),new AltarOfferingCategory(gui,IMPURITY_OFFERINGS,true));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        // Ask the actual machine for transmutation results, including all current tag members.
        var machine = new EctchantmentEntity(BlockPos.ZERO, com.howierux.mortalexpansion.catalog.CatalogBlocks.ECTCHANTMENT_MACHINE.defaultBlockState());
        var operations = new ArrayList<ProcessRecipe>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack input = item.getDefaultInstance();
            if (input.isEmpty()) continue;
            ItemStack output = machine.result(input);
            if (output.isEmpty() && !com.howierux.mortalexpansion.combat.DemiTangibleDamage.isDemiTangibleWeapon(input)) {
                output = input.copyWithCount(1);
                output.set(com.howierux.mortalexpansion.combat.CombatRuntime.DEMI_COMPONENT, true);
                output.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }
            if (!output.isEmpty()) operations.add(new ProcessRecipe(List.of(Ingredient.of(input)), output,
                List.of(Component.translatable("jei.mortal_expansion.charge", 4000,
                    40000), Component.translatable("jei.mortal_expansion.charge_fuel"))));
        }
        registration.addRecipes(PURITY_OFFERINGS,AltarOfferingRecipe.forAltar(false));
        registration.addRecipes(IMPURITY_OFFERINGS,AltarOfferingRecipe.forAltar(true));
        registration.addRecipes(ECTCHANTMENT, operations);
        registration.addRecipes(PURIFICATION, List.of(new ProcessRecipe(
            List.of(Ingredient.of(com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM), Ingredient.of(PurificationStationEntity.REAGENTS), Ingredient.of(Items.WATER_BUCKET)),
            new ItemStack(com.howierux.mortalexpansion.catalog.CatalogItems.PURE_ECTOPLASM), List.of(
                Component.translatable("jei.mortal_expansion.purification_cost", 1000,
                    PurificationStationEntity.FE_PER_CRAFT, PurificationStationEntity.PROCESS_TICKS),
                Component.translatable("jei.mortal_expansion.bucket_return")))));
        registration.addRecipes(CAULDRON, List.of(
            cauldron(Items.WATER_BUCKET, com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM, com.howierux.mortalexpansion.catalog.CatalogItems.LIQUID_ECTOPLASM_BUCKET),
            cauldron(Items.WATER_BUCKET, com.howierux.mortalexpansion.catalog.CatalogItems.GRAVE_MOSS, com.howierux.mortalexpansion.catalog.CatalogItems.HOLY_WATER_BUCKET),
            cauldron(com.howierux.mortalexpansion.catalog.CatalogItems.LIQUID_ECTOPLASM_BUCKET, com.howierux.mortalexpansion.catalog.CatalogItems.GRAVE_MOSS, com.howierux.mortalexpansion.catalog.CatalogItems.PURIFIED_ECTOPLASM_BUCKET),
            cauldron(com.howierux.mortalexpansion.catalog.CatalogItems.HOLY_WATER_BUCKET, com.howierux.mortalexpansion.catalog.CatalogItems.RAW_ECTOPLASM, com.howierux.mortalexpansion.catalog.CatalogItems.PURIFIED_ECTOPLASM_BUCKET)));
        registration.addIngredientInfo(com.howierux.mortalexpansion.catalog.CatalogItems.BOOK_OF_ECTCETERA, Component.translatable("jei.mortal_expansion.book_info"));
        registration.addIngredientInfo(com.howierux.mortalexpansion.catalog.CatalogItems.ECTCHANTMENT_MACHINE, Component.translatable("jei.mortal_expansion.charge_fuel"));
        registration.addIngredientInfo(com.howierux.mortalexpansion.catalog.CatalogItems.ECTOPLASMIC_GENERATOR, Component.translatable("jei.mortal_expansion.generator_info"));
        registration.addIngredientInfo(com.howierux.mortalexpansion.catalog.CatalogItems.HAINT_FURNACE, Component.translatable("jei.mortal_expansion.furnace_info"));
    }
    private static ProcessRecipe cauldron(Item fluidBucket, Item reagent, Item result) {
        return new ProcessRecipe(List.of(Ingredient.of(fluidBucket), Ingredient.of(reagent)), new ItemStack(result),
            List.of(Component.translatable("jei.mortal_expansion.cauldron_heat"), Component.translatable("jei.mortal_expansion.cauldron_buckets")));
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(PURITY_OFFERINGS,com.howierux.mortalexpansion.catalog.CatalogItems.ALTAR_OF_PURITY);
        registration.addRecipeCatalysts(IMPURITY_OFFERINGS,com.howierux.mortalexpansion.catalog.CatalogItems.ALTAR_OF_IMPURITY);
        registration.addRecipeCatalysts(ECTCHANTMENT, com.howierux.mortalexpansion.catalog.CatalogItems.ECTCHANTMENT_MACHINE);
        registration.addRecipeCatalysts(PURIFICATION, com.howierux.mortalexpansion.catalog.CatalogItems.PURIFICATION_STATION);
        registration.addRecipeCatalysts(CAULDRON, Items.CAULDRON);
        registration.addRecipeCatalysts(RecipeTypes.SMELTING, com.howierux.mortalexpansion.catalog.CatalogItems.HAUNT_FURNACE, com.howierux.mortalexpansion.catalog.CatalogItems.HAINT_FURNACE);
        registration.addRecipeCatalysts(RecipeTypes.FUELING, com.howierux.mortalexpansion.catalog.CatalogItems.HAUNT_FURNACE, com.howierux.mortalexpansion.catalog.CatalogItems.HAINT_FURNACE);
    }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(com.howierux.mortalexpansion.client.screen.PurificationStationScreen.class,
            20, 124, 136, 8, PURIFICATION);
    }
    @Override public void registerAdvanced(IAdvancedRegistration registration) {
        registration.addRecipeCategoryDecorator(RecipeTypes.CRAFTING, new SecretRecipeOverlay<>());
        registration.addRecipeCategoryDecorator(RecipeTypes.SMITHING, new SecretRecipeOverlay<>());
    }
    @Override public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(com.howierux.mortalexpansion.catalog.CatalogItems.BOOK_OF_ECTCETERA, new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return EctceteraProgression.tier(stack); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return "tier=" + EctceteraProgression.tier(stack); }
        });
    }
    @Override public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime runtime){org.slf4j.LoggerFactory.getLogger("mortal_expansion").info("Mortal Expansion optional JEI integration ready: machine, cauldron, altar and secret-recipe pages.");}
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK,
            List.of(EctceteraProgression.book(0), EctceteraProgression.book(1), EctceteraProgression.book(2), EctceteraProgression.book(3), EctceteraProgression.book(4)));
    }
}
