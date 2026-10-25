package com.howierux.mortalexpansion.machine;

import net.fabricmc.fabric.api.transfer.v1.fluid.*;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.network.chat.Component;
import com.mojang.serialization.MapCodec;
import java.util.*;

public class FluidMachineBlock extends BaseEntityBlock {
 public FluidMachineBlock(Properties p){super(p);}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(FluidMachineBlock::new);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FluidMachineEntity(pos,state);}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
  if(ContainerItemContext.ofPlayerHand(player,hand).find(FluidStorage.ITEM)==null)return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  if(!level.isClientSide&&level.getBlockEntity(pos) instanceof FluidMachineEntity tank)FluidStorageUtil.interactWithFluidStorage(tank.tank,player,hand);
  // Consume rejected fluid-container clicks too, so buckets cannot spill on top of a full tank.
  return ItemInteractionResult.SUCCESS;
 }
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
  if(!level.isClientSide&&level.getBlockEntity(pos) instanceof FluidMachineEntity tank){
   if(this instanceof Pipe){Direction side=selectedSide(state,pos,hit);tank.cycle(side);player.displayClientMessage(Component.translatable("message.mortal_expansion.ecto_pipe.mode",Component.translatable("message.mortal_expansion.ecto_pipe.mode."+tank.mode(side))),true);}
   else player.displayClientMessage(Component.translatable("message.mortal_expansion.ecto_fluid.status",tank.tank.isResourceBlank()?Component.translatable("message.mortal_expansion.ecto_fluid.empty"):Component.translatable(tank.tank.variant.getFluid().defaultFluidState().createLegacyBlock().getBlock().getDescriptionId()),tank.tank.amount/FluidMachineEntity.MB,tank.capacityMb()),true);
  }
  return InteractionResult.sidedSuccess(level.isClientSide);
 }
 public static Direction selectedSide(BlockState state,BlockPos pos,BlockHitResult hit){
  var delta=hit.getLocation().subtract(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
  if(state.getBlock() instanceof Pipe)for(Direction d:Direction.values())if(state.getValue(property(d))&&delta.x*d.getStepX()+delta.y*d.getStepY()+delta.z*d.getStepZ()>0.1875+1E-6)return d;
  return hit.getDirection();
 }
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){
  var drops=super.getDrops(state,builder);if(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FluidMachineEntity be)for(var drop:drops)be.saveToItem(drop,builder.getLevel().registryAccess());return drops;
 }
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof FluidMachineEntity be)FluidNetworks.remove(be);super.onRemove(state,level,pos,next,moving);}
 static BooleanProperty property(Direction d){return switch(d){case DOWN->BlockStateProperties.DOWN;case UP->BlockStateProperties.UP;case NORTH->BlockStateProperties.NORTH;case SOUTH->BlockStateProperties.SOUTH;case WEST->BlockStateProperties.WEST;case EAST->BlockStateProperties.EAST;};}
 public static final class Pipe extends FluidMachineBlock {
  public Pipe(Properties p){super(p);var state=stateDefinition.any();for(Direction d:Direction.values())state=state.setValue(property(d),false);registerDefaultState(state);}
  @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(Pipe::new);}
  @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b){for(Direction d:Direction.values())b.add(property(d));}
  private static boolean connects(LevelAccessor level,BlockPos pos,Direction d){var at=pos.relative(d);if(level instanceof net.minecraft.server.level.ServerLevel server&&server.getChunkSource().getChunkNow(at.getX()>>4,at.getZ()>>4)==null)return false;return level.getBlockState(at).getBlock() instanceof FluidMachineBlock;}
  @Override public BlockState getStateForPlacement(BlockPlaceContext c){var state=defaultBlockState();for(Direction d:Direction.values())state=state.setValue(property(d),connects(c.getLevel(),c.getClickedPos(),d));return state;}
  @Override protected BlockState updateShape(BlockState state,Direction d,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos other){if(level.getBlockEntity(pos) instanceof FluidMachineEntity be)FluidNetworks.invalidate(be);return state.setValue(property(d),connects(level,pos,d));}
  @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){var shape=box(5,5,5,11,11,11);for(Direction d:Direction.values())if(state.getValue(property(d)))shape=Shapes.or(shape,box(d==Direction.WEST?0:5,d==Direction.DOWN?0:5,d==Direction.NORTH?0:5,d==Direction.EAST?16:11,d==Direction.UP?16:11,d==Direction.SOUTH?16:11));return shape;}
  @Override protected BlockState rotate(BlockState state,Rotation r){var result=state;for(Direction d:Direction.values())result=result.setValue(property(r.rotate(d)),state.getValue(property(d)));return result;}
  @Override protected BlockState mirror(BlockState state,Mirror m){var result=state;for(Direction d:Direction.values())result=result.setValue(property(m.mirror(d)),state.getValue(property(d)));return result;}
 }
}
