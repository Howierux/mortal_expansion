package com.howierux.mortalexpansion.integration.jei;

import com.howierux.mortalexpansion.haunt.EmfScale;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.List;
/** Fuel-use pages: an altar is a catalyst, never a fictitious crafting output. */
public final class AltarOfferingCategory implements IRecipeCategory<AltarOfferingRecipe> {
    private final RecipeType<AltarOfferingRecipe> type;private final boolean impure;
    private final ItemStack altar;private final IDrawable icon;
    public AltarOfferingCategory(IGuiHelper gui,RecipeType<AltarOfferingRecipe> type,boolean impure){
        this.type=type;this.impure=impure;altar=new ItemStack(impure?com.howierux.mortalexpansion.catalog.CatalogItems.ALTAR_OF_IMPURITY:com.howierux.mortalexpansion.catalog.CatalogItems.ALTAR_OF_PURITY);
        icon=gui.createDrawableIngredient(VanillaTypes.ITEM_STACK,altar);
    }
    @Override public RecipeType<AltarOfferingRecipe> getRecipeType(){return type;}
    @Override public Component getTitle(){return Component.translatable(impure?"jei.mortal_expansion.impurity_offerings":"jei.mortal_expansion.purity_offerings");}
    @Override public IDrawable getIcon(){return icon;}
    @Override public int getWidth(){return 176;}
    @Override public int getHeight(){return 116;}
    @Override public void setRecipe(IRecipeLayoutBuilder builder,AltarOfferingRecipe recipe,IFocusGroup focuses){
        builder.addInputSlot(12,12).addItemStack(recipe.offering());
        builder.addSlot(RecipeIngredientRole.CATALYST,146,12).addItemStack(altar);
        if(!recipe.remainder().isEmpty())builder.addOutputSlot(78,12).addItemStack(recipe.remainder());
    }
    @Override public void draw(AltarOfferingRecipe recipe,IRecipeSlotsView slots,GuiGraphics graphics,double mouseX,double mouseY){
        var font=Minecraft.getInstance().font;
        graphics.fill(0,0,getWidth(),getHeight(),impure?0xFF35252C:0xFF172421);
        graphics.renderOutline(10,10,20,20,0xFFB4E9CB);graphics.renderOutline(144,10,20,20,0xFFB4E9CB);
        graphics.blitSprite(ResourceLocation.withDefaultNamespace("container/furnace/lit_progress"),44,12,14,14);
        if(!recipe.remainder().isEmpty())graphics.renderOutline(76,10,20,20,0xFFB4E9CB);
        int y=38;
        for(var note:List.of(
                Component.translatable("jei.mortal_expansion.altar_burn",seconds(recipe.burnTicks())),
                Component.translatable(impure?"jei.mortal_expansion.altar_raises":"jei.mortal_expansion.altar_cleanses",
                        EmfScale.decimal(com.howierux.mortalexpansion.machine.AltarEntity.CLEANSE_HUNDREDTHS),seconds(com.howierux.mortalexpansion.machine.AltarEntity.CLEANSE_INTERVAL)),
                Component.translatable("jei.mortal_expansion.altar_area"),
                Component.translatable("jei.mortal_expansion.altar_consumption",seconds(com.howierux.mortalexpansion.machine.AltarEntity.OFFERING_INTERVAL)))){
            for(var line:font.split(note,166)){graphics.drawString(font,line,5,y,0xFFC3D3CA,false);y+=10;}
        }
    }
    private static String seconds(int ticks){return java.math.BigDecimal.valueOf(ticks).divide(java.math.BigDecimal.valueOf(20)).stripTrailingZeros().toPlainString();}
}
