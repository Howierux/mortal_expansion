package com.howierux.mortalexpansion.haunt;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import java.util.UUID;

/** Transient server sample, scoped to a player and loaded chunk rather than saved as equipment state. */
public record EmfReading(ResourceLocation dimension, int chunkX, int chunkZ, UUID owner, int chunkHundredths, int personalHundredths) {
    public static final StreamCodec<RegistryFriendlyByteBuf, EmfReading> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, EmfReading::dimension,
            ByteBufCodecs.VAR_INT, EmfReading::chunkX, ByteBufCodecs.VAR_INT, EmfReading::chunkZ,
            UUIDUtil.STREAM_CODEC, EmfReading::owner,
            ByteBufCodecs.VAR_INT, EmfReading::chunkHundredths, ByteBufCodecs.VAR_INT, EmfReading::personalHundredths,
            EmfReading::new);
    public EmfReading {
        chunkHundredths = Math.max(0, Math.min(FractionalHaunt.MAX_UNITS, chunkHundredths));
        personalHundredths = Math.max(0, Math.min(PlayerHauntManager.MAX_PERSONAL_UNITS, personalHundredths));
    }
    public int chunkHaunt() { return chunkHundredths / FractionalHaunt.UNITS_PER_POINT; }
    public int personalHaunt() { return personalHundredths / FractionalHaunt.UNITS_PER_POINT; }
    public boolean isCurrentFor(Player player) {
        var pos = player.chunkPosition();
        return owner.equals(player.getUUID()) && dimension.equals(player.level().dimension().location())
                && chunkX == pos.x && chunkZ == pos.z;
    }
}
