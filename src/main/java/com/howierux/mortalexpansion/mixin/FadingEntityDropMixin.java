package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(Entity.class)
public abstract class FadingEntityDropMixin {
    @ModifyVariable(method="spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",at=@At("HEAD"),argsOnly=true)
    private ItemStack mortalExpansion$carryOpacity(ItemStack stack){
        if((Object)this instanceof FallingBlockEntity entity && stack.getItem() instanceof BlockItem){int opacity=(entity.hasData(ModAttachments.FADING_ENTITY)?entity.getData(ModAttachments.FADING_ENTITY):100);if(opacity<100){stack=stack.copy();stack.set(ModDataComponents.FADING.get(),opacity);}}return stack;
    }
}
