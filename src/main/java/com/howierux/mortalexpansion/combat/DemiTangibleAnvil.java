package com.howierux.mortalexpansion.combat;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
/** Demi-Tangible alone bypasses item enchantability/book vetoes. Other enchantments retain normal checks. */
@EventBusSubscriber(modid="mortal_expansion")
public final class DemiTangibleAnvil {
    @SubscribeEvent(priority=EventPriority.LOWEST,receiveCanceled=true)
    public static void update(AnvilUpdateEvent event){
        var left=event.getLeft();var book=event.getRight();
        if(left.isEmpty() || !book.is(Items.ENCHANTED_BOOK))return;
        var offered=book.getOrDefault(DataComponents.STORED_ENCHANTMENTS,ItemEnchantments.EMPTY);
        var demi=offered.keySet().stream().filter(holder->holder.is(ModEnchantments.DEMI_TANGIBLE)).findFirst().orElse(null);
        if(demi==null)return;
        var output=left.copy();
        if(left.is(Items.BOOK)){
            if(left.getCount()!=1)return;
            output=new ItemStack(Items.ENCHANTED_BOOK);output.applyComponents(left.getComponentsPatch());
        }
        var component=output.is(Items.ENCHANTED_BOOK)?DataComponents.STORED_ENCHANTMENTS:DataComponents.ENCHANTMENTS;
        var existing=output.getOrDefault(component,ItemEnchantments.EMPTY);
        var enchantments=new ItemEnchantments.Mutable(existing);boolean changed=false;
        long cost=(long)left.getOrDefault(DataComponents.REPAIR_COST,0)+book.getOrDefault(DataComponents.REPAIR_COST,0);
        if(existing.getLevel(demi)==0){enchantments.set(demi,1);cost+=Math.max(1,demi.value().getAnvilCost()/2);changed=true;}
        for(var entry:offered.entrySet()){
            var holder=entry.getKey();if(holder.is(ModEnchantments.DEMI_TANGIBLE))continue;
            boolean supported=left.is(Items.BOOK) || left.is(Items.ENCHANTED_BOOK)
                    || EnchantmentHelper.canStoreEnchantments(left) && left.supportsEnchantment(holder);
            if(!supported || enchantments.keySet().stream().anyMatch(current->!current.equals(holder) && !Enchantment.areCompatible(current,holder)))continue;
            int before=enchantments.getLevel(holder),after=Math.min(holder.value().getMaxLevel(),before==entry.getIntValue()?before+1:Math.max(before,entry.getIntValue()));
            if(after>before){enchantments.set(holder,after);cost+=(long)Math.max(1,holder.value().getAnvilCost()/2)*after;changed=true;}
        }
        String name=event.getName();
        if(name!=null){
            if(name.isBlank()){
                if(output.has(DataComponents.CUSTOM_NAME)){output.remove(DataComponents.CUSTOM_NAME);cost++;changed=true;}
            }else if(!name.equals(output.getHoverName().getString())){output.set(DataComponents.CUSTOM_NAME,Component.literal(name));cost++;changed=true;}
        }
        if(!changed)return;
        output.set(component,enchantments.toImmutable());
        int prior=Math.max(left.getOrDefault(DataComponents.REPAIR_COST,0),book.getOrDefault(DataComponents.REPAIR_COST,0));
        output.set(DataComponents.REPAIR_COST,(int)Math.min(Integer.MAX_VALUE,2L*prior+1));
        event.setCanceled(false);event.setOutput(output);event.setMaterialCost(1);event.setCost(Math.max(1,cost));
    }
}
