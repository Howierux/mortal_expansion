package com.howierux.mortalexpansion.block;
import com.mojang.serialization.MapCodec;
import com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
public final class AltarOfImpurityBlock extends AltarOfPurityBlock {
    public static final MapCodec<AltarOfImpurityBlock> CODEC=simpleCodec(AltarOfImpurityBlock::new);
    public AltarOfImpurityBlock(BlockBehaviour.Properties properties){super(properties);}
    @Override protected MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec(){return CODEC;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return AltarOfPurityBlockEntity.impurity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,ModBlockEntities.ALTAR_OF_IMPURITY.get(),AltarOfPurityBlockEntity::serverTick);}
}
