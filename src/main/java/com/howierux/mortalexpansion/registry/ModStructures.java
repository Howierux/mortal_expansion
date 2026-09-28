package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.worldgen.structure.GraveyardCemeteryPiece;
import com.howierux.mortalexpansion.worldgen.structure.GraveyardCemeteryStructure;
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

    public static final DeferredHolder<StructureType<?>, StructureType<GraveyardCemeteryStructure>>
            GRAVEYARD_CEMETERY = STRUCTURE_TYPES.register(
                    "graveyard_cemetery",
                    () -> () -> GraveyardCemeteryStructure.CODEC
            );

    public static final DeferredHolder<StructurePieceType, StructurePieceType>
            GRAVEYARD_CEMETERY_PIECE = STRUCTURE_PIECES.register(
                    "graveyard_cemetery",
                    () -> GraveyardCemeteryPiece::new
            );

    public static void register(IEventBus modBus) {
        STRUCTURE_TYPES.register(modBus);
        STRUCTURE_PIECES.register(modBus);
    }
}
