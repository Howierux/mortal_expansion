package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.worldgen.structure.GraveyardFencePiece;
import com.howierux.mortalexpansion.worldgen.structure.GraveyardFenceStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModStructures {
    private ModStructures() {}

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, MortalExpansion.MOD_ID);

    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, MortalExpansion.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<GraveyardFenceStructure>>
            GRAVEYARD_FENCE = STRUCTURE_TYPES.register(
                    "graveyard_fence",
                    () -> () -> GraveyardFenceStructure.CODEC
            );

    public static final DeferredHolder<StructurePieceType, StructurePieceType>
            GRAVEYARD_FENCE_PIECE = STRUCTURE_PIECES.register(
                    "graveyard_fence",
                    () -> GraveyardFencePiece::new
            );

    public static void register(IEventBus modBus) {
        STRUCTURE_TYPES.register(modBus);
        STRUCTURE_PIECES.register(modBus);
    }
}
