package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.item.HauntedPickaxeItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ItemEntity.class)
public abstract class EquipmentDroppedItemMixin {
 @Inject(method="tick",at=@At("TAIL")) private void mortalExpansion$repair(CallbackInfo ci){var e=(ItemEntity)(Object)this;if(e.isAlive()&&e.level() instanceof ServerLevel level&&e.getItem().getItem() instanceof HauntedPickaxeItem&&HauntedPickaxeItem.repair(e.getItem(),level,e.blockPosition(),level.getServer().overworld().getGameTime()))e.setItem(e.getItem().copy());}
}
