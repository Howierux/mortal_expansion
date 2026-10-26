package com.howierux.mortalexpansion.haunt.effects;

import com.howierux.mortalexpansion.combat.DemiTangibleDamage;
import com.howierux.mortalexpansion.combat.ModCombatItemTags;
import com.howierux.mortalexpansion.entity.ModEntityTags;
import com.howierux.mortalexpansion.machine.HauntFurnaceEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;

/** Small effects composed from existing systems. All deadlines use overworld game time. */
public final class AdditionalHauntEffects {
    public static final String TEMP_OWNER = "MortalExpansionHauntOwner";
    public static final String EXPIRES = "MortalExpansionHauntExpires";
    private AdditionalHauntEffects() {}
    private static boolean start(ServerPlayer player, String key, int ticks) {
        var state = HauntProtection.state(player);
        if (state.getLong(key) > HauntProtection.now(player)) return false;
        state.putLong(key, HauntProtection.now(player) + ticks);
        HauntProtection.state(player, state);
        return true;
    }
    public static boolean perform(ServerPlayer player, HauntEffectKind kind) {
        return switch (kind) {
            case HELPFUL_WHISPERS -> whispers(player);
            case EMPOWERMENT -> {
                if (!DemiTangibleDamage.isDemiTangibleWeapon(player.getMainHandItem())) yield false;
                boolean ok = start(player, "EmpoweredUntil", 400);
                if (ok) HauntEffects.particles(player);
                yield ok;
            }
            case UNWANTED_COMPANY -> {
                // missing function: a visible non-hostile follower; repeated footsteps work now.
                boolean ok = start(player, "CompanyUntil", 120);
                if (ok) HauntEffects.sound(player, SoundEvents.STONE_STEP, player.position().subtract(player.getLookAngle().scale(2)), 0.2F);
                yield ok;
            }
            // missing function: decoy track objects; the existing fleeting apparition works now.
            case FALSE_TRACES -> HauntEffects.shadow(player);
            case RESTLESS_VISITATION -> spawnEncounter(player, 1, 600, false) > 0;
            case INTERFERENCE -> interference(player);
            case MARKED_PRESENCE -> {
                if (hostiles(player).isEmpty()) yield false;
                boolean ok = start(player, "MarkedUntil", 400);
                if (ok) attract(player);
                yield ok;
            }
            case PURSUER -> spawnEncounter(player, 1, 2400, true) > 0;
            case VEIL_BREACH -> {
                if (player.serverLevel().getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) yield false;
                boolean ok = start(player, "BreachAt", 100);
                if (ok) {
                    var state = HauntProtection.state(player);
                    state.putString("BreachDimension", player.level().dimension().location().toString());
                    HauntProtection.state(player, state);
                    player.serverLevel().sendParticles(player, ParticleTypes.SOUL, true, player.getX(), player.getY()+1,
                            player.getZ(), 20, 1, 1, 1, 0.02);
                }
                yield ok;
            }
            default -> false;
        };
    }
    public static boolean whispers(ServerPlayer player) {
        var level = player.serverLevel(); BlockPos found = null;
        // Bounded loaded-only search, with a rare event roll rather than continuous scans.
        for (BlockPos pos : BlockPos.betweenClosed(player.blockPosition().offset(-12,-4,-12), player.blockPosition().offset(12,4,12))) {
            if (level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) == null) continue;
            var state = level.getBlockState(pos);
            if (!state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.GRAVE_MOSS) && !state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.GRAVESTONE)) continue;
            if (found == null || pos.distSqr(player.blockPosition()) < found.distSqr(player.blockPosition())) found = pos.immutable();
        }
        if (found == null) return false;
        HauntEffects.sound(player, SoundEvents.SOUL_ESCAPE.value(), Vec3.atCenterOf(found), 0.4F);
        level.sendParticles(player, ParticleTypes.END_ROD, true, found.getX()+0.5, found.getY()+0.5, found.getZ()+0.5, 8, 0.3, 0.3, 0.3, 0.01);
        return true;
    }
    public static boolean interference(ServerPlayer player) {
        for (var be : HauntEffects.loadedAnchors(player.serverLevel(), player.position(), 8)) {
            if (!(be instanceof HauntFurnaceEntity furnace) || Vec3.atCenterOf(be.getBlockPos()).distanceToSqr(player.position()) > 64
                    || furnace.getItem(0).isEmpty() || !furnace.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)
                    || player.serverLevel().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.SMELTING,
                            new net.minecraft.world.item.crafting.SingleRecipeInput(furnace.getItem(0)), player.serverLevel()).isEmpty()) continue;
            if (com.howierux.mortalexpansion.haunt.PurificationWardTracker.isChunkProtected(player.serverLevel(), new net.minecraft.world.level.ChunkPos(be.getBlockPos()))) continue;
            var data = HauntState.data(furnace);
            if (data.getLong("MortalExpansionInterferenceUntil") > HauntProtection.now(player)) continue;
            data.putLong("MortalExpansionInterferenceUntil", HauntProtection.now(player) + 60);
            furnace.setChanged(); return true;
        }
        // missing function: pause/resume integration for other future haunt-sensitive machines.
        return false;
    }
    private static java.util.List<Mob> hostiles(ServerPlayer player) {
        return player.serverLevel().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(32),
                mob -> mob.isAlive() && mob.getType().is(ModEntityTags.INTANGIBLE)
                        && mob.getType().getCategory() == MobCategory.MONSTER && mob.distanceToSqr(player) > 64);
    }
    private static void attract(ServerPlayer player) {
        for (var mob : hostiles(player)) {
            if (mob.getTarget() != null) continue; // Do not steal another player's combat target.
            var data = HauntState.data(mob);
            data.putUUID("MortalExpansionMarkedBy", player.getUUID());
            data.putLong("MortalExpansionMarkedUntil", HauntProtection.state(player).getLong("MarkedUntil"));
            mob.setTarget(player);
        }
    }
    public static int spawnEncounter(ServerPlayer player, int maximum, int lifetime, boolean pursuit) {
        var level = player.serverLevel();
        if (HauntProtection.protectedPlayer(player) || level.getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) return 0;
        if (!level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(48), mob -> HauntState.data(mob).hasUUID(TEMP_OWNER)).isEmpty()) return 0;
        int spawned = 0;
        for (int i = 0; i < Math.min(3, maximum); i++) {
            if (!HauntEffects.capAllows(level, MobCategory.MONSTER, player.position())) break;
            var ghost = com.howierux.mortalexpansion.entity.CombatEntities.GHOST.create(level);
            if (ghost == null) break;
            Vec3 at = HauntEffects.findSpawn(player, ghost, player.position(), 10, true);
            if (at == null || !HauntEffects.spawn(player, ghost, at)) continue;
            HauntState.data(ghost).putUUID(TEMP_OWNER, player.getUUID());
            HauntState.data(ghost).putLong(EXPIRES, HauntProtection.now(player) + lifetime);
            HauntState.data(ghost).putBoolean("MortalExpansionPursuit", pursuit);
            ghost.setTarget(player); spawned++;
        }
        return spawned;
    }
    public static void tick(ServerLevel level) {
        if(level.getGameTime()%20!=0)return;
        long now = level.getServer().overworld().getGameTime();
        for (var player : level.players()) {
            var state = HauntProtection.state(player);
            boolean protectedPlayer = HauntProtection.protectedPlayer(player);
            if (!player.isAlive() || player.isSpectator() || player.isCreative() || protectedPlayer) {
                state.remove("CompanyUntil"); state.remove("MarkedUntil"); state.remove("BreachAt");
                HauntProtection.state(player, state); continue;
            }
            if (state.getLong("CompanyUntil") > now) HauntEffects.sound(player, SoundEvents.STONE_STEP,
                    player.position().subtract(player.getLookAngle().scale(2)), 0.15F);
            if (state.getLong("MarkedUntil") > now) attract(player);
            long breach = state.getLong("BreachAt");
            if (breach > 0 && breach <= now) {
                state.remove("BreachAt"); HauntProtection.state(player, state);
                boolean valid = now - breach <= 100 && state.getString("BreachDimension").equals(level.dimension().location().toString());
                int spawned = valid ? spawnEncounter(player, 3, 600, false) : 0;
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(spawned > 0
                        ? "message.mortal_expansion.haunt.breach_open" : "message.mortal_expansion.haunt.breach_fade"), false);
            }
        }
        cleanup(level, now);
    }
    public static void cleanup(ServerLevel level, long now) {
        // Snapshot only managed entities: discarding a mob mutates the level entity lookup.
        var managed = java.util.stream.StreamSupport.stream(level.getAllEntities().spliterator(), false)
                .filter(entity -> entity instanceof Mob && (HauntState.data(entity).hasUUID(TEMP_OWNER)
                        || HauntState.data(entity).hasUUID("MortalExpansionMarkedBy"))).toList();
        for (var entity : managed) {
            if (!(entity instanceof Mob mob)) continue;
            var data = HauntState.data(mob);
            if (data.hasUUID("MortalExpansionMarkedBy")) {
                var marked = level.getServer().getPlayerList().getPlayer(data.getUUID("MortalExpansionMarkedBy"));
                if (now >= data.getLong("MortalExpansionMarkedUntil") || marked == null || marked.level() != level
                        || !marked.isAlive() || marked.isSpectator() || marked.isCreative()
                        || HauntProtection.protectedPlayer(marked) || HauntProtection.state(marked).getLong("MarkedUntil") <= now
                        || mob.distanceToSqr(marked) > 32*32) {
                    if (mob.getTarget() != null && mob.getTarget().getUUID().equals(data.getUUID("MortalExpansionMarkedBy"))) mob.setTarget(null);
                    data.remove("MortalExpansionMarkedBy"); data.remove("MortalExpansionMarkedUntil");
                }
            }
            if (!data.hasUUID(TEMP_OWNER)) continue;
            var owner = level.getServer().getPlayerList().getPlayer(data.getUUID(TEMP_OWNER));
            if (now >= data.getLong(EXPIRES) || owner == null || owner.level() != level || !owner.isAlive()
                    || owner.isSpectator() || owner.isCreative() || HauntProtection.protectedPlayer(owner)
                    || mob.distanceToSqr(owner) > 48*48
                    || com.howierux.mortalexpansion.haunt.PurificationWardTracker.isChunkProtected(level, mob.chunkPosition())) {
                mob.discard();
            } else if (data.getBoolean("MortalExpansionPursuit")) mob.setTarget(owner);
        }
    }
}
