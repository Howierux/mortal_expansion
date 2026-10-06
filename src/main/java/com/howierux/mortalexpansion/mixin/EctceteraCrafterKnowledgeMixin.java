package com.howierux.mortalexpansion.mixin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Vanilla Crafter has no owner; players teach it their permanent knowledge by opening it. */
@Mixin(CrafterBlock.class)
public abstract class EctceteraCrafterKnowledgeMixin {
    @Inject(method="dispenseFrom",at=@At("HEAD"),cancellable=true)
    private void ectcetera$gate(BlockState state,ServerLevel level,BlockPos pos,CallbackInfo ci){
        if(!(level.getBlockEntity(pos) instanceof net.minecraft.world.level.block.entity.CrafterBlockEntity crafter))return;
        var recipe=CrafterBlock.getPotentialResults(level,crafter.asCraftInput());if(recipe.isEmpty())return;
        int required=com.howierux.mortalexpansion.guide.InsightSecret.required(recipe.get().id());
        if(required!=0 && (crafter.getPersistentData().getInt("MortalExpansionEctceteraKnowledge")&required)==0){level.levelEvent(1050,pos,0);ci.cancel();}
    }
}
