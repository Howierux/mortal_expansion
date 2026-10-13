package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.block.DreamcatcherBlock;
import com.howierux.mortalexpansion.blockentity.DreamcatcherBlockEntity;
import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DreamcatcherGameTests {
    private static DreamcatcherBlockEntity place(GameTestHelper h, ServerPlayer p, int x) {
        var pos=h.absolutePos(new BlockPos(x,2,3));
        h.getLevel().setBlock(pos.south(),Blocks.STONE.defaultBlockState(),3);
        var state=ModBlocks.DREAMCATCHER.get().defaultBlockState();
        h.getLevel().setBlock(pos,state,3);
        ModBlocks.DREAMCATCHER.get().setPlacedBy(h.getLevel(),pos,state,p,new ItemStack(ModItems.DREAMCATCHER.get()));
        return (DreamcatcherBlockEntity)h.getLevel().getBlockEntity(pos);
    }
    @GameTest(template="deferred_items_test")
    public static void ownership_persists_and_unload_removal_disable_protection(GameTestHelper h) {
        var owner=h.makeMockServerPlayerInLevel();var other=h.makeMockServerPlayerInLevel();
        var catcher=place(h,owner,3);
        h.assertTrue(Dreamcatchers.active(owner) && !Dreamcatchers.active(other),"Only the placer is protected");
        var saved=catcher.saveWithoutMetadata(h.getLevel().registryAccess());
        var restored=new DreamcatcherBlockEntity(catcher.getBlockPos(),catcher.getBlockState());
        restored.loadAdditional(saved,h.getLevel().registryAccess());
        h.assertTrue(owner.getUUID().equals(restored.owner()),"Placer UUID survives save/load");
        catcher.onChunkUnloaded();
        h.assertFalse(Dreamcatchers.active(owner),"Unloaded decoration does not protect or force a load");
        catcher.onLoad();
        h.assertTrue(Dreamcatchers.active(owner),"Reload restores its owner protection");
        h.getLevel().setBlock(catcher.getBlockPos().south(),Blocks.AIR.defaultBlockState(),3);
        h.assertTrue(h.getLevel().getBlockState(catcher.getBlockPos()).isAir() && !Dreamcatchers.active(owner),
                "Losing wall support breaks the decoration and removes protection immediately");
        owner.discard();other.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void a_single_coin_flip_blocks_positive_and_negative_events_without_stacking(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();place(h,p,3);place(h,p,5);
        int caught=0;
        for(int i=0;i<200;i++) {
            boolean expected=net.minecraft.util.RandomSource.create(i*7919L).nextBoolean();
            p.getRandom().setSeed(i*7919L);
            h.assertTrue(Dreamcatchers.prevent(p)==expected,"Multiple catchers use one exact 50% coin flip");
            if(expected)caught++;
            Dreamcatchers.consumeFeedback(p);
        }
        h.assertTrue(caught>70 && caught<130,"Both caught and uncaught outcomes occur");
        long blockingSeed=0;
        while(!net.minecraft.util.RandomSource.create(blockingSeed).nextBoolean())blockingSeed++;
        p.getRandom().setSeed(blockingSeed);
        h.assertTrue(HauntEffects.perform(p,HauntEffectKind.HIT) && Dreamcatchers.consumeFeedback(p),
                "A negative event is handled without its effect");
        var book=EctceteraProgression.book(2);p.getInventory().setItem(0,book);
        PlayerHauntManager.setTier(p,HauntTier.SUPERNATURAL);
        p.getRandom().setSeed(blockingSeed);
        h.assertTrue(HauntEffects.perform(p,HauntEffectKind.SPECTRAL_INSIGHT)
                && Dreamcatchers.consumeFeedback(p) && SpectralInsight.secrets(book)==0,
                "A positive Insight can be caught without revealing a secret or rerolling");
        h.getLevel().setBlock(h.absolutePos(new BlockPos(3,2,3)),Blocks.AIR.defaultBlockState(),3);
        h.getLevel().setBlock(h.absolutePos(new BlockPos(5,2,3)),Blocks.AIR.defaultBlockState(),3);
        p.getRandom().setSeed(blockingSeed);
        h.assertFalse(Dreamcatchers.prevent(p),"Breaking the last loaded catcher stops interception");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void sleep_hook_overrides_only_nearby_monsters_for_the_owner(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var other=h.makeMockServerPlayerInLevel();
        place(h,p,3);var pos=h.absolutePos(new BlockPos(2,2,2));
        for(var problem:net.minecraft.world.entity.player.Player.BedSleepingProblem.values()) {
            var event=new net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent(p,pos,problem);
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
            h.assertTrue(event.getProblem()==(problem==net.minecraft.world.entity.player.Player.BedSleepingProblem.NOT_SAFE?null:problem),
                    "Dreamcatcher preserves daytime, dimension, distance and blocked-bed restrictions");
        }
        var unowned=new net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent(other,pos,
                net.minecraft.world.entity.player.Player.BedSleepingProblem.NOT_SAFE);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(unowned);
        h.assertTrue(unowned.getProblem()!=null,"Another player cannot use the placer protection");
        p.discard();other.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void thread_revelation_unlocks_dreamcatcher_recipe_and_book_branch(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();
        var id=net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"dreamcatcher");
        h.assertFalse(SpectralInsight.canCraft(p,id),"Dreamcatcher recipe starts as hidden knowledge");
        SpectralInsight.learnPlayer(p,64);
        var book=EctceteraProgression.book(2);p.getInventory().setItem(0,book);
        PlayerHauntManager.setTier(p,HauntTier.SUPERNATURAL);
        h.assertTrue(SpectralInsight.perform(p) && SpectralInsight.canCraft(p,id)
                && p.getRecipeBook().contains(id),"Ectothread revelation also awards Dreamcatcher");
        var node=EctceteraResearchMap.nodes(4).stream().filter(n->n.chapter().key().equals("dreamcatcher")).findFirst().orElseThrow();
        h.assertTrue(node.parent().equals("ectothread") && !EctceteraResearchMap.unlocked("dreamcatcher",2,0)
                && EctceteraResearchMap.unlocked("dreamcatcher",2,1),"Book branch follows exactly the same secret bit");
        var recipe=(net.minecraft.world.item.crafting.CraftingRecipe)h.getLevel().getRecipeManager().byKey(id).orElseThrow().value();
        var input=net.minecraft.world.item.crafting.CraftingInput.of(3,3,java.util.List.of(
                new ItemStack(Items.STICK),new ItemStack(ModItems.ECTOTHREAD.get()),new ItemStack(Items.STICK),
                new ItemStack(ModItems.ECTOTHREAD.get()),new ItemStack(ModItems.ECTOTHREAD.get()),new ItemStack(ModItems.ECTOTHREAD.get()),
                new ItemStack(Items.STICK),new ItemStack(ModItems.ECTOTHREAD.get()),new ItemStack(Items.STICK)));
        h.assertTrue(recipe.matches(input,h.getLevel()) && recipe.assemble(input,h.getLevel().registryAccess()).is(ModItems.DREAMCATCHER.get()),
                "Four sticks and five Ectothread craft one Dreamcatcher");
        p.discard();h.succeed();
    }
}
