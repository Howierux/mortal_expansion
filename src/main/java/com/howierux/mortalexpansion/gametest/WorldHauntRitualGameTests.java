package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.gametest.*;
import java.util.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WorldHauntRitualGameTests {
    private static void raw(LevelChunk chunk,int hundredths){
        chunk.setData(ModAttachments.HAUNT_TIER,HauntTier.fromValue(hundredths/100).id());
        chunk.setData(ModAttachments.HAUNT_PROGRESS,HauntTier.progressFromValue(hundredths/100));
        chunk.setData(ModAttachments.HAUNT_FRACTION,hundredths%100);
        chunk.setData(ModAttachments.HAUNT_RITUAL_REVISION,0);
    }
    private static int units(LevelChunk chunk){return chunk.getData(ModAttachments.HAUNT_TIER)*1000+chunk.getData(ModAttachments.HAUNT_PROGRESS)*100+chunk.getData(ModAttachments.HAUNT_FRACTION);}
    @GameTest(template="deferred_items_test",batch="world_ritual")
    public static void ordered_rituals_persist_and_apply_once_to_unloaded_and_new_chunks(GameTestHelper h){
        var level=h.getLevel();var data=new WorldHauntRitualData();data.record(true);data.record(false);
        var restored=WorldHauntRitualData.load(data.save(new CompoundTag(),level.registryAccess()),level.registryAccess());
        var chunk=new LevelChunk(level,new ChunkPos(10000000,10000000));raw(chunk,6537);
        restored.apply(chunk);h.assertTrue(units(chunk)==5900,"Missed rise caps at 69 before a later fall subtracts ten");
        restored.apply(chunk);h.assertTrue(units(chunk)==5900,"Repeated loading/read cannot reapply rituals");
        var saved=new net.neoforged.neoforge.attachment.AttachmentHolder.AsField(chunk);
        saved.deserializeInternal(level.registryAccess(),chunk.writeAttachmentsToNBT(level.registryAccess()));
        h.assertTrue(saved.getData(ModAttachments.HAUNT_RITUAL_REVISION)==2,"Applied revision persists with chunk attachments");
        var fresh=new LevelChunk(level,new ChunkPos(10000001,10000001));raw(fresh,1537);restored.apply(fresh);
        h.assertTrue(units(fresh)==1537,"Future chunk processes previous rituals in order while retaining uncapped fractions");
        restored.record(false);restored.record(false);restored.apply(fresh);
        h.assertTrue(units(fresh)==0,"Purification clamps at zero");
        h.assertTrue(!level.hasChunkAt(new BlockPos(160000000,64,160000000)),"Detached reconciliation does not load or generate terrain");
        h.succeed();
    }
    @GameTest(template="deferred_items_test",batch="world_ritual")
    public static void stars_in_both_altars_shift_loaded_overworld_without_changing_recovery(GameTestHelper h){
        var level=h.getLevel();var pos=h.absolutePos(new BlockPos(4,2,4));
        var center=level.getChunkAt(pos);var neighbor=level.getChunk(center.getPos().x+1,center.getPos().z);
        var data=WorldHauntRitualData.get(level);var savedData=data.save(new CompoundTag(),level.registryAccess());
        var snapshots=new HashMap<LevelChunk,int[]>();
        var chunks=new ArrayList<>(WorldHauntRitual.loadedChunks(level));chunks.add(center);chunks.add(neighbor);
        for(var chunk:chunks)snapshots.put(chunk,new int[]{chunk.getData(ModAttachments.HAUNT_TIER),chunk.getData(ModAttachments.HAUNT_PROGRESS),chunk.getData(ModAttachments.HAUNT_FRACTION),chunk.getData(ModAttachments.HAUNT_RITUAL_REVISION)});
        try {
            HauntManager.setValue(level,center.getPos(),15);HauntManager.addHundredths(level,center.getPos(),37);
            HauntManager.setValue(level,neighbor.getPos(),65);
            int floor=HauntSimulationHandler.determineSourceFloorValue(center),revision=data.revision();
            level.setBlock(pos,ModBlocks.ALTAR_OF_IMPURITY.get().defaultBlockState(),3);
            var impure=(AltarOfPurityBlockEntity)level.getBlockEntity(pos);
            h.assertTrue(impure.getItems().isItemValid(0,new ItemStack(ModItems.RITUAL_STAR.get())),"Overworld altar accepts Ritual Star");
            impure.getItems().setStackInSlot(0,new ItemStack(ModItems.RITUAL_STAR.get()));
            AltarOfPurityBlockEntity.serverTick(level,pos,impure.getBlockState(),impure);
            h.assertTrue(impure.getItems().getStackInSlot(0).isEmpty() && impure.getBurnTicks()==0 && data.revision()==revision+1,"Exactly one star is sacrificed for a ritual, not fuel");
            h.assertTrue(HauntManager.getHundredths(level,center.getPos())==2537 && HauntManager.getHundredths(level,neighbor.getPos())==6900,"Loaded chunks shift immediately and respect maximum Haunt");
            h.assertTrue(HauntSimulationHandler.determineSourceFloorValue(center)==floor,"Natural recovery baseline remains unchanged");
            level.setBlock(pos,ModBlocks.ALTAR_OF_PURITY.get().defaultBlockState(),3);
            var pure=(AltarOfPurityBlockEntity)level.getBlockEntity(pos);pure.getItems().setStackInSlot(0,new ItemStack(ModItems.RITUAL_STAR.get()));
            AltarOfPurityBlockEntity.serverTick(level,pos,pure.getBlockState(),pure);
            h.assertTrue(HauntManager.getHundredths(level,center.getPos())==1537 && HauntManager.getHundredths(level,neighbor.getPos())==5900 && data.revision()==revision+2,"Purity reverses the current values by ten and rituals are repeatable");
            var nether=level.getServer().getLevel(Level.NETHER);
            var blocked=new AltarOfPurityBlockEntity(new BlockPos(0,64,0),ModBlocks.ALTAR_OF_PURITY.get().defaultBlockState());blocked.setLevel(nether);
            blocked.getItems().setStackInSlot(0,new ItemStack(ModItems.RITUAL_STAR.get()));
            h.assertFalse(blocked.getItems().isItemValid(0,new ItemStack(ModItems.RITUAL_STAR.get())),"Nether altar rejects Ritual Stars");
            AltarOfPurityBlockEntity.serverTick(nether,blocked.getBlockPos(),blocked.getBlockState(),blocked);
            h.assertTrue(blocked.getItems().getStackInSlot(0).getCount()==1 && data.revision()==revision+2,"Non-Overworld sacrifice spends nothing and records no ritual");
        } finally {
            level.getDataStorage().set(WorldHauntRitualData.KEY,WorldHauntRitualData.load(savedData,level.registryAccess()));
            snapshots.forEach((chunk,values)->{
                chunk.setData(ModAttachments.HAUNT_TIER,values[0]);chunk.setData(ModAttachments.HAUNT_PROGRESS,values[1]);
                chunk.setData(ModAttachments.HAUNT_FRACTION,values[2]);chunk.setData(ModAttachments.HAUNT_RITUAL_REVISION,values[3]);
                com.howierux.mortalexpansion.blockentity.HauntFurnaceBlockEntity.onChunkTierChanged(chunk,HauntTier.fromId(values[0]));
            });
        }
        var blood=new ItemStack(ModItems.BLOODSTONE.get());var star=new ItemStack(Items.NETHER_STAR);
        var input=CraftingInput.of(3,3,List.of(blood,blood,blood,blood,star,blood,blood,blood,blood));
        var crafted=level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,input,level).orElseThrow().value().assemble(input,level.registryAccess());
        h.assertTrue(crafted.is(ModItems.RITUAL_STAR.get()) && crafted.getCount()==1,"Nether Star surrounded by eight Bloodstone crafts one Ritual Star");
        h.succeed();
    }
}
