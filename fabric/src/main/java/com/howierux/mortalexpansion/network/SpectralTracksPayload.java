package com.howierux.mortalexpansion.network;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import com.howierux.mortalexpansion.catalog.Catalog;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
public record SpectralTracksPayload(boolean visible,BlockPos target) implements CustomPacketPayload {
 public static final Type<SpectralTracksPayload> TYPE=new Type<>(Catalog.id("spectral_tracks"));
 public static final StreamCodec<io.netty.buffer.ByteBuf,SpectralTracksPayload> CODEC=StreamCodec.composite(ByteBufCodecs.BOOL,SpectralTracksPayload::visible,BlockPos.STREAM_CODEC,SpectralTracksPayload::target,SpectralTracksPayload::new);
 public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static boolean send(ServerPlayer p,BlockPos pos){if(!ServerPlayNetworking.canSend(p,TYPE))return false;ServerPlayNetworking.send(p,new SpectralTracksPayload(true,pos));return true;}
 public static void clear(ServerPlayer p){if(ServerPlayNetworking.canSend(p,TYPE))ServerPlayNetworking.send(p,new SpectralTracksPayload(false,BlockPos.ZERO));}
}
