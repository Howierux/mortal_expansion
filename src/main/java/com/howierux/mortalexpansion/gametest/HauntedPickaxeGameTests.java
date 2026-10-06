package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.item.*;
import com.howierux.mortalexpansion.haunt.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HauntedPickaxeGameTests {
    @GameTest(template="deferred_items_test")
    public static void both_pickaxes_gain_wear_speed_only_on_pickaxe_materials(GameTestHelper h){
        for(var item:new Item[]{ModItems.BLOODSTONE_PICKAXE.get(),ModItems.SPECTRALITE_PICKAXE.get()}){
            var stack=new ItemStack(item);float full=stack.getDestroySpeed(Blocks.STONE.defaultBlockState()),dirt=stack.getDestroySpeed(Blocks.DIRT.defaultBlockState());
            h.assertTrue(full>1,"Original tier mining speed remains");stack.setDamageValue(stack.getMaxDamage()/2);
            float half=stack.getDestroySpeed(Blocks.STONE.defaultBlockState());h.assertTrue(half>full*1.49 && half<=full*1.51,"Half wear gives roughly 1.5 times base speed");
            stack.setDamageValue(stack.getMaxDamage()-1);float worn=stack.getDestroySpeed(Blocks.STONE.defaultBlockState());
            h.assertTrue(worn>half && worn<=full*2 && worn>full*1.99,"Nearly broken pickaxe approaches the bounded two-times boost");
            h.assertTrue(stack.getDestroySpeed(Blocks.DIRT.defaultBlockState())==dirt,"Wrong-tool materials do not receive the pickaxe boost");
        }h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void repair_intervals_scale_and_each_activation_costs_one_hundredth(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,2,5));int previous=1201;
        for(var tier:HauntTier.values()){
            int interval=HauntedPickaxeItem.repairInterval(tier);
            if(tier.id()==0){h.assertTrue(interval==0,"Void disables repair");continue;}
            h.assertTrue(interval<previous,"Higher tiers repair more frequently");previous=interval;
            for(var item:new Item[]{ModItems.BLOODSTONE_PICKAXE.get(),ModItems.SPECTRALITE_PICKAXE.get()}){
                HauntManager.setTier(h.getLevel(),pos,tier);int before=HauntManager.getHundredths(h.getLevel(),pos);
                var stack=new ItemStack(item);stack.setDamageValue(10);
                h.assertFalse(HauntedPickaxeItem.repair(stack,h.getLevel(),pos,0),"First exposure initializes clock, not a free repair");
                h.assertFalse(HauntedPickaxeItem.repair(stack,h.getLevel(),pos,interval-1),"Repair waits for the tier interval");
                h.assertTrue(HauntedPickaxeItem.repair(stack,h.getLevel(),pos,interval),"Due activation repairs");
                h.assertTrue(stack.getDamageValue()==9 && HauntManager.getHundredths(h.getLevel(),pos)==before-1,"Exactly one durability and 0.01 Haunt per activation");
                h.assertFalse(HauntedPickaxeItem.repair(stack,h.getLevel(),pos,interval),"Moving the stack cannot double repair in the same tick");
            }
        }h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void void_boundary_stops_repairs_and_full_tools_cost_nothing(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,2,5));var stack=new ItemStack(ModItems.BLOODSTONE_PICKAXE.get());stack.setDamageValue(4);
        HauntManager.setValue(h.getLevel(),pos,10);HauntedPickaxeItem.repair(stack,h.getLevel(),pos,0);
        h.assertTrue(HauntedPickaxeItem.repair(stack,h.getLevel(),pos,1200),"Natural boundary can pay its last activation");
        h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==999 && HauntManager.getTier(h.getLevel(),pos)==HauntTier.VOID,"Fractional withdrawal crosses the named tier exactly");
        h.assertTrue(!HauntedPickaxeItem.repair(stack,h.getLevel(),pos,2400) && stack.getDamageValue()==3,"Void never repairs despite retaining low-tier points");
        HauntManager.setValue(h.getLevel(),pos,20);stack.setDamageValue(0);
        h.assertTrue(!HauntedPickaxeItem.repair(stack,h.getLevel(),pos,3000) && HauntManager.getHundredths(h.getLevel(),pos)==2000,"Full durability costs no Haunt");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void carried_and_dropped_repairs_use_local_chunk_not_personal_haunt(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,2,5));var p=h.makeMockServerPlayerInLevel();p.setPos(pos.getCenter());PlayerHauntManager.setValue(p,40);
        HauntManager.setValue(h.getLevel(),pos,17);long now=h.getLevel().getServer().overworld().getGameTime();
        var stack=new ItemStack(ModItems.SPECTRALITE_PICKAXE.get());stack.setDamageValue(4);stack.set(ModDataComponents.PICKAXE_REPAIR_TIME.get(),now-1200);
        p.getInventory().setItem(10,stack);stack.getItem().inventoryTick(stack,h.getLevel(),p,10,false);
        h.assertTrue(stack.getDamageValue()==3 && HauntManager.getHundredths(h.getLevel(),pos)==1699,"Inventory storage repairs against current chunk");
        var dropped=new ItemStack(ModItems.BLOODSTONE_PICKAXE.get());dropped.setDamageValue(4);dropped.set(ModDataComponents.PICKAXE_REPAIR_TIME.get(),now-1200);
        var entity=new net.minecraft.world.entity.item.ItemEntity(h.getLevel(),pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,dropped);
        ((HauntedPickaxeItem)dropped.getItem()).onEntityItemUpdate(dropped,entity);
        h.assertTrue(entity.getItem().getDamageValue()==3 && HauntManager.getHundredths(h.getLevel(),pos)==1698,"Dropped item repairs and updates its visible stack");
        h.assertTrue(PlayerHauntManager.getValue(p)==40,"Personal exposure is not consumed");p.discard();entity.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void chunk_fractions_persist_and_whole_point_changes_preserve_them(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,2,5));var chunk=h.getLevel().getChunkAt(pos);
        h.assertTrue(new net.neoforged.neoforge.attachment.AttachmentHolder.AsField(chunk).getData(ModAttachments.HAUNT_FRACTION)==0,"Older chunk data defaults to zero fraction");
        HauntManager.setValue(h.getLevel(),pos,20);HauntManager.addHundredths(h.getLevel(),pos,-1);HauntManager.addPoints(h.getLevel(),pos,1);
        h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==2099,"Whole-point additions preserve the fraction");HauntManager.addPoints(h.getLevel(),pos,-1);
        h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==1999,"Whole-point withdrawal preserves the fraction");
        var restored=new net.neoforged.neoforge.attachment.AttachmentHolder.AsField(chunk);restored.deserializeInternal(h.getLevel().registryAccess(),chunk.writeAttachmentsToNBT(h.getLevel().registryAccess()));
        h.assertTrue(restored.getData(ModAttachments.HAUNT_FRACTION)==99 && restored.getData(ModAttachments.HAUNT_TIER)==1 && restored.getData(ModAttachments.HAUNT_PROGRESS)==9,"Serialized chunk fields retain exact 19.99");
        HauntManager.addHundredths(h.getLevel(),pos,Long.MAX_VALUE);h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==6900,"Upper bound is exact");
        HauntManager.addHundredths(h.getLevel(),pos,Long.MIN_VALUE);h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==0,"Lower bound cannot underflow");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void emf_and_pickaxe_network_roundtrips_preserve_new_precision_and_clock(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,2,5));var p=h.makeMockServerPlayerInLevel();p.setPos(pos.getCenter());
        HauntManager.setValue(h.getLevel(),pos,17);HauntManager.addHundredths(h.getLevel(),pos,-1);var reader=new ItemStack(ModItems.PRECISE_EMF_READER.get());EmfReaderItem.sample(reader,p);
        h.assertTrue(EmfScale.decimal(EmfReaderItem.hundredths(reader,p)).equals("16.99") && EmfReaderItem.value(reader,p)==16,"Precise reader sees fractional chunk Haunt; basic scale stays whole");
        var buffer=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),h.getLevel().registryAccess());
        try{ItemStack.STREAM_CODEC.encode(buffer,reader);h.assertTrue(EmfReaderItem.reading(ItemStack.STREAM_CODEC.decode(buffer),p).chunkHundredths()==1699,"Client receives exact fractional chunk reading");}finally{buffer.release();}
        var tool=new ItemStack(ModItems.BLOODSTONE_PICKAXE.get());tool.setDamageValue(10);tool.set(ModDataComponents.PICKAXE_REPAIR_TIME.get(),12345L);
        var ops=net.minecraft.resources.RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE,h.getLevel().registryAccess());
        var restored=ItemStack.CODEC.parse(ops,ItemStack.CODEC.encodeStart(ops,tool).getOrThrow()).getOrThrow();
        h.assertTrue(restored.get(ModDataComponents.PICKAXE_REPAIR_TIME.get())==12345L && restored.getDamageValue()==10,"Saved pickaxe retains cooldown and wear");
        var wire=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),h.getLevel().registryAccess());
        try{ItemStack.STREAM_CODEC.encode(wire,tool);h.assertTrue(ItemStack.STREAM_CODEC.decode(wire).get(ModDataComponents.PICKAXE_REPAIR_TIME.get())==12345L,"Client pickaxe cooldown codec stays synchronized");}finally{wire.release();}
        p.discard();h.succeed();
    }
}
