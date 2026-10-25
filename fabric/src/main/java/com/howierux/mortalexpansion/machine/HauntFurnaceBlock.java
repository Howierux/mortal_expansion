package com.howierux.mortalexpansion.machine;



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
        return new HauntFurnaceEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type,
                Machines.FURNACE, HauntFurnaceEntity::serverTick);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof HauntFurnaceEntity furnace) {
            player.openMenu(furnace);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }
    public static final class Haint extends HauntFurnaceBlock {
        public static final net.minecraft.world.level.block.state.properties.BooleanProperty HAUNT_POWERED=net.minecraft.world.level.block.state.properties.BooleanProperty.create("haunt_powered");
        public Haint(Properties properties){super(properties);registerDefaultState(defaultBlockState().setValue(HAUNT_POWERED,false));}
        @Override protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<net.minecraft.world.level.block.Block,BlockState> b){super.createBlockStateDefinition(b);b.add(HAUNT_POWERED);}
        @Override public MapCodec<FurnaceBlock> codec(){return simpleCodec(Haint::new);}
    }
}
