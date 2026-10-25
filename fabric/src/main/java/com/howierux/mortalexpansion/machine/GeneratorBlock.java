package com.howierux.mortalexpansion.machine;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class GeneratorBlock extends BaseEntityBlock {
 public GeneratorBlock(Properties p){super(p);}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(GeneratorBlock::new);}
 @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new GeneratorEntity(p,s);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> t){return l.isClientSide?null:(world,pos,state,be)->{if(be instanceof GeneratorEntity generator&&world instanceof ServerLevel server)generator.tick(server);};}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(!level.isClientSide&&level.getBlockEntity(pos) instanceof GeneratorEntity generator)player.openMenu(generator);return InteractionResult.sidedSuccess(level.isClientSide);}
 @Override protected void onRemove(BlockState s,Level l,BlockPos p,BlockState next,boolean moving){if(!s.is(next.getBlock())&&l.getBlockEntity(p) instanceof GeneratorEntity generator)generator.dropInventory();super.onRemove(s,l,p,next,moving);}
}
