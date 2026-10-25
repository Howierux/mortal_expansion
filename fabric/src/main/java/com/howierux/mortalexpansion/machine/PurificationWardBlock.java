package com.howierux.mortalexpansion.machine;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class PurificationWardBlock extends BaseEntityBlock {
 public PurificationWardBlock(Properties p){super(p);}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(PurificationWardBlock::new);}
 @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new WardEntity(p,s);}
 @Override protected void onRemove(BlockState s,Level level,BlockPos pos,BlockState next,boolean moved){if(!s.is(next.getBlock())&&level instanceof ServerLevel server)PurificationWards.release(server,pos);super.onRemove(s,level,pos,next,moved);}
 public static final class WardEntity extends BlockEntity {public WardEntity(BlockPos p,BlockState s){super(Machines.WARD,p,s);}}
}
