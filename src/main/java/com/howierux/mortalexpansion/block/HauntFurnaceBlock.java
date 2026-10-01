package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.blockentity.HauntFurnaceBlockEntity;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

public class HauntFurnaceBlock extends FurnaceBlock {
    public static final MapCodec<FurnaceBlock> CODEC = simpleCodec(HauntFurnaceBlock::new);
    public HauntFurnaceBlock(Properties properties) { super(properties); }

    @Override
    public MapCodec<FurnaceBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HauntFurnaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type,
                ModBlockEntities.HAUNT_FURNACE.get(), HauntFurnaceBlockEntity::serverTick);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof HauntFurnaceBlockEntity furnace) {
            player.openMenu(furnace);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }
}
