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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FADING = DATA_COMPONENTS.registerComponentType("fading",builder->builder.persistent(Codec.intRange(20,80)).networkSynchronized(ByteBufCodecs.VAR_INT));

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

    /** Missing component identifies pre-progression books, which retain all chapters. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ECTCETERA_TIER =
            DATA_COMPONENTS.registerComponentType("ectcetera_tier",
                    builder -> builder.persistent(Codec.intRange(0, 4)).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DAMNED_SECRETS = DATA_COMPONENTS.registerComponentType("damned_secrets",builder->builder.persistent(Codec.intRange(0,1)).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ECTCETERA_SECRETS =
            DATA_COMPONENTS.registerComponentType("ectcetera_secrets", builder -> builder.persistent(Codec.intRange(0,127)).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> PICKAXE_REPAIR_TIME =
            DATA_COMPONENTS.registerComponentType("pickaxe_repair_time", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

    /** Live EMF samples synchronize through inventory updates and intentionally do not persist. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<com.howierux.mortalexpansion.haunt.EmfReading>> EMF_READING =
            DATA_COMPONENTS.registerComponentType("emf_reading", builder -> builder.networkSynchronized(
                    com.howierux.mortalexpansion.haunt.EmfReading.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SCEPTER_CHARGE =
            DATA_COMPONENTS.registerComponentType("scepter_charge", builder -> builder
                    .persistent(Codec.intRange(0,12)).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static void register(IEventBus modBus) {
        DATA_COMPONENTS.register(modBus);
    }
}
