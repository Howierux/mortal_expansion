package com.howierux.mortalexpansion.network;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class SpiritInteractionPayloads {
    private SpiritInteractionPayloads(){}
    public record Open(String token,Component title,Component body,int mode) implements CustomPacketPayload {
        public static final Type<Open> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("mortal_expansion","spirit_options"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Open> CODEC=StreamCodec.composite(ByteBufCodecs.STRING_UTF8,Open::token,
            ComponentSerialization.STREAM_CODEC,Open::title,ComponentSerialization.STREAM_CODEC,Open::body,ByteBufCodecs.VAR_INT,Open::mode,Open::new);
        public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record Choice(String token,int action) implements CustomPacketPayload {
        public static final Type<Choice> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("mortal_expansion","spirit_choice"));
        public static final StreamCodec<io.netty.buffer.ByteBuf,Choice> CODEC=StreamCodec.composite(ByteBufCodecs.stringUtf8(36),Choice::token,ByteBufCodecs.VAR_INT,Choice::action,Choice::new);
        public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void open(ServerPlayer p,String token,Component title,Component body,int mode){
        if(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(p,Open.TYPE))net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(p,new Open(token,title,body,mode));
    }
}
