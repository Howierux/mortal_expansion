package com.howierux.mortalexpansion.network;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

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
        public static final StreamCodec<io.netty.buffer.ByteBuf,Choice> CODEC=StreamCodec.composite(ByteBufCodecs.STRING_UTF8,Choice::token,ByteBufCodecs.VAR_INT,Choice::action,Choice::new);
        public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static void register(IEventBus bus){bus.addListener(SpiritInteractionPayloads::handlers);}
    private static void handlers(RegisterPayloadHandlersEvent event){
        var registrar=event.registrar("1");
        registrar.playToClient(Open.TYPE,Open.CODEC,(payload,context)->com.howierux.mortalexpansion.client.SpiritOptionsScreen.handle(payload,context));
        registrar.playToServer(Choice.TYPE,Choice.CODEC,(payload,context)->{
            if(context.player() instanceof ServerPlayer p)com.howierux.mortalexpansion.quest.SpiritInteraction.choose(p,payload.token(),payload.action());
        });
    }
    public static void open(ServerPlayer p,String token,Component title,Component body,int mode){
        if(p.connection.hasChannel(Open.TYPE))PacketDistributor.sendToPlayer(p,new Open(token,title,body,mode));
    }
}
