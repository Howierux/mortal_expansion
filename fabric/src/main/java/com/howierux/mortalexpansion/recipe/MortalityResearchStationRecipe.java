package com.howierux.mortalexpansion.recipe;
import com.howierux.mortalexpansion.catalog.CatalogItems;
import com.howierux.mortalexpansion.guide.BookRecipes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
public final class MortalityResearchStationRecipe extends ShapedRecipe {
    public MortalityResearchStationRecipe(){super("",CraftingBookCategory.MISC,ShapedRecipePattern.of(java.util.Map.of('B',Ingredient.of(CatalogItems.BOOK_OF_ECTCETERA),'S',Ingredient.of(CatalogItems.HOLY_SALT),'O',Ingredient.of(Items.OBSIDIAN)),java.util.List.of(" B ","SOS","OOO")),new ItemStack(CatalogItems.MORTALITY_RESEARCH_STATION));}
    @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input){var remaining=super.getRemainingItems(input);for(int i=0;i<input.size();i++)if(input.getItem(i).is(CatalogItems.BOOK_OF_ECTCETERA))remaining.set(i,input.getItem(i).copyWithCount(1));return remaining;}
    @Override public RecipeSerializer<?> getSerializer(){return BookRecipes.STATION;}
    public static final class Serializer implements RecipeSerializer<MortalityResearchStationRecipe>{
        public MapCodec<MortalityResearchStationRecipe> codec(){return MapCodec.unit(MortalityResearchStationRecipe::new);}
        public StreamCodec<RegistryFriendlyByteBuf,MortalityResearchStationRecipe> streamCodec(){
            // This fixed recipe has no per-instance fields. A unit codec checks object equality,
            // which rejects separately loaded recipe instances during the login recipe packet.
            return StreamCodec.of((buffer, recipe) -> {}, buffer -> new MortalityResearchStationRecipe());
        }
    }
}
