package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.fancifier.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
@Mixin(BlockItem.class)
public abstract class FadingBlockItemMixin {
    @WrapMethod(method="place")
    private InteractionResult mortalExpansion$place(BlockPlaceContext context,Operation<InteractionResult> original) {
        int opacity=FancifierRules.opacity(context.getItemInHand());if(opacity==100 || context.getLevel().isClientSide)return original.call(context);
        var previous=FadingWorld.PLACEMENT.get();if(previous!=null && previous.level==context.getLevel())return original.call(context);var scope=new FadingWorld.Placement(context.getLevel(),opacity);FadingWorld.PLACEMENT.set(scope);
        try {var result=original.call(context);if(result.consumesAction())for(var pos:scope.positions)if(!context.getLevel().getBlockState(pos).isAir())FadingWorld.set(context.getLevel(),pos,opacity);return result;}
        finally {if(previous==null)FadingWorld.PLACEMENT.remove();else FadingWorld.PLACEMENT.set(previous);}
    }
    @WrapOperation(method="place",at=@At(value="INVOKE",target="Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean mortalExpansion$target(BlockItem item,BlockPlaceContext context,BlockState state,Operation<Boolean> original){
        var scope=FadingWorld.PLACEMENT.get();if(scope!=null){scope.block=state.getBlock();scope.positions.add(context.getClickedPos().immutable());}
        return original.call(item,context,state);
    }
}
