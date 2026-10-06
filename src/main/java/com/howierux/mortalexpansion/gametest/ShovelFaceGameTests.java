package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ShovelFaceGameTests {
    private static void cube(GameTestHelper h,BlockPos c){
        for(var pos:BlockPos.betweenClosed(c.offset(-1,-1,-1),c.offset(1,1,1)))h.getLevel().setBlock(pos,Blocks.DIRT.defaultBlockState(),3);
    }
    private static void faceCase(GameTestHelper h,int n){
        var c=h.absolutePos(new BlockPos(7,4,7));cube(h,c);
        var face=Direction.values()[n%6];var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setNoGravity(true);
        p.setPos(c.getX()+0.5,c.getY(),c.getZ()-3);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(n<6?ModItems.BLOODSTONE_SHOVEL.get():ModItems.SPECTRALITE_SHOVEL.get()));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new LeftClickBlock(p,c,face,LeftClickBlock.Action.START));
        p.setYRot(45);p.setXRot(30); // Facing and delayed STOP must not replace the originally targeted face.
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new LeftClickBlock(p,c,Direction.SOUTH,LeftClickBlock.Action.STOP));
        h.assertTrue(p.gameMode.destroyBlock(c),"Native centre harvest succeeds");
        h.runAfterDelay(2,()->{
            for(int x=-1;x<=1;x++)for(int y=-1;y<=1;y++)for(int z=-1;z<=1;z++){
                boolean plane=switch(face.getAxis()){case X->x==0;case Y->y==0;case Z->z==0;};
                h.assertTrue(h.getLevel().getBlockState(c.offset(x,y,z)).isAir()==plane,"Only the plane perpendicular to the selected face is removed");
            }
            h.assertTrue(p.getMainHandItem().getDamageValue()==9,"Face targeting keeps per-block durability");p.discard();
            if(n<11)faceCase(h,n+1);else h.succeed();
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=100)
    public static void both_shovels_follow_all_six_target_faces_independent_of_facing(GameTestHelper h){faceCase(h,0);}
    @GameTest(template="deferred_items_test",timeoutTicks=40)
    public static void aborted_face_is_not_reused_for_a_later_break(GameTestHelper h){
        var c=h.absolutePos(new BlockPos(7,4,7));cube(h,c);var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setNoGravity(true);
        p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(ModItems.BLOODSTONE_SHOVEL.get()));p.setPos(c.getX()+0.5,c.getY()+4,c.getZ()+0.5);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new LeftClickBlock(p,c,Direction.WEST,LeftClickBlock.Action.START));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new LeftClickBlock(p,c,Direction.WEST,LeftClickBlock.Action.ABORT));
        p.setXRot(-90); // Look away: no matching ray hit, so only ordinary single-block mining is allowed.
        p.gameMode.destroyBlock(c);
        h.runAfterDelay(2,()->{h.assertTrue(h.getLevel().getBlockState(c.above()).is(Blocks.DIRT) && p.getMainHandItem().getDamageValue()==1,"Aborted or unknown face cannot schedule area mining");p.discard();h.succeed();});
    }
}
