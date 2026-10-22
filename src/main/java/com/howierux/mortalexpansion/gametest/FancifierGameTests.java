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
        for(int choice=1;choice<100;choice++) {
            h.assertTrue(menu.clickMenuButton(p,choice),"Opacity selection accepted");
            h.assertTrue(menu.clickMenuButton(p,FancifierMenu.APPLY),"Apply processes stack");
            var out=menu.getSlot(1).remove(13);
            h.assertTrue(out.getCount()==13 && FancifierRules.opacity(out)==choice,"Partial output retains selected opacity");
            if(choice==1 || choice==99){
                var ops=level.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
                var saved=ItemStack.CODEC.encodeStart(ops,out).getOrThrow();
                h.assertTrue(FancifierRules.opacity(ItemStack.CODEC.parse(ops,saved).getOrThrow())==choice,"New boundary percentages persist on items");
            }
            var rest=menu.getSlot(1).remove(64);h.assertTrue(rest.getCount()==51,"Output conserves all 64 inputs");
            menu.getSlot(0).set(out);menu.clickMenuButton(p,100);h.assertTrue(menu.clickMenuButton(p,FancifierMenu.APPLY),"100% restores source");
            var restored=menu.getSlot(1).remove(64);
            h.assertTrue(restored.getCount()==13 && ItemStack.isSameItemSameComponents(restored,original),"Original components survive conversion and restoration");
            menu.getSlot(0).set(original.copy());
        }
        h.assertFalse(menu.clickMenuButton(p,-1) || menu.clickMenuButton(p,101),"Malformed buttons rejected");
        menu.getSlot(0).set(new ItemStack(Items.CHEST));menu.clickMenuButton(p,20);
        h.assertTrue(menu.clickMenuButton(p,FancifierMenu.APPLY),"Containers can now be faded without replacing their item type");
        h.assertTrue(menu.getSlot(1).remove(64).is(Items.CHEST),"Chest remains a native chest item");
        h.assertTrue(FancifierRules.accepts(new ItemStack(Items.WHEAT_SEEDS)),"Planting items are supported");
        h.assertTrue(FancifierRules.accepts(new ItemStack(Items.SAND)),"Falling blocks are supported");
        h.assertFalse(FancifierRules.accepts(new ItemStack(Items.DIAMOND_SWORD)),"Non-block items are excluded");
        menu.getSlot(0).set(original.copy());menu.clickMenuButton(p,20);menu.clickMenuButton(p,FancifierMenu.APPLY);
        menu.getSlot(0).set(new ItemStack(Items.STONE,8));
        h.assertFalse(menu.clickMenuButton(p,FancifierMenu.APPLY),"Different source cannot overwrite a pending output");
        menu.removed(p);
        int stone=0,wrapped=0;for(var stack:p.getInventory().items){if(stack.is(Items.STONE))stone+=stack.getCount();if(stack.is(Items.OAK_PLANKS) && FancifierRules.opacity(stack)==20)wrapped+=stack.getCount();}
        h.assertTrue(stone==8 && wrapped==64 && menu.getSlot(0).getItem().isEmpty() && menu.getSlot(1).getItem().isEmpty(),"Closing returns both slots without loss or duplication");
        p.discard();h.succeed();
    }
    private static net.minecraft.core.BlockPos place(GameTestHelper h,net.minecraft.server.level.ServerPlayer player,net.minecraft.core.BlockPos relative,ItemStack stack,int opacity) {
        var pos=h.absolutePos(relative);h.getLevel().setBlockAndUpdate(pos.below(),Blocks.STONE.defaultBlockState());
        var faded=FancifierRules.convert(stack,opacity,h.getLevel().registryAccess());player.setItemInHand(InteractionHand.MAIN_HAND,faded);player.setPos(pos.getCenter().add(2,0,2));
        var context=new BlockPlaceContext(player,InteractionHand.MAIN_HAND,faded,new BlockHitResult(pos.below().getCenter().add(0,0.5,0),Direction.UP,pos.below(),false));
        h.assertTrue(((BlockItem)faded.getItem()).place(context).consumesAction(),"Native block placement succeeds");return pos;
    }
    @GameTest(template="deferred_items_test")
    public static void native_slab_retains_shape_opacity_drops_and_chunk_persistence(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var level=h.getLevel();var source=new ItemStack(Items.OAK_SLAB);
        source.set(DataComponents.CUSTOM_NAME,Component.literal("Ghostly slab"));var expected=FancifierRules.convert(source,37,level.registryAccess());
        var pos=place(h,p,new BlockPos(7,3,7),source,37);
        h.assertTrue(level.getBlockState(pos).is(Blocks.OAK_SLAB) && level.getBlockEntity(pos)==null,"Slab uses its native state without a proxy block entity");
        h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,pos)==37,"Opacity is separate chunk metadata");
        h.assertTrue(Math.abs(level.getBlockState(pos).getCollisionShape(level,pos).max(Direction.Axis.Y)-0.5)<0.001,"Slab collision remains native");
        var chunk=level.getChunkAt(pos);var data=chunk.getData(ModAttachments.FADING_BLOCKS);
        var saved=com.howierux.mortalexpansion.fancifier.FadingData.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,data).getOrThrow();
        h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingData.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,saved).getOrThrow().equals(data),"Chunk opacity codec roundtrips");
        var buf=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),level.registryAccess());
        com.howierux.mortalexpansion.fancifier.FadingData.STREAM_CODEC.encode(buf,data);
        h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingData.STREAM_CODEC.decode(buf).equals(data),"Chunk sync roundtrips");buf.release();
        var state=level.getBlockState(pos);level.removeBlock(pos,false);
        h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,pos)==100,"Removing a block clears its opacity");
        var drops=Block.getDrops(state,level,pos,null);
        h.assertTrue(drops.size()==1 && drops.getFirst().is(Items.OAK_SLAB) && FancifierRules.opacity(drops.getFirst())==37,"Native drops retain fading after removal");
        level.setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,pos)==100,"Replacement cannot inherit stale fading");
        com.howierux.mortalexpansion.fancifier.FadingWorld.set(level,pos,20);
        var stoneDrops=Block.getDrops(level.getBlockState(pos),level,pos,null);
        h.assertTrue(stoneDrops.size()==1 && stoneDrops.getFirst().is(Items.COBBLESTONE) && FancifierRules.opacity(stoneDrops.getFirst())==20,"Native transformed block loot such as cobblestone retains fading");
        var light=place(h,p,new BlockPos(4,3,7),new ItemStack(Items.GLOWSTONE),20);
        h.assertTrue(level.getBlockState(light).getLightEmission(level,light)==15,"Native light emission is preserved");
        var barrier=place(h,p,new BlockPos(10,3,7),new ItemStack(ModItems.GRAVESTONE.get()),20);
        h.assertFalse(com.howierux.mortalexpansion.entity.mob.SpectralPhasingRules.canEnterHorizontally(p,new AABB(barrier)),"Native spectral barrier remains functional");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void faded_chest_furnace_door_fence_and_crop_keep_native_behavior(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var level=h.getLevel();
        var chest=new ItemStack(Items.CHEST);chest.set(DataComponents.CONTAINER,net.minecraft.world.item.component.ItemContainerContents.fromItems(java.util.List.of(new ItemStack(Items.DIAMOND,3))));
        var chestPos=place(h,p,new BlockPos(3,3,3),chest,60);
        var nativeChest=(net.minecraft.world.level.block.entity.ChestBlockEntity)level.getBlockEntity(chestPos);
        h.assertTrue(nativeChest!=null && nativeChest.getItem(0).is(Items.DIAMOND) && nativeChest.getItem(0).getCount()==3,"Native chest retains its supplied inventory");
        nativeChest.setItem(1,new ItemStack(Items.EMERALD,5));
        var saved=nativeChest.saveWithFullMetadata(level.registryAccess());nativeChest.loadWithComponents(saved,level.registryAccess());
        h.assertTrue(nativeChest.getItem(1).getCount()==5,"Native inventory persists after loading");
        var furnacePos=place(h,p,new BlockPos(7,3,3),new ItemStack(Items.FURNACE),40);
        var furnace=(net.minecraft.world.level.block.entity.FurnaceBlockEntity)level.getBlockEntity(furnacePos);
        furnace.setItem(0,new ItemStack(Items.IRON_ORE));furnace.setItem(1,new ItemStack(Items.COAL));
        for(int i=0;i<205;i++)net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.serverTick(level,furnacePos,level.getBlockState(furnacePos),furnace);
        h.assertTrue(furnace.getItem(2).is(Items.IRON_INGOT),"Faded furnace cooks with its native ticker");
        h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,furnacePos)==40,"Cooking state changes preserve opacity");
        var door=place(h,p,new BlockPos(11,3,3),new ItemStack(Items.OAK_DOOR),80);
        h.assertTrue(level.getBlockState(door).is(Blocks.OAK_DOOR) && level.getBlockState(door.above()).is(Blocks.OAK_DOOR),"Both native door halves are present");
        h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,door)==80 && com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,door.above())==80,"Both door halves inherit opacity");
        var old=level.getBlockState(door);((net.minecraft.world.level.block.DoorBlock)old.getBlock()).setOpen(p,level,old,door,true);
        h.assertTrue(level.getBlockState(door).getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN),"Faded door still opens");
        var fence=place(h,p,new BlockPos(3,3,10),new ItemStack(Items.OAK_FENCE),20);
        level.setBlockAndUpdate(fence.east(),Blocks.OAK_FENCE.defaultBlockState());
        h.assertTrue(level.getBlockState(fence).getValue(net.minecraft.world.level.block.FenceBlock.EAST),"Faded fences connect natively");
        var crop=h.absolutePos(new BlockPos(7,3,10));level.setBlockAndUpdate(crop.below(),Blocks.FARMLAND.defaultBlockState());
        var seed=FancifierRules.convert(new ItemStack(Items.WHEAT_SEEDS),60,level.registryAccess());p.setItemInHand(InteractionHand.MAIN_HAND,seed);
        var ctx=new BlockPlaceContext(p,InteractionHand.MAIN_HAND,seed,new BlockHitResult(crop.below().getCenter().add(0,0.5,0),Direction.UP,crop.below(),false));
        h.assertTrue(((BlockItem)seed.getItem()).place(ctx).consumesAction(),"Faded seeds plant wheat");
        ((net.minecraft.world.level.block.CropBlock)Blocks.WHEAT).performBonemeal(level,level.random,crop,level.getBlockState(crop));
        h.assertTrue(level.getBlockState(crop).getValue(net.minecraft.world.level.block.CropBlock.AGE)>0 && com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,crop)==60,"Native crop growth keeps fading");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void falling_and_piston_blocks_carry_fading(GameTestHelper h) {
        var level=h.getLevel();var sand=h.absolutePos(new BlockPos(9,6,9));level.setBlockAndUpdate(new BlockPos(sand.getX(),h.absolutePos(new BlockPos(0,1,0)).getY(),sand.getZ()),Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(sand,Blocks.SAND.defaultBlockState());com.howierux.mortalexpansion.fancifier.FadingWorld.set(level,sand,40);
        var falling=net.minecraft.world.entity.item.FallingBlockEntity.fall(level,sand,level.getBlockState(sand));
        h.assertTrue(falling.getData(ModAttachments.FADING_ENTITY)==40,"Falling entity captures source opacity");
        for(int i=0;i<35 && !falling.isRemoved();i++)falling.tick();
        var landed=BlockPos.containing(falling.position());
        h.assertTrue(level.getBlockState(landed).is(Blocks.SAND) && com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,landed)==40,"Landed sand keeps its opacity and native physics");
        var piston=h.absolutePos(new BlockPos(3,3,6));var pushed=piston.east();
        level.setBlockAndUpdate(piston,Blocks.STICKY_PISTON.defaultBlockState().setValue(net.minecraft.world.level.block.piston.PistonBaseBlock.FACING,Direction.EAST));
        com.howierux.mortalexpansion.fancifier.FadingWorld.set(level,piston,20);
        level.setBlockAndUpdate(pushed,Blocks.STONE.defaultBlockState());com.howierux.mortalexpansion.fancifier.FadingWorld.set(level,pushed,60);
        level.setBlockAndUpdate(piston.west(),Blocks.REDSTONE_BLOCK.defaultBlockState());
        var pistonState=level.getBlockState(piston);
        h.assertTrue(pistonState.triggerEvent(level,piston,0,Direction.EAST.get3DDataValue()),"Native piston extends");
        var moved=pushed.east();h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,moved)==60,"Moving piston transfers opacity to the destination");
        var moving=(net.minecraft.world.level.block.piston.PistonMovingBlockEntity)level.getBlockEntity(moved);moving.finalTick();
        h.assertTrue(level.getBlockState(moved).is(Blocks.STONE) && com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,moved)==60,"Final native piston state keeps opacity");
        for(int i=0;i<4;i++)if(level.getBlockEntity(pushed) instanceof net.minecraft.world.level.block.piston.PistonMovingBlockEntity head)net.minecraft.world.level.block.piston.PistonMovingBlockEntity.tick(level,pushed,level.getBlockState(pushed),head);
        level.setBlockAndUpdate(piston.west(),Blocks.AIR.defaultBlockState());
        h.assertTrue(level.getBlockState(piston).triggerEvent(level,piston,1,Direction.EAST.get3DDataValue()),"Sticky piston retracts natively");
        for(var target:java.util.List.of(piston,pushed))for(int i=0;i<4;i++)if(level.getBlockEntity(target) instanceof net.minecraft.world.level.block.piston.PistonMovingBlockEntity movingPart)net.minecraft.world.level.block.piston.PistonMovingBlockEntity.tick(level,target,level.getBlockState(target),movingPart);
        h.assertTrue(level.getBlockState(piston).is(Blocks.STICKY_PISTON) && com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,piston)==20,"Retracting piston base retains its own fading");
        h.assertTrue(level.getBlockState(pushed).is(Blocks.STONE) && com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,pushed)==60,"Sticky pull transfers the carried block opacity back");
        h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void magenta_glass_shared_faces_are_culled_at_eighty_percent(GameTestHelper h) {
        var level=h.getLevel();var first=h.absolutePos(new BlockPos(5,3,5));var second=first.east();
        for(var pos:java.util.List.of(first,second)) {
            level.setBlockAndUpdate(pos,ModBlocks.FANCIFIED_BLOCK.get().defaultBlockState());
            ((FancifiedBlockEntity)level.getBlockEntity(pos)).configure(new ItemStack(Items.MAGENTA_STAINED_GLASS),Blocks.MAGENTA_STAINED_GLASS.defaultBlockState(),80);
        }
        var view=new com.howierux.mortalexpansion.fancifier.FancifierFaces(level,80);
        var state=Blocks.MAGENTA_STAINED_GLASS.defaultBlockState();
        h.assertFalse(Block.shouldRenderFace(state,view,first,Direction.EAST,second),"Shared Magenta Glass face is hidden from first block");
        h.assertFalse(Block.shouldRenderFace(state,view,second,Direction.WEST,first),"Shared face is hidden from second block too");
        h.assertTrue(Block.shouldRenderFace(state,view,first,Direction.UP,first.above()),"Exterior surface remains visible");
        var neighbor=(FancifiedBlockEntity)level.getBlockEntity(second);
        neighbor.configure(new ItemStack(Items.MAGENTA_STAINED_GLASS),state,60);
        h.assertTrue(Block.shouldRenderFace(state,view,first,Direction.EAST,second),"Different opacity retains its visual boundary");
        var connections=new com.howierux.mortalexpansion.fancifier.FancifierConnections(level);
        h.assertTrue(connections.getBlockState(first)==state && connections.getBlockState(second)==state,"Texture connection view unwraps matching legacy glass across different opacity");
        h.assertFalse(Block.shouldRenderFace(state,connections,first,Direction.EAST,second),"Texture connection visibility sees the matching material, not an opacity boundary");
        neighbor.configure(new ItemStack(Items.BLUE_STAINED_GLASS),Blocks.BLUE_STAINED_GLASS.defaultBlockState(),80);
        h.assertTrue(Block.shouldRenderFace(state,view,first,Direction.EAST,second),"Different colored glass retains its boundary");
        for(var pos:java.util.List.of(first,second))((FancifiedBlockEntity)level.getBlockEntity(pos)).configure(new ItemStack(Items.STONE),Blocks.STONE.defaultBlockState(),80);
        h.assertFalse(Block.shouldRenderFace(Blocks.STONE.defaultBlockState(),view,first,Direction.EAST,second),"Solid decorations also remove overlapping internal faces");
        level.setBlockAndUpdate(first,state);level.setBlockAndUpdate(second,state);
        com.howierux.mortalexpansion.fancifier.FadingWorld.set(level,first,1);
        com.howierux.mortalexpansion.fancifier.FadingWorld.set(level,second,99);
        level.setBlockAndUpdate(second.east(),state);
        h.assertTrue(connections.getBlockState(first)==state && connections.getBlockState(second)==state && connections.getBlockState(second.east())==state,"Native 1%, 99%, and ordinary 100% glass keep the same connection identity");
        h.assertTrue(new com.howierux.mortalexpansion.fancifier.FancifierFaces(level,1).getBlockState(second).isAir(),"Drawing face visibility stays opacity-sensitive independently of connections");
        h.succeed();
    }

    @GameTest(template="deferred_items_test")
    public static void placement_cancellation_modded_capabilities_and_native_transitions(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();var level=h.getLevel();var pos=h.absolutePos(new BlockPos(4,3,4));
        level.setBlockAndUpdate(pos.below(),Blocks.STONE.defaultBlockState());p.setPos(pos.getCenter().add(2,0,2));
        var stack=FancifierRules.convert(new ItemStack(Items.STONE,2),20,level.registryAccess());p.setItemInHand(InteractionHand.MAIN_HAND,stack);
        var hit=new BlockHitResult(pos.below().getCenter().add(0,0.5,0),Direction.UP,pos.below(),false);
        java.util.function.Consumer<net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent> cancel=event->{if(event.getPos().equals(pos))event.setCanceled(true);};
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);
        try{p.gameMode.useItemOn(p,level,stack,InteractionHand.MAIN_HAND,hit);}finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);}
        h.assertTrue(level.getBlockState(pos).isAir() && com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,pos)==100,"Cancelled placement creates neither block nor opacity");
        p.gameMode.useItemOn(p,level,stack,InteractionHand.MAIN_HAND,hit);
        h.assertTrue(level.getBlockState(pos).is(Blocks.STONE) && com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,pos)==20,"Successful native transaction commits opacity");
        var tankPos=place(h,p,new BlockPos(8,3,4),new ItemStack(ModItems.ECTANK.get()),40);
        var tank=(com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity)level.getBlockEntity(tankPos);
        var handler=level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,tankPos,Direction.UP);
        h.assertTrue(tank!=null && handler!=null && handler.fill(new net.neoforged.neoforge.fluids.FluidStack(net.minecraft.world.level.material.Fluids.WATER,1000),net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE)==1000 && tank.tank.getFluidAmount()==1000,"Faded modded tank retains its real block entity and fluid capability");
        var cauldron=place(h,p,new BlockPos(4,3,9),new ItemStack(Items.CAULDRON),60);
        level.setBlockAndUpdate(cauldron,Blocks.WATER_CAULDRON.defaultBlockState());
        h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,cauldron)==60,"Filling a cauldron keeps its fading");
        var copper=place(h,p,new BlockPos(8,3,9),new ItemStack(Items.COPPER_BLOCK),80);
        level.setBlockAndUpdate(copper,Blocks.EXPOSED_COPPER.defaultBlockState());
        level.setBlockAndUpdate(copper,Blocks.WAXED_EXPOSED_COPPER.defaultBlockState());
        h.assertTrue(com.howierux.mortalexpansion.fancifier.FadingWorld.opacity(level,copper)==80,"Copper ageing and waxing keep fading");
        p.discard();h.succeed();
    }

}
