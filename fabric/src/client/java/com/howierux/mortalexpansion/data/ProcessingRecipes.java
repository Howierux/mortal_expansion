package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.catalog.*;
import net.fabricmc.fabric.api.datagen.v1.*;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.concurrent.CompletableFuture;
/** Existing public cooking recipes; knowledge-gated block crafting remains with the book port. */
public final class ProcessingRecipes extends FabricRecipeProvider {
 public ProcessingRecipes(FabricDataOutput output,CompletableFuture<HolderLookup.Provider> lookup){super(output,lookup);}
 @Override public void buildRecipes(RecipeOutput output){
  ShapedRecipeBuilder.shaped(RecipeCategory.MISC,CatalogItems.RITUAL_STAR).pattern("BBB").pattern("BSB").pattern("BBB").define('B',CatalogItems.BLOODSTONE).define('S',net.minecraft.world.item.Items.NETHER_STAR).unlockedBy("has_nether_star",has(net.minecraft.world.item.Items.NETHER_STAR)).save(output,Catalog.id("ritual_star"));
  cooking(output,CatalogItems.HOLY_WATER_BUCKET,CatalogItems.HOLY_SALT,"holy_water_bucket_smelting");
  cooking(output,CatalogItems.LIQUID_ECTOPLASM_BUCKET,CatalogItems.RAW_ECTOPLASM,"liquid_ectoplasm_bucket_smelting");
  cooking(output,CatalogItems.PURIFIED_ECTOPLASM_BUCKET,CatalogItems.PURE_ECTOPLASM,"purified_ectoplasm_bucket_smelting");
 }
 private void cooking(RecipeOutput output,Item input,Item result,String id){SimpleCookingRecipeBuilder.smelting(Ingredient.of(input),RecipeCategory.MISC,result,0,200).unlockedBy("has_fluid",has(input)).save(output,Catalog.id(id));}
}
