package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.item.AreaShovelMining;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AreaShovelGameTests {
    private static net.minecraft.server.level.ServerPlayer player(GameTestHelper h,Item tool,BlockPos centre,Direction.Axis axis){
        var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setNoGravity(true);
        p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(tool));
        p.setPos(centre.getX()+0.5-(axis==Direction.Axis.X?4:0),centre.getY(),centre.getZ()+0.5-(axis==Direction.Axis.Z?4:0));
        p.setYRot(axis==Direction.Axis.X?-90:0);return p;
    }
    private static void clear(GameTestHelper h,BlockPos centre){for(int x=-2;x<=2;x++)for(int y=-2;y<=2;y++)for(int z=-2;z<=2;z++)h.getLevel().setBlock(centre.offset(x,y,z),Blocks.AIR.defaultBlockState(),3);}
    private static void wall(GameTestHelper h,BlockPos c,Direction.Axis axis,net.minecraft.world.level.block.Block block){
        clear(h,c);for(int y=-1;y<=1;y++)for(int side=-1;side<=1;side++){
            var pos=axis==Direction.Axis.X?c.offset(0,y,side):c.offset(side,y,0);h.getLevel().setBlock(pos,block.defaultBlockState(),3);
        }
    }
    private static long remaining(GameTestHelper h,BlockPos min,int sx,int sy,int sz){
        long n=0;for(int x=0;x<sx;x++)for(int y=0;y<sy;y++)for(int z=0;z<sz;z++)if(h.getLevel().getBlockState(min.offset(x,y,z)).is(Blocks.DIRT))n++;return n;
    }
    private static void planeCase(GameTestHelper h,int number){
        var axis=number%2==0?Direction.Axis.Z:Direction.Axis.X;var tool=number<2?ModItems.BLOODSTONE_SHOVEL.get():ModItems.SPECTRALITE_SHOVEL.get();
        var c=h.absolutePos(new BlockPos(7,2,7));wall(h,c,axis,Blocks.DIRT);var behind=axis==Direction.Axis.X?c.east():c.south();h.getLevel().setBlock(behind,Blocks.DIRT.defaultBlockState(),3);
        var p=player(h,tool,c,axis);h.assertTrue(p.gameMode.destroyBlock(c),"Normal centre mining succeeds");
        h.runAfterDelay(2,()->{
            for(int y=-1;y<=1;y++)for(int side=-1;side<=1;side++)h.assertTrue(h.getLevel().getBlockState(axis==Direction.Axis.X?c.offset(0,y,side):c.offset(side,y,0)).isAir(),"Both shovels remove a vertical centred plane in both orientations");
            h.assertTrue(h.getLevel().getBlockState(behind).is(Blocks.DIRT),"Depth remains one; recursive expansion is suppressed");
            h.assertTrue(p.getMainHandItem().getDamageValue()==9,"Normal durability is charged per mined dirt block");p.discard();
            if(number<3)planeCase(h,number+1);else h.succeed();
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=60)
    public static void both_shovels_mine_only_the_centred_vertical_plane(GameTestHelper h){planeCase(h,0);}
    @GameTest(template="deferred_items_test",timeoutTicks=40)
    public static void sneak_preserves_single_block_and_normal_path_creation(GameTestHelper h){
        var c=h.absolutePos(new BlockPos(7,2,7));wall(h,c,Direction.Axis.Z,Blocks.DIRT);var p=player(h,ModItems.SPECTRALITE_SHOVEL.get(),c,Direction.Axis.Z);p.setShiftKeyDown(true);p.gameMode.destroyBlock(c);
        h.runAfterDelay(2,()->{
            h.assertTrue(h.getLevel().getBlockState(c.east()).is(Blocks.DIRT) && p.getMainHandItem().getDamageValue()==1,"Sneaking disables the extra mining");
            var path=h.absolutePos(new BlockPos(3,1,3));h.getLevel().setBlock(path,Blocks.DIRT.defaultBlockState(),3);h.getLevel().setBlock(path.above(),Blocks.AIR.defaultBlockState(),3);
            p.gameMode.useItemOn(p,h.getLevel(),p.getMainHandItem(),net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(path.getCenter(),Direction.UP,path,false));
            h.assertTrue(h.getLevel().getBlockState(path).is(Blocks.DIRT_PATH),"Sneak right-click keeps normal shovel paths");p.discard();h.succeed();
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=60)
    public static void charged_release_requires_full_charge_and_stops_at_sixty_four(GameTestHelper h){
        var min=h.absolutePos(new BlockPos(8,1,6));for(int x=0;x<5;x++)for(int y=0;y<4;y++)for(int z=0;z<4;z++)h.getLevel().setBlock(min.offset(x,y,z),Blocks.DIRT.defaultBlockState(),3);
        var root=min.offset(0,1,2);var isolated=h.absolutePos(new BlockPos(1,2,1));h.getLevel().setBlock(isolated,Blocks.DIRT.defaultBlockState(),3);
        var p=player(h,ModItems.SPECTRALITE_SHOVEL.get(),root,Direction.Axis.X);p.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES,root.getCenter());
        p.gameMode.useItem(p,h.getLevel(),p.getMainHandItem(),net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(p.isUsingItem(),"Normal right-click begins charging");for(int i=0;i<20;i++)p.doTick();p.releaseUsingItem();
        h.assertTrue(remaining(h,min,5,4,4)==80,"Early release digs nothing");
        p.gameMode.useItem(p,h.getLevel(),p.getMainHandItem(),net.minecraft.world.InteractionHand.MAIN_HAND);
        for(int i=0;i<45;i++)p.doTick();h.assertFalse(p.isBlocking(),"Charge pose does not grant shield protection");p.releaseUsingItem();
        h.assertTrue(remaining(h,min,5,4,4)==16 && p.getMainHandItem().getDamageValue()==64,"Full native charge releases exactly sixty-four successful normal breaks");
        h.assertTrue(h.getLevel().getBlockState(isolated).is(Blocks.DIRT),"Disconnected material is left alone");
        h.runAfterDelay(2,()->{h.assertTrue(remaining(h,min,5,4,4)==16,"Charged mining cannot recursively schedule ordinary 3x3 jobs");p.discard();h.succeed();});
    }
    @GameTest(template="deferred_items_test",timeoutTicks=40)
    public static void block_vetoes_are_respected_and_are_not_connectivity_bridges(GameTestHelper h){
        var c=h.absolutePos(new BlockPos(7,2,7));wall(h,c,Direction.Axis.Z,Blocks.DIRT);var p=player(h,ModItems.BLOODSTONE_SHOVEL.get(),c,Direction.Axis.Z);var blocked=c.east();
        java.util.function.Consumer<net.neoforged.neoforge.event.level.BlockEvent.BreakEvent> veto=e->{if(e.getPlayer()==p && e.getPos().equals(blocked))e.setCanceled(true);};
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(veto);p.gameMode.destroyBlock(c);
        h.runAfterDelay(2,()->{
            try{h.assertTrue(h.getLevel().getBlockState(blocked).is(Blocks.DIRT) && p.getMainHandItem().getDamageValue()==8,"A protected neighbour remains and costs no wear");}
            finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(veto);}
            clear(h,c);h.getLevel().setBlock(c,Blocks.DIRT.defaultBlockState(),3);h.getLevel().setBlock(blocked,Blocks.DIRT.defaultBlockState(),3);h.getLevel().setBlock(c.east(2),Blocks.DIRT.defaultBlockState(),3);
            p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.SPECTRALITE_SHOVEL.get()));
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(veto);
            try{h.assertTrue(AreaShovelMining.connected(p,c)==1 && h.getLevel().getBlockState(c.east(2)).is(Blocks.DIRT),"Connected dig cannot cross a protected bridge");}
            finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(veto);p.discard();}h.succeed();
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=40)
    public static void broken_or_changed_tools_stop_queued_area_mining(GameTestHelper h){
        var c=h.absolutePos(new BlockPos(7,2,7));wall(h,c,Direction.Axis.Z,Blocks.DIRT);var p=player(h,ModItems.BLOODSTONE_SHOVEL.get(),c,Direction.Axis.Z);
        p.getMainHandItem().setDamageValue(p.getMainHandItem().getMaxDamage()-1);p.gameMode.destroyBlock(c);
        h.runAfterDelay(2,()->{
            h.assertTrue(p.getMainHandItem().isEmpty() && h.getLevel().getBlockState(c.east()).is(Blocks.DIRT),"Broken tool cannot mine extra blocks for free");
            wall(h,c,Direction.Axis.Z,Blocks.DIRT);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.BLOODSTONE_SHOVEL.get()));p.gameMode.destroyBlock(c);
            p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.SPECTRALITE_SHOVEL.get()));
            h.runAfterDelay(2,()->{h.assertTrue(h.getLevel().getBlockState(c.east()).is(Blocks.DIRT),"Changing tools cancels the queued action");p.discard();h.succeed();});
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=40)
    public static void area_mining_preserves_silk_touch_loot(GameTestHelper h){
        var c=h.absolutePos(new BlockPos(7,2,7));wall(h,c,Direction.Axis.Z,Blocks.GRASS_BLOCK);var p=player(h,ModItems.SPECTRALITE_SHOVEL.get(),c,Direction.Axis.Z);
        var silk=h.getLevel().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH);p.getMainHandItem().enchant(silk,1);p.gameMode.destroyBlock(c);
        h.runAfterDelay(2,()->{
            int grass=h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(c).inflate(3),e->e.getItem().is(Items.GRASS_BLOCK)).stream().mapToInt(e->e.getItem().getCount()).sum();
            h.assertTrue(grass==9,"All nine normal loot evaluations retain Silk Touch");p.discard();h.succeed();
        });
    }
}
