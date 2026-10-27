package com.howierux.mortalexpansion.network;

import com.howierux.mortalexpansion.guide.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import java.util.*;

/** Private read-only snapshots. There is deliberately no inventory mutation/menu protocol. */
public final class EnvyPayloads {
    private EnvyPayloads() {}
    public record Request(String target) implements CustomPacketPayload {
        public static final Type<Request> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("mortal_expansion","envy_request"));
        public static final StreamCodec<io.netty.buffer.ByteBuf,Request> CODEC=StreamCodec.composite(ByteBufCodecs.stringUtf8(36),Request::target,Request::new);
        public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public record View(String target,String name,List<ItemStack> items) implements CustomPacketPayload {
        public static final Type<View> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("mortal_expansion","envy_view"));
        public static final StreamCodec<RegistryFriendlyByteBuf,View> CODEC=StreamCodec.composite(
                ByteBufCodecs.stringUtf8(36),View::target,ByteBufCodecs.stringUtf8(64),View::name,
                ByteBufCodecs.collection(ArrayList::new,ItemStack.OPTIONAL_STREAM_CODEC,41),View::items,View::new);
        public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    public static List<ItemStack> snapshot(ServerPlayer target){
        var items=new ArrayList<ItemStack>(41);
        for(int i=0;i<41;i++)items.add(target.getInventory().getItem(i).copy());
        return List.copyOf(items);
    }
    public static void inspect(ServerPlayer viewer,String targetId){
        if(!DamnedAbilities.active(viewer,DamnedSecret.ENVY))return;
        UUID id;try{id=UUID.fromString(targetId);}catch(IllegalArgumentException ex){return;}
        var state=((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)viewer).getAttachedOrCreate(com.howierux.mortalexpansion.content.RemainingRuntime.ABILITIES);long now=viewer.level().getGameTime();
        if(state.contains("envy_time") && now>=state.getLong("envy_time") && now-state.getLong("envy_time")<10)return;
        state.putLong("envy_time",now);
        var target=viewer.getServer().getPlayerList().getPlayer(id);
        if(target==null || target==viewer){DamnedAbilities.hint(viewer,"envy_gone");return;}
        if(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(viewer,View.TYPE)){
            net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(viewer,new View(targetId,target.getGameProfile().getName(),snapshot(target)));
            DamnedAbilities.hint(viewer,"envy");
        }
    }
}
