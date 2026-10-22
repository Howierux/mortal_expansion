package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.fancifier.FancifierRules;
import com.howierux.mortalexpansion.menu.FancifierMenu;
import com.howierux.mortalexpansion.blockentity.FancifiedBlockEntity;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.inventory.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FancifierGameTests {
    @GameTest(template="deferred_items_test")
    public static void stack_processing_partial_outputs_restoration_and_close(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var level=h.getLevel();var pos=h.absolutePos(new BlockPos(5,2,5));
        level.setBlockAndUpdate(pos,ModBlocks.FANCIFIER.get().defaultBlockState());p.setPos(pos.getCenter());
        var menu=new FancifierMenu(1,p.getInventory(),ContainerLevelAccess.create(level,pos));
        var original=new ItemStack(Items.OAK_PLANKS,64);original.set(DataComponents.CUSTOM_NAME,Component.literal("Keepsake boards"));
        menu.getSlot(0).set(original.copy());
        for(int choice=0;choice<4;choice++) {
            h.assertTrue(menu.clickMenuButton(p,choice),"Opacity selection accepted");
            h.assertTrue(menu.clickMenuButton(p,5),"Apply processes stack");
            var out=menu.getSlot(1).remove(13);
            h.assertTrue(out.getCount()==13 && FancifierRules.opacity(out)==(choice+1)*20,"Partial output retains selected opacity");
            var rest=menu.getSlot(1).remove(64);h.assertTrue(rest.getCount()==51,"Output conserves all 64 inputs");
            menu.getSlot(0).set(out);menu.clickMenuButton(p,4);h.assertTrue(menu.clickMenuButton(p,5),"100% restores source");
            var restored=menu.getSlot(1).remove(64);
            h.assertTrue(restored.getCount()==13 && ItemStack.isSameItemSameComponents(restored,original),"Original components survive conversion and restoration");
            menu.getSlot(0).set(original.copy());
        }
        h.assertFalse(menu.clickMenuButton(p,-1) || menu.clickMenuButton(p,99),"Malformed buttons rejected");
        menu.getSlot(0).set(new ItemStack(Items.CHEST));menu.clickMenuButton(p,0);
        h.assertFalse(menu.clickMenuButton(p,5),"Containers cannot be converted even with an injected slot");
        h.assertFalse(FancifierRules.accepts(new ItemStack(Items.WHEAT_SEEDS)),"Crops excluded");
        h.assertFalse(FancifierRules.accepts(new ItemStack(Items.SAND)),"Falling blocks excluded");
        menu.getSlot(0).set(original.copy());menu.clickMenuButton(p,0);menu.clickMenuButton(p,5);
        menu.getSlot(0).set(new ItemStack(Items.STONE,8));
        h.assertFalse(menu.clickMenuButton(p,5),"Different source cannot overwrite a pending output");
        menu.removed(p);
        int stone=0,wrapped=0;for(var stack:p.getInventory().items){if(stack.is(Items.STONE))stone+=stack.getCount();if(stack.is(ModItems.FANCIFIED_BLOCK.get()))wrapped+=stack.getCount();}
        h.assertTrue(stone==8 && wrapped==64 && menu.getSlot(0).getItem().isEmpty() && menu.getSlot(1).getItem().isEmpty(),"Closing returns both slots without loss or duplication");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void placed_slab_retains_shape_orientation_opacity_and_drops(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var level=h.getLevel();var pos=h.absolutePos(new BlockPos(7,3,7));
        level.setBlockAndUpdate(pos.below(),Blocks.STONE.defaultBlockState());
        var source=new ItemStack(Items.OAK_SLAB);source.set(DataComponents.CUSTOM_NAME,Component.literal("Ghostly slab"));
        var item=FancifierRules.convert(source,40,level.registryAccess());p.setItemInHand(InteractionHand.MAIN_HAND,item);
        var hit=new BlockHitResult(pos.below().getCenter().add(0,0.5,0),Direction.UP,pos.below(),false);
        var context=new BlockPlaceContext(p,InteractionHand.MAIN_HAND,item,hit);
        h.assertTrue(((BlockItem)item.getItem()).place(context).consumesAction(),"Faded slab places natively");
        var be=(FancifiedBlockEntity)level.getBlockEntity(pos);
        h.assertTrue(be!=null && be.opacity()==40 && be.original().is(Blocks.OAK_SLAB),"Original slab and opacity stored");
        var shape=level.getBlockState(pos).getCollisionShape(level,pos);
        h.assertTrue(Math.abs(shape.max(Direction.Axis.Y)-0.5)<0.001,"Slab remains a half-height solid collision");
        be.loadWithComponents(be.saveWithFullMetadata(level.registryAccess()),level.registryAccess());
        h.assertTrue(be.opacity()==40 && be.original().is(Blocks.OAK_SLAB),"State survives block entity save/load");
        var drops=Block.getDrops(level.getBlockState(pos),level,pos,be);
        h.assertTrue(drops.size()==1 && ItemStack.isSameItemSameComponents(drops.getFirst(),item),"Breaking preserves source components and opacity");
        var picked=ModBlocks.FANCIFIED_BLOCK.get().getCloneItemStack(level,pos,level.getBlockState(pos));
        h.assertTrue(ItemStack.isSameItemSameComponents(picked,item),"Pick-block preserves appearance");
        var recipe=level.getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING,
                net.minecraft.world.item.crafting.CraftingInput.of(3,3,java.util.List.of(new ItemStack(Items.IRON_INGOT),new ItemStack(ModItems.RAW_ECTOPLASM.get()),new ItemStack(Items.IRON_INGOT),new ItemStack(ModItems.RAW_ECTOPLASM.get()),new ItemStack(Items.STONECUTTER),new ItemStack(ModItems.RAW_ECTOPLASM.get()),new ItemStack(Items.IRON_INGOT),new ItemStack(Items.REDSTONE),new ItemStack(Items.IRON_INGOT))),level).orElseThrow();
        h.assertTrue(recipe.value().getResultItem(level.registryAccess()).is(ModItems.FANCIFIER.get()),"Requested crafting pattern is loaded");
        var wetPos=pos.east(2);level.setBlockAndUpdate(wetPos,net.minecraft.world.level.material.Fluids.WATER.defaultFluidState().createLegacyBlock());
        var wet=FancifierRules.convert(new ItemStack(Items.OAK_SLAB),60,level.registryAccess());p.setItemInHand(InteractionHand.MAIN_HAND,wet);
        var wetContext=new BlockPlaceContext(p,InteractionHand.MAIN_HAND,wet,new BlockHitResult(wetPos.getCenter(),Direction.UP,wetPos,false));
        h.assertTrue(((BlockItem)wet.getItem()).place(wetContext).consumesAction(),"Slab can be placed in water");
        h.assertTrue(level.getFluidState(wetPos).is(net.minecraft.tags.FluidTags.WATER),"Slab retains waterlogging");
        var lightPos=pos.west(2);level.setBlockAndUpdate(lightPos,ModBlocks.FANCIFIED_BLOCK.get().defaultBlockState());
        var light=(FancifiedBlockEntity)level.getBlockEntity(lightPos);
        light.configure(new ItemStack(Items.GLOWSTONE),Blocks.GLOWSTONE.defaultBlockState(),20);
        h.assertTrue(level.getBlockState(lightPos).getLightEmission(level,lightPos)==15,"Light-emitting decorations retain their light");
        h.assertFalse(ModBlocks.FANCIFIED_BLOCK.get().canPlaceLiquid(p,level,lightPos,level.getBlockState(lightPos),net.minecraft.world.level.material.Fluids.WATER),"Full cubes do not become waterloggable");
        light.configure(new ItemStack(ModItems.GRAVESTONE.get()),ModBlocks.GRAVESTONE.get().defaultBlockState(),20);
        h.assertFalse(com.howierux.mortalexpansion.entity.mob.SpectralPhasingRules.canEnterHorizontally(p,new AABB(lightPos)),"Fading a spectral-solid block retains its spirit barrier");
        h.assertTrue(com.howierux.mortalexpansion.guide.EctceteraResearchMap.nodes(2).stream().anyMatch(node->node.chapter().key().equals("fancifier")),"Ectoplasm book tab documents Fancifier");
        h.assertTrue(com.howierux.mortalexpansion.guide.EctceteraResearchMap.unlocked("fancifier",2,0)
                && !com.howierux.mortalexpansion.guide.EctceteraResearchMap.unlocked("fancifier",1,0),"Book guidance follows ectoplasm upgrade");
        p.discard();h.succeed();
    }
}
