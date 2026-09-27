package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.block.SpiritwoodSaplingBlock;
import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.worldgen.ModTreeGrowers;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MortalExpansion.MOD_ID);

    public static final DeferredBlock<RotatedPillarBlock> SPIRITWOOD_LOG = BLOCKS.registerBlock(
            "spiritwood_log", RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)
    );

    public static final DeferredBlock<Block> SPIRITWOOD_PLANKS = BLOCKS.registerSimpleBlock(
            "spiritwood_planks",
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
    );

    public static final DeferredBlock<StairBlock> SPIRITWOOD_STAIRS = BLOCKS.registerBlock(
            "spiritwood_stairs",
            properties -> new StairBlock(SPIRITWOOD_PLANKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)
    );

    public static final DeferredBlock<SlabBlock> SPIRITWOOD_SLAB = BLOCKS.registerBlock(
            "spiritwood_slab", SlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)
    );

    public static final DeferredBlock<FenceBlock> SPIRITWOOD_FENCE = BLOCKS.registerBlock(
            "spiritwood_fence", FenceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)
    );

    public static final DeferredBlock<FenceGateBlock> SPIRITWOOD_FENCE_GATE = BLOCKS.registerBlock(
            "spiritwood_fence_gate",
            properties -> new FenceGateBlock(WoodType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)
    );

    public static final DeferredBlock<DoorBlock> SPIRITWOOD_DOOR = BLOCKS.registerBlock(
            "spiritwood_door",
            properties -> new DoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)
    );

    public static final DeferredBlock<TrapDoorBlock> SPIRITWOOD_TRAPDOOR = BLOCKS.registerBlock(
            "spiritwood_trapdoor",
            properties -> new TrapDoorBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)
    );

    public static final DeferredBlock<PressurePlateBlock> SPIRITWOOD_PRESSURE_PLATE = BLOCKS.registerBlock(
            "spiritwood_pressure_plate",
            properties -> new PressurePlateBlock(BlockSetType.OAK, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)
    );

    public static final DeferredBlock<ButtonBlock> SPIRITWOOD_BUTTON = BLOCKS.registerBlock(
            "spiritwood_button",
            properties -> new ButtonBlock(BlockSetType.OAK, 30, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)
    );

    public static final DeferredBlock<Block> GRAVESTONE = BLOCKS.registerSimpleBlock(
            "gravestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
    );

    public static final DeferredBlock<GraveBlock> GRAVE = BLOCKS.registerBlock(
            "grave",
            GraveBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
    );

    public static final DeferredBlock<DropExperienceBlock> BLOODSTONE_ORE = BLOCKS.registerBlock(
            "bloodstone_ore",
            properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE)
    );

    public static final DeferredBlock<LeavesBlock> SPIRITWOOD_LEAVES = BLOCKS.registerBlock(
            "spiritwood_leaves", LeavesBlock::new,
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
