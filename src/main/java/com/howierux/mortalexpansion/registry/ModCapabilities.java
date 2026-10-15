package com.howierux.mortalexpansion.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class ModCapabilities {
    private ModCapabilities() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModCapabilities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,ModBlockEntities.ECTO_FLUID.get(),(tank,side)->tank.handler(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ModBlockEntities.ALTAR_OF_IMPURITY.get(),(altar,side)->altar.getAutomationItems());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ModBlockEntities.ALTAR_OF_PURITY.get(),(altar,side)->altar.getAutomationItems());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.HAUNT_FURNACE.get(),
                (furnace, side) -> side == null
                        ? new net.neoforged.neoforge.items.wrapper.InvWrapper(furnace)
                        : new net.neoforged.neoforge.items.wrapper.SidedInvWrapper(furnace, side));

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ECTCHANTMENT_MACHINE.get(),
                (blockEntity, side) -> blockEntity.getInputOnlyEnergyCapability()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ECTCHANTMENT_MACHINE.get(),
                (blockEntity, side) -> blockEntity.getAutomationItems()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ECTOPLASMIC_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ECTOPLASMIC_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getAutomationItems()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ENCHANTMENT_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.PURIFICATION_STATION.get(),
                (blockEntity, side) -> blockEntity.getInputOnlyEnergyCapability()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.PURIFICATION_STATION.get(),
                (blockEntity, side) -> blockEntity.getAutomationItems()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.PURIFICATION_STATION.get(),
                (blockEntity, side) -> blockEntity.getWaterTank()
        );
    }
}
