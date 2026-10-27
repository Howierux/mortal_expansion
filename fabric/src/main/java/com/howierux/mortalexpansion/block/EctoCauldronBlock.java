package com.howierux.mortalexpansion.block;
public final class EctoCauldronBlock extends net.minecraft.world.level.block.Block {
 public EctoCauldronBlock(Properties p){super(p);}
 protected net.minecraft.world.phys.shapes.VoxelShape getShape(net.minecraft.world.level.block.state.BlockState s,net.minecraft.world.level.BlockGetter w,net.minecraft.core.BlockPos p,net.minecraft.world.phys.shapes.CollisionContext c){return net.minecraft.world.phys.shapes.Shapes.join(net.minecraft.world.phys.shapes.Shapes.block(),box(2,4,2,14,16,14),net.minecraft.world.phys.shapes.BooleanOp.ONLY_FIRST);}
}
