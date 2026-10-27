package com.howierux.mortalexpansion.spiritwell;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class SpiritwellBlock extends BaseEntityBlock {
 public SpiritwellBlock(Properties p){super(p);}
 protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(SpiritwellBlock::new);}
 public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SpiritwellBlockEntity(pos,state);}
 protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){super.onPlace(state,level,pos,old,moving);if(level instanceof net.minecraft.server.level.ServerLevel server)SpiritwellIndex.get(server).add(pos);}
 protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!next.is(this)&&level instanceof net.minecraft.server.level.ServerLevel server)SpiritwellIndex.get(server).remove(pos);super.onRemove(state,level,pos,next,moving);}
 protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){if(player instanceof net.minecraft.server.level.ServerPlayer server)GravePorting.open(server,pos);return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);}
}
