package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.fancifier.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
/** Surround the framework placement transaction, so cancellations finish before opacity is committed. */
@Mixin(ItemStack.class)
public abstract class FadingItemUseMixin {
    @WrapMethod(method="useOn")
    private InteractionResult mortalExpansion$transaction(UseOnContext context,Operation<InteractionResult> original){
        var stack=(ItemStack)(Object)this;int opacity=FancifierRules.opacity(stack);
        if(opacity==100 || !(stack.getItem() instanceof BlockItem) || context.getLevel().isClientSide)return original.call(context);
        var previous=FadingWorld.PLACEMENT.get();var scope=new FadingWorld.Placement(context.getLevel(),opacity);FadingWorld.PLACEMENT.set(scope);
        try{var result=original.call(context);if(result.consumesAction())for(var pos:scope.positions)if(!context.getLevel().getBlockState(pos).isAir())FadingWorld.set(context.getLevel(),pos,opacity);return result;}
        finally{if(previous==null)FadingWorld.PLACEMENT.remove();else FadingWorld.PLACEMENT.set(previous);}
    }
}
