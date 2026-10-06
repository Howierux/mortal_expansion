package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.entity.mob.HolySaltBarrierTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PlayerUpwardPhasingGameTests {
    private static void room(GameTestHelper h){for(int x=0;x<15;x++)for(int z=0;z<15;z++){
        h.setBlock(new BlockPos(x,1,z),Blocks.STONE);for(int y=2;y<12;y++)h.setBlock(new BlockPos(x,y,z),y==4?Blocks.STONE:Blocks.AIR);
    }}
    private static void equip(net.minecraft.server.level.ServerPlayer p,Item... pieces){
        var slots=new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET};
        for(int i=0;i<4;i++)p.setItemSlot(slots[i],new ItemStack(pieces[i]));
    }
    private static net.minecraft.server.level.ServerPlayer player(GameTestHelper h){
        var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setNoGravity(true);
        var pos=h.absolutePos(new BlockPos(7,2,7));p.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);return p;
    }
    @GameTest(template="deferred_items_test")
    public static void all_garb_players_phase_upward_and_keep_downward_floor_collision(GameTestHelper h){
        room(h);Item[][] sets={
            {ModItems.GHOST_GARB_HELMET.get(),ModItems.GHOST_GARB_CHESTPLATE.get(),ModItems.GHOST_GARB_LEGGINGS.get(),ModItems.GHOST_GARB_BOOTS.get()},
            {ModItems.HAINT_GARB_HELMET.get(),ModItems.HAINT_GARB_CHESTPLATE.get(),ModItems.HAINT_GARB_LEGGINGS.get(),ModItems.HAINT_GARB_BOOTS.get()},
            {ModItems.SPECTRALITE_HAINT_HELMET.get(),ModItems.SPECTRALITE_HAINT_CHESTPLATE.get(),ModItems.SPECTRALITE_HAINT_LEGGINGS.get(),ModItems.SPECTRALITE_HAINT_BOOTS.get()}};
        for(var set:sets){var p=player(h);equip(p,set);var start=p.position();p.move(MoverType.SELF,new Vec3(0,2,0));
            h.assertTrue(Math.abs(p.getY()-start.y-2)<.001,"Every full spectral set phases through ordinary ceilings");
            p.setPos(start);p.move(MoverType.SELF,new Vec3(0,-5,0));h.assertTrue(Math.abs(p.getY()-start.y)<.001,"Downward movement keeps floors");
            p.setItemSlot(EquipmentSlot.HEAD,ItemStack.EMPTY);p.setPos(start);p.move(MoverType.SELF,new Vec3(0,2,0));
            h.assertTrue(p.getY()<start.y+1,"Incomplete gear restores ceiling collision");p.discard();
        }h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void player_upward_sweeps_cannot_cross_spectral_solids_or_salt(GameTestHelper h){
        room(h);var p=player(h);equip(p,ModItems.GHOST_GARB_HELMET.get(),ModItems.GHOST_GARB_CHESTPLATE.get(),ModItems.GHOST_GARB_LEGGINGS.get(),ModItems.GHOST_GARB_BOOTS.get());var start=p.position();
        h.setBlock(new BlockPos(7,6,7),ModBlocks.GRAVESTONE.get());p.move(MoverType.SELF,new Vec3(0,8,0));
        h.assertTrue(p.getY()<start.y+3,"Fast rise cannot tunnel through spectral-solid blocks");
        h.setBlock(new BlockPos(7,6,7),Blocks.AIR);p.setPos(start);var salt=h.absolutePos(new BlockPos(7,1,7));HolySaltBarrierTracker.markPresent(h.getLevel(),salt);
        try{p.move(MoverType.SELF,new Vec3(0,2,0));h.assertTrue(p.getY()==start.y,"Salt columns block player rise");}
        finally{HolySaltBarrierTracker.invalidate(h.getLevel(),salt);p.discard();}h.succeed();
    }
}
