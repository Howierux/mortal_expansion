package com.howierux.mortalexpansion.network;

import com.howierux.mortalexpansion.spiritwell.GravePorting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
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
                ByteBufCodecs.STRING_UTF8,Choose::token,ByteBufCodecs.VAR_INT,Choose::index,Choose::new);
        public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void register(IEventBus bus){bus.addListener(GravePortPayloads::handlers);}
    private static void handlers(RegisterPayloadHandlersEvent event){
        var registrar=event.registrar("1");
        registrar.playToClient(Open.TYPE,Open.CODEC,(payload,context)->
                com.howierux.mortalexpansion.client.GravePortScreen.handle(payload,context));
        registrar.playToServer(Choose.TYPE,Choose.CODEC,(payload,context)->{
            if(context.player() instanceof ServerPlayer player)GravePorting.choose(player,payload.token(),payload.index());
        });
    }
    public static void send(ServerPlayer player,String token,List<Destination> destinations){
        if(player.connection.hasChannel(Open.TYPE))PacketDistributor.sendToPlayer(player,new Open(token,destinations));
    }
}
