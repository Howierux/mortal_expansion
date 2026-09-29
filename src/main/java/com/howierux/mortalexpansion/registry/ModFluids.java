package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModFluids {
    private ModFluids() {}

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.FLUID_TYPES,
                    MortalExpansion.MOD_ID
            );

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(
                    BuiltInRegistries.FLUID,
                    MortalExpansion.MOD_ID
            );

    public static final DeferredHolder<FluidType, FluidType> LIQUID_ECTOPLASM_TYPE =
            FLUID_TYPES.register(
                    "liquid_ectoplasm",
                    () -> new FluidType(
                            FluidType.Properties.create()
                                    .density(1150)
                                    .viscosity(2600)
                    )
            );

    public static final DeferredHolder<FluidType, FluidType> HOLY_WATER_TYPE =
            FLUID_TYPES.register(
                    "holy_water",
                    () -> new FluidType(
                            FluidType.Properties.create()
                                    .density(1000)
                                    .viscosity(1000)
                    )
            );

    public static final DeferredHolder<FluidType, FluidType> PURIFIED_ECTOPLASM_TYPE =
            FLUID_TYPES.register(
                    "purified_ectoplasm",
                    () -> new FluidType(
                            FluidType.Properties.create()
                                    .density(950)
                                    .viscosity(700)
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> LIQUID_ECTOPLASM =
            FLUIDS.register(
                    "liquid_ectoplasm",
                    () -> new BaseFlowingFluid.Source(liquidEctoplasmProperties())
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_LIQUID_ECTOPLASM =
            FLUIDS.register(
                    "flowing_liquid_ectoplasm",
                    () -> new BaseFlowingFluid.Flowing(liquidEctoplasmProperties())
            );

    public static final DeferredHolder<Fluid, FlowingFluid> HOLY_WATER =
            FLUIDS.register(
                    "holy_water",
                    () -> new BaseFlowingFluid.Source(holyWaterProperties())
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_HOLY_WATER =
            FLUIDS.register(
                    "flowing_holy_water",
                    () -> new BaseFlowingFluid.Flowing(holyWaterProperties())
            );

    public static final DeferredHolder<Fluid, FlowingFluid> PURIFIED_ECTOPLASM =
            FLUIDS.register(
                    "purified_ectoplasm",
                    () -> new BaseFlowingFluid.Source(purifiedEctoplasmProperties())
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_PURIFIED_ECTOPLASM =
            FLUIDS.register(
                    "flowing_purified_ectoplasm",
                    () -> new BaseFlowingFluid.Flowing(purifiedEctoplasmProperties())
            );

    private static BaseFlowingFluid.Properties liquidEctoplasmProperties() {
        return new BaseFlowingFluid.Properties(
                LIQUID_ECTOPLASM_TYPE,
                LIQUID_ECTOPLASM,
                FLOWING_LIQUID_ECTOPLASM
        )
                .bucket(ModItems.LIQUID_ECTOPLASM_BUCKET)
                .block(ModBlocks.LIQUID_ECTOPLASM_BLOCK)
                .tickRate(18)
                .slopeFindDistance(2)
                .levelDecreasePerBlock(2);
    }

    private static BaseFlowingFluid.Properties holyWaterProperties() {
        return new BaseFlowingFluid.Properties(
                HOLY_WATER_TYPE,
                HOLY_WATER,
                FLOWING_HOLY_WATER
        )
                .bucket(ModItems.HOLY_WATER_BUCKET)
                .block(ModBlocks.HOLY_WATER_BLOCK)
                .tickRate(5)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(1);
    }

    private static BaseFlowingFluid.Properties purifiedEctoplasmProperties() {
        return new BaseFlowingFluid.Properties(
                PURIFIED_ECTOPLASM_TYPE,
                PURIFIED_ECTOPLASM,
                FLOWING_PURIFIED_ECTOPLASM
        )
                .bucket(ModItems.PURIFIED_ECTOPLASM_BUCKET)
                .block(ModBlocks.PURIFIED_ECTOPLASM_BLOCK)
                .tickRate(2)
                .slopeFindDistance(6)
                .levelDecreasePerBlock(1);
    }

    public static void register(IEventBus modBus) {
        FLUID_TYPES.register(modBus);
        FLUIDS.register(modBus);
    }
}
