package com.howierux.mortalexpansion.machine;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import java.util.List;
public final class PurificationStationBlock extends BaseEntityBlock {
 public PurificationStationBlock(Properties p){super(p);}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(PurificationStationBlock::new);}
 @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new PurificationStationEntity(p,s);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> t){return l.isClientSide?null:createTickerHelper(t,Machines.STATION,(level,pos,state,be)->be.tick((net.minecraft.server.level.ServerLevel)level));}
 @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult h){if(!l.isClientSide&&l.getBlockEntity(p) instanceof PurificationStationEntity be)player.openMenu(be);return InteractionResult.sidedSuccess(l.isClientSide);}
 @Override public void setPlacedBy(Level l,BlockPos p,BlockState s,LivingEntity placer,ItemStack stack){super.setPlacedBy(l,p,s,placer,stack);var data=stack.get(Machines.STATION_DATA);if(!l.isClientSide&&data!=null&&l.getBlockEntity(p) instanceof PurificationStationEntity be)be.restorePortable(data);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){var drops=super.getDrops(state,builder);if(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof PurificationStationEntity be)for(var drop:drops)drop.set(Machines.STATION_DATA,be.portable());return drops;}
 @Override protected void onRemove(BlockState s,Level l,BlockPos p,BlockState next,boolean moving){if(!s.is(next.getBlock())&&l.getBlockEntity(p) instanceof PurificationStationEntity be)be.dropInventory();super.onRemove(s,l,p,next,moving);}
}
