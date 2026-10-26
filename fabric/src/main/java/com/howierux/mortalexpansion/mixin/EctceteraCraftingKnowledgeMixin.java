package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.guide.SpectralInsight;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

/** Crafting input has no player context and post-craft events cannot safely reject a recipe. */
@Mixin(CraftingMenu.class)
public abstract class EctceteraCraftingKnowledgeMixin {
    @Redirect(method="slotChangedCraftingGrid",at=@At(value="INVOKE",target="Lnet/minecraft/world/inventory/ResultContainer;setRecipeUsed(Lnet/minecraft/world/level/Level;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/crafting/RecipeHolder;)Z"))
    private static boolean ectcetera$gate(ResultContainer result,Level level,ServerPlayer player,RecipeHolder<?> recipe){
        return SpectralInsight.canCraft(player,recipe.id()) && result.setRecipeUsed(level,player,recipe);
    }
}
