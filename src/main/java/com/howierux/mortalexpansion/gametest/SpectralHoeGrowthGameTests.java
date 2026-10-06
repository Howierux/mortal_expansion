package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.haunt.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpectralHoeGrowthGameTests {
    private static net.minecraft.server.level.ServerPlayer farmer(GameTestHelper h,BlockPos pos,Item hoe){
        var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
        p.setPos(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5);p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(hoe));return p;
    }
    private static void plant(GameTestHelper h,BlockPos pos){
        for(int x=(pos.getX()>>4)-1;x<=(pos.getX()>>4)+1;x++)for(int z=(pos.getZ()>>4)-1;z<=(pos.getZ()>>4)+1;z++)h.getLevel().getChunk(x,z);
        h.getLevel().setBlock(pos.below(),Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE,7),3);
        h.getLevel().setBlock(pos,Blocks.WHEAT.defaultBlockState(),3);
    }
    private static net.minecraft.world.InteractionResult use(GameTestHelper h,net.minecraft.server.level.ServerPlayer p,BlockPos pos){
        return p.gameMode.useItemOn(p,h.getLevel(),p.getMainHandItem(),InteractionHand.MAIN_HAND,
                new net.minecraft.world.phys.BlockHitResult(pos.getCenter(),net.minecraft.core.Direction.UP,pos,false));
    }
    @GameTest(template="deferred_items_test")
    public static void one_right_click_matures_crop_and_pays_per_native_pulse(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,3,5));plant(h,pos);var p=farmer(h,pos,ModItems.SPECTRALITE_HOE.get());
        HauntManager.setValue(h.getLevel(),pos,40);PlayerHauntManager.setValue(p,23);
        h.assertTrue(use(h,p,pos).consumesAction(),"Real right-click pipeline consumes the crop action");
        h.assertTrue(h.getLevel().getBlockState(pos).getValue(CropBlock.AGE)==7,"One click applies bonemeal until wheat is mature");
        int cost=40-HauntManager.getValue(h.getLevel(),pos);
        h.assertTrue(cost>=2 && cost<=4,"Haunt is paid per native two-to-five-stage bonemeal application");
        h.assertTrue(PlayerHauntManager.getValue(p)==23 && p.getMainHandItem().getDamageValue()==0,"Personal Haunt and hoe durability are not charged for growth");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void insufficient_haunt_stops_without_overdraft_or_mature_charges(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,3,5));plant(h,pos);var p=farmer(h,pos,ModItems.SPECTRALITE_HOE.get());HauntManager.setValue(h.getLevel(),pos,1);
        use(h,p,pos);int age=h.getLevel().getBlockState(pos).getValue(CropBlock.AGE);
        h.assertTrue(age>=2 && age<=5 && HauntManager.getValue(h.getLevel(),pos)==0,"One point pays one partial growth pulse and stops");
        use(h,p,pos);h.assertTrue(h.getLevel().getBlockState(pos).getValue(CropBlock.AGE)==age,"Empty chunk cannot produce free growth");
        h.getLevel().setBlock(pos,Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE,7),3);HauntManager.setValue(h.getLevel(),pos,10);
        use(h,p,pos);h.assertTrue(HauntManager.getValue(h.getLevel(),pos)==10,"Mature crops cost nothing");
        h.getLevel().setBlock(pos,Blocks.MELON_STEM.defaultBlockState().setValue(StemBlock.AGE,7),3);use(h,p,pos);
        h.assertTrue(HauntManager.getValue(h.getLevel(),pos)==10,"Mature stems cannot be repeatedly charged to produce fruit");
        h.getLevel().setBlock(pos,Blocks.STONE.defaultBlockState(),3);use(h,p,pos);
        h.assertTrue(HauntManager.getValue(h.getLevel(),pos)==10,"Unsupported targets cost nothing");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void bloodstone_hoe_does_not_gain_the_spectralite_active_ability(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,3,5));plant(h,pos);var p=farmer(h,pos,ModItems.BLOODSTONE_HOE.get());HauntManager.setValue(h.getLevel(),pos,40);
        use(h,p,pos);h.assertTrue(h.getLevel().getBlockState(pos).getValue(CropBlock.AGE)==0 && HauntManager.getValue(h.getLevel(),pos)==40,"Bloodstone remains a tilling tool without active crop growth");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void target_chunk_pays_even_when_player_stands_in_another_chunk(GameTestHelper h){
        var base=h.absolutePos(new BlockPos(5,3,5));int edge=(base.getX()>>4)*16+15;
        var pos=new BlockPos(edge+1,base.getY(),base.getZ());plant(h,pos);var p=farmer(h,pos,ModItems.SPECTRALITE_HOE.get());p.setPos(edge+0.5,pos.getY(),pos.getZ()+0.5);
        HauntManager.setValue(h.getLevel(),p.blockPosition(),0);HauntManager.setValue(h.getLevel(),pos,20);
        use(h,p,pos);h.assertTrue(h.getLevel().getBlockState(pos).getValue(CropBlock.AGE)==7,"Neighboring crop receives growth");
        h.assertTrue(HauntManager.getValue(h.getLevel(),p.blockPosition())==0 && HauntManager.getValue(h.getLevel(),pos)<20,"Only clicked crop's chunk is charged");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void canceled_bonemeal_preserves_crop_and_haunt(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(5,3,5));plant(h,pos);var p=farmer(h,pos,ModItems.SPECTRALITE_HOE.get());HauntManager.setValue(h.getLevel(),pos,20);
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.player.BonemealEvent> cancel=event->{if(event.getPlayer()==p && event.getPos().equals(pos))event.setCanceled(true);};
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);
        try{use(h,p,pos);h.assertTrue(h.getLevel().getBlockState(pos).getValue(CropBlock.AGE)==0 && HauntManager.getValue(h.getLevel(),pos)==20,"Framework veto consumes neither Haunt nor growth");}
        finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);}
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock> deny=event->{if(event.getEntity()==p && event.getPos().equals(pos))event.setCanceled(true);};
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(deny);
        try{use(h,p,pos);h.assertTrue(h.getLevel().getBlockState(pos).getValue(CropBlock.AGE)==0 && HauntManager.getValue(h.getLevel(),pos)==20,"Right-click protection also prevents growth and withdrawal");}
        finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(deny);p.discard();}h.succeed();
    }
}
