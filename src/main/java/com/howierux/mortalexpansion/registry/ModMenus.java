package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.menu.EctchantmentMachineMenu;
import com.howierux.mortalexpansion.menu.EctoplasmicGeneratorMenu;
import com.howierux.mortalexpansion.menu.EnchantmentGeneratorMenu;
import com.howierux.mortalexpansion.menu.PurificationStationMenu;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModMenus {
    private ModMenus() {}

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MortalExpansion.MOD_ID);

    public static final Supplier<MenuType<EctchantmentMachineMenu>> ECTCHANTMENT_MACHINE =
            MENUS.register(
                    "ectchantment_machine",
                    () -> new MenuType<>(
                            EctchantmentMachineMenu::new,
                            FeatureFlags.DEFAULT_FLAGS
                    )
            );

    public static final Supplier<MenuType<EctoplasmicGeneratorMenu>> ECTOPLASMIC_GENERATOR =
            MENUS.register(
                    "ectoplasmic_generator",
                    () -> new MenuType<>(
                            EctoplasmicGeneratorMenu::new,
                            FeatureFlags.DEFAULT_FLAGS
                    )
            );

    public static final Supplier<MenuType<EnchantmentGeneratorMenu>> ENCHANTMENT_GENERATOR =
            MENUS.register(
                    "enchantment_generator",
                    () -> new MenuType<>(
                            EnchantmentGeneratorMenu::new,
                            FeatureFlags.DEFAULT_FLAGS
                    )
            );

    public static final Supplier<MenuType<PurificationStationMenu>> PURIFICATION_STATION =
            MENUS.register(
                    "purification_station",
                    () -> IMenuTypeExtension.create(
                            PurificationStationMenu::new
                    )
            );

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}
