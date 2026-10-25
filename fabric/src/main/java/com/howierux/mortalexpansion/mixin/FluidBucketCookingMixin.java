package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.catalog.CatalogItems;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Native cooking omits input crafting remainders; Fabric has no successful-smelt callback.
 * Observe actual input consumption, so blocked output and mere fuel consumption never produce a bucket. */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FluidBucketCookingMixin {
 @Inject(method="serverTick",at=@At("HEAD"))
 private static void capture(Level level,BlockPos pos,BlockState state,AbstractFurnaceBlockEntity furnace,CallbackInfo ci,@Share("meBuckets") LocalIntRef before){
  var input=furnace.getItem(0);before.set(input.is(CatalogItems.HOLY_WATER_BUCKET)||input.is(CatalogItems.LIQUID_ECTOPLASM_BUCKET)||input.is(CatalogItems.PURIFIED_ECTOPLASM_BUCKET)?input.getCount():0);
 }
 @Inject(method="serverTick",at=@At("RETURN"))
 private static void restore(Level level,BlockPos pos,BlockState state,AbstractFurnaceBlockEntity furnace,CallbackInfo ci,@Share("meBuckets") LocalIntRef before){
  if(before.get()<=0||furnace.getItem(0).getCount()>=before.get())return;
  if(furnace.getItem(0).isEmpty())furnace.setItem(0,new ItemStack(Items.BUCKET));
  else net.minecraft.world.Containers.dropItemStack(level,pos.getX()+.5,pos.getY()+1,pos.getZ()+.5,new ItemStack(Items.BUCKET));
 }
}
