package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class EctankBlock extends BaseEntityBlock {
    public static final MapCodec<EctankBlock> CODEC=simpleCodec(EctankBlock::new);
    public EctankBlock(Properties properties){super(properties);}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new EctoFluidBlockEntity(pos,state);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        return interact(stack,state,level,pos,player,hand,hit);
    }
    public static ItemInteractionResult interact(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(net.neoforged.neoforge.fluids.FluidUtil.getFluidHandler(stack).isEmpty())return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(level.isClientSide)return ItemInteractionResult.SUCCESS;
        if(level.getBlockEntity(pos) instanceof EctoFluidBlockEntity tank
                && net.neoforged.neoforge.fluids.FluidUtil.interactWithFluidHandler(player,hand,tank.tank))return ItemInteractionResult.SUCCESS;
        // A rejected bucket interaction must never spill its fluid on top of the block.
        return ItemInteractionResult.CONSUME;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof EctoFluidBlockEntity tank)status(player,tank);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    public static void status(Player player,EctoFluidBlockEntity tank){
        player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.ecto_fluid.status",
                tank.tank.isEmpty()?net.minecraft.network.chat.Component.translatable("message.mortal_expansion.ecto_fluid.empty"):tank.tank.getFluid().getHoverName(),
                tank.tank.getFluidAmount(),tank.tank.getCapacity()),true);
    }
    @Override protected java.util.List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return portable(super.getDrops(state,builder),builder);}
    public static java.util.List<ItemStack> portable(java.util.List<ItemStack> drops,LootParams.Builder builder){
        if(builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof EctoFluidBlockEntity tank)
            for(var drop:drops)if(drop.getItem() instanceof BlockItem)tank.saveToItem(drop,builder.getLevel().registryAccess());
        return drops;
    }
}
