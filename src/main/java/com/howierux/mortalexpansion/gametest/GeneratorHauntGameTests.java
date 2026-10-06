package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.EctoplasmicGeneratorBlockEntity;
import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GeneratorHauntGameTests {
    private static EctoplasmicGeneratorBlockEntity generator(GameTestHelper h){
        var rel=new BlockPos(7,2,7);h.setBlock(rel,ModBlocks.ECTOPLASMIC_GENERATOR.get());return (EctoplasmicGeneratorBlockEntity)h.getLevel().getBlockEntity(h.absolutePos(rel));
    }
    private static void tick(GameTestHelper h,EctoplasmicGeneratorBlockEntity g){EctoplasmicGeneratorBlockEntity.serverTick(h.getLevel(),g.getBlockPos(),g.getBlockState(),g);}
    private static void burn(GameTestHelper h,EctoplasmicGeneratorBlockEntity g,int ticks){for(int i=0;i<ticks;i++){g.getEnergyStorage().extractEnergy(2000,false);tick(h,g);}}
    @GameTest(template="deferred_items_test")
    public static void each_fuel_emits_its_haunt_once_on_actual_burn_completion(GameTestHelper h){
        var g=generator(h);var pos=g.getBlockPos();
        for(int type=0;type<2;type++){
            HauntManager.setValue(h.getLevel(),pos,30);HauntManager.addHundredths(h.getLevel(),pos,7);
            g.getItems().setStackInSlot(0,new ItemStack(type==0?ModItems.RAW_ECTOPLASM.get():ModItems.PURE_ECTOPLASM.get()));
            int ticks=type==0?EctoplasmicGeneratorBlockEntity.RAW_BURN_TICKS:EctoplasmicGeneratorBlockEntity.PURE_BURN_TICKS;
            burn(h,g,ticks-1);h.assertTrue(g.getBurnTicksRemaining()==1 && HauntManager.getHundredths(h.getLevel(),pos)==3007,"No Haunt is emitted before fuel finishes");
            burn(h,g,1);h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==3007+(type==0?100:300),"Raw adds one point and purified adds three, preserving fractions");
            tick(h,g);h.assertTrue(g.getItems().getStackInSlot(0).isEmpty() && !g.isBurning(),"One consumed item cannot generate again");
        }h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void full_energy_buffers_pause_burning_and_do_not_emit_haunt(GameTestHelper h){
        var g=generator(h);var pos=g.getBlockPos();HauntManager.setValue(h.getLevel(),pos,10);
        var tag=g.saveWithoutMetadata(h.getLevel().registryAccess());tag.putInt("Energy",EctoplasmicGeneratorBlockEntity.ENERGY_CAPACITY);g.loadAdditional(tag,h.getLevel().registryAccess());
        g.getItems().setStackInSlot(0,new ItemStack(ModItems.RAW_ECTOPLASM.get()));tick(h,g);tick(h,g);
        h.assertTrue(g.getBurnTicksRemaining()==EctoplasmicGeneratorBlockEntity.RAW_BURN_TICKS && HauntManager.getValue(h.getLevel(),pos)==10,"Full generator emits neither progress nor Haunt");
        burn(h,g,EctoplasmicGeneratorBlockEntity.RAW_BURN_TICKS);h.assertTrue(HauntManager.getValue(h.getLevel(),pos)==11,"Resumed generation eventually emits one payment");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void saved_and_legacy_burns_resume_without_duplicate_emissions_and_respect_cap(GameTestHelper h){
        var g=generator(h);var pos=g.getBlockPos();HauntManager.setValue(h.getLevel(),pos,20);g.getItems().setStackInSlot(0,new ItemStack(ModItems.PURE_ECTOPLASM.get()));burn(h,g,10);
        var tag=g.saveWithoutMetadata(h.getLevel().registryAccess());g.loadAdditional(tag,h.getLevel().registryAccess());burn(h,g,EctoplasmicGeneratorBlockEntity.PURE_BURN_TICKS-10);
        h.assertTrue(HauntManager.getValue(h.getLevel(),pos)==23,"Saved purified burn retains its three-point output");
        tag=g.saveWithoutMetadata(h.getLevel().registryAccess());g.loadAdditional(tag,h.getLevel().registryAccess());tick(h,g);h.assertTrue(HauntManager.getValue(h.getLevel(),pos)==23,"Loading a finished burn cannot re-emit");
        g.getItems().setStackInSlot(0,new ItemStack(ModItems.RAW_ECTOPLASM.get()));burn(h,g,10);tag=g.saveWithoutMetadata(h.getLevel().registryAccess());tag.remove("BurnHaunt");g.loadAdditional(tag,h.getLevel().registryAccess());
        HauntManager.setValue(h.getLevel(),pos,68);HauntManager.addHundredths(h.getLevel(),pos,50);burn(h,g,EctoplasmicGeneratorBlockEntity.RAW_BURN_TICKS-10);
        h.assertTrue(HauntManager.getHundredths(h.getLevel(),pos)==6900,"Legacy active fuel gains its normal output and clamps at Haunt maximum");h.succeed();
    }
}
