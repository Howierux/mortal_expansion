package com.howierux.mortalexpansion.combat;
import com.howierux.mortalexpansion.catalog.CatalogBlocks;
import net.minecraft.world.level.block.state.BlockState;
public final class SpiritSignals {public static boolean isTorch(BlockState state){return state.is(CatalogBlocks.SPIRIT_TORCH)||state.is(CatalogBlocks.SPIRIT_WALL_TORCH);}}
