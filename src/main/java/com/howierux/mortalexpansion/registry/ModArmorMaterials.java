package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public final class ModArmorMaterials {
    private ModArmorMaterials() {}

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, MortalExpansion.MOD_ID);

    /**
     * Bloodstone uses diamond-equivalent defense and toughness, with 1.5x
     * diamond durability to match the Bloodstone tool philosophy.
     */
    public static final Holder<ArmorMaterial> BLOODSTONE = ARMOR_MATERIALS.register(
            "bloodstone",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.BODY, 11);
                    }),
                    10,
                    SoundEvents.ARMOR_EQUIP_DIAMOND,
                    () -> Ingredient.of(ModItems.BLOODSTONE.get()),
                    List.of(new ArmorMaterial.Layer(
                            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"bloodstone_armor")
                    )),
                    2.0F,
                    0.0F
            )
    );

    /**
     * Spectralite is the Netherite-style smithing upgrade to Bloodstone. It
     * keeps the same defense values, raises toughness/knockback resistance,
     * and uses 1.3x Bloodstone durability.
     */
    public static final Holder<ArmorMaterial> SPECTRALITE = ARMOR_MATERIALS.register(
            "spectralite",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 8);
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.BODY, 11);
                    }),
                    15,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(Items.NETHERITE_INGOT),
                    List.of(new ArmorMaterial.Layer(
                            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"spectralite_armor")
                    )),
                    3.0F,
                    0.1F
            )
    );

    public static final Holder<ArmorMaterial> GHOST_GARB = ARMOR_MATERIALS.register("ghost_garb",
            () -> garbMaterial(() -> Ingredient.of(ModItems.ECTOTHREAD.get())));
    public static final Holder<ArmorMaterial> HAINT_GARB = ARMOR_MATERIALS.register("haint_garb",
            () -> garbMaterial(() -> Ingredient.of(ModItems.PURIFIED_ECTOTHREAD.get())));

    /** Both garb sets share stats and leather layers; each retains its own repair ingredient. */
    private static ArmorMaterial garbMaterial(java.util.function.Supplier<Ingredient> repair) {
        return new ArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, 1); map.put(ArmorItem.Type.CHESTPLATE, 3);
                    map.put(ArmorItem.Type.LEGGINGS, 2); map.put(ArmorItem.Type.BOOTS, 1); map.put(ArmorItem.Type.BODY, 3);
                }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, repair,
                List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("leather"), "", true),
                        new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("leather"), "_overlay", false)), 0.0F, 0.0F);
    }

    public static void register(IEventBus modBus) {
        ARMOR_MATERIALS.register(modBus);
    }
}
