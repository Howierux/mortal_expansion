package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.menu.AltarOfPurityMenu;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PurityAltarGameTests {
    private static AltarOfPurityBlockEntity altar(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(2048,2,2048));h.getLevel().setBlock(pos,Blocks.AIR.defaultBlockState(),3);h.getLevel().setBlock(pos,ModBlocks.ALTAR_OF_PURITY.get().defaultBlockState(),3);
        return (AltarOfPurityBlockEntity)h.getLevel().getBlockEntity(pos);
    }
    private static void ticks(GameTestHelper h,AltarOfPurityBlockEntity a,int count){for(int i=0;i<count;i++)AltarOfPurityBlockEntity.serverTick(h.getLevel(),a.getBlockPos(),a.getBlockState(),a);}
    @GameTest(template="deferred_items_test")
    public static void offerings_have_their_durations_and_reject_regular_furnace_fuel(GameTestHelper h){
        Item[] fuels={ModItems.GRAVE_MOSS.get(),ModItems.HOLY_SALT.get(),ModItems.PURE_ECTOPLASM.get(),ModItems.SPIRITWOOD_LOG.get(),ModItems.PURIFIED_SPIRITWOOD_LOG.get()};int[] durations={600,1200,2400,600,1200};
        for(int i=0;i<fuels.length;i++){var a=altar(h);a.getItems().setStackInSlot(0,new ItemStack(fuels[i]));ticks(h,a,1);
            h.assertTrue(a.getBurnTicks()==durations[i]-1 && a.getItems().getStackInSlot(0).isEmpty(),"Exactly one offering adds its fuel-specific duration");}
        var a=altar(h);h.assertFalse(a.getItems().isItemValid(0,new ItemStack(Items.COAL)),"Ordinary coal is not a purity offering");h.assertFalse(a.getItems().isItemValid(1,new ItemStack(ModItems.HOLY_SALT.get())),"Returned-container slot is output only");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void offerings_stack_slowly_and_full_reserves_do_not_waste_items(GameTestHelper h){
        var a=altar(h);a.getItems().setStackInSlot(0,new ItemStack(ModItems.HOLY_SALT.get(),3));ticks(h,a,1);ticks(h,a,249);
        h.assertTrue(a.getItems().getStackInSlot(0).getCount()==2 && a.getBurnTicks()==950,"One offering lasts until the 12.5-second consumption interval");
        ticks(h,a,1);h.assertTrue(a.getItems().getStackInSlot(0).getCount()==1 && a.getBurnTicks()==2149,"The next offering stacks duration while already burning");
        var saved=a.saveWithoutMetadata(h.getLevel().registryAccess());saved.putInt("BurnTicks",AltarOfPurityBlockEntity.MAX_BURN_TICKS);saved.putInt("OfferingDelay",0);a.loadAdditional(saved,h.getLevel().registryAccess());ticks(h,a,10);
        h.assertTrue(a.getItems().getStackInSlot(0).getCount()==1 && a.getBurnTicks()==AltarOfPurityBlockEntity.MAX_BURN_TICKS-10,"A full reserve cannot discard an offering or exceed capacity");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void cleansing_uses_exactly_the_center_and_eight_adjacent_loaded_chunks(GameTestHelper h){
        var a=altar(h);var center=new net.minecraft.world.level.ChunkPos(a.getBlockPos());
        for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){var cp=new net.minecraft.world.level.ChunkPos(center.x+dx,center.z+dz);h.getLevel().getChunk(cp.x,cp.z);HauntManager.setValue(h.getLevel(),cp,20);HauntManager.addHundredths(h.getLevel(),cp,7);}
        var outside=new net.minecraft.world.level.ChunkPos(center.x+2,center.z);h.getLevel().getChunk(outside.x,outside.z);HauntManager.setValue(h.getLevel(),outside,20);
        h.assertTrue(AltarOfPurityBlockEntity.cleanse(h.getLevel(),a.getBlockPos())==9,"One pulse covers all nine loaded chunks");
        for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++)h.assertTrue(HauntManager.getHundredths(h.getLevel(),new net.minecraft.world.level.ChunkPos(center.x+dx,center.z+dz))==1997,"Each chunk loses exactly 0.10 Haunt");
        h.assertTrue(HauntManager.getValue(h.getLevel(),outside)==20,"Chunks outside the 3x3 square remain unchanged");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void cleansing_requires_active_burn_time_and_clamps_at_zero(GameTestHelper h){
        var a=altar(h);HauntManager.setValue(h.getLevel(),a.getBlockPos(),0);HauntManager.addHundredths(h.getLevel(),a.getBlockPos(),5);ticks(h,a,200);
        h.assertTrue(HauntManager.getHundredths(h.getLevel(),a.getBlockPos())==5,"An idle altar never cleanses");
        a.getItems().setStackInSlot(0,new ItemStack(ModItems.GRAVE_MOSS.get()));ticks(h,a,199);h.assertTrue(HauntManager.getHundredths(h.getLevel(),a.getBlockPos())==5,"No early cleansing pulse");
        ticks(h,a,1);h.assertTrue(HauntManager.getHundredths(h.getLevel(),a.getBlockPos())==0,"A burning pulse clamps fractional Haunt at zero");
        ticks(h,a,400);HauntManager.setValue(h.getLevel(),a.getBlockPos(),10);ticks(h,a,200);h.assertTrue(HauntManager.getValue(h.getLevel(),a.getBlockPos())==10,"An exhausted altar stops cleansing");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void stored_burn_inventory_and_tick_phases_survive_load_and_menu_sync(GameTestHelper h){
        var a=altar(h);a.getItems().setStackInSlot(0,new ItemStack(ModItems.PURE_ECTOPLASM.get(),2));ticks(h,a,25);var saved=a.saveWithoutMetadata(h.getLevel().registryAccess());
        a.loadAdditional(saved,h.getLevel().registryAccess());h.assertTrue(a.getBurnTicks()==2375 && a.getItems().getStackInSlot(0).getCount()==1,"Saved fuel and accumulated time survive loading");
        ticks(h,a,225);h.assertTrue(a.getItems().getStackInSlot(0).getCount()==1,"Save/load retains the next-offering delay");ticks(h,a,1);h.assertTrue(a.getItems().getStackInSlot(0).isEmpty(),"Next offering is accepted at the original cadence");
        saved=a.saveWithoutMetadata(h.getLevel().registryAccess());saved.putInt("BurnTicks",72000);a.loadAdditional(saved,h.getLevel().registryAccess());var data=a.createMenuData();
        h.assertTrue(((data.get(0)&65535)|((data.get(1)&65535)<<16))==72000,"Two vanilla data slots preserve burn reserves above signed-short range");h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void menu_shift_click_and_break_drops_preserve_unspent_offerings(GameTestHelper h){
        h.setBlock(new BlockPos(7,2,7),ModBlocks.ALTAR_OF_PURITY.get());
        var a=(AltarOfPurityBlockEntity)h.getLevel().getBlockEntity(h.absolutePos(new BlockPos(7,2,7)));var p=h.makeMockServerPlayerInLevel();p.setPos(a.getBlockPos().getCenter());
        // Headless mock clients do not negotiate NeoForge advanced-open-screen payloads.
        var menu=new AltarOfPurityMenu(1,p.getInventory(),a);p.containerMenu=menu;
        h.assertTrue(menu.stillValid(p),"Nearby player can use the registered altar menu");p.getInventory().setItem(9,new ItemStack(ModItems.HOLY_SALT.get(),4));menu.quickMoveStack(p,2);
        h.assertTrue(a.getItems().getStackInSlot(0).getCount()==4 && p.getInventory().getItem(9).isEmpty(),"Shift click inserts offerings without loss");
        menu.quickMoveStack(p,0);h.assertTrue(a.getItems().getStackInSlot(0).isEmpty(),"Unspent offerings can be withdrawn");a.getItems().setStackInSlot(0,new ItemStack(ModItems.HOLY_SALT.get(),4));h.getLevel().setBlock(a.getBlockPos(),Blocks.AIR.defaultBlockState(),3);
        h.assertFalse(menu.stillValid(p),"Breaking altar invalidates menu");int dropped=h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(a.getBlockPos()).inflate(2),e->e.getItem().is(ModItems.HOLY_SALT.get())).stream().mapToInt(e->e.getItem().getCount()).sum();
        h.assertTrue(dropped==4,"Breaking altar returns unspent offerings: "+dropped);p.discard();h.succeed();
    }
}
