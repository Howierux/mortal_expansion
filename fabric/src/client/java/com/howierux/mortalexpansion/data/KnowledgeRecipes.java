package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.recipe.*;
import net.fabricmc.fabric.api.datagen.v1.*;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import java.util.concurrent.CompletableFuture;
public final class KnowledgeRecipes extends FabricRecipeProvider {
 public KnowledgeRecipes(FabricDataOutput output,CompletableFuture<HolderLookup.Provider> lookup){super(output,lookup);}
 @Override public void buildRecipes(RecipeOutput output){
  ProcessingRecipes.emit(output);
  RecipeOutput out=new RecipeOutput(){
   public net.minecraft.advancements.Advancement.Builder advancement(){return output.advancement();}
   public void accept(ResourceLocation id,Recipe<?> recipe,net.minecraft.advancements.AdvancementHolder advancement){output.accept(id,recipe,InsightSecret.required(id)==0?advancement:null);}
  };
  ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,CatalogItems.BOOK_OF_ECTCETERA).requires(Items.BOOK).requires(Items.ROTTEN_FLESH).unlockedBy("has_flesh",has(Items.ROTTEN_FLESH)).save(out,Catalog.id("book_of_ectcetera"));
  shape(out,"emf_reader",RecipeCategory.TOOLS,CatalogItems.EMF_READER,1,new String[]{"III","RSR","III"},java.util.Map.of('I',Items.IRON_INGOT,'R',Items.REDSTONE,'S',CatalogItems.HOLY_SALT));
  shapeless(out,"precise_emf_reader",CatalogItems.PRECISE_EMF_READER,1,CatalogItems.EMF_READER,CatalogItems.RAW_ECTOPLASM);
  for(int tier=0;tier<4;tier++){
   var id=Catalog.id("book_of_ectcetera_upgrade_"+(tier+1));
   var advancement=out.advancement().parent(ResourceLocation.withDefaultNamespace("recipes/root")).addCriterion("has_book",has(CatalogItems.BOOK_OF_ECTCETERA)).rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(id)).build(id.withPrefix("recipes/misc/"));
   out.accept(id,new EctceteraUpgradeRecipe(tier),advancement);
  }
  out.accept(Catalog.id("mortality_research_station"),new MortalityResearchStationRecipe(),null);
  shape(out,"ectothread",RecipeCategory.MISC,CatalogItems.ECTOTHREAD,8,new String[]{"SSS","SES","SSS"},java.util.Map.of('E',CatalogItems.RAW_ECTOPLASM,'S',Items.STRING));
  shape(out,"purified_ectothread",RecipeCategory.MISC,CatalogItems.PURIFIED_ECTOTHREAD,8,new String[]{"SSS","SES","SSS"},java.util.Map.of('E',CatalogItems.PURE_ECTOPLASM,'S',Items.STRING));
  shape(out,"dreamcatcher",RecipeCategory.MISC,CatalogItems.DREAMCATCHER,1,new String[]{"SES","EEE","SES"},java.util.Map.of('E',CatalogItems.ECTOTHREAD,'S',Items.STICK));
  shape(out,"purified_dreamcatcher",RecipeCategory.MISC,CatalogItems.PURIFIED_DREAMCATCHER,1,new String[]{"SES","EEE","SES"},java.util.Map.of('E',CatalogItems.PURIFIED_ECTOTHREAD,'S',Items.STICK));
  shapeless(out,"purified_dreamcatcher_upgrade",CatalogItems.PURIFIED_DREAMCATCHER,1,CatalogItems.DREAMCATCHER,CatalogItems.PURIFIED_ECTOTHREAD);
  shape(out,"altar_of_purity",RecipeCategory.MISC,CatalogItems.ALTAR_OF_PURITY,1,new String[]{"B","F"},java.util.Map.of('B',Items.BEACON,'F',CatalogItems.HAINT_FURNACE));
  shape(out,"altar_of_impurity",RecipeCategory.MISC,CatalogItems.ALTAR_OF_IMPURITY,1,new String[]{"B","F"},java.util.Map.of('B',Items.BEACON,'F',CatalogItems.HAUNT_FURNACE));
  shape(out,"ghost_garb_helmet",RecipeCategory.COMBAT,CatalogItems.GHOST_GARB_HELMET,1,new String[]{"EEE","E E"},java.util.Map.of('E',CatalogItems.ECTOTHREAD));
  shape(out,"ghost_garb_chestplate",RecipeCategory.COMBAT,CatalogItems.GHOST_GARB_CHESTPLATE,1,new String[]{"E E","EEE","EEE"},java.util.Map.of('E',CatalogItems.ECTOTHREAD));
  shape(out,"ghost_garb_leggings",RecipeCategory.COMBAT,CatalogItems.GHOST_GARB_LEGGINGS,1,new String[]{"EEE","E E","E E"},java.util.Map.of('E',CatalogItems.ECTOTHREAD));
  shape(out,"ghost_garb_boots",RecipeCategory.COMBAT,CatalogItems.GHOST_GARB_BOOTS,1,new String[]{"E E","E E"},java.util.Map.of('E',CatalogItems.ECTOTHREAD));
  shape(out,"haint_garb_helmet",RecipeCategory.COMBAT,CatalogItems.HAINT_GARB_HELMET,1,new String[]{"EEE","E E"},java.util.Map.of('E',CatalogItems.PURIFIED_ECTOTHREAD));
  shape(out,"haint_garb_chestplate",RecipeCategory.COMBAT,CatalogItems.HAINT_GARB_CHESTPLATE,1,new String[]{"E E","EEE","EEE"},java.util.Map.of('E',CatalogItems.PURIFIED_ECTOTHREAD));
  shape(out,"haint_garb_leggings",RecipeCategory.COMBAT,CatalogItems.HAINT_GARB_LEGGINGS,1,new String[]{"EEE","E E","E E"},java.util.Map.of('E',CatalogItems.PURIFIED_ECTOTHREAD));
  shape(out,"haint_garb_boots",RecipeCategory.COMBAT,CatalogItems.HAINT_GARB_BOOTS,1,new String[]{"E E","E E"},java.util.Map.of('E',CatalogItems.PURIFIED_ECTOTHREAD));
  out.accept(Catalog.id("haint_garb_helmet_smithing"),new TemplateFreeSmithingRecipe(Ingredient.of(CatalogItems.GHOST_GARB_HELMET),Ingredient.of(CatalogItems.PURE_ECTOPLASM),new ItemStack(CatalogItems.HAINT_GARB_HELMET)),null);
  out.accept(Catalog.id("haint_garb_chestplate_smithing"),new TemplateFreeSmithingRecipe(Ingredient.of(CatalogItems.GHOST_GARB_CHESTPLATE),Ingredient.of(CatalogItems.PURE_ECTOPLASM),new ItemStack(CatalogItems.HAINT_GARB_CHESTPLATE)),null);
  out.accept(Catalog.id("haint_garb_leggings_smithing"),new TemplateFreeSmithingRecipe(Ingredient.of(CatalogItems.GHOST_GARB_LEGGINGS),Ingredient.of(CatalogItems.PURE_ECTOPLASM),new ItemStack(CatalogItems.HAINT_GARB_LEGGINGS)),null);
  out.accept(Catalog.id("haint_garb_boots_smithing"),new TemplateFreeSmithingRecipe(Ingredient.of(CatalogItems.GHOST_GARB_BOOTS),Ingredient.of(CatalogItems.PURE_ECTOPLASM),new ItemStack(CatalogItems.HAINT_GARB_BOOTS)),null);
  out.accept(Catalog.id("spectralite_haint_helmet_smithing"),new TemplateFreeSmithingRecipe(Ingredient.of(CatalogItems.HAINT_GARB_HELMET),Ingredient.of(CatalogItems.SPECTRALITE_HELMET),new ItemStack(CatalogItems.SPECTRALITE_HAINT_HELMET)),null);
  out.accept(Catalog.id("spectralite_haint_chestplate_smithing"),new TemplateFreeSmithingRecipe(Ingredient.of(CatalogItems.HAINT_GARB_CHESTPLATE),Ingredient.of(CatalogItems.SPECTRALITE_CHESTPLATE),new ItemStack(CatalogItems.SPECTRALITE_HAINT_CHESTPLATE)),null);
  out.accept(Catalog.id("spectralite_haint_leggings_smithing"),new TemplateFreeSmithingRecipe(Ingredient.of(CatalogItems.HAINT_GARB_LEGGINGS),Ingredient.of(CatalogItems.SPECTRALITE_LEGGINGS),new ItemStack(CatalogItems.SPECTRALITE_HAINT_LEGGINGS)),null);
  out.accept(Catalog.id("spectralite_haint_boots_smithing"),new TemplateFreeSmithingRecipe(Ingredient.of(CatalogItems.HAINT_GARB_BOOTS),Ingredient.of(CatalogItems.SPECTRALITE_BOOTS),new ItemStack(CatalogItems.SPECTRALITE_HAINT_BOOTS)),null);
  shape(out,"haunt_furnace",RecipeCategory.MISC,CatalogItems.HAUNT_FURNACE,1,new String[]{"GGG","G G","GGG"},java.util.Map.of('G',CatalogItems.GRAVESTONE));
  shape(out,"haint_furnace",RecipeCategory.MISC,CatalogItems.HAINT_FURNACE,1,new String[]{"GGG","G G","GGG"},java.util.Map.of('G',CatalogItems.PURIFIED_GRAVESTONE));
  shape(out,"ectoplasmic_generator",RecipeCategory.REDSTONE,CatalogItems.ECTOPLASMIC_GENERATOR,1,new String[]{"GGG","GEG","GFG"},java.util.Map.of('E',CatalogItems.RAW_ECTOPLASM,'F',CatalogItems.HAUNT_FURNACE,'G',Items.GOLD_INGOT));
  shape(out,"enchantment_generator",RecipeCategory.REDSTONE,CatalogItems.ENCHANTMENT_GENERATOR,1,new String[]{"IRI","IGI","IFI"},java.util.Map.of('F',CatalogItems.HAUNT_FURNACE,'G',Items.GRINDSTONE,'I',Items.IRON_INGOT,'R',Items.REDSTONE));
  shape(out,"purification_station",RecipeCategory.MISC,CatalogItems.PURIFICATION_STATION,1,new String[]{"III","RIR","IPI"},java.util.Map.of('I',Items.IRON_INGOT,'P',CatalogItems.PURIFIED_SPIRITWOOD_PLANKS,'R',Items.REDSTONE));
  shape(out,"purification_ward",RecipeCategory.MISC,CatalogItems.PURIFICATION_WARD,1,new String[]{"MPM","PPP","MPM"},java.util.Map.of('M',CatalogItems.GRAVE_MOSS,'P',CatalogItems.PURIFIED_SPIRITWOOD_PLANKS));
  shape(out,"ectank",RecipeCategory.MISC,CatalogItems.ECTANK,1,new String[]{"SSS","SGS","SSS"},java.util.Map.of('G',Items.GLASS,'S',CatalogItems.GRAVESTONE));
  shape(out,"purified_ectank",RecipeCategory.MISC,CatalogItems.PURIFIED_ECTANK,1,new String[]{"SSS","SGS","SSS"},java.util.Map.of('G',Items.GLASS,'S',CatalogItems.PURIFIED_GRAVESTONE));
  shape(out,"ecto_pipe",RecipeCategory.MISC,CatalogItems.ECTO_PIPE,16,new String[]{"SSS","G G","SSS"},java.util.Map.of('G',Items.GLASS,'S',CatalogItems.GRAVESTONE));
  shape(out,"purified_ecto_pipe",RecipeCategory.MISC,CatalogItems.PURIFIED_ECTO_PIPE,16,new String[]{"SSS","G G","SSS"},java.util.Map.of('G',Items.GLASS,'S',CatalogItems.PURIFIED_GRAVESTONE));
  shape(out,"purified_gravestone",RecipeCategory.BUILDING_BLOCKS,CatalogItems.PURIFIED_GRAVESTONE,8,new String[]{"GGG","GEG","GGG"},java.util.Map.of('E',CatalogItems.PURE_ECTOPLASM,'G',CatalogItems.GRAVESTONE));
  shapeless(out,"spiritwood_planks",CatalogItems.SPIRITWOOD_PLANKS,4,CatalogItems.SPIRITWOOD_LOG);
  shape(out,"purified_spiritwood_log",RecipeCategory.BUILDING_BLOCKS,CatalogItems.PURIFIED_SPIRITWOOD_LOG,8,new String[]{"LLL","LEL","LLL"},java.util.Map.of('E',CatalogItems.PURE_ECTOPLASM,'L',CatalogItems.SPIRITWOOD_LOG));
  shapeless(out,"purified_spiritwood_planks",CatalogItems.PURIFIED_SPIRITWOOD_PLANKS,4,CatalogItems.PURIFIED_SPIRITWOOD_LOG);
 }
 private void shape(RecipeOutput output,String id,RecipeCategory category,ItemLike result,int count,String[] pattern,java.util.Map<Character,ItemLike> keys){var builder=ShapedRecipeBuilder.shaped(category,result,count);for(var row:pattern)builder.pattern(row);var ordered=new java.util.TreeMap<>(keys);ordered.forEach(builder::define);builder.unlockedBy("has_material",has(ordered.firstEntry().getValue())).save(output,Catalog.id(id));}
 private void shapeless(RecipeOutput output,String id,ItemLike result,int count,ItemLike... ingredients){var builder=ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,result,count);for(var ingredient:ingredients)builder.requires(ingredient);builder.unlockedBy("has_material",has(ingredients[0])).save(output,Catalog.id(id));}
}
