package com.howierux.mortalexpansion.network;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
/** Private viewer knowledge: never broadcast one player's revelation to other players. */
public record DarkholdKnowledgePayload(int secrets) implements CustomPacketPayload {
    public static final Type<DarkholdKnowledgePayload> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"darkhold_knowledge"));
    public static final StreamCodec<io.netty.buffer.ByteBuf,DarkholdKnowledgePayload> CODEC=StreamCodec.composite(
            ByteBufCodecs.VAR_INT,DarkholdKnowledgePayload::secrets,DarkholdKnowledgePayload::new);
    public DarkholdKnowledgePayload { secrets &= 255; }
    @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    public static void register(IEventBus bus){bus.addListener(DarkholdKnowledgePayload::handlers);}
    private static void handlers(RegisterPayloadHandlersEvent event){
        event.registrar("1").playToClient(TYPE,CODEC,(payload,context)->context.enqueueWork(
                ()->context.player().setData(ModAttachments.DAMNED_KNOWLEDGE,payload.secrets())));
    }
    public static void send(ServerPlayer player){
        if(player.connection.hasChannel(TYPE))PacketDistributor.sendToPlayer(player,
                new DarkholdKnowledgePayload(player.getData(ModAttachments.DAMNED_KNOWLEDGE)));
    }
}
