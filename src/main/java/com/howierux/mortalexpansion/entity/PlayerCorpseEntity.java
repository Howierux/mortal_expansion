package com.howierux.mortalexpansion.entity;

import com.howierux.mortalexpansion.registry.ModAttachments;
import com.howierux.mortalexpansion.respawn.DeferredDeathItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import java.util.Optional;
import java.util.UUID;

/** Persistent, invulnerable body. Only its owner's active journey can recover it. */
public final class PlayerCorpseEntity extends ArmorStand {
    private static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(
            PlayerCorpseEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private GameType returnMode = GameType.SURVIVAL;

    public PlayerCorpseEntity(EntityType<? extends ArmorStand> type, Level level) {
        super(type, level);
        setInvulnerable(true);
        setNoGravity(true);
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER, Optional.empty());
    }
    public Optional<UUID> owner() { return entityData.get(OWNER); }
    @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }
    @Override public net.minecraft.world.InteractionResult interactAt(net.minecraft.world.entity.player.Player player,
            net.minecraft.world.phys.Vec3 at, net.minecraft.world.InteractionHand hand) {
        return net.minecraft.world.InteractionResult.PASS;
    }
    public void initialize(ServerPlayer player) {
        entityData.set(OWNER, Optional.of(player.getUUID()));
        returnMode = player.gameMode.getGameModeForPlayer();
        if (returnMode == GameType.SPECTATOR) returnMode = GameType.SURVIVAL;
        moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0);
        setCustomName(net.minecraft.network.chat.Component.literal(player.getGameProfile().getName() + "'s corpse"));
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        owner().ifPresent(id -> tag.putUUID("CorpseOwner", id));
        tag.putInt("ReturnMode", returnMode.getId());
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(OWNER, tag.hasUUID("CorpseOwner") ? Optional.of(tag.getUUID("CorpseOwner")) : Optional.empty());
        returnMode = GameType.byId(tag.getInt("ReturnMode"));
        if (returnMode == GameType.SPECTATOR) returnMode = GameType.SURVIVAL;
    }
    @Override public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel level) || owner().isEmpty()) return;
        var ownerPlayer = level.getServer().getPlayerList().getPlayer(owner().get());
        if (ownerPlayer != null && !ownerPlayer.isSpectator() && !getUUID().toString().equals(ownerPlayer.getData(ModAttachments.SPIRIT_JOURNEY_CORPSE))) {
            discard();
            return;
        }
        if (!(ownerPlayer instanceof ServerPlayer player) || player.level() != level || !player.isAlive() || !player.isSpectator()
                || !getUUID().toString().equals(player.getData(ModAttachments.SPIRIT_JOURNEY_CORPSE))
                || distanceToSqr(player) > 4) return;
        // Avoid reviving inside solid terrain; leave the body recoverable if obstructed.
        for (int dy = 0; dy <= 3; dy++) {
            var at = position().add(0, dy, 0);
            if (!level.noCollision(player, player.getBoundingBox().move(at.subtract(player.position())))) continue;
            player.teleportTo(level, at.x, at.y, at.z, getYRot(), 0);
            player.setGameMode(returnMode);
            player.setHealth(player.getMaxHealth());
            player.clearFire();
            player.fallDistance = 0;
            player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
            player.setData(ModAttachments.SPIRIT_JOURNEY_CORPSE, "");
            com.howierux.mortalexpansion.respawn.SpiritJourneyHandler.syncState(player);
            DeferredDeathItems.recoverHeld(player);
            discard();
            return;
        }
    }
}
