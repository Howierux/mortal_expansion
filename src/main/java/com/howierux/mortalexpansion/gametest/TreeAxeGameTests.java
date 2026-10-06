package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.item.TreeAxeFelling;
import com.howierux.mortalexpansion.haunt.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TreeAxeGameTests {
    private static net.minecraft.server.level.ServerPlayer player(GameTestHelper h,Item item,BlockPos pos){
        var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setNoGravity(true);
        p.setPos(pos.getX()+0.5,pos.getY(),pos.getZ()-3.5);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(item));return p;
    }
    private static java.util.List<BlockPos> tree(GameTestHelper h,BlockPos root){
        for(var pos:BlockPos.betweenClosed(root.offset(-2,-1,-2),root.offset(4,7,4)))h.getLevel().setBlock(pos,Blocks.AIR.defaultBlockState(),3);
        var logs=java.util.List.of(root.below(),root,root.above(),root.above(2),root.offset(1,3,0),root.offset(2,4,0));
        for(var pos:logs)h.getLevel().setBlock(pos,Blocks.OAK_LOG.defaultBlockState(),3);
        h.getLevel().setBlock(root.below(),Blocks.STRIPPED_OAK_LOG.defaultBlockState(),3);
        h.getLevel().setBlock(root.offset(2,5,0),Blocks.OAK_LEAVES.defaultBlockState(),3);return logs;
    }
    private static void fellCase(GameTestHelper h,int index){
        var root=h.absolutePos(new BlockPos(6,2,6));var logs=tree(h,root);var other=root.offset(0,2,1);h.getLevel().setBlock(other,Blocks.BIRCH_LOG.defaultBlockState(),3);
        var p=player(h,index==0?ModItems.BLOODSTONE_AXE.get():ModItems.SPECTRALITE_AXE.get(),root);
        h.assertTrue(p.gameMode.destroyBlock(root),"Normal log mining succeeds");
        h.runAfterDelay(2,()->{
            h.assertTrue(logs.stream().allMatch(pos->h.getLevel().getBlockState(pos).isAir()),"Whole connected tree, lower stump, diagonal branches and stripped log family are felled");
            h.assertTrue(h.getLevel().getBlockState(other).is(Blocks.BIRCH_LOG),"A different wood family is not merged into the tree: " + h.getLevel().getBlockState(other) + " oak tags " + Blocks.OAK_LOG.builtInRegistryHolder().tags().toList());
            h.assertTrue(p.getMainHandItem().getDamageValue()==6,"Each log uses normal durability");p.discard();if(index==0)fellCase(h,1);else h.succeed();
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=60)
    public static void both_axes_fell_branches_stumps_and_matching_log_variants(GameTestHelper h){fellCase(h,0);}
    @GameTest(template="deferred_items_test",timeoutTicks=50)
    public static void structures_placed_foliage_and_sneaking_do_not_auto_fell(GameTestHelper h){
        var root=h.absolutePos(new BlockPos(6,2,6));tree(h,root);h.getLevel().setBlock(root.offset(2,5,0),Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT,true),3);
        h.assertTrue(TreeAxeFelling.discover(h.getLevel(),root).isEmpty(),"Placed foliage does not establish a natural tree");
        var p=player(h,ModItems.BLOODSTONE_AXE.get(),root);p.gameMode.destroyBlock(root);
        h.runAfterDelay(2,()->{
            h.assertTrue(h.getLevel().getBlockState(root.above()).is(Blocks.OAK_LOG),"A log structure is mined normally");tree(h,root);p.setShiftKeyDown(true);p.gameMode.destroyBlock(root);
            h.runAfterDelay(2,()->{h.assertTrue(h.getLevel().getBlockState(root.above()).is(Blocks.OAK_LOG),"Sneaking preserves single-log mining on a real tree");p.discard();h.succeed();});
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=50)
    public static void protected_branches_and_broken_axes_stop_felling(GameTestHelper h){
        var root=h.absolutePos(new BlockPos(6,2,6));tree(h,root);var blocked=root.above(2);var p=player(h,ModItems.SPECTRALITE_AXE.get(),root);
        java.util.function.Consumer<net.neoforged.neoforge.event.level.BlockEvent.BreakEvent> veto=e->{if(e.getPlayer()==p && e.getPos().equals(blocked))e.setCanceled(true);};net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(veto);p.gameMode.destroyBlock(root);
        h.runAfterDelay(2,()->{
            try{h.assertTrue(h.getLevel().getBlockState(blocked).is(Blocks.OAK_LOG) && h.getLevel().getBlockState(root.offset(2,4,0)).is(Blocks.OAK_LOG),"Protected trunk is not a bridge to higher branches");}
            finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(veto);}
            tree(h,root);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.BLOODSTONE_AXE.get()));p.getMainHandItem().setDamageValue(p.getMainHandItem().getMaxDamage()-1);p.gameMode.destroyBlock(root);
            h.runAfterDelay(2,()->{h.assertTrue(p.getMainHandItem().isEmpty() && h.getLevel().getBlockState(root.above()).is(Blocks.OAK_LOG),"Tool break ends the harvest without free extra blocks");p.discard();h.succeed();});
        });
    }
    private static void planting(GameTestHelper h,BlockPos pos,net.minecraft.world.level.block.Block sapling){
        int radius=16;for(int x=(pos.getX()-radius)>>4;x<=(pos.getX()+radius)>>4;x++)for(int z=(pos.getZ()-radius)>>4;z<=(pos.getZ()+radius)>>4;z++)h.getLevel().getChunk(x,z);
        for(var at:BlockPos.betweenClosed(pos.offset(-4,-1,-4),pos.offset(4,15,4)))h.getLevel().setBlock(at,at.getY()==pos.getY()-1?Blocks.DIRT.defaultBlockState():Blocks.AIR.defaultBlockState(),3);
        h.getLevel().setBlock(pos,sapling.defaultBlockState(),3);
    }
    private static void use(GameTestHelper h,net.minecraft.server.level.ServerPlayer p,BlockPos pos){
        p.gameMode.useItemOn(p,h.getLevel(),p.getMainHandItem(),net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(pos.getCenter(),Direction.UP,pos,false));
    }
    @GameTest(template="deferred_items_test")
    public static void spectralite_sapling_use_pays_fractional_chunk_cost_and_keeps_personal_haunt(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(6,2,6));planting(h,pos,Blocks.OAK_SAPLING);var p=player(h,ModItems.SPECTRALITE_AXE.get(),pos);HauntManager.setValue(h.getLevel(),pos,20);PlayerHauntManager.setValue(p,33);
        h.getLevel().getRandom().setSeed(12345);use(h,p,pos);
        h.assertTrue(h.getLevel().getBlockState(pos).getValue(SaplingBlock.STAGE)==1,"Native bonemeal advances the sapling");
        h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==1990 && PlayerHauntManager.getValue(p)==33 && p.getMainHandItem().getDamageValue()==0,"Growth costs only 0.10 target-chunk Haunt");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void native_growth_creates_vanilla_and_spiritwood_trees(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(6,2,6));var p=player(h,ModItems.SPECTRALITE_AXE.get(),pos);
        for(var sapling:new net.minecraft.world.level.block.Block[]{Blocks.OAK_SAPLING,ModBlocks.SPIRITWOOD_SAPLING.get()}){
            planting(h,pos,sapling);h.getLevel().setBlock(pos,sapling.defaultBlockState().setValue(SaplingBlock.STAGE,1),3);HauntManager.setValue(h.getLevel(),pos,20);
            h.getLevel().getRandom().setSeed(12345);use(h,p,pos);
            h.assertTrue(h.getLevel().getBlockState(pos).is(net.minecraft.tags.BlockTags.LOGS),"Native vanilla and Spiritwood tree grower behavior is retained");
            h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==1990,"Successful tree growth still uses one application cost");
        }p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void low_funds_bloodstone_and_bonemeal_veto_cannot_spend_or_grow(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(6,2,6));planting(h,pos,Blocks.OAK_SAPLING);var p=player(h,ModItems.SPECTRALITE_AXE.get(),pos);HauntManager.setValue(h.getLevel(),pos,0);HauntManager.addHundredths(h.getLevel(),pos,9);
        use(h,p,pos);h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==9 && h.getLevel().getBlockState(pos).getValue(SaplingBlock.STAGE)==0,"Insufficient fractional funds cause no mutation");
        HauntManager.setValue(h.getLevel(),pos,20);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.BLOODSTONE_AXE.get()));use(h,p,pos);
        h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==2000 && h.getLevel().getBlockState(pos).getValue(SaplingBlock.STAGE)==0,"Bloodstone does not gain the growth ability");p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.SPECTRALITE_AXE.get()));
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.player.BonemealEvent> cancel=e->{if(e.getPlayer()==p && e.getPos().equals(pos))e.setCanceled(true);};net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);
        try{use(h,p,pos);h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==2000 && h.getLevel().getBlockState(pos).getValue(SaplingBlock.STAGE)==0,"Native bonemeal veto preserves funds and sapling");}
        finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);p.discard();}h.succeed();
    }
}
