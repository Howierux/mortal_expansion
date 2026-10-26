package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.fancifier.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
@Mixin(FallingBlockEntity.class)
public abstract class FadingFallingBlockMixin {
    @WrapMethod(method="fall") private static FallingBlockEntity mortalExpansion$fall(Level level,BlockPos pos,BlockState state,Operation<FallingBlockEntity> original){
        int opacity=FadingWorld.opacity(level,pos);var entity=original.call(level,pos,state);if(opacity<100)((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)entity).setAttached(com.howierux.mortalexpansion.content.ContentRuntime.FADING_ENTITY,opacity);return entity;
    }
    @WrapMethod(method="tick") private void mortalExpansion$land(Operation<Void> original){
        var entity=(FallingBlockEntity)(Object)this;int opacity=(((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)entity).hasAttached(com.howierux.mortalexpansion.content.ContentRuntime.FADING_ENTITY)?((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)entity).getAttachedOrCreate(com.howierux.mortalexpansion.content.ContentRuntime.FADING_ENTITY):100);
        if(entity.level().isClientSide || opacity>=100){original.call();return;}
        var previous=FadingWorld.PLACEMENT.get();var scope=new FadingWorld.Placement(entity.level(),opacity);scope.block=entity.getBlockState().getBlock();FadingWorld.PLACEMENT.set(scope);
        try{original.call();for(var pos:scope.positions)if(!entity.level().getBlockState(pos).isAir())FadingWorld.set(entity.level(),pos,opacity);}
        finally{if(previous==null)FadingWorld.PLACEMENT.remove();else FadingWorld.PLACEMENT.set(previous);}
    }
}
