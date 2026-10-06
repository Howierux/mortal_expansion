package com.howierux.mortalexpansion.haunt.effects;

import com.howierux.mortalexpansion.blockentity.HauntFurnaceBlockEntity;
import com.howierux.mortalexpansion.haunt.PurificationWardTracker;
import com.howierux.mortalexpansion.mixin.HauntFurnaceAccessor;
import com.howierux.mortalexpansion.network.HauntEffectPayloads;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.spiritwell.SpiritwellProtection;
import com.howierux.mortalexpansion.blockentity.SpiritwellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Server-authoritative operations. False means ineligible/blocked, without consuming an event. */
public final class HauntEffects {
    public static final String EVENT_MOB = "MortalExpansionAmbient";
    private static final java.util.Map<ServerLevel, SpawnReservations> RESERVATIONS = new java.util.WeakHashMap<>();
    private static final class SpawnReservations {
        long tick;
        final java.util.EnumMap<MobCategory, Integer> counts = new java.util.EnumMap<>(MobCategory.class);
    }
    private static SpawnReservations reservations(ServerLevel level) {
        var current = RESERVATIONS.computeIfAbsent(level, unused -> new SpawnReservations());
        if (current.tick != level.getGameTime()) {
            current.tick = level.getGameTime();
            current.counts.clear();
        }
        return current;
    }
    private HauntEffects() {}
    public static boolean perform(ServerPlayer player, HauntEffectKind kind) {
        if (!kind.implemented() || (kind.negative && HauntProtection.protectedPlayer(player))) return false;
        return switch (kind) {
            case HIT -> { sound(player, SoundEvents.STONE_STEP, player.position().subtract(horizontal(player).scale(2)), 0.15F); yield true; }
            case SHADOW -> shadow(player);
            case SLOW -> {
                boolean ok = !player.isInWater() && !player.onClimbable()
                        && HauntProtection.apply(player, MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true);
                if (ok) sound(player, SoundEvents.SOUL_ESCAPE.value(), player.position(), 0.2F);
                yield ok;
            }
            case SHOVE -> shove(player);
            case SHUFFLE -> shuffle(player);
            case GOOD_POTION -> potion(player, false);
            case BAD_POTION -> potion(player, true);
            case SPIRIT -> spawnSpirit(player);
            case GHOST -> {
                if (!spawnGhostNear(player)) HauntEventData.get(player.serverLevel()).deferGhost();
                yield true;
            }
            case FURNACE -> finishNearbyFurnace(player);
            case PLANKS -> convert(player, true);
            case COBBLE -> convert(player, false);
            case HELPFUL_WHISPERS, EMPOWERMENT, UNWANTED_COMPANY, FALSE_TRACES,
                    RESTLESS_VISITATION, INTERFERENCE, MARKED_PRESENCE, PURSUER, VEIL_BREACH
                    -> AdditionalHauntEffects.perform(player, kind);
            // missing function: dependency-gated benefits are listed in HauntEffectKind.implemented().
            case SECOND_SIGHT -> com.howierux.mortalexpansion.discovery.SpectralDiscovery.startTracks(player);
            case SPECTRAL_INSIGHT -> com.howierux.mortalexpansion.guide.SpectralInsight.perform(player);
            case SPIRIT_COMMISSION, HELPFUL_VISITATION,
                    SPIRIT_RECOGNITION, VEIL_REVELATION -> false;
        };
    }
    private static Vec3 horizontal(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 v = new Vec3(look.x, 0, look.z);
        return v.lengthSqr() < 0.001 ? new Vec3(0, 0, 1) : v.normalize();
    }
    public static void sound(ServerPlayer player, SoundEvent sound, Vec3 pos, float volume) {
        player.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.AMBIENT,
                pos.x, pos.y, pos.z, volume, 0.9F, player.getRandom().nextLong()));
    }
    public static void particles(ServerPlayer player) {
        player.serverLevel().sendParticles(player, ParticleTypes.HAPPY_VILLAGER, false,
                player.getX(), player.getY() + 1, player.getZ(), 10, 0.5, 0.5, 0.5, 0.02);
    }
    private static boolean potion(ServerPlayer player, boolean negative) {
        List<Holder<MobEffect>> choices = new ArrayList<>(negative
                ? List.of(MobEffects.WEAKNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SLOWDOWN)
                : List.of(MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED));
        choices.removeIf(player::hasEffect);
        if (choices.isEmpty()) return false;
        return HauntProtection.apply(player, choices.get(player.getRandom().nextInt(choices.size())), 100, 0, negative);
    }
    public static boolean shadow(ServerPlayer player) {
        Vec3 side = horizontal(player).yRot(player.getRandom().nextBoolean() ? 0.85F : -0.85F);
        Vec3 center = player.position().add(side.scale(6));
        for (int dy = -1; dy <= 1; dy++) {
            Vec3 at = new Vec3(center.x, Math.floor(player.getY()) + dy, center.z);
            if (!safeStanding(player.serverLevel(), at, 0.6, 1.95, false)) continue;
            if (!HauntEffectPayloads.shadow(player, at)) return false;
            HauntProtection.repairGarb(player);
            return true;
        }
        return false;
    }
    public static boolean shove(ServerPlayer player) {
        if (!player.onGround() || player.isInWater() || player.onClimbable() || player.isPassenger()) return false;
        Vec3 offset = horizontal(player).scale(-1);
        for (int i = 1; i <= 4; i++) {
            Vec3 at = player.position().add(offset.scale(i / 4.0));
            if (!safeStanding(player.serverLevel(), at, player.getBbWidth(), player.getBbHeight(), false)) return false;
        }
        Vec3 destination = player.position().add(offset);
        player.connection.teleport(destination.x, destination.y, destination.z, player.getYRot(), player.getXRot());
        player.setDeltaMovement(Vec3.ZERO);
        player.hurtMarked = true;
        particles(player);
        return true;
    }
    public static boolean shuffle(ServerPlayer player) {
        if (player.containerMenu != player.inventoryMenu) return false;
        List<Integer> occupied = new ArrayList<>();
        for (int i = 9; i < 36; i++) if (!player.getInventory().getItem(i).isEmpty()) occupied.add(i);
        if (occupied.size() < 2) return false;
        int a = occupied.remove(player.getRandom().nextInt(occupied.size()));
        int b = occupied.get(player.getRandom().nextInt(occupied.size()));
        ItemStack first = player.getInventory().getItem(a), second = player.getInventory().getItem(b);
        player.getInventory().setItem(a, second);
        player.getInventory().setItem(b, first);
        player.inventoryMenu.broadcastChanges();
        particles(player);
        return true;
    }
    public static boolean convert(ServerPlayer player, boolean planks) {
        if (player.containerMenu != player.inventoryMenu) return false;
        var data = HauntEventData.get(player.serverLevel());
        long now = HauntProtection.now(player), chunk = new ChunkPos(player.blockPosition()).toLong();
        if (!data.chunkReady(chunk, now)) return false;
        for (int source = 0; source < 36; source++) {
            ItemStack input = player.getInventory().getItem(source);
            boolean matches = planks ? input.is(ItemTags.PLANKS)
                    && BuiltInRegistries.ITEM.getKey(input.getItem()).getNamespace().equals("minecraft")
                    : input.is(Items.COBBLESTONE);
            if (!matches) continue;
            Item resultItem = (planks ? ModBlocks.SPIRITWOOD_PLANKS.get() : ModBlocks.GRAVESTONE.get()).asItem();
            ItemStack result = new ItemStack(resultItem);
            result.applyComponents(input.getComponentsPatch());
            int destination = -1;
            for (int i = 0; i < 36; i++) {
                ItemStack existing = player.getInventory().getItem(i);
                if (i == source && input.getCount() == 1) { destination = i; break; }
                if (existing.isEmpty() || (ItemStack.isSameItemSameComponents(existing, result)
                        && existing.getCount() < existing.getMaxStackSize())) { destination = i; break; }
            }
            if (destination < 0) continue;
            input.shrink(1);
            ItemStack current = player.getInventory().getItem(destination);
            if (current.isEmpty()) player.getInventory().setItem(destination, result);
            else current.grow(1);
            player.inventoryMenu.broadcastChanges();
            data.coolChunk(chunk, now);
            particles(player);
            return true;
        }
        return false;
    }
    public static List<BlockEntity> loadedAnchors(ServerLevel level, Vec3 center, int radius) {
        List<BlockEntity> entries = new ArrayList<>();
        int minX = Math.floorDiv((int) Math.floor(center.x - radius), 16);
        int maxX = Math.floorDiv((int) Math.floor(center.x + radius), 16);
        int minZ = Math.floorDiv((int) Math.floor(center.z - radius), 16);
        int maxZ = Math.floorDiv((int) Math.floor(center.z + radius), 16);
        for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
            var chunk = level.getChunkSource().getChunkNow(x, z);
            if (chunk != null) entries.addAll(chunk.getBlockEntities().values());
        }
        return entries;
    }
    public static List<SpiritwellBlockEntity> wellsNear(ServerPlayer player) {
        return loadedAnchors(player.serverLevel(), player.position(), 100).stream()
                .filter(be -> be instanceof SpiritwellBlockEntity && !be.isRemoved())
                .map(be -> (SpiritwellBlockEntity) be)
                .filter(well -> wellPosition(well).distanceToSqr(player.position()) <= 10000)
                .sorted(Comparator.comparingDouble(well -> wellPosition(well).distanceToSqr(player.position())))
                .toList();
    }
    public static SpiritwellBlockEntity nearestWell(ServerPlayer player) {
        var wells = wellsNear(player);
        return wells.isEmpty() ? null : wells.get(0);
    }
    public static boolean spawnGhostNear(ServerPlayer player) {
        for (var well : wellsNear(player)) if (spawnGhost(player, well)) return true;
        return false;
    }
    public static Vec3 wellPosition(SpiritwellBlockEntity well) {
        BlockPos pos = well.getBlockPos();
        return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
    public static boolean safeStanding(ServerLevel level, Vec3 at, double width, double height, boolean hostile) {
        AABB body = new AABB(at.x - width / 2, at.y, at.z - width / 2, at.x + width / 2, at.y + height, at.z + width / 2);
        AABB inside = body.deflate(0.0001);
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(inside.minX, inside.minY, inside.minZ),
                BlockPos.containing(inside.maxX, inside.maxY, inside.maxZ))) {
            if (level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) == null
                    || !level.getFluidState(pos).isEmpty() || level.getBlockState(pos).is(net.minecraft.tags.BlockTags.FIRE)) return false;
        }
        if (!level.getWorldBorder().isWithinBounds(body) || !level.noCollision(body)) return false;
        for (BlockPos below : BlockPos.betweenClosed(
                BlockPos.containing(body.minX + 0.001, at.y - 0.01, body.minZ + 0.001),
                BlockPos.containing(body.maxX - 0.001, at.y - 0.01, body.maxZ - 0.001))) {
            var state = level.getBlockState(below);
            if (!state.isFaceSturdy(level, below, Direction.UP) || !state.getFluidState().isEmpty()
                    || state.is(net.minecraft.world.level.block.Blocks.MAGMA_BLOCK)
                    || state.is(net.minecraft.world.level.block.Blocks.CAMPFIRE)
                    || state.is(net.minecraft.world.level.block.Blocks.SOUL_CAMPFIRE)) return false;
        }
        BlockPos feet = BlockPos.containing(at);
        return !hostile || (!PurificationWardTracker.isChunkProtected(level, new ChunkPos(feet))
                && !SpiritwellProtection.isProtected(level, feet));
    }
    public static boolean capAllows(ServerLevel level, MobCategory category, Vec3 center) {
        var state = level.getChunkSource().getLastSpawnState();
        if (state != null) {
            int limit = Math.max(1, category.getMaxInstancesPerChunk() * state.getSpawnableChunkCount() / 289);
            // The vanilla snapshot predates event spawns. Reserve successes
            // so several players cannot exceed its cap within the same tick.
            if (state.getMobCategoryCounts().getInt(category)
                    + reservations(level).counts.getOrDefault(category, 0) >= limit) return false;
        }
        return level.getEntitiesOfClass(Mob.class, new AABB(BlockPos.containing(center)).inflate(32),
                mob -> mob.getType().getCategory() == category).size() < category.getMaxInstancesPerChunk();
    }
    public static Vec3 findSpawn(ServerPlayer player, Mob mob, Vec3 center, int radius, boolean hostile) {
        for (int attempt = 0; attempt < 32; attempt++) {
            int dx = player.getRandom().nextInt(radius * 2 + 1) - radius;
            int dz = player.getRandom().nextInt(radius * 2 + 1) - radius;
            for (int dy = -3; dy <= 1; dy++) {
                Vec3 at = new Vec3(Math.floor(center.x) + dx + 0.5, Math.floor(center.y) + dy, Math.floor(center.z) + dz + 0.5);
                if (at.distanceToSqr(center) > radius * radius || at.distanceToSqr(player.position()) < 2.25) continue;
                if (safeStanding(player.serverLevel(), at, mob.getBbWidth(), mob.getBbHeight(), hostile)
                        && player.serverLevel().noCollision(mob, mob.getBoundingBox().move(at.subtract(mob.position())))) return at;
            }
        }
        return null;
    }
    public static boolean spawn(ServerPlayer player, Mob mob, Vec3 at) {
        ServerLevel level = player.serverLevel();
        mob.moveTo(at.x, at.y, at.z, player.getRandom().nextFloat() * 360, 0);
        EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.EVENT, null);
        if (mob.isSpawnCancelled()) return false;
        mob.getPersistentData().putBoolean(EVENT_MOB, true);
        boolean added = level.addFreshEntity(mob);
        if (added) reservations(level).counts.merge(mob.getType().getCategory(), 1, Integer::sum);
        return added;
    }
    public static boolean spawnSpirit(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (!capAllows(level, MobCategory.CREATURE, player.position())) return false;
        if (!level.getEntitiesOfClass(com.howierux.mortalexpansion.entity.mob.SpiritEntity.class,
                player.getBoundingBox().inflate(32), spirit -> spirit.getPersistentData().getBoolean(EVENT_MOB)).isEmpty()) return false;
        var spirit = ModEntities.SPIRIT.get().create(level);
        if (spirit == null) return false;
        Vec3 at = findSpawn(player, spirit, player.position(), 5, false);
        if (at == null || !spawn(player, spirit, at)) return false;
        particles(player);
        HauntProtection.repairGarb(player);
        return true;
    }
    public static boolean spawnGhost(ServerPlayer player, SpiritwellBlockEntity well) {
        if (HauntProtection.protectedPlayer(player) || well.isRemoved() || well.getLevel() != player.level()
                || wellPosition(well).distanceToSqr(player.position()) > 10000) return false;
        ServerLevel level = player.serverLevel();
        Vec3 center = wellPosition(well);
        if (!capAllows(level, MobCategory.MONSTER, center) || !level.getEntitiesOfClass(
                com.howierux.mortalexpansion.entity.mob.GhostEntity.class, new AABB(well.getBlockPos()).inflate(16),
                ghost -> ghost.getPersistentData().getBoolean(EVENT_MOB)).isEmpty()) return false;
        var ghost = ModEntities.GHOST.get().create(level);
        if (ghost == null) return false;
        Vec3 at = findSpawn(player, ghost, center, 6, true);
        if (at == null) return false;
        // Warn before spawning the real hostile entity.
        sound(player, SoundEvents.SOUL_ESCAPE.value(), center, 0.7F);
        level.sendParticles(player, ParticleTypes.SOUL, true, center.x, center.y, center.z, 16, 0.7, 0.7, 0.7, 0.03);
        if (!spawn(player, ghost, at)) return false;
        ghost.setTarget(player);
        HauntProtection.repairGarb(player);
        return true;
    }
    public static boolean finishNearbyFurnace(ServerPlayer player) {
        for (BlockEntity be : loadedAnchors(player.serverLevel(), player.position(), 8)) {
            if (be instanceof AbstractFurnaceBlockEntity furnace
                    && Vec3.atCenterOf(be.getBlockPos()).distanceToSqr(player.position()) <= 64
                    && HauntEventData.get(player.serverLevel()).chunkReady(new ChunkPos(be.getBlockPos()).toLong(), HauntProtection.now(player))
                    && finishFurnace(player.serverLevel(), furnace)) {
                HauntEventData.get(player.serverLevel()).coolChunk(new ChunkPos(be.getBlockPos()).toLong(), HauntProtection.now(player));
                particles(player); return true;
            }
        }
        return false;
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean finishFurnace(ServerLevel level, AbstractFurnaceBlockEntity furnace) {
        if (furnace.getPersistentData().getLong("MortalExpansionInterferenceUntil") > level.getServer().overworld().getGameTime()) return false;
        HauntFurnaceAccessor access = (HauntFurnaceAccessor) furnace;
        if (access.mortalExpansion$litTime() <= 1 || furnace.getItem(0).isEmpty()) return false;
        SingleRecipeInput input = new SingleRecipeInput(furnace.getItem(0));
        var recipe = level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>) access.mortalExpansion$recipeType(), input, level);
        if (recipe.isEmpty()) return false;
        ItemStack result = recipe.get().value().assemble(input, level.registryAccess()), output = furnace.getItem(2);
        int max = Math.min(furnace.getMaxStackSize(), result.getMaxStackSize());
        if (result.isEmpty() || (!output.isEmpty() && !ItemStack.isSameItemSameComponents(output, result))
                || output.getCount() + result.getCount() > max) return false;
        int previous = access.mortalExpansion$progress(), before = furnace.getItem(0).getCount();
        access.mortalExpansion$progress(Math.max(0, access.mortalExpansion$total() - 1));
        if (furnace instanceof HauntFurnaceBlockEntity haunted)
            HauntFurnaceBlockEntity.serverTick(level, furnace.getBlockPos(), furnace.getBlockState(), haunted);
        else AbstractFurnaceBlockEntity.serverTick(level, furnace.getBlockPos(), furnace.getBlockState(), furnace);
        boolean cooked = furnace.getItem(0).getCount() == before - 1;
        if (!cooked) access.mortalExpansion$progress(previous);
        furnace.setChanged();
        return cooked;
    }
}
