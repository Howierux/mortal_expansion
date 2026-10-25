package com.howierux.mortalexpansion.haunt;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

/** Fabric-specific persistence boundary. Whole hundredths are immutable and copied/synchronized by the API. */
public final class FabricHauntData {
    private FabricHauntData(){}
    public static final AttachmentType<Integer> CHUNK = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath("mortal_expansion","chunk_haunt_units"),
            builder->builder.persistent(Codec.intRange(0,FractionalHaunt.MAX_UNITS)));
    public static final AttachmentType<Integer> RITUAL_REVISION=AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath("mortal_expansion","haunt_ritual_revision"),
            builder->builder.initializer(()->0).persistent(Codec.intRange(0,Integer.MAX_VALUE)));
    public static final AttachmentType<Integer> PLAYER = AttachmentRegistry.create(
            ResourceLocation.fromNamespaceAndPath("mortal_expansion","player_haunt_units"),
            builder->builder.initializer(()->HauntTier.NATURAL.midpointValue()*100)
                    .persistent(Codec.intRange(0,FractionalHaunt.MAX_UNITS)).copyOnDeath()
                    .syncWith(ByteBufCodecs.VAR_INT,AttachmentSyncPredicate.targetOnly()));
    public static void initialize(){} // Force registration from the common initializer before saved data is read.
}
