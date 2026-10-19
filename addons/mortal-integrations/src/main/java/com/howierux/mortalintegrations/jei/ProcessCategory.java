package com.howierux.mortalintegrations.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ProcessCategory implements IRecipeCategory<ProcessRecipe> {
    private final RecipeType<ProcessRecipe> type;
    private final Component title;
    private final IDrawable icon;
    public ProcessCategory(IGuiHelper gui, RecipeType<ProcessRecipe> type, String titleKey, ItemStack icon) {
        this.type = type; this.title = Component.translatable(titleKey);
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, icon);
    }
    @Override public RecipeType<ProcessRecipe> getRecipeType() { return type; }
    @Override public Component getTitle() { return title; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 176; }
    @Override public int getHeight() { return 110; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, ProcessRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < recipe.inputs().size(); i++) {
            builder.addInputSlot(10 + i * 24, 10).addIngredients(recipe.inputs().get(i));
        }
        builder.addOutputSlot(146, 10).addItemStack(recipe.output());
    }
    @Override public void draw(ProcessRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double x, double y) {
        var font = Minecraft.getInstance().font;
        graphics.fill(0, 0, getWidth(), getHeight(), 0xFF172421);
        for (int i = 0; i < recipe.inputs().size(); i++) graphics.renderOutline(8 + i * 24, 8, 20, 20, 0xFF6A947E);
        graphics.renderOutline(144, 8, 20, 20, 0xFFB4E9CB);
        graphics.drawString(font, "->", 119, 14, 0xFFB4E9CB, false);
        int lineY = 36;
        for (var note : recipe.notes()) {
            for (var line : font.split(note, 166)) {
                graphics.drawString(font, line, 5, lineY, 0xFFC3D3CA, false);
                lineY += 10;
            }
        }
    }
}
