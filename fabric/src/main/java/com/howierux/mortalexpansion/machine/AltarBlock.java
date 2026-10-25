package com.howierux.mortalexpansion.machine;
import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
public final class AltarBlock extends BaseEntityBlock {
 public AltarBlock(Properties p){super(p);registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.LIT,false));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(AltarBlock::new);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b){b.add(BlockStateProperties.LIT);}
 @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new AltarEntity(p,s);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> t){return l.isClientSide?null:createTickerHelper(t,s.is(CatalogBlocks.ALTAR_OF_IMPURITY)?Machines.IMPURITY:Machines.PURITY,(world,pos,state,be)->be.tick((net.minecraft.server.level.ServerLevel)world));}
 @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult h){if(!l.isClientSide&&l.getBlockEntity(p) instanceof AltarEntity altar)player.openMenu(altar);return InteractionResult.sidedSuccess(l.isClientSide);}
 @Override protected void onRemove(BlockState s,Level l,BlockPos p,BlockState next,boolean moved){if(!s.is(next.getBlock())&&l.getBlockEntity(p) instanceof AltarEntity altar)altar.dropInventory();super.onRemove(s,l,p,next,moved);}
}
