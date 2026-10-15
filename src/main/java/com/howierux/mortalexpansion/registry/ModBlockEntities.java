package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.EctchantmentMachineBlockEntity;
import com.howierux.mortalexpansion.blockentity.EctoplasmicGeneratorBlockEntity;
import com.howierux.mortalexpansion.blockentity.EnchantmentGeneratorBlockEntity;
import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.howierux.mortalexpansion.blockentity.HauntFurnaceBlockEntity;
import com.howierux.mortalexpansion.blockentity.PurificationWardBlockEntity;
import com.howierux.mortalexpansion.blockentity.PurificationStationBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MortalExpansion.MOD_ID);

    public static final Supplier<BlockEntityType<com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity>> ECTO_FLUID =
            BLOCK_ENTITY_TYPES.register("ecto_fluid",()->BlockEntityType.Builder.of(com.howierux.mortalexpansion.blockentity.EctoFluidBlockEntity::new,
                    ModBlocks.ECTANK.get(),ModBlocks.PURIFIED_ECTANK.get(),ModBlocks.ECTO_PIPE.get(),ModBlocks.PURIFIED_ECTO_PIPE.get()).build(null));
    public static final Supplier<BlockEntityType<com.howierux.mortalexpansion.blockentity.MortalGolemBaseBlockEntity>> MORTAL_GOLEM_BASE =
            BLOCK_ENTITY_TYPES.register("mortal_golem_base",()->BlockEntityType.Builder.of(
                    com.howierux.mortalexpansion.blockentity.MortalGolemBaseBlockEntity::new,ModBlocks.MORTAL_GOLEM_BASE.get()).build(null));
    public static final Supplier<BlockEntityType<com.howierux.mortalexpansion.blockentity.WispLanternBlockEntity>> WISP_O_LANTERN =
            BLOCK_ENTITY_TYPES.register("wisp_o_lantern",()->BlockEntityType.Builder.of(
                    com.howierux.mortalexpansion.blockentity.WispLanternBlockEntity::new,ModBlocks.WISP_O_LANTERN.get()).build(null));
    public static final Supplier<BlockEntityType<com.howierux.mortalexpansion.blockentity.DreamcatcherBlockEntity>> DREAMCATCHER =
            BLOCK_ENTITY_TYPES.register("dreamcatcher", () -> BlockEntityType.Builder.of(
                    com.howierux.mortalexpansion.blockentity.DreamcatcherBlockEntity::new, ModBlocks.DREAMCATCHER.get(), ModBlocks.PURIFIED_DREAMCATCHER.get()).build(null));

    public static final Supplier<BlockEntityType<com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity>> ALTAR_OF_IMPURITY = BLOCK_ENTITY_TYPES.register("altar_of_impurity",()->BlockEntityType.Builder.of(com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity::impurity,ModBlocks.ALTAR_OF_IMPURITY.get()).build(null));
    public static final Supplier<BlockEntityType<com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity>> ALTAR_OF_PURITY = BLOCK_ENTITY_TYPES.register("altar_of_purity",()->BlockEntityType.Builder.of(com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity::new,ModBlocks.ALTAR_OF_PURITY.get()).build(null));

    public static final Supplier<BlockEntityType<com.howierux.mortalexpansion.blockentity.MortalityResearchStationBlockEntity>> MORTALITY_RESEARCH_STATION=BLOCK_ENTITY_TYPES.register("mortality_research_station",()->BlockEntityType.Builder.of(com.howierux.mortalexpansion.blockentity.MortalityResearchStationBlockEntity::new,ModBlocks.MORTALITY_RESEARCH_STATION.get()).build(null));
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

    public static final Supplier<BlockEntityType<EnchantmentGeneratorBlockEntity>> ENCHANTMENT_GENERATOR =
            BLOCK_ENTITY_TYPES.register(
                    "enchantment_generator",
                    () -> BlockEntityType.Builder.of(
                            EnchantmentGeneratorBlockEntity::new,
                            ModBlocks.ENCHANTMENT_GENERATOR.get()
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<PurificationStationBlockEntity>> PURIFICATION_STATION =
            BLOCK_ENTITY_TYPES.register(
                    "purification_station",
                    () -> BlockEntityType.Builder.of(
                            PurificationStationBlockEntity::new,
                            ModBlocks.PURIFICATION_STATION.get()
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<PurificationWardBlockEntity>> PURIFICATION_WARD =
            BLOCK_ENTITY_TYPES.register(
                    "purification_ward",
                    () -> BlockEntityType.Builder.of(
                            PurificationWardBlockEntity::new,
                            ModBlocks.PURIFICATION_WARD.get()
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

    public static final Supplier<BlockEntityType<HauntFurnaceBlockEntity>> HAUNT_FURNACE =
            BLOCK_ENTITY_TYPES.register("haunt_furnace",
                    () -> BlockEntityType.Builder.of(HauntFurnaceBlockEntity::new,
                            ModBlocks.HAUNT_FURNACE.get(), ModBlocks.HAINT_FURNACE.get()).build(null));

    public static final Supplier<BlockEntityType<com.howierux.mortalexpansion.blockentity.SpiritwellBlockEntity>> SPIRITWELL =
            BLOCK_ENTITY_TYPES.register("spiritwell", () -> BlockEntityType.Builder.of(
                    com.howierux.mortalexpansion.blockentity.SpiritwellBlockEntity::new,
                    ModBlocks.SPIRITWELL.get()).build(null));

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}
