package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.haunt.HauntTier;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GhostTargetGameTests {
    private static void fight(GameTestHelper h,EntityType<? extends Mob> victimType){
        var level=h.getLevel();
        for(int x=1;x<=11;x++)for(int z=2;z<=8;z++){
            var floor=h.absolutePos(new BlockPos(x,2,z));level.setBlock(floor,Blocks.STONE.defaultBlockState(),3);
            level.getChunkAt(floor).setData(ModAttachments.HAUNT_TIER,HauntTier.DEAD_RISING.id());
            for(int y=3;y<=7;y++)h.setBlock(new BlockPos(x,y,z),Blocks.AIR);
        }
        var ghost=ModEntities.GHOST.get().create(level);var victim=victimType.create(level);
        ghost.setPos(h.absolutePos(new BlockPos(3,3,5)).getBottomCenter());victim.setPos(h.absolutePos(new BlockPos(7,3,5)).getBottomCenter());
        ghost.setPersistenceRequired();victim.setPersistenceRequired();level.addFreshEntity(ghost);level.addFreshEntity(victim);
        h.succeedWhen(()->{
            h.assertTrue(ghost.getTarget()==victim,"Ghost autonomously selects the peaceful spectral creature");
            h.assertTrue(victim.getHealth()<victim.getMaxHealth(),"Ghost melee damages its intangible target");
            h.assertTrue(victim.getTarget()==ghost,"Damaged creature retaliates against the Ghost");
            h.assertTrue(ghost.getHealth()<ghost.getMaxHealth(),"Retaliatory ranged bolt damages the Ghost");
            ghost.discard();victim.discard();
        });
    }
    @GameTest(template="deferred_items_test",timeoutTicks=180)
    public static void ghost_targets_spirit_and_receives_retaliation(GameTestHelper h){fight(h,ModEntities.SPIRIT.get());}
    @GameTest(template="deferred_items_test",timeoutTicks=180)
    public static void ghost_targets_elemental_and_receives_retaliation(GameTestHelper h){fight(h,ModEntities.ELEMENTAL.get());}
}
