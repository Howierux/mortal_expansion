package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.SpiritwellBlockEntity;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpiritwellGameTests {
    private static final BlockPos WELL = new BlockPos(7, 4, 7);
    private static void floor(GameTestHelper helper) {
        for (int x = 0; x < 15; x++) for (int z = 0; z < 15; z++)
            helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
    }
    private static boolean generate(GameTestHelper helper) {
        var feature = helper.getLevel().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                .get(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "spiritwell"));
        helper.assertTrue(feature != null, "Configured worldgen feature loads");
        return feature.place(helper.getLevel(), helper.getLevel().getChunkSource().getGenerator(),
                RandomSource.create(42), helper.absolutePos(new BlockPos(7, 2, 7)));
    }
    @GameTest(template = "deferred_items_test")
    public static void worldgen_is_two_blocks_above_land_and_persists(GameTestHelper helper) {
        floor(helper);
        helper.assertTrue(generate(helper), "Open dry surface generates a well");
        helper.assertBlockPresent(ModBlocks.SPIRITWELL.get(), WELL);
        BlockPos pos = helper.absolutePos(WELL);
        var state = helper.getLevel().getBlockState(pos);
        helper.assertTrue(state.getCollisionShape(helper.getLevel(), pos).isEmpty(), "Intangible collision");
        var well = helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(well instanceof SpiritwellBlockEntity, "Dedicated future interaction anchor");
        var saved = well.saveWithFullMetadata(helper.getLevel().registryAccess());
        helper.assertTrue(BlockEntity.loadStatic(pos, state, saved, helper.getLevel().registryAccess())
                instanceof SpiritwellBlockEntity, "Anchor persists across chunk save/load");
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void worldgen_does_not_replace_water_or_obstacles(GameTestHelper helper) {
        floor(helper);
        helper.setBlock(new BlockPos(7, 2, 7), Blocks.WATER);
        helper.assertFalse(generate(helper), "No water surface wells");
        helper.assertBlockPresent(Blocks.WATER, new BlockPos(7, 2, 7));
        helper.setBlock(new BlockPos(7, 2, 7), Blocks.AIR);
        helper.setBlock(new BlockPos(8, 4, 7), Blocks.STONE);
        helper.assertFalse(generate(helper), "No occupied protection volume");
        helper.assertBlockPresent(Blocks.STONE, new BlockPos(8, 4, 7));
        helper.assertBlockNotPresent(ModBlocks.SPIRITWELL.get(), WELL);
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void player_placement_is_denied_inside_and_allowed_outside(GameTestHelper helper) {
        floor(helper);
        helper.setBlock(WELL, ModBlocks.SPIRITWELL.get());
        var player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(Items.COBBLESTONE, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos inside = helper.absolutePos(new BlockPos(7, 1, 7));
        BlockPos outside = helper.absolutePos(new BlockPos(11, 1, 7));
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(inside).add(0, 0.5, 0), Direction.UP, inside, false)));
        helper.assertBlockPresent(Blocks.AIR, new BlockPos(7, 2, 7));
        helper.assertTrue(stack.getCount() == 2, "Denied placement does not consume items");
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(outside).add(0, 0.5, 0), Direction.UP, outside, false)));
        helper.assertBlockPresent(Blocks.COBBLESTONE, new BlockPos(11, 2, 7));
        player.discard();
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void multi_place_and_piston_cannot_cross_protection_edge(GameTestHelper helper) {
        helper.setBlock(WELL, ModBlocks.SPIRITWELL.get());
        var level = helper.getLevel();
        BlockPos near = helper.absolutePos(WELL.offset(2, 0, 0));
        BlockPos far = helper.absolutePos(WELL.offset(3, 0, 0));
        var snapshots = List.of(BlockSnapshot.create(level.dimension(), level, far),
                BlockSnapshot.create(level.dimension(), level, near));
        var multi = new BlockEvent.EntityMultiPlaceEvent(snapshots, Blocks.STONE.defaultBlockState(), null);
        NeoForge.EVENT_BUS.post(multi);
        helper.assertTrue(multi.isCanceled(), "Multi-block placement checks every placed position");
        BlockPos piston = helper.absolutePos(WELL.offset(4, 0, 0));
        level.setBlockAndUpdate(piston, Blocks.PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.WEST));
        level.setBlockAndUpdate(far, Blocks.STONE.defaultBlockState());
        var move = new PistonEvent.Pre(level, piston, Direction.WEST, PistonEvent.PistonMoveType.EXTEND);
        NeoForge.EVENT_BUS.post(move);
        helper.assertTrue(move.isCanceled(), "Piston cannot push a block inside the radius");
        var fluid = new BlockEvent.FluidPlaceBlockEvent(level, near, far, Blocks.COBBLESTONE.defaultBlockState());
        NeoForge.EVENT_BUS.post(fluid);
        helper.assertTrue(fluid.isCanceled(), "Fluid-generated solid blocks respect protection");
        helper.succeed();
    }
}
