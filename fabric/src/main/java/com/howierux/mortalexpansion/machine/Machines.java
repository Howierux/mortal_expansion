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
 public static final BlockEntityType<ResearchStationEntity> RESEARCH=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("mortality_research_station"),BlockEntityType.Builder.of(ResearchStationEntity::new,CatalogBlocks.MORTALITY_RESEARCH_STATION).build(null));
 public static final ExtendedScreenHandlerType<ResearchMenu,BlockPos> RESEARCH_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("mortality_research_station"),new ExtendedScreenHandlerType<>(ResearchMenu::new,BlockPos.STREAM_CODEC));
 public static final BlockEntityType<AltarEntity> PURITY=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("altar_of_purity"),BlockEntityType.Builder.of(AltarEntity::new,CatalogBlocks.ALTAR_OF_PURITY).build(null));
 public static final BlockEntityType<AltarEntity> IMPURITY=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("altar_of_impurity"),BlockEntityType.Builder.of(AltarEntity::new,CatalogBlocks.ALTAR_OF_IMPURITY).build(null));
 public static final ExtendedScreenHandlerType<AltarMenu,BlockPos> PURITY_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("altar_of_purity"),new ExtendedScreenHandlerType<>(AltarMenu::new,BlockPos.STREAM_CODEC));
 public static final ExtendedScreenHandlerType<AltarMenu,BlockPos> IMPURITY_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("altar_of_impurity"),new ExtendedScreenHandlerType<>(AltarMenu::new,BlockPos.STREAM_CODEC));
 public static final BlockEntityType<HauntFurnaceEntity> FURNACE=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("haunt_furnace"),BlockEntityType.Builder.of(HauntFurnaceEntity::new,CatalogBlocks.HAUNT_FURNACE,CatalogBlocks.HAINT_FURNACE).build(null));
 public static final BlockEntityType<PurificationStationEntity> STATION=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("purification_station"),BlockEntityType.Builder.of(PurificationStationEntity::new,CatalogBlocks.PURIFICATION_STATION).build(null));
 public static final BlockEntityType<PurificationWardBlock.WardEntity> WARD=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,Catalog.id("purification_ward"),BlockEntityType.Builder.of(PurificationWardBlock.WardEntity::new,CatalogBlocks.PURIFICATION_WARD).build(null));
 public static final net.minecraft.core.component.DataComponentType<net.minecraft.nbt.CompoundTag> STATION_DATA=Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,Catalog.id("purification_station_data"),net.minecraft.core.component.DataComponentType.<net.minecraft.nbt.CompoundTag>builder().persistent(net.minecraft.nbt.CompoundTag.CODEC).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.COMPOUND_TAG).build());
 public static final ExtendedScreenHandlerType<PurificationStationMenu,BlockPos> STATION_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("purification_station"),new ExtendedScreenHandlerType<>(PurificationStationMenu::new,BlockPos.STREAM_CODEC));
 public static final ExtendedScreenHandlerType<GeneratorMenu,BlockPos> ECTO_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("ectoplasmic_generator"),new ExtendedScreenHandlerType<>(GeneratorMenu::new,BlockPos.STREAM_CODEC));
 public static final ExtendedScreenHandlerType<GeneratorMenu,BlockPos> ENCHANT_MENU=Registry.register(BuiltInRegistries.MENU,Catalog.id("enchantment_generator"),new ExtendedScreenHandlerType<>(GeneratorMenu::new,BlockPos.STREAM_CODEC));
 public static void initialize(){
  ItemStorage.SIDED.registerForBlockEntity(InventoryStorage::of,PURITY);
  ItemStorage.SIDED.registerForBlockEntity(InventoryStorage::of,IMPURITY);
  FluidStorage.SIDED.registerForBlockEntity((be,side)->be.water,STATION);
  EnergyStorage.SIDED.registerForBlockEntity((be,side)->be.energy,STATION);
  ItemStorage.SIDED.registerForBlockEntity(InventoryStorage::of,STATION);
  ItemStorage.SIDED.registerForBlockEntity(InventoryStorage::of,FURNACE);
  ServerWorldEvents.LOAD.register((server,level)->PurificationWards.restore(level));
  ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be,level)->{if(be instanceof PurificationWardBlock.WardEntity)PurificationWards.add(level,be.getBlockPos());});
  ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((be,level)->{if(be instanceof PurificationWardBlock.WardEntity)com.howierux.mortalexpansion.haunt.PurificationWardTracker.remove(level,be.getBlockPos());});
  ServerTickEvents.END_WORLD_TICK.register(PurificationWards::tick);
  ServerLifecycleEvents.SERVER_STOPPED.register(server->PurificationWards.clear());
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
