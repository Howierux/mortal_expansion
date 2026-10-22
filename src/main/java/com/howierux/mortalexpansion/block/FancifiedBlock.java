package com.howierux.mortalexpansion.block;
import com.howierux.mortalexpansion.blockentity.FancifiedBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
public final class FancifiedBlock extends BaseEntityBlock implements net.minecraft.world.level.block.SimpleWaterloggedBlock {
    public static final MapCodec<FancifiedBlock> CODEC = simpleCodec(FancifiedBlock::new);
    public FancifiedBlock(Properties properties) { super(properties); registerDefaultState(stateDefinition.any().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED,false)); }
    @Override protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block,BlockState> builder) { builder.add(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED); }
    @Override public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        var source=com.howierux.mortalexpansion.fancifier.FancifierRules.original(context.getItemInHand(),context.getLevel().registryAccess());
        boolean wet=source.getItem() instanceof net.minecraft.world.item.BlockItem item
                && item.getBlock().defaultBlockState().hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED)
                && context.getLevel().getFluidState(context.getClickedPos()).is(net.minecraft.tags.FluidTags.WATER);
        return defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED,wet);
    }
    @Override public boolean canPlaceLiquid(Player player,BlockGetter level,BlockPos pos,BlockState state,net.minecraft.world.level.material.Fluid fluid) {
        return original(level,pos).hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED)
                && SimpleWaterloggedBlock.super.canPlaceLiquid(player,level,pos,state,fluid);
    }
    @Override public boolean placeLiquid(LevelAccessor level,BlockPos pos,BlockState state,net.minecraft.world.level.material.FluidState fluid) {
        return canPlaceLiquid(null,level,pos,state,fluid.getType()) && SimpleWaterloggedBlock.super.placeLiquid(level,pos,state,fluid);
    }
    @Override protected net.minecraft.world.level.material.FluidState getFluidState(BlockState state) {
        return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED) ? net.minecraft.world.level.material.Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    @Override protected BlockState updateShape(BlockState state,net.minecraft.core.Direction direction,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos neighborPos) {
        if(state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED))level.scheduleTick(pos,net.minecraft.world.level.material.Fluids.WATER,net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
        return super.updateShape(state,direction,neighbor,level,pos,neighborPos);
    }
    @Override public int getLightEmission(BlockState state,BlockGetter level,BlockPos pos) { return original(level,pos).getLightEmission(level,pos); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return new FancifiedBlockEntity(pos,state); }
    private BlockState original(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof FancifiedBlockEntity be ? be.original() : Blocks.STONE.defaultBlockState();
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) { return original(level,pos).getShape(level,pos,context); }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context) { return original(level,pos).getCollisionShape(level,pos,context); }
    @Override protected float getDestroyProgress(BlockState state,Player player,BlockGetter level,BlockPos pos) { return original(level,pos).getDestroyProgress(player,level,pos); }
    @Override public boolean canHarvestBlock(BlockState state,BlockGetter level,BlockPos pos,Player player) { return original(level,pos).canHarvestBlock(level,pos,player); }
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state) { return level.getBlockEntity(pos) instanceof FancifiedBlockEntity be ? be.asItem() : ItemStack.EMPTY; }
    @Override protected java.util.List<ItemStack> getDrops(BlockState state,LootParams.Builder params) {
        var be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        return be instanceof FancifiedBlockEntity block && !block.asItem().isEmpty() ? java.util.List.of(block.asItem()) : java.util.List.of();
    }
}
