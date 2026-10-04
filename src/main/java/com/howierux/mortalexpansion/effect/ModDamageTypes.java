package com.howierux.mortalexpansion.effect;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public final class ModDamageTypes {
    private ModDamageTypes() {}
    public static final ResourceKey<DamageType> DEMI_TANGIBLE_PLAYER = key("demi_tangible_player");
    public static final ResourceKey<DamageType> DEMI_TANGIBLE_MOB = key("demi_tangible_mob");
    private static ResourceKey<DamageType> key(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, path));
    }
    public static final ResourceKey<DamageType> SANCTIFICATION = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID, "sanctification"));
}
