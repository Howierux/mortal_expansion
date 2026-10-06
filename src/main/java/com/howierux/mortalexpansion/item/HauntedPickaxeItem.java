package com.howierux.mortalexpansion.item;
import com.howierux.mortalexpansion.registry.ModDataComponents;
import com.howierux.mortalexpansion.haunt.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;

/** Supported item speed/tick hooks; repairs use exact chunk Haunt rather than player exposure. */
public final class HauntedPickaxeItem extends PickaxeItem {
    public static final float MAX_MULTIPLIER=2.0F;
    public static final int REPAIR_AMOUNT=1,COST_HUNDREDTHS=1;
    public HauntedPickaxeItem(Tier tier,Properties properties){super(tier,properties);}
    public static float wearMultiplier(ItemStack stack){
        if(!stack.isDamageableItem() || stack.getMaxDamage()<=0)return 1;
        float wear=Math.clamp((float)stack.getDamageValue()/stack.getMaxDamage(),0,1);
        return 1+(MAX_MULTIPLIER-1)*wear;
    }
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){
        float base=super.getDestroySpeed(stack,state);
        return state.is(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE)?base*wearMultiplier(stack):base;
    }
    public static int repairInterval(HauntTier tier){return switch(tier.id()){
        case 1->1200;case 2->600;case 3->300;case 4->200;case 5->100;case 6->40;default->0;};}
    public static boolean repair(ItemStack stack,ServerLevel level,BlockPos pos,long now){
        if(!stack.isDamaged()){stack.remove(ModDataComponents.PICKAXE_REPAIR_TIME.get());return false;}
        if(!level.hasChunkAt(pos))return false;
        int interval=repairInterval(HauntManager.getTier(level,pos));
        if(interval==0){stack.remove(ModDataComponents.PICKAXE_REPAIR_TIME.get());return false;}
        Long last=stack.get(ModDataComponents.PICKAXE_REPAIR_TIME.get());
        if(last==null || now<last){stack.set(ModDataComponents.PICKAXE_REPAIR_TIME.get(),now);return false;}
        if(now-last<interval || HauntManager.getHundredths(level,pos)<COST_HUNDREDTHS)return false;
        HauntManager.addHundredths(level,pos,-COST_HUNDREDTHS);
        stack.setDamageValue(Math.max(0,stack.getDamageValue()-REPAIR_AMOUNT));
        stack.set(ModDataComponents.PICKAXE_REPAIR_TIME.get(),now);return true;
    }
    @Override public void inventoryTick(ItemStack stack,Level level,Entity holder,int slot,boolean selected){
        if(level instanceof ServerLevel server)repair(stack,server,holder.blockPosition(),server.getServer().overworld().getGameTime());
    }
    @Override public boolean onEntityItemUpdate(ItemStack stack,ItemEntity entity){
        if(entity.level() instanceof ServerLevel server && repair(stack,server,entity.blockPosition(),server.getServer().overworld().getGameTime()))entity.setItem(stack.copy());
        return false;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<Component> tooltip,TooltipFlag flag){
        super.appendHoverText(stack,context,tooltip,flag);
        tooltip.add(Component.translatable("tooltip.mortal_expansion.pickaxe.wear",String.format(java.util.Locale.ROOT,"%.2f",wearMultiplier(stack))).withStyle(net.minecraft.ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.mortal_expansion.pickaxe.repair",EmfScale.decimal(COST_HUNDREDTHS)).withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}
