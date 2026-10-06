package com.howierux.mortalexpansion.mixin;
import net.minecraft.world.inventory.*;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Smithing recipes expose no player-aware eligibility hook; reject before any pickup or input consumption. */
@Mixin(SmithingMenu.class)
public abstract class EctceteraSmithingKnowledgeMixin extends ItemCombinerMenu {
    protected EctceteraSmithingKnowledgeMixin(MenuType<?> type,int id,Inventory inventory,ContainerLevelAccess access){super(type,id,inventory,access);}
    @Inject(method="createResult",at=@At("TAIL"))
    private void ectcetera$gate(CallbackInfo ci){
        var recipe=resultSlots.getRecipeUsed();
        if(player instanceof net.minecraft.server.level.ServerPlayer && recipe!=null
                && !com.howierux.mortalexpansion.guide.SpectralInsight.canCraft(player,recipe.id())){
            resultSlots.setItem(0,net.minecraft.world.item.ItemStack.EMPTY);resultSlots.setRecipeUsed(null);
        }
    }
}
