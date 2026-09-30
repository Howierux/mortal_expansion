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
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ECTCHANTMENT_MACHINE.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage()
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
                (blockEntity, side) -> blockEntity.getEnergyStorage()
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
