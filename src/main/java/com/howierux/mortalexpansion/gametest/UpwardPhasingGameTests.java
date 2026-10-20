package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.entity.mob.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class UpwardPhasingGameTests {
    private static void room(GameTestHelper h){for(int x=0;x<15;x++)for(int z=0;z<15;z++){
        h.setBlock(new BlockPos(x,1,z),Blocks.STONE);for(int y=2;y<12;y++)h.setBlock(new BlockPos(x,y,z),Blocks.AIR);
        h.setBlock(new BlockPos(x,4,z),Blocks.STONE);
    }}
    private static void ceiling(GameTestHelper h,Mob mob){
        int height=(int)Math.ceil(2+mob.getBbHeight());
        for(int x=0;x<15;x++)for(int z=0;z<15;z++){
            for(int y=2;y<12;y++)h.setBlock(new BlockPos(x,y,z),Blocks.AIR);
            h.setBlock(new BlockPos(x,height,z),Blocks.STONE);
        }
    }
    private static <T extends Mob> T at(GameTestHelper h,EntityType<T> type){
        var mob=type.create(h.getLevel());var p=h.absolutePos(new BlockPos(7,2,7));mob.setPos(p.getX()+0.5,p.getY(),p.getZ()+0.5);
        com.howierux.mortalexpansion.haunt.HauntManager.setTier(h.getLevel(),p,com.howierux.mortalexpansion.haunt.HauntTier.ELDRITCH);return mob;
    }
    @GameTest(template="deferred_items_test")
    public static void ghost_spirit_phantom_and_tagged_ghast_pass_upward_but_keep_floors(GameTestHelper h){
        room(h);for(var type:java.util.List.of(ModEntities.GHOST.get(),ModEntities.SPIRIT.get(),EntityType.PHANTOM,EntityType.GHAST)){
            var mob=at(h,type);ceiling(h,mob);double y=mob.getY();mob.move(MoverType.SELF,new Vec3(0,2,0));
            h.assertTrue(Math.abs(mob.getY()-y-2)<0.001,"All intangible mob kinds pass upward through ordinary ceilings");
            mob.setPos(mob.getX(),y,mob.getZ());mob.move(MoverType.SELF,new Vec3(0,-5,0));
            h.assertTrue(Math.abs(mob.getY()-y)<0.001,"Intangible mobs still stand on ordinary floors");mob.discard();
        }h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void spectral_solids_and_salt_stop_upward_sweeps(GameTestHelper h){
        room(h);var ghost=at(h,ModEntities.GHOST.get());double y=ghost.getY();
        h.setBlock(new BlockPos(7,6,7),ModBlocks.GRAVESTONE.get());ghost.move(MoverType.SELF,new Vec3(0,8,0));
        h.assertTrue(ghost.getY()<y+3,"Fast upward motion cannot tunnel through spectral-solid blocks");
        h.setBlock(new BlockPos(7,6,7),Blocks.AIR);ghost.setPos(ghost.getX(),y,ghost.getZ());var salt=h.absolutePos(new BlockPos(7,1,7));
        HolySaltBarrierTracker.markPresent(h.getLevel(),salt);
        try{ghost.move(MoverType.SELF,new Vec3(0,2,0));h.assertTrue(ghost.getY()==y,"Salt columns block upward motion at every height");}
        finally{HolySaltBarrierTracker.invalidate(h.getLevel(),salt);ghost.discard();}h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void tangible_mobs_and_tangible_pharaohl_keep_ceiling_collision(GameTestHelper h){
        room(h);var zombie=at(h,EntityType.ZOMBIE);double y=zombie.getY();zombie.move(MoverType.SELF,new Vec3(0,2,0));
        h.assertTrue(zombie.getY()<y+1,"Ordinary mobs retain ceiling collision");zombie.discard();
        var boss=at(h,ModEntities.PHARAOHL.get());ceiling(h,boss);y=boss.getY();boss.tickCount=0;boss.move(MoverType.SELF,new Vec3(0,2,0));
        h.assertTrue(boss.getY()<y+1,"Tangible boss phase retains ceiling collision");
        boss.setPos(boss.getX(),y,boss.getZ());boss.tickCount=160;boss.move(MoverType.SELF,new Vec3(0,2,0));
        h.assertTrue(Math.abs(boss.getY()-y-2)<0.001,"Intangible boss phase can rise through blocks");boss.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void spectral_pursuit_steers_toward_higher_targets(GameTestHelper h){
        room(h);var ghost=at(h,ModEntities.GHOST.get());var target=h.makeMockServerPlayerInLevel();target.setPos(ghost.position().add(0,4,0));ghost.setTarget(target);
        var goal=new SpectralMeleeAttackGoal(ghost,1);h.assertTrue(goal.canUse(),"A target above an ordinary ceiling remains eligible");
        goal.tick();ghost.getMoveControl().tick();h.assertTrue(ghost.getDeltaMovement().y>0,"Pursuit actively steers upward");h.assertTrue(ghost.zza==0,"A directly higher target does not make the mob walk away sideways");
        double y=ghost.getY();ghost.move(MoverType.SELF,ghost.getDeltaMovement());h.assertTrue(ghost.getY()>y+0.1,"Steering rises through the ceiling instead of stopping on it");
        target.discard();ghost.discard();h.succeed();
    }
}
