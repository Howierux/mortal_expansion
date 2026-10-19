package com.howierux.mortalexpansion.integration.jei;

import com.howierux.mortalexpansion.guide.InsightSecret;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Marks the recipe's origin, not client knowledge: permanent server knowledge isn't client-synced. */
public final class SecretRecipeOverlay<R extends Recipe<?>> implements IRecipeCategoryDecorator<RecipeHolder<R>> {
    private static final java.util.Set<net.minecraft.resources.ResourceLocation> SECRET_RECIPES =
            java.util.Arrays.stream(InsightSecret.values()).flatMap(secret -> secret.recipes().stream())
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
    @Override public void draw(RecipeHolder<R> recipe, IRecipeCategory<RecipeHolder<R>> category,
            IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        if (!SECRET_RECIPES.contains(recipe.id())) return;
        int width = category.getWidth(), height = category.getHeight();
        graphics.renderOutline(0, 0, width, height, 0xCC75DCA4);
        graphics.fill(2, height - 11, width - 2, height - 1, 0xCD102A23);
        graphics.pose().pushPose();
        graphics.pose().translate(width / 2.0F, height - 9, 220);
        graphics.pose().scale(0.65F, 0.65F, 1);
        graphics.drawCenteredString(Minecraft.getInstance().font,
                Component.translatable("jei.mortal_expansion.secret_banner"), 0, 0, 0xFFA7F4CD);
        graphics.pose().popPose();
    }
    @Override public void decorateTooltips(ITooltipBuilder tooltip, RecipeHolder<R> recipe,
            IRecipeCategory<RecipeHolder<R>> category, IRecipeSlotsView slots, double x, double y) {
        if (SECRET_RECIPES.contains(recipe.id()) && y >= category.getHeight() - 11 && y < category.getHeight()) {
            tooltip.add(Component.translatable("jei.mortal_expansion.secret_hint"));
        }
    }
}
