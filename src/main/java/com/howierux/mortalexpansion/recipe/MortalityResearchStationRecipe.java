package com.howierux.mortalexpansion.recipe;
import com.howierux.mortalexpansion.registry.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
public final class MortalityResearchStationRecipe extends ShapedRecipe {
    public MortalityResearchStationRecipe(){super("",CraftingBookCategory.MISC,ShapedRecipePattern.of(java.util.Map.of('B',Ingredient.of(ModItems.BOOK_OF_ECTCETERA.get()),'S',Ingredient.of(ModItems.HOLY_SALT.get()),'O',Ingredient.of(Items.OBSIDIAN)),java.util.List.of(" B ","SOS","OOO")),new ItemStack(ModItems.MORTALITY_RESEARCH_STATION.get()));}
    @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input){var remaining=super.getRemainingItems(input);for(int i=0;i<input.size();i++)if(input.getItem(i).is(ModItems.BOOK_OF_ECTCETERA.get()))remaining.set(i,input.getItem(i).copyWithCount(1));return remaining;}
    @Override public RecipeSerializer<?> getSerializer(){return ModRecipeSerializers.MORTALITY_RESEARCH_STATION.get();}
    public static final class Serializer implements RecipeSerializer<MortalityResearchStationRecipe>{
        public MapCodec<MortalityResearchStationRecipe> codec(){return MapCodec.unit(MortalityResearchStationRecipe::new);}
        public StreamCodec<RegistryFriendlyByteBuf,MortalityResearchStationRecipe> streamCodec(){return StreamCodec.unit(new MortalityResearchStationRecipe());}
    }
}
