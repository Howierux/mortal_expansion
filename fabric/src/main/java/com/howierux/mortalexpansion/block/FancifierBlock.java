package com.howierux.mortalexpansion.block;
import com.howierux.mortalexpansion.menu.FancifierMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
public final class FancifierBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<FancifierBlock> CODEC = simpleCodec(FancifierBlock::new);
    public FancifierBlock(Properties properties) { super(properties); registerDefaultState(stateDefinition.any().setValue(FACING,net.minecraft.core.Direction.NORTH)); }
    @Override protected MapCodec<? extends HorizontalDirectionalBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block,BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) { return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite()); }
    @Override protected VoxelShape getShape(BlockState state,net.minecraft.world.level.BlockGetter level,BlockPos pos,CollisionContext context) { return Block.box(0,0,0,16,9,16); }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) {
        if (!level.isClientSide) player.openMenu(new SimpleMenuProvider((id,inventory,p)->new FancifierMenu(id,inventory,ContainerLevelAccess.create(level,pos)),Component.translatable("block.mortal_expansion.fancifier")));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
