package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.block.SpiritwoodSaplingBlock;
import com.howierux.mortalexpansion.worldgen.ModTreeGrowers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MortalExpansion.MOD_ID);

    public static final DeferredBlock<RotatedPillarBlock> SPIRITWOOD_LOG = BLOCKS.registerBlock(
            "spiritwood_log",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
    );

    public static final DeferredBlock<LeavesBlock> SPIRITWOOD_LEAVES = BLOCKS.registerBlock(
            "spiritwood_leaves",
            LeavesBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
    );

    public static final DeferredBlock<SpiritwoodSaplingBlock> SPIRITWOOD_SAPLING = BLOCKS.registerBlock(
            "spiritwood_sapling",
            properties -> new SpiritwoodSaplingBlock(ModTreeGrowers.SPIRITWOOD, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
    );

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
