package com.howierux.mortalexpansion.travel;
import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import net.minecraft.core.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
/** Grave binding/inscriptions support journeys. Missing function: flower naturalization and Spirit workstation conversion. */
public final class GraveBlock extends BaseEntityBlock {
 public GraveBlock(Properties p){super(p);registerDefaultState(stateDefinition.any().setValue(CatalogBlocks.GRAVE_DEPTH,1).setValue(CatalogBlocks.GRAVE_NATURAL,false).setValue(BlockStateProperties.HORIZONTAL_FACING,Direction.NORTH));}
 protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(GraveBlock::new);}
 protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState>b){b.add(CatalogBlocks.GRAVE_DEPTH,CatalogBlocks.GRAVE_NATURAL,BlockStateProperties.HORIZONTAL_FACING);}
 protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new GraveEntity(pos,state);}
 public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext c){return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,c.getHorizontalDirection().getOpposite());}
 public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(placer instanceof net.minecraft.server.level.ServerPlayer p&&level.getBlockEntity(pos) instanceof GraveEntity grave)grave.placed(p);}
 protected net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,net.minecraft.world.phys.shapes.CollisionContext c){int depth=s.getValue(CatalogBlocks.GRAVE_DEPTH)*4;return s.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis()==Direction.Axis.X?box(depth,0,0,depth+8,16,16):box(0,0,depth,16,16,depth+8);}
 public static net.minecraft.world.phys.Vec3 inscriptionAnchor(BlockState state){Direction facing=state.getValue(BlockStateProperties.HORIZONTAL_FACING);double center=.25+state.getValue(CatalogBlocks.GRAVE_DEPTH)*.25;return new net.minecraft.world.phys.Vec3((facing.getAxis()==Direction.Axis.X?center:.5)+facing.getStepX()*.252,.70,(facing.getAxis()==Direction.Axis.Z?center:.5)+facing.getStepZ()*.252);}
}
