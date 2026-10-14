package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.item.PharaohlScepterItem;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.inventory.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ScepterChargeGameTests {
    @GameTest(template="deferred_items_test")
    public static void inventory_reload_caps_consumes_one_and_persists(GameTestHelper h){
        var player=h.makeMockServerPlayerInLevel();var item=ModItems.PHARAOHL_SCEPTER.get();
        var stack=new ItemStack(item);player.getInventory().setItem(0,stack);
        var slot=new Slot(player.getInventory(),0,0,0);var raw=new ItemStack(ModItems.RAW_ECTOPLASM.get(),4);
        h.assertTrue(PharaohlScepterItem.charge(stack)==0 && item.getBarWidth(stack)==0,"New scepters start empty");
        h.assertFalse(item.overrideOtherStackedOnMe(stack,raw,slot,ClickAction.PRIMARY,player,net.minecraft.world.entity.SlotAccess.NULL),"Left-click does not reload");
        for(int i=1;i<=3;i++){
            h.assertTrue(item.overrideOtherStackedOnMe(stack,raw,slot,ClickAction.SECONDARY,player,net.minecraft.world.entity.SlotAccess.NULL)
                    && PharaohlScepterItem.charge(stack)==i*4 && raw.getCount()==4-i,"Inventory right-click consumes one raw and adds four charges");
        }
        item.overrideOtherStackedOnMe(stack,raw,slot,ClickAction.SECONDARY,player,net.minecraft.world.entity.SlotAccess.NULL);
        h.assertTrue(raw.getCount()==1 && item.getBarWidth(stack)==13,"Full scepter wastes no ammo");
        stack.set(ModDataComponents.SCEPTER_CHARGE.get(),1);var pure=new ItemStack(ModItems.PURE_ECTOPLASM.get(),2);
        item.overrideOtherStackedOnMe(stack,pure,slot,ClickAction.SECONDARY,player,net.minecraft.world.entity.SlotAccess.NULL);
        h.assertTrue(PharaohlScepterItem.charge(stack)==12 && pure.getCount()==1,"One purified fills the bar");
        var restored=ItemStack.parse(h.getLevel().registryAccess(),stack.save(h.getLevel().registryAccess())).orElseThrow();
        h.assertTrue(PharaohlScepterItem.charge(restored)==12,"Charges survive item serialization");
        player.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void summoning_spends_one_charge_and_failed_uses_spend_none(GameTestHelper h){
        var player=h.makeMockServerPlayerInLevel();player.setPos(h.absolutePos(new net.minecraft.core.BlockPos(5,2,5)).getCenter());
        var item=ModItems.PHARAOHL_SCEPTER.get();var stack=new ItemStack(item);player.setItemInHand(InteractionHand.MAIN_HAND,stack);
        h.assertTrue(item.use(h.getLevel(),player,InteractionHand.MAIN_HAND).getResult()==InteractionResult.FAIL,"Empty scepter cannot summon");
        PharaohlScepterItem.recharge(stack,new ItemStack(ModItems.PURE_ECTOPLASM.get()));
        for(int i=11;i>=0;i--){
            item.use(h.getLevel(),player,InteractionHand.MAIN_HAND);
            h.assertTrue(PharaohlScepterItem.charge(stack)==i && player.getCooldowns().isOnCooldown(item),"Each summon spends exactly one twelfth and starts cooldown");
            item.use(h.getLevel(),player,InteractionHand.MAIN_HAND);
            h.assertTrue(PharaohlScepterItem.charge(stack)==i,"Cooldown spends no additional charge");
            player.getCooldowns().removeCooldown(item);
        }
        var box=player.getBoundingBox().inflate(10);
        var mummies=h.getLevel().getEntitiesOfClass(com.howierux.mortalexpansion.entity.mob.MummyEntity.class,box);
        h.assertTrue(mummies.size()==24,"Twelve charges summon twelve pairs");mummies.forEach(net.minecraft.world.entity.Entity::discard);
        PharaohlScepterItem.recharge(stack,new ItemStack(ModItems.RAW_ECTOPLASM.get()));
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityJoinLevelEvent> cancel=e->{
            if(e.getEntity() instanceof com.howierux.mortalexpansion.entity.mob.MummyEntity)e.setCanceled(true);
        };
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);
        try{
            h.assertTrue(item.use(h.getLevel(),player,InteractionHand.MAIN_HAND).getResult()==InteractionResult.FAIL
                    && PharaohlScepterItem.charge(stack)==4 && !player.getCooldowns().isOnCooldown(item),"Canceled summons spend neither charge nor cooldown");
        }finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);}
        player.discard();h.succeed();
    }
}
