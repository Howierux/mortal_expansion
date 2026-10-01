package com.howierux.mortalexpansion.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.FurnaceBlock;

public final class HaintFurnaceBlock extends HauntFurnaceBlock {
    public static final MapCodec<HaintFurnaceBlock> CODEC = simpleCodec(HaintFurnaceBlock::new);
    public HaintFurnaceBlock(Properties properties) { super(properties); }

    @Override
    public MapCodec<? extends FurnaceBlock> codec() { return CODEC; }
}
