package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.SpiritwellBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Persistent intangible world anchor; no item, loot, collision, or gameplay tick. */
public final class SpiritwellBlock extends BaseEntityBlock {
    public static final MapCodec<SpiritwellBlock> CODEC = simpleCodec(SpiritwellBlock::new);
    public SpiritwellBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.box(0.2,0.2,0.2,0.8,0.8,0.8);
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
    @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,net.minecraft.world.level.Level level,
            BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){
        if(player instanceof net.minecraft.server.level.ServerPlayer server)
            com.howierux.mortalexpansion.spiritwell.GravePorting.open(server,pos);
        return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpiritwellBlockEntity(pos, state);
    }
}
