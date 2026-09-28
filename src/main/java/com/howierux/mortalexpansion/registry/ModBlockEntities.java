package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.EctchantmentMachineBlockEntity;
import com.howierux.mortalexpansion.blockentity.EctoplasmicGeneratorBlockEntity;
import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MortalExpansion.MOD_ID);

    public static final Supplier<BlockEntityType<EctchantmentMachineBlockEntity>> ECTCHANTMENT_MACHINE =
            BLOCK_ENTITY_TYPES.register(
                    "ectchantment_machine",
                    () -> BlockEntityType.Builder.of(
                            EctchantmentMachineBlockEntity::new,
                            ModBlocks.ECTCHANTMENT_MACHINE.get()
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<EctoplasmicGeneratorBlockEntity>> ECTOPLASMIC_GENERATOR =
            BLOCK_ENTITY_TYPES.register(
                    "ectoplasmic_generator",
                    () -> BlockEntityType.Builder.of(
                            EctoplasmicGeneratorBlockEntity::new,
                            ModBlocks.ECTOPLASMIC_GENERATOR.get()
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<GraveBlockEntity>> GRAVE =
            BLOCK_ENTITY_TYPES.register(
                    "grave",
                    () -> BlockEntityType.Builder.of(
                            GraveBlockEntity::new,
                            ModBlocks.GRAVE.get()
                    ).build(null)
            );

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}
