package com.howierux.mortalexpansion.data;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ModRecipes extends RecipeProvider {
    public ModRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) { super(output, lookup); }
    @Override protected void buildRecipes(RecipeOutput output) {
        // Most existing recipes have no unlock advancement. Keep their behavior and
        // retain the separately authored Book advancement rather than introducing new unlocks.
        RecipeOutput recipesOnly = new RecipeOutput() {
            @Override public Advancement.Builder advancement() { return output.advancement(); }
            @Override public void accept(ResourceLocation id, Recipe<?> recipe, AdvancementHolder advancement,
                    net.neoforged.neoforge.common.conditions.ICondition... conditions) {
                output.accept(id, recipe, null, conditions);
            }
        };
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,ModItems.ALTAR_OF_IMPURITY.get()).pattern("B").pattern("F")
                .define('B',Items.BEACON).define('F',ModItems.HAUNT_FURNACE.get()).unlockedBy("has_beacon",has(Items.BEACON)).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,ModItems.ALTAR_OF_PURITY.get()).pattern("B").pattern("F")
                .define('B',Items.BEACON).define('F',ModItems.HAINT_FURNACE.get()).unlockedBy("has_beacon",has(Items.BEACON)).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.ENCHANTMENT_GENERATOR.get())
                .pattern("IRI").pattern("IGI").pattern("IFI")
                .define('I', Items.IRON_INGOT).define('G', Items.GRINDSTONE).define('R', Items.REDSTONE)
                .define('F', ModItems.HAUNT_FURNACE.get())
                .unlockedBy("has_haunt_furnace", has(ModItems.HAUNT_FURNACE.get())).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.ECTOPLASMIC_GENERATOR.get())
                .pattern("GGG").pattern("GEG").pattern("GFG")
                .define('G', Items.GOLD_INGOT).define('E', ModItems.RAW_ECTOPLASM.get()).define('F', ModItems.HAUNT_FURNACE.get())
                .unlockedBy("has_haunt_furnace", has(ModItems.HAUNT_FURNACE.get())).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.ECTCHANTMENT_MACHINE.get())
                .pattern("PPP").pattern("PEP").pattern("PCP")
                .define('P', ModItems.PURIFIED_GRAVESTONE.get()).define('E', Items.ENCHANTING_TABLE).define('C', ModItems.PURE_ECTOPLASM.get())
                .unlockedBy("has_pure_ectoplasm", has(ModItems.PURE_ECTOPLASM.get())).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.PRECISE_EMF_READER.get())
                .requires(ModItems.EMF_READER.get()).requires(ModItems.RAW_ECTOPLASM.get())
                .unlockedBy("has_emf_reader", has(ModItems.EMF_READER.get())).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.EMF_READER.get())
                .pattern("III").pattern("RSR").pattern("III")
                .define('I', Items.IRON_INGOT).define('S', ModItems.HOLY_SALT.get()).define('R', Items.REDSTONE)
                .unlockedBy("has_holy_salt", has(ModItems.HOLY_SALT.get())).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ECTOTHREAD.get(), 8).pattern("SSS").pattern("SES").pattern("SSS")
                .define('S', Items.STRING).define('E', ModItems.RAW_ECTOPLASM.get()).unlockedBy("has_ectoplasm", has(ModItems.RAW_ECTOPLASM.get())).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PURIFIED_ECTOTHREAD.get(), 8).pattern("SSS").pattern("SES").pattern("SSS")
                .define('S', Items.STRING).define('E', ModItems.PURE_ECTOPLASM.get()).unlockedBy("has_pure_ectoplasm", has(ModItems.PURE_ECTOPLASM.get())).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GHOST_GARB_HELMET.get()).pattern("EEE").pattern("E E")
                .define('E', ModItems.ECTOTHREAD.get()).unlockedBy("has_ectothread", has(ModItems.ECTOTHREAD.get())).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GHOST_GARB_CHESTPLATE.get()).pattern("E E").pattern("EEE").pattern("EEE")
                .define('E', ModItems.ECTOTHREAD.get()).unlockedBy("has_ectothread", has(ModItems.ECTOTHREAD.get())).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GHOST_GARB_LEGGINGS.get()).pattern("EEE").pattern("E E").pattern("E E")
                .define('E', ModItems.ECTOTHREAD.get()).unlockedBy("has_ectothread", has(ModItems.ECTOTHREAD.get())).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GHOST_GARB_BOOTS.get()).pattern("E E").pattern("E E")
                .define('E', ModItems.ECTOTHREAD.get()).unlockedBy("has_ectothread", has(ModItems.ECTOTHREAD.get())).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HAINT_GARB_HELMET.get()).pattern("EEE").pattern("E E")
                .define('E', ModItems.PURIFIED_ECTOTHREAD.get()).unlockedBy("has_purified_ectothread", has(ModItems.PURIFIED_ECTOTHREAD.get())).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HAINT_GARB_CHESTPLATE.get()).pattern("E E").pattern("EEE").pattern("EEE")
                .define('E', ModItems.PURIFIED_ECTOTHREAD.get()).unlockedBy("has_purified_ectothread", has(ModItems.PURIFIED_ECTOTHREAD.get())).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HAINT_GARB_LEGGINGS.get()).pattern("EEE").pattern("E E").pattern("E E")
                .define('E', ModItems.PURIFIED_ECTOTHREAD.get()).unlockedBy("has_purified_ectothread", has(ModItems.PURIFIED_ECTOTHREAD.get())).save(recipesOnly);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.HAINT_GARB_BOOTS.get()).pattern("E E").pattern("E E")
                .define('E', ModItems.PURIFIED_ECTOTHREAD.get()).unlockedBy("has_purified_ectothread", has(ModItems.PURIFIED_ECTOTHREAD.get())).save(recipesOnly);
        recipesOnly.accept(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "haint_garb_helmet_smithing"),
                new com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe(Ingredient.of(ModItems.GHOST_GARB_HELMET.get()),
                        Ingredient.of(ModItems.PURE_ECTOPLASM.get()), new net.minecraft.world.item.ItemStack(ModItems.HAINT_GARB_HELMET.get())), null);
        recipesOnly.accept(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_haint_helmet_smithing"),
                new com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe(Ingredient.of(ModItems.HAINT_GARB_HELMET.get()),
                        Ingredient.of(ModItems.SPECTRALITE_HELMET.get()), new net.minecraft.world.item.ItemStack(ModItems.SPECTRALITE_HAINT_HELMET.get())), null);
        recipesOnly.accept(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "haint_garb_chestplate_smithing"),
                new com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe(Ingredient.of(ModItems.GHOST_GARB_CHESTPLATE.get()),
                        Ingredient.of(ModItems.PURE_ECTOPLASM.get()), new net.minecraft.world.item.ItemStack(ModItems.HAINT_GARB_CHESTPLATE.get())), null);
        recipesOnly.accept(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_haint_chestplate_smithing"),
                new com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe(Ingredient.of(ModItems.HAINT_GARB_CHESTPLATE.get()),
                        Ingredient.of(ModItems.SPECTRALITE_CHESTPLATE.get()), new net.minecraft.world.item.ItemStack(ModItems.SPECTRALITE_HAINT_CHESTPLATE.get())), null);
        recipesOnly.accept(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "haint_garb_leggings_smithing"),
                new com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe(Ingredient.of(ModItems.GHOST_GARB_LEGGINGS.get()),
                        Ingredient.of(ModItems.PURE_ECTOPLASM.get()), new net.minecraft.world.item.ItemStack(ModItems.HAINT_GARB_LEGGINGS.get())), null);
        recipesOnly.accept(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_haint_leggings_smithing"),
                new com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe(Ingredient.of(ModItems.HAINT_GARB_LEGGINGS.get()),
                        Ingredient.of(ModItems.SPECTRALITE_LEGGINGS.get()), new net.minecraft.world.item.ItemStack(ModItems.SPECTRALITE_HAINT_LEGGINGS.get())), null);
        recipesOnly.accept(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "haint_garb_boots_smithing"),
                new com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe(Ingredient.of(ModItems.GHOST_GARB_BOOTS.get()),
                        Ingredient.of(ModItems.PURE_ECTOPLASM.get()), new net.minecraft.world.item.ItemStack(ModItems.HAINT_GARB_BOOTS.get())), null);
        recipesOnly.accept(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_haint_boots_smithing"),
                new com.howierux.mortalexpansion.recipe.TemplateFreeSmithingRecipe(Ingredient.of(ModItems.HAINT_GARB_BOOTS.get()),
                        Ingredient.of(ModItems.SPECTRALITE_BOOTS.get()), new net.minecraft.world.item.ItemStack(ModItems.SPECTRALITE_HAINT_BOOTS.get())), null);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOODSTONE_AXE.get(), 1).pattern("BB").pattern("BS").pattern(" S").define('B', ModItems.BLOODSTONE.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.BLOODSTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "bloodstone_axe"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOODSTONE_BOOTS.get(), 1).pattern("B B").pattern("B B").define('B', ModItems.BLOODSTONE.get()).unlockedBy("has_ingredient", has(ModItems.BLOODSTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "bloodstone_boots"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOODSTONE_CHESTPLATE.get(), 1).pattern("B B").pattern("BBB").pattern("BBB").define('B', ModItems.BLOODSTONE.get()).unlockedBy("has_ingredient", has(ModItems.BLOODSTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "bloodstone_chestplate"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOODSTONE_HELMET.get(), 1).pattern("BBB").pattern("B B").define('B', ModItems.BLOODSTONE.get()).unlockedBy("has_ingredient", has(ModItems.BLOODSTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "bloodstone_helmet"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOODSTONE_HOE.get(), 1).pattern("BB").pattern(" S").pattern(" S").define('B', ModItems.BLOODSTONE.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.BLOODSTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "bloodstone_hoe"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOODSTONE_LEGGINGS.get(), 1).pattern("BBB").pattern("B B").pattern("B B").define('B', ModItems.BLOODSTONE.get()).unlockedBy("has_ingredient", has(ModItems.BLOODSTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "bloodstone_leggings"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOODSTONE_PICKAXE.get(), 1).pattern("BBB").pattern(" S ").pattern(" S ").define('B', ModItems.BLOODSTONE.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.BLOODSTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "bloodstone_pickaxe"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOODSTONE_SHOVEL.get(), 1).pattern("B").pattern("S").pattern("S").define('B', ModItems.BLOODSTONE.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.BLOODSTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "bloodstone_shovel"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BLOODSTONE_SWORD.get(), 1).pattern("B").pattern("B").pattern("S").define('B', ModItems.BLOODSTONE.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.BLOODSTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "bloodstone_sword"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, com.howierux.mortalexpansion.guide.EctceteraProgression.book(0)).requires(Items.BOOK).requires(Items.ROTTEN_FLESH).unlockedBy("has_ingredient", has(Items.BOOK)).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "book_of_ectcetera"));
        for (int tier = 0; tier < 3; tier++) {
            recipesOnly.accept(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "book_of_ectcetera_upgrade_" + (tier + 1)),
                    new com.howierux.mortalexpansion.recipe.EctceteraUpgradeRecipe(tier), null);
        }
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.GRAVE.get(), 3).pattern("GG").pattern("GG").pattern("GG").define('G', ModItems.GRAVESTONE.get()).unlockedBy("has_ingredient", has(ModItems.GRAVESTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "grave"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GRAVESTONE_SWORD.get(), 1).pattern("G").pattern("G").pattern("S").define('G', ModItems.GRAVESTONE.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.GRAVESTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "gravestone_sword"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HAINT_FURNACE.get(), 1).pattern("GGG").pattern("G G").pattern("GGG").define('G', ModItems.PURIFIED_GRAVESTONE.get()).unlockedBy("has_ingredient", has(ModItems.PURIFIED_GRAVESTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "haint_furnace"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HAUNT_FURNACE.get(), 1).pattern("GGG").pattern("G G").pattern("GGG").define('G', ModItems.GRAVESTONE.get()).unlockedBy("has_ingredient", has(ModItems.GRAVESTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "haunt_furnace"));
        SimpleCookingRecipeBuilder.generic(Ingredient.of(ModItems.HOLY_WATER_BUCKET.get()), RecipeCategory.MISC,
                ModItems.HOLY_SALT.get(), 0.0F, 200, RecipeSerializer.SMELTING_RECIPE,
                (group, category, ingredient, result, experience, time) ->
                        new SmeltingRecipe(group, CookingBookCategory.MISC, ingredient, result, experience, time))
                .unlockedBy("has_ingredient", has(ModItems.HOLY_WATER_BUCKET.get()))
                .save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "holy_water_bucket_smelting"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.LIQUID_ECTOPLASM_BUCKET.get()), RecipeCategory.MISC, ModItems.RAW_ECTOPLASM.get(), 0.0F, 200).unlockedBy("has_ingredient", has(ModItems.LIQUID_ECTOPLASM_BUCKET.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "liquid_ectoplasm_bucket_smelting"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PURIFICATION_STATION.get(), 1).pattern("III").pattern("RIR").pattern("IPI").define('I', Items.IRON_INGOT).define('R', Items.REDSTONE).define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(Items.IRON_INGOT)).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purification_station"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PURIFICATION_WARD.get(), 1).pattern("MPM").pattern("PPP").pattern("MPM").define('M', ModItems.GRAVE_MOSS.get()).define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.GRAVE_MOSS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purification_ward"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.PURIFIED_ECTOPLASM_BUCKET.get()), RecipeCategory.MISC, ModItems.PURE_ECTOPLASM.get(), 0.0F, 200).unlockedBy("has_ingredient", has(ModItems.PURIFIED_ECTOPLASM_BUCKET.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_ectoplasm_bucket_smelting"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_GRAVESTONE.get(), 8).pattern("GGG").pattern("GEG").pattern("GGG").define('G', ModItems.GRAVESTONE.get()).define('E', ModItems.PURE_ECTOPLASM.get()).unlockedBy("has_ingredient", has(ModItems.GRAVESTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_gravestone"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.PURIFIED_GRAVESTONE_SWORD.get(), 1).pattern("G").pattern("G").pattern("S").define('G', ModItems.PURIFIED_GRAVESTONE.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.PURIFIED_GRAVESTONE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_gravestone_sword"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_BUTTON.get(), 1).pattern("P").define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_button"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_DOOR.get(), 3).pattern("PP").pattern("PP").pattern("PP").define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_door"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_FENCE.get(), 3).pattern("PSP").pattern("PSP").define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_fence"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_FENCE_GATE.get(), 1).pattern("SPS").pattern("SPS").define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_fence_gate"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_LOG.get(), 8).pattern("LLL").pattern("LEL").pattern("LLL").define('L', ModItems.SPIRITWOOD_LOG.get()).define('E', ModItems.PURE_ECTOPLASM.get()).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_LOG.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_log"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_PLANKS.get(), 4).requires(ModItems.PURIFIED_SPIRITWOOD_LOG.get()).unlockedBy("has_ingredient", has(ModItems.PURIFIED_SPIRITWOOD_LOG.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_planks"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_PRESSURE_PLATE.get(), 1).pattern("PP").define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_pressure_plate"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_SLAB.get(), 6).pattern("PPP").define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_slab"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_STAIRS.get(), 4).pattern("P  ").pattern("PP ").pattern("PPP").define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_stairs"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.PURIFIED_SPIRITWOOD_TRAPDOOR.get(), 2).pattern("PPP").pattern("PPP").define('P', ModItems.PURIFIED_SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "purified_spiritwood_trapdoor"));
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLOODSTONE_AXE.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, ModItems.SPECTRALITE_AXE.get()).unlocks("has_ingredient", has(ModItems.BLOODSTONE_AXE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_axe_smithing"));
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLOODSTONE_BOOTS.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, ModItems.SPECTRALITE_BOOTS.get()).unlocks("has_ingredient", has(ModItems.BLOODSTONE_BOOTS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_boots_smithing"));
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLOODSTONE_CHESTPLATE.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, ModItems.SPECTRALITE_CHESTPLATE.get()).unlocks("has_ingredient", has(ModItems.BLOODSTONE_CHESTPLATE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_chestplate_smithing"));
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLOODSTONE_HELMET.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, ModItems.SPECTRALITE_HELMET.get()).unlocks("has_ingredient", has(ModItems.BLOODSTONE_HELMET.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_helmet_smithing"));
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLOODSTONE_HOE.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, ModItems.SPECTRALITE_HOE.get()).unlocks("has_ingredient", has(ModItems.BLOODSTONE_HOE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_hoe_smithing"));
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLOODSTONE_LEGGINGS.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, ModItems.SPECTRALITE_LEGGINGS.get()).unlocks("has_ingredient", has(ModItems.BLOODSTONE_LEGGINGS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_leggings_smithing"));
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLOODSTONE_PICKAXE.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, ModItems.SPECTRALITE_PICKAXE.get()).unlocks("has_ingredient", has(ModItems.BLOODSTONE_PICKAXE.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_pickaxe_smithing"));
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLOODSTONE_SHOVEL.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, ModItems.SPECTRALITE_SHOVEL.get()).unlocks("has_ingredient", has(ModItems.BLOODSTONE_SHOVEL.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_shovel_smithing"));
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ModItems.BLOODSTONE_SWORD.get()), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.COMBAT, ModItems.SPECTRALITE_SWORD.get()).unlocks("has_ingredient", has(ModItems.BLOODSTONE_SWORD.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spectralite_sword_smithing"));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.SPIRIT_TORCH.get(), 1).pattern("H").pattern("S").define('H', ModItems.HOLY_SALT.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.HOLY_SALT.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spirit_torch"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModItems.SPIRITWOOD_BUTTON.get(), 1).requires(ModItems.SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_button"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.SPIRITWOOD_DOOR.get(), 3).pattern("PP").pattern("PP").pattern("PP").define('P', ModItems.SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_door"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.SPIRITWOOD_FENCE.get(), 3).pattern("PSP").pattern("PSP").define('P', ModItems.SPIRITWOOD_PLANKS.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_fence"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.SPIRITWOOD_FENCE_GATE.get(), 1).pattern("SPS").pattern("SPS").define('P', ModItems.SPIRITWOOD_PLANKS.get()).define('S', Items.STICK).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_fence_gate"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.SPIRITWOOD_PLANKS.get(), 4).requires(ModItems.SPIRITWOOD_LOG.get()).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_LOG.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_planks"));
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.SPIRITWOOD_PRESSURE_PLATE.get(), 1).pattern("PP").define('P', ModItems.SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_pressure_plate"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.SPIRITWOOD_SLAB.get(), 6).pattern("PPP").define('P', ModItems.SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_slab"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.SPIRITWOOD_STAIRS.get(), 4).pattern("P  ").pattern("PP ").pattern("PPP").define('P', ModItems.SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_stairs"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.SPIRITWOOD_TRAPDOOR.get(), 2).pattern("PPP").pattern("PPP").define('P', ModItems.SPIRITWOOD_PLANKS.get()).unlockedBy("has_ingredient", has(ModItems.SPIRITWOOD_PLANKS.get())).save(recipesOnly, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwood_trapdoor"));
    }
}
