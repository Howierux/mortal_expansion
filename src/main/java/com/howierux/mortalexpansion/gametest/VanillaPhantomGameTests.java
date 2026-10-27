package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.entity.IntangibleEntities;
import com.howierux.mortalexpansion.entity.mob.HolySaltBarrierTracker;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class VanillaPhantomGameTests {
    @GameTest(template="deferred_items_test")
    public static void vanilla_phantom_is_intangible_but_sanctification_and_magic_still_damage(GameTestHelper h) {
        var phantom=EntityType.PHANTOM.create(h.getLevel());
        phantom.setPos(h.absolutePos(new BlockPos(6,3,6)).getCenter());
        h.assertTrue(IntangibleEntities.isIntangible(phantom),"Vanilla Phantom uses shared intangible tag");
        float health=phantom.getHealth();
        h.assertFalse(phantom.hurt(phantom.damageSources().generic(),2),"Ordinary damage is blocked");
        h.assertTrue(phantom.getHealth()==health,"Blocked damage preserves health");
        h.assertTrue(phantom.hurt(phantom.damageSources().magic(),2) && phantom.getHealth()<health,"Magic bypasses intangibility");
        phantom.invulnerableTime=0;phantom.tickCount=40;health=phantom.getHealth();
        ModEffects.SANCTIFIED.get().applyEffectTick(phantom,0);
        h.assertTrue(phantom.getHealth()<health,"Consecration damage still affects the undead Phantom");
        phantom.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void phantom_phases_stone_but_cannot_cross_spectral_solids_or_salt(GameTestHelper h) {
        for(int x=2;x<=10;x++)for(int y=2;y<=7;y++)for(int z=4;z<=8;z++)h.setBlock(new BlockPos(x,y,z),Blocks.AIR);
        var phantom=EntityType.PHANTOM.create(h.getLevel());var start=h.absolutePos(new BlockPos(4,3,6)).getCenter();
        phantom.setPos(start);
        for(int y=2;y<=7;y++)for(int z=4;z<=8;z++)h.setBlock(new BlockPos(6,y,z),Blocks.STONE);
        phantom.move(MoverType.SELF,new Vec3(4,0,0));
        h.assertTrue(Math.abs(phantom.getX()-start.x-4)<0.001,"Horizontal phasing passes ordinary wall");
        phantom.setPos(start);
        for(int y=2;y<=7;y++)for(int z=4;z<=8;z++)h.setBlock(new BlockPos(6,y,z),ModBlocks.SPIRITWOOD_LOG.get());
        phantom.move(MoverType.SELF,new Vec3(4,0,0));
        h.assertTrue(phantom.getX()<start.x+2,"Swept movement cannot tunnel through Spiritwood");
        for(int y=2;y<=7;y++)for(int z=4;z<=8;z++)h.setBlock(new BlockPos(6,y,z),Blocks.AIR);
        phantom.setPos(start);var salt=h.absolutePos(new BlockPos(6,2,6));HolySaltBarrierTracker.markPresent(h.getLevel(),salt);
        try {
            phantom.move(MoverType.SELF,new Vec3(4,0,0));
            h.assertTrue(phantom.getX()<start.x+2,"Holy Salt column stops flight across its boundary");
        } finally {HolySaltBarrierTracker.invalidate(h.getLevel(),salt);}
        phantom.setPos(start);phantom.move(MoverType.SELF,new Vec3(4,0,0));
        h.assertTrue(Math.abs(phantom.getX()-start.x-4)<0.001,"Removing salt immediately permits passage");
        var torchA=h.absolutePos(new BlockPos(6,2,4));var torchB=h.absolutePos(new BlockPos(6,2,8));
        com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker.addTorch(h.getLevel(),torchA);
        com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker.addTorch(h.getLevel(),torchB);
        try {
            h.assertTrue(com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker.hasBarrierColumn(h.getLevel(),salt.getX(),salt.getZ()),"Linked torches form a spectral boundary");
            phantom.setPos(start);phantom.move(MoverType.SELF,new Vec3(4,0,0));
            h.assertTrue(phantom.getX()<start.x+2,"Spirit Torch link stops Phantom flight");
        } finally {
            com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker.removeTorch(h.getLevel(),torchA);
            com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker.removeTorch(h.getLevel(),torchB);
        }
        phantom.setPos(start);phantom.move(MoverType.SELF,new Vec3(4,0,0));
        h.assertTrue(Math.abs(phantom.getX()-start.x-4)<0.001,"Removing torches immediately permits passage");
        phantom.discard();h.succeed();
    }
}
