package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.fancifier.FadingWorld;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class FadingDropsMixin {
    @ModifyReturnValue(method="getDrops",at=@At("RETURN"))
    private List<ItemStack> mortalExpansion$drops(List<ItemStack> drops,LootParams.Builder params){
        var origin=params.getOptionalParameter(LootContextParams.ORIGIN);return origin==null?drops:FadingWorld.decorateDrops(drops,params.getLevel(),BlockPos.containing(origin),(BlockState)(Object)this);
    }
}
