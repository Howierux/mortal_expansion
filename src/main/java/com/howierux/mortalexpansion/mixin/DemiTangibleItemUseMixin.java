package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.combat.DemiTangibleProjectiles;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
/** The framework has no generic projectile-with-firing-stack event for arbitrary modded items. */
@Mixin(ItemStack.class)
public abstract class DemiTangibleItemUseMixin {
    @WrapMethod(method="use")
    private InteractionResultHolder<ItemStack> mortalExpansion$use(Level level,Player player,InteractionHand hand,Operation<InteractionResultHolder<ItemStack>> original){
        return DemiTangibleProjectiles.using((ItemStack)(Object)this,player,()->original.call(level,player,hand));
    }
    @WrapMethod(method="finishUsingItem")
    private ItemStack mortalExpansion$finish(Level level,LivingEntity actor,Operation<ItemStack> original){
        return DemiTangibleProjectiles.using((ItemStack)(Object)this,actor,()->original.call(level,actor));
    }
    @WrapMethod(method={"useOn","onItemUseFirst"})
    private InteractionResult mortalExpansion$useOn(net.minecraft.world.item.context.UseOnContext context,Operation<InteractionResult> original){
        var player=context.getPlayer();
        return player==null?original.call(context):DemiTangibleProjectiles.using((ItemStack)(Object)this,player,()->original.call(context));
    }
    @WrapMethod(method={"releaseUsing","onUseTick"})
    private void mortalExpansion$shoot(Level level,LivingEntity actor,int ticks,Operation<Void> original){
        DemiTangibleProjectiles.using((ItemStack)(Object)this,actor,()->{original.call(level,actor,ticks);return null;});
    }
}
