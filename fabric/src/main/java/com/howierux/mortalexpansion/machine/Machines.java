package com.howierux.mortalexpansion.machine;
import com.howierux.mortalexpansion.catalog.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.*;
import team.reborn.energy.api.EnergyStorage;

public final class Machines {
 public static final BlockEntityType<FluidMachineEntity> FLUID=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("ecto_fluid"),BlockEntityType.Builder.of(FluidMachineEntity::new,CatalogBlocks.ECTANK,CatalogBlocks.PURIFIED_ECTANK,CatalogBlocks.ECTO_PIPE,CatalogBlocks.PURIFIED_ECTO_PIPE).build(null));
 public static final BlockEntityType<GeneratorEntity> ECTO=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("ectoplasmic_generator"),BlockEntityType.Builder.of(GeneratorEntity::new,CatalogBlocks.ECTOPLASMIC_GENERATOR).build(null));
 public static final BlockEntityType<GeneratorEntity> ENCHANT=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("enchantment_generator"),BlockEntityType.Builder.of(GeneratorEntity::new,CatalogBlocks.ENCHANTMENT_GENERATOR).build(null));
 public static final ExtendedScreenHandlerType<GeneratorMenu,BlockPos> ECTO_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("ectoplasmic_generator"),new ExtendedScreenHandlerType<>(GeneratorMenu::new,BlockPos.STREAM_CODEC));
 public static final ExtendedScreenHandlerType<GeneratorMenu,BlockPos> ENCHANT_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("enchantment_generator"),new ExtendedScreenHandlerType<>(GeneratorMenu::new,BlockPos.STREAM_CODEC));
 public static void initialize(){
  FluidStorage.SIDED.registerForBlockEntity(FluidMachineEntity::storage,FLUID);
  EnergyStorage.SIDED.registerForBlockEntity((be,side)->be.energy,ECTO);
  EnergyStorage.SIDED.registerForBlockEntity((be,side)->be.energy,ENCHANT);
  ItemStorage.SIDED.registerForBlockEntity(InventoryStorage::of,ECTO);
  ItemStorage.SIDED.registerForBlockEntity(InventoryStorage::of,ENCHANT);
  ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be,world)->{if(be instanceof FluidMachineEntity fluid)FluidNetworks.add(fluid);});
  ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((be,world)->{if(be instanceof FluidMachineEntity fluid)FluidNetworks.remove(fluid);});
  ServerTickEvents.END_WORLD_TICK.register(FluidNetworks::tick);
  ServerLifecycleEvents.SERVER_STOPPED.register(server->FluidNetworks.clear());
 }
}
