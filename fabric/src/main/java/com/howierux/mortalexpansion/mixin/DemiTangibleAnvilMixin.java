package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.combat.CombatRuntime;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Add only Demi-Tangible when vanilla cannot enchant the item; retain native handling of all other enchantments. */
@Mixin(AnvilMenu.class)
public abstract class DemiTangibleAnvilMixin extends ItemCombinerMenu {
 @Shadow @Final private DataSlot cost;
 @Shadow private int repairItemCountCost;
 @Shadow private String itemName;
 protected DemiTangibleAnvilMixin(MenuType<?> type,int id,Inventory inventory,ContainerLevelAccess access){super(type,id,inventory,access);}
 @Inject(method="createResult",at=@At("TAIL"))
 private void demi(CallbackInfo ci){
  var left=inputSlots.getItem(0);var book=inputSlots.getItem(1);if(left.isEmpty()||!book.is(Items.ENCHANTED_BOOK))return;
  var offered=book.getOrDefault(DataComponents.STORED_ENCHANTMENTS,ItemEnchantments.EMPTY);
  var holder=offered.keySet().stream().filter(e->e.is(CombatRuntime.DEMI_ENCHANTMENT)).findFirst().orElse(null);if(holder==null)return;
  var nativeResult=resultSlots.getItem(0);boolean fallback=nativeResult.isEmpty();var output=fallback?left.copy():nativeResult.copy();
  if(output.is(Items.BOOK)){if(output.getCount()!=1)return;output=new ItemStack(Items.ENCHANTED_BOOK);output.applyComponents(left.getComponentsPatch());}
  var component=output.is(Items.ENCHANTED_BOOK)?DataComponents.STORED_ENCHANTMENTS:DataComponents.ENCHANTMENTS;
  var current=output.getOrDefault(component,ItemEnchantments.EMPTY);if(current.getLevel(holder)>0)return;
  var mutable=new ItemEnchantments.Mutable(current);mutable.set(holder,1);output.set(component,mutable.toImmutable());
  int prior=Math.max(left.getOrDefault(DataComponents.REPAIR_COST,0),book.getOrDefault(DataComponents.REPAIR_COST,0));output.set(DataComponents.REPAIR_COST,(int)Math.min(Integer.MAX_VALUE,2L*prior+1));
  if(fallback&&itemName!=null){if(itemName.isBlank())output.remove(DataComponents.CUSTOM_NAME);else output.set(DataComponents.CUSTOM_NAME,Component.literal(itemName));}
  long charge=fallback?(long)left.getOrDefault(DataComponents.REPAIR_COST,0)+book.getOrDefault(DataComponents.REPAIR_COST,0)+2:Math.max(0,cost.get())+2L;
  cost.set((int)Math.min(Integer.MAX_VALUE,Math.max(1,charge)));repairItemCountCost=1;resultSlots.setItem(0,output);broadcastChanges();
 }
}
