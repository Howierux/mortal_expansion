package com.howierux.mortalexpansion.haunt.effects;
import com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

/** Loaded-only altar benefits use the existing positive event and shared world cooldowns. */
public final class AltarHauntBenefits {
    private AltarHauntBenefits(){}
    public static boolean perform(ServerPlayer player,HauntEffectKind kind){
        if(kind!=HauntEffectKind.OFFERING_GIFT && kind!=HauntEffectKind.OFFERING_BLESSING)return false;
        var data=HauntEventData.get(player.serverLevel());long now=HauntProtection.now(player);
        var altars=HauntEffects.loadedAnchors(player.serverLevel(),player.position(),8).stream()
            .filter(be->be instanceof AltarOfPurityBlockEntity && !be.isRemoved() && Vec3.atCenterOf(be.getBlockPos()).distanceToSqr(player.position())<=64)
            .map(be->(AltarOfPurityBlockEntity)be).filter(altar->!altar.isImpure()).sorted(java.util.Comparator.comparingDouble(a->Vec3.atCenterOf(a.getBlockPos()).distanceToSqr(player.position()))).toList();
        for(var altar:altars){long chunk=new ChunkPos(altar.getBlockPos()).toLong();if(!data.chunkReady(chunk,now))continue;
            boolean success=kind==HauntEffectKind.OFFERING_GIFT?altar.addOfferingGift():altar.blessNextOffering();
            if(!success)continue;data.coolChunk(chunk,now);HauntEffects.particles(player);return true;
        }return false;
    }
}
