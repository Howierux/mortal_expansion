package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    private ModDataComponents() {}

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(
                    Registries.DATA_COMPONENT_TYPE,
                    MortalExpansion.MOD_ID
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DEMI_TANGIBLE =
            DATA_COMPONENTS.registerComponentType(
                    "demi_tangible",
                    builder -> builder
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> PURIFICATION_STATION_DATA =
            DATA_COMPONENTS.registerComponentType(
                    "purification_station_data",
                    builder -> builder.persistent(CompoundTag.CODEC)
            );

    public static void register(IEventBus modBus) {
        DATA_COMPONENTS.register(modBus);
    }
}
