package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MortalExpansion.MOD_ID);

    public static final Supplier<CreativeModeTab> MORTAL_EXPANSION = CREATIVE_TABS.register(
            "mortal_expansion",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mortal_expansion"))
                    .icon(() -> new ItemStack(ModItems.RAW_ECTOPLASM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(com.howierux.mortalexpansion.guide.EctceteraProgression.book(0));
                        output.accept(ModItems.RAW_ECTOPLASM.get());
                        output.accept(ModItems.ALTAR_OF_PURITY.get());
                        output.accept(ModItems.PURE_ECTOPLASM.get());
                        output.accept(ModItems.BLOODSTONE.get());

                        output.accept(ModItems.BLOODSTONE_ORE_STONE.get());
                        output.accept(ModItems.BLOODSTONE_ORE.get());

                        output.accept(ModItems.GRAVESTONE.get());
                        output.accept(ModItems.GRAVE_MOSS.get());
                        output.accept(ModItems.PURIFIED_GRAVESTONE.get());
                        output.accept(ModItems.GRAVE.get());
                        output.accept(ModItems.HAUNT_FURNACE.get());
                        output.accept(ModItems.HAINT_FURNACE.get());
                        output.accept(ModItems.STONE_FENCE.get());

                        output.accept(ModItems.SPIRITWOOD_LOG.get());
                        output.accept(ModItems.SPIRITWOOD_PLANKS.get());
                        output.accept(ModItems.SPIRITWOOD_STAIRS.get());
                        output.accept(ModItems.SPIRITWOOD_SLAB.get());
                        output.accept(ModItems.SPIRITWOOD_FENCE.get());
                        output.accept(ModItems.SPIRITWOOD_FENCE_GATE.get());
                        output.accept(ModItems.SPIRITWOOD_DOOR.get());
                        output.accept(ModItems.SPIRITWOOD_TRAPDOOR.get());
                        output.accept(ModItems.SPIRITWOOD_PRESSURE_PLATE.get());
                        output.accept(ModItems.SPIRITWOOD_BUTTON.get());
                        output.accept(ModItems.SPIRITWOOD_LEAVES.get());
                        output.accept(ModItems.SPIRITWOOD_SAPLING.get());

                        output.accept(ModItems.PURIFIED_SPIRITWOOD_LOG.get());
                        output.accept(ModItems.PURIFIED_SPIRITWOOD_PLANKS.get());
                        output.accept(ModItems.PURIFIED_SPIRITWOOD_STAIRS.get());
                        output.accept(ModItems.PURIFIED_SPIRITWOOD_SLAB.get());
                        output.accept(ModItems.PURIFIED_SPIRITWOOD_FENCE.get());
                        output.accept(ModItems.PURIFIED_SPIRITWOOD_FENCE_GATE.get());
                        output.accept(ModItems.PURIFIED_SPIRITWOOD_DOOR.get());
                        output.accept(ModItems.PURIFIED_SPIRITWOOD_TRAPDOOR.get());
                        output.accept(ModItems.PURIFIED_SPIRITWOOD_PRESSURE_PLATE.get());
                        output.accept(ModItems.PURIFIED_SPIRITWOOD_BUTTON.get());

                        output.accept(ModItems.ECTCHANTMENT_MACHINE.get());
                        output.accept(ModItems.ECTOPLASMIC_GENERATOR.get());
                        output.accept(ModItems.ENCHANTMENT_GENERATOR.get());
                        output.accept(ModItems.PURIFICATION_WARD.get());
                        output.accept(ModItems.PURIFICATION_STATION.get());

                        output.accept(ModItems.LIQUID_ECTOPLASM_BUCKET.get());
                        output.accept(ModItems.HOLY_WATER_BUCKET.get());
                        output.accept(ModItems.PURIFIED_ECTOPLASM_BUCKET.get());
                        output.accept(ModItems.HOLY_SALT.get());
                        output.accept(ModItems.EMF_READER.get());
                output.accept(ModItems.PRECISE_EMF_READER.get());
                        output.accept(ModItems.SPIRIT_TORCH.get());

                        output.accept(ModItems.GRAVESTONE_SWORD.get());
                        output.accept(ModItems.PURIFIED_GRAVESTONE_SWORD.get());
                        output.accept(ModItems.PHARAOHL_SCEPTER.get());

                        output.accept(ModItems.ECTOTHREAD.get());
                        output.accept(ModItems.PURIFIED_ECTOTHREAD.get());
                        output.accept(ModItems.GHOST_GARB_HELMET.get());
                        output.accept(ModItems.GHOST_GARB_CHESTPLATE.get());
                        output.accept(ModItems.GHOST_GARB_LEGGINGS.get());
                        output.accept(ModItems.GHOST_GARB_BOOTS.get());
                        output.accept(ModItems.HAINT_GARB_HELMET.get());
                        output.accept(ModItems.HAINT_GARB_CHESTPLATE.get());
                        output.accept(ModItems.HAINT_GARB_LEGGINGS.get());
                        output.accept(ModItems.HAINT_GARB_BOOTS.get());
                        output.accept(ModItems.SPECTRALITE_HAINT_HELMET.get());
                        output.accept(ModItems.SPECTRALITE_HAINT_CHESTPLATE.get());
                        output.accept(ModItems.SPECTRALITE_HAINT_LEGGINGS.get());
                        output.accept(ModItems.SPECTRALITE_HAINT_BOOTS.get());


                        output.accept(ModItems.BLOODSTONE_HELMET.get());
                        output.accept(ModItems.BLOODSTONE_CHESTPLATE.get());
                        output.accept(ModItems.BLOODSTONE_LEGGINGS.get());
                        output.accept(ModItems.BLOODSTONE_BOOTS.get());

                        output.accept(ModItems.BLOODSTONE_SWORD.get());
                        output.accept(ModItems.BLOODSTONE_PICKAXE.get());
                        output.accept(ModItems.BLOODSTONE_AXE.get());
                        output.accept(ModItems.BLOODSTONE_SHOVEL.get());
                        output.accept(ModItems.BLOODSTONE_HOE.get());

                        output.accept(ModItems.SPECTRALITE_HELMET.get());
                        output.accept(ModItems.SPECTRALITE_CHESTPLATE.get());
                        output.accept(ModItems.SPECTRALITE_LEGGINGS.get());
                        output.accept(ModItems.SPECTRALITE_BOOTS.get());

                        output.accept(ModItems.SPECTRALITE_SWORD.get());
                        output.accept(ModItems.SPECTRALITE_PICKAXE.get());
                        output.accept(ModItems.SPECTRALITE_AXE.get());
                        output.accept(ModItems.SPECTRALITE_SHOVEL.get());
                        output.accept(ModItems.SPECTRALITE_HOE.get());
                    })
                    .build()
    );

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
    }
}
