package com.howierux.mortalexpansion.integration.jei;

import com.howierux.mortalexpansion.blockentity.EctchantmentMachineBlockEntity;
import com.howierux.mortalexpansion.blockentity.PurificationStationBlockEntity;
import com.howierux.mortalexpansion.guide.EctceteraProgression;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.MortalExpansion;
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
    private static RecipeType<ProcessRecipe> type(String path) {
        return RecipeType.create(MortalExpansion.MOD_ID, path, ProcessRecipe.class);
    }
    @Override public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "jei");
    }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            new ProcessCategory(gui, ECTCHANTMENT, "jei.mortal_expansion.ectchantment", new ItemStack(ModItems.ECTCHANTMENT_MACHINE.get())),
            new ProcessCategory(gui, PURIFICATION, "jei.mortal_expansion.purification", new ItemStack(ModItems.PURIFICATION_STATION.get())),
            new ProcessCategory(gui, CAULDRON, "jei.mortal_expansion.cauldron", new ItemStack(Items.CAULDRON)));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        // Ask the actual machine for transmutation results, including all current tag members.
        var machine = new EctchantmentMachineBlockEntity(BlockPos.ZERO, ModBlocks.ECTCHANTMENT_MACHINE.get().defaultBlockState());
        var enchantment = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModEnchantments.DEMI_TANGIBLE);
        var operations = new ArrayList<ProcessRecipe>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack input = item.getDefaultInstance();
            if (input.isEmpty()) continue;
            ItemStack output = machine.getTransmutationResult(input);
            if (output.isEmpty() && input.supportsEnchantment(enchantment)) {
                output = input.copyWithCount(1);
                output.set(ModDataComponents.DEMI_TANGIBLE.get(), true);
                output.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }
            if (!output.isEmpty()) operations.add(new ProcessRecipe(List.of(Ingredient.of(input)), output,
                List.of(Component.translatable("jei.mortal_expansion.charge", EctchantmentMachineBlockEntity.ENCHANTMENT_COST,
                    EctchantmentMachineBlockEntity.FE_ENCHANTMENT_COST), Component.translatable("jei.mortal_expansion.charge_fuel"))));
        }
        registration.addRecipes(ECTCHANTMENT, operations);
        registration.addRecipes(PURIFICATION, List.of(new ProcessRecipe(
            List.of(Ingredient.of(ModItems.RAW_ECTOPLASM.get()), Ingredient.of(PurificationStationBlockEntity.PURIFICATION_REAGENTS), Ingredient.of(Items.WATER_BUCKET)),
            new ItemStack(ModItems.PURE_ECTOPLASM.get()), List.of(
                Component.translatable("jei.mortal_expansion.purification_cost", PurificationStationBlockEntity.WATER_PER_CRAFT,
                    PurificationStationBlockEntity.FE_PER_CRAFT, PurificationStationBlockEntity.PROCESS_TICKS),
                Component.translatable("jei.mortal_expansion.bucket_return")))));
        registration.addRecipes(CAULDRON, List.of(
            cauldron(Items.WATER_BUCKET, ModItems.RAW_ECTOPLASM.get(), ModItems.LIQUID_ECTOPLASM_BUCKET.get()),
            cauldron(Items.WATER_BUCKET, ModItems.GRAVE_MOSS.get(), ModItems.HOLY_WATER_BUCKET.get()),
            cauldron(ModItems.LIQUID_ECTOPLASM_BUCKET.get(), ModItems.GRAVE_MOSS.get(), ModItems.PURIFIED_ECTOPLASM_BUCKET.get()),
            cauldron(ModItems.HOLY_WATER_BUCKET.get(), ModItems.RAW_ECTOPLASM.get(), ModItems.PURIFIED_ECTOPLASM_BUCKET.get())));
        registration.addIngredientInfo(ModItems.BOOK_OF_ECTCETERA.get(), Component.translatable("jei.mortal_expansion.book_info"));
        registration.addIngredientInfo(ModItems.ECTCHANTMENT_MACHINE.get(), Component.translatable("jei.mortal_expansion.charge_fuel"));
        registration.addIngredientInfo(ModItems.ECTOPLASMIC_GENERATOR.get(), Component.translatable("jei.mortal_expansion.generator_info"));
        registration.addIngredientInfo(ModItems.HAINT_FURNACE.get(), Component.translatable("jei.mortal_expansion.furnace_info"));
    }
    private static ProcessRecipe cauldron(Item fluidBucket, Item reagent, Item result) {
        return new ProcessRecipe(List.of(Ingredient.of(fluidBucket), Ingredient.of(reagent)), new ItemStack(result),
            List.of(Component.translatable("jei.mortal_expansion.cauldron_heat"), Component.translatable("jei.mortal_expansion.cauldron_buckets")));
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(ECTCHANTMENT, ModItems.ECTCHANTMENT_MACHINE.get());
        registration.addRecipeCatalysts(PURIFICATION, ModItems.PURIFICATION_STATION.get());
        registration.addRecipeCatalysts(CAULDRON, Items.CAULDRON);
        registration.addRecipeCatalysts(RecipeTypes.SMELTING, ModItems.HAUNT_FURNACE.get(), ModItems.HAINT_FURNACE.get());
        registration.addRecipeCatalysts(RecipeTypes.FUELING, ModItems.HAUNT_FURNACE.get(), ModItems.HAINT_FURNACE.get());
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
        registration.registerSubtypeInterpreter(ModItems.BOOK_OF_ECTCETERA.get(), new ISubtypeInterpreter<ItemStack>() {
            @Override public Object getSubtypeData(ItemStack stack, UidContext context) { return EctceteraProgression.tier(stack); }
            @Override public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) { return "tier=" + EctceteraProgression.tier(stack); }
        });
    }
    @Override public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraIngredients(VanillaTypes.ITEM_STACK,
            List.of(EctceteraProgression.book(0), EctceteraProgression.book(1), EctceteraProgression.book(2), EctceteraProgression.book(3)));
    }
}
