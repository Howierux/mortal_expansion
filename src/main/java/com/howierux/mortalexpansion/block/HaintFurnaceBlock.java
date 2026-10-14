package com.howierux.mortalexpansion.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.FurnaceBlock;

public final class HaintFurnaceBlock extends HauntFurnaceBlock {
    public static final MapCodec<FurnaceBlock> CODEC = simpleCodec(HaintFurnaceBlock::new);
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty HAUNT_POWERED =
            net.minecraft.world.level.block.state.properties.BooleanProperty.create("haunt_powered");
    public HaintFurnaceBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(HAUNT_POWERED,false));
    }
    @Override protected void createBlockStateDefinition(
            net.minecraft.world.level.block.state.StateDefinition.Builder<net.minecraft.world.level.block.Block,
                    net.minecraft.world.level.block.state.BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAUNT_POWERED);
    }

    @Override
    public MapCodec<FurnaceBlock> codec() { return CODEC; }
}
