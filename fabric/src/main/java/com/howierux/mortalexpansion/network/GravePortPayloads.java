package com.howierux.mortalexpansion.network;

import com.howierux.mortalexpansion.spiritwell.GravePorting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;

public final class GravePortPayloads {
    private GravePortPayloads(){}
    public record Destination(String dimension,BlockPos pos){
        public static final StreamCodec<io.netty.buffer.ByteBuf,Destination> CODEC=StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,Destination::dimension,BlockPos.STREAM_CODEC,Destination::pos,Destination::new);
    }
    public record Open(String token,List<Destination> destinations) implements CustomPacketPayload {
        public static final Type<Open> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("mortal_expansion","grave_port_open"));
        public static final StreamCodec<io.netty.buffer.ByteBuf,Open> CODEC=StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,Open::token,Destination.CODEC.apply(ByteBufCodecs.list()),Open::destinations,Open::new);
        public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record Choose(String token,int index) implements CustomPacketPayload {
        public static final Type<Choose> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("mortal_expansion","grave_port_choose"));
        public static final StreamCodec<io.netty.buffer.ByteBuf,Choose> CODEC=StreamCodec.composite(
                ByteBufCodecs.stringUtf8(36),Choose::token,ByteBufCodecs.VAR_INT,Choose::index,Choose::new);
        public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void send(ServerPlayer player,String token,List<Destination> destinations){if(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(player,Open.TYPE))net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player,new Open(token,destinations));}
}
