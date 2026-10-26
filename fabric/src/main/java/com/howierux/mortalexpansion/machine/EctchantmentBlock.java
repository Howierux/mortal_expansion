package com.howierux.mortalexpansion.machine;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class EctchantmentBlock extends BaseEntityBlock {
 public EctchantmentBlock(Properties p){super(p);}protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(EctchantmentBlock::new);}protected RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new EctchantmentEntity(pos,state);}
 public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,com.howierux.mortalexpansion.content.ContentRuntime.ECTCHANT,(world,pos,block,machine)->machine.tick());}
 protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player p,net.minecraft.world.phys.BlockHitResult hit){if(!level.isClientSide&&level.getBlockEntity(pos) instanceof EctchantmentEntity machine)p.openMenu(machine);return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);}
 protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!next.is(this)&&level.getBlockEntity(pos) instanceof EctchantmentEntity machine){net.minecraft.world.Containers.dropContents(level,pos,machine);level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moving);}
}
