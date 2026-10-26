package com.howierux.mortalexpansion.quest;

import com.howierux.mortalexpansion.entity.mob.SpiritEntity;
import com.howierux.mortalexpansion.network.SpiritInteractionPayloads;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import java.util.*;

/** Short-lived, player-bound interaction authorization. No persistent NPC dependency. */
public final class SpiritInteraction {
    private record Session(UUID spirit,ResourceKey<Level> dimension,String token,int mode,String offer,long expires){}
    private static final Map<ServerPlayer,Session> SESSIONS=new WeakHashMap<>();
    private SpiritInteraction(){}
    public static boolean available(ServerPlayer p,SpiritEntity spirit){
        return p.isAlive() && p.gameMode.getGameModeForPlayer()!=net.minecraft.world.level.GameType.SPECTATOR && spirit.isAlive() && !spirit.isBaby() && !spirit.isTrading()
            && p.level()==spirit.level() && p.distanceToSqr(spirit)<=Math.pow(p.entityInteractionRange()+1,2);
    }
    public static String open(ServerPlayer p,SpiritEntity spirit){
        return show(p,spirit,0,"",Component.translatable("screen.mortal_expansion.spirit.prompt"));
    }
    private static String show(ServerPlayer p,SpiritEntity spirit,int mode,String offer,Component body){
        if(!available(p,spirit))return "";
        String token=UUID.randomUUID().toString();SESSIONS.put(p,new Session(spirit.getUUID(),p.level().dimension(),token,mode,offer,p.getServer().overworld().getGameTime()+1200));
        SpiritInteractionPayloads.open(p,token,spirit.getDisplayName(),body,mode);return token;
    }
    public static String token(ServerPlayer p){var session=SESSIONS.get(p);return session==null?"":session.token();}
    public static boolean choose(ServerPlayer p,String token,int action){
        var session=SESSIONS.get(p);
        if(session==null || !session.token().equals(token))return false;
        SESSIONS.remove(p); // Consume before dispatch; replay/double-clicks cannot repeat an action.
        if(action==5)return true;
        if(session.expires()<p.getServer().overworld().getGameTime() || !session.dimension().equals(p.level().dimension())
            || !(p.serverLevel().getEntity(session.spirit()) instanceof SpiritEntity spirit) || !available(p,spirit))return false;
        if(action==0 && session.mode()==0){
            if(spirit.getOffers().isEmpty())return false;
            spirit.setTradingPlayer(p);spirit.openTradingScreen(p,spirit.getDisplayName(),spirit.getExpertise());return true;
        }
        if(action==1 && session.mode()==0){
            var state=SpiritQuests.state(p);
            if(state.active().isPresent()){
                show(p,spirit,2,"",SpiritQuests.describe(state.active().get()).copy().append("\n")
                    .append(Component.translatable("screen.mortal_expansion.spirit.progress",state.active().get().progress(),state.active().get().required())));return true;
            }
            SpiritQuests.offer(p,spirit.getDisplayName().getString(),false);state=SpiritQuests.state(p);
            var offer=state.offer().filter(q->q.expires()>p.getServer().overworld().getGameTime()).orElse(null);
            if(offer!=null)show(p,spirit,1,offer.id(),SpiritQuests.describe(offer));
            else show(p,spirit,2,"",Component.translatable("screen.mortal_expansion.spirit.rest"));return true;
        }
        if((action==2 || action==3) && session.mode()==1){
            boolean ok=action==2?SpiritQuests.accept(p,session.offer()):SpiritQuests.decline(p,session.offer());
            if(ok)open(p,spirit);return ok;
        }
        if(action==4 && session.mode()!=0){open(p,spirit);return true;}
        return false;
    }
}
