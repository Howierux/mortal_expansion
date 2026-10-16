package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.*;
import java.util.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpectralDaylightGameTests {
    @GameTest(template="deferred_items_test",timeoutTicks=240)
    public static void spirits_and_elementals_are_daylight_immune_even_at_void(GameTestHelper h){
        var level=h.getLevel();level.setDayTime(6000);var mobs=new ArrayList<Mob>();
        var types=List.of(ModEntities.SPIRIT.get(),ModEntities.ELEMENTAL.get());
        for(int i=0;i<types.size();i++){
            var pos=h.absolutePos(new BlockPos(3+i*3,3,5));
            level.setBlock(pos.below(),Blocks.STONE.defaultBlockState(),3);
            for(int y=pos.getY();y<level.getMaxBuildHeight();y++)level.setBlock(new BlockPos(pos.getX(),y,pos.getZ()),Blocks.AIR.defaultBlockState(),2);
            var mob=(Mob)types.get(i).create(level);mob.setPos(pos.getBottomCenter());mob.setNoAi(true);mob.setPersistenceRequired();level.addFreshEntity(mob);mobs.add(mob);
            level.getChunkAt(pos).setData(ModAttachments.HAUNT_TIER,HauntTier.VOID.id());
            h.assertTrue(level.canSeeSky(pos),"Exposed daylight test location");
            h.assertTrue(HauntDaylightRules.sunlightProtected(mob),"Intrinsic sun protection even at Void");
            h.assertFalse(HauntDaylightRules.applies(mob),"No daylight shelter goal or movement restrictions");
            h.assertTrue(HauntDaylightRules.sustains(level,pos,mob),"Daylight does not require a sustaining Haunt tier");
            var motion=new Vec3(32,0,0);h.assertTrue(HauntDaylightRules.limitMovement(mob,motion).equals(motion),"Daylight cannot block crossing chunk boundaries");
        }
        var zombie=EntityType.ZOMBIE.create(level);zombie.setPos(mobs.getFirst().position());
        h.assertTrue(HauntDaylightRules.applies(zombie) && !HauntDaylightRules.sunlightProtected(zombie),"Zombie daylight rules still require chunk protection");
        h.runAfterDelay(200,()->{
            for(var mob:mobs){h.assertTrue(mob.isAlive() && !mob.isOnFire() && mob.getHealth()==mob.getMaxHealth(),"Daylight causes no ignition or damage");mob.discard();}
            h.succeed();
        });
    }
}
