package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.block.HaintFurnaceBlock;
import com.howierux.mortalexpansion.blockentity.HauntFurnaceBlockEntity;
import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HaintFurnaceAppearanceGameTests {
    private static void tick(GameTestHelper h,HauntFurnaceBlockEntity f) {
        HauntFurnaceBlockEntity.serverTick(h.getLevel(),f.getBlockPos(),h.getLevel().getBlockState(f.getBlockPos()),f);
    }
    @GameTest(template="deferred_items_test")
    public static void ghast_eyes_follow_actual_haunt_cooking_and_clear_when_idle_fueled_or_paused(GameTestHelper h) {
        var pos=h.absolutePos(new BlockPos(4,2,4));
        var state=ModBlocks.HAINT_FURNACE.get().defaultBlockState().setValue(AbstractFurnaceBlock.FACING,Direction.SOUTH);
        h.getLevel().setBlock(pos,state,3);
        var f=(HauntFurnaceBlockEntity)h.getLevel().getBlockEntity(pos);
        HauntManager.setValue(h.getLevel(),pos,40);
        f.setItem(0,new ItemStack(Items.RAW_IRON,3));f.setItem(1,new ItemStack(Items.COAL));
        tick(h,f);
        h.assertTrue(h.getLevel().getBlockState(pos).getValue(HaintFurnaceBlock.HAUNT_POWERED),
                "Demonic-tier cooking selects the ghast-eyed front");
        int burn=f.saveWithoutMetadata(h.getLevel().registryAccess()).getInt("BurnTime");
        tick(h,f);
        h.assertTrue(f.saveWithoutMetadata(h.getLevel().registryAccess()).getInt("BurnTime")==burn,
                "Haunt-powered rendering agrees with preserved fuel time");
        f.setItem(0,ItemStack.EMPTY);tick(h,f);
        h.assertFalse(h.getLevel().getBlockState(pos).getValue(HaintFurnaceBlock.HAUNT_POWERED),
                "An idle Haint furnace does not display ghast eyes");
        f.setItem(0,new ItemStack(Items.RAW_IRON,3));HauntManager.setValue(h.getLevel(),pos,10);tick(h,f);
        h.assertTrue(!h.getLevel().getBlockState(pos).getValue(HaintFurnaceBlock.HAUNT_POWERED)
                && f.saveWithoutMetadata(h.getLevel().registryAccess()).getInt("BurnTime")<burn,
                "Fuel-powered cooking uses the ordinary front and spends fuel time");
        HauntManager.setValue(h.getLevel(),pos,40);tick(h,f);
        h.assertTrue(h.getLevel().getBlockState(pos).getValue(HaintFurnaceBlock.HAUNT_POWERED),
                "Crossing the tier threshold resumes ghast-eyed cooking");
        f.getPersistentData().putLong("MortalExpansionInterferenceUntil",h.getLevel().getServer().overworld().getGameTime()+200);
        tick(h,f);
        h.assertTrue(!h.getLevel().getBlockState(pos).getValue(HaintFurnaceBlock.HAUNT_POWERED)
                && h.getLevel().getBlockState(pos).getValue(AbstractFurnaceBlock.FACING)==Direction.SOUTH,
                "Paused cooking clears the eyes without rotating the furnace");
        h.succeed();
    }
}
