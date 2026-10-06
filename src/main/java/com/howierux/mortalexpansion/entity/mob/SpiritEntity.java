package com.howierux.mortalexpansion.entity.mob;

import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

/** A passive spirit merchant using vanilla merchant inventory, offers, and menu persistence. */
public final class SpiritEntity extends AbstractVillager implements HorizontalPhasingMob {
    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> EXPERTISE = net.minecraft.network.syncher.SynchedEntityData.defineId(SpiritEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> TRADE_XP = net.minecraft.network.syncher.SynchedEntityData.defineId(SpiritEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);
    private BlockPos workstation;
    private net.minecraft.resources.ResourceKey<Level> workstationDimension;
    private CompoundTag originalVillager;
    private long lastRestockDay = Long.MIN_VALUE;

    public SpiritEntity(EntityType<? extends SpiritEntity> type, Level level) {
        super(type, level);
        moveControl = new SpectralMoveControl(this);
        setPathfindingMalus(PathType.WATER, -1);
        setPathfindingMalus(PathType.WATER_BORDER, -1);
    }
    @Override protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder); builder.define(EXPERTISE, 1); builder.define(TRADE_XP, 0);
    }
    public int getExpertise() { return entityData.get(EXPERTISE); }
    @Override public int getVillagerXp() { return entityData.get(TRADE_XP); }
    // Like Villager, inherit the no-op Merchant.overrideXp: the result slot invokes it
    // after notifyTrade, whose rewardTradeXp already credits expertise XP.
    private void setTradeXp(int xp) { entityData.set(TRADE_XP, Math.max(0, xp)); }
    public static AttributeSupplier.Builder createAttributes() {
        return AbstractVillager.createMobAttributes().add(Attributes.MAX_HEALTH, 18)
                .add(Attributes.MOVEMENT_SPEED, 0.24).add(Attributes.FOLLOW_RANGE, 24);
    }
    public void bindGrave(BlockPos pos, CompoundTag villager) {
        workstation = pos.immutable(); workstationDimension = level().dimension();
        originalVillager = villager.copy(); setPersistenceRequired();
    }
    public BlockPos workstation() { return workstation; }
    public ServerLevel workstationLevel(ServerLevel current) {
        return current.getServer().getLevel(workstationDimension == null ? current.dimension() : workstationDimension);
    }
    public CompoundTag originalVillager() { return originalVillager == null ? null : originalVillager.copy(); }
    public boolean graveCreated() { return workstation != null && originalVillager != null; }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.9));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }
    @Override public void move(MoverType type, Vec3 motion) {
        Vec3 safe = SpiritMovement.prepare(this, type, motion);
        super.move(type, new Vec3(0, safe.y, 0));
        SpiritMovement.horizontal(this, safe);
    }
    @Override public boolean isInWall() { return false; }
    @Override public boolean hasLineOfSight(Entity entity) {
        return entity instanceof Player ? SpectralPhasingRules.canTrackThroughBlocks(this, entity) : super.hasLineOfSight(entity);
    }
    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!isAlive() || isBaby() || isTrading()) return super.mobInteract(player, hand);
        if(player.isSecondaryUseActive()){
            if(player instanceof net.minecraft.server.level.ServerPlayer server){
                if(!com.howierux.mortalexpansion.quest.SpiritQuests.offer(server,getDisplayName().getString()))
                    com.howierux.mortalexpansion.quest.SpiritQuests.status(server);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!level().isClientSide && !getOffers().isEmpty()) {
            setTradingPlayer(player);
            openTradingScreen(player, getDisplayName(), getExpertise());
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
    @Override protected void updateTrades() { SpiritTrades.populate(getOffers(), getExpertise()); }
    @Override protected void rewardTradeXp(MerchantOffer offer) {
        setTradeXp((int)Math.min(Integer.MAX_VALUE, (long)getVillagerXp() + offer.getXp()));
        while (getExpertise() < 5 && getVillagerXp() >= net.minecraft.world.entity.npc.VillagerData.getMaxXpPerLevel(getExpertise())) {
            int next = getExpertise() + 1; entityData.set(EXPERTISE, next);
            SpiritTrades.unlock(getOffers(), next);
        }
        if (getTradingPlayer() instanceof net.minecraft.server.level.ServerPlayer player) {
            player.sendMerchantOffers(player.containerMenu.containerId, getOffers(), getExpertise(), getVillagerXp(), true, true);
        }
        if (offer.shouldRewardExp()) level().addFreshEntity(new ExperienceOrb(level(), getX(), getY() + 0.5, getZ(), 3));
    }
    @Override public boolean showProgressBar() { return true; }
    @Override public boolean canRestock() { return true; }
    @Override public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) { return null; }
    @Override public void tick() {
        super.tick();
        if (level() instanceof ServerLevel server) {
            if (graveCreated() && !SpiritWorkstations.validate(server, this)) return;
            long day = server.getDayTime() / 24000;
            if (lastRestockDay == Long.MIN_VALUE) lastRestockDay = day;
            else if (day > lastRestockDay && !isTrading()) {
                if (!graveCreated() || workstationLevel(server) == server && server.hasChunkAt(workstation) && blockPosition().distSqr(workstation) <= 64) {
                    getOffers().forEach(offer -> { offer.updateDemand(); offer.resetUses(); }); lastRestockDay = day;
                }
            }
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("SpiritRestockDay", lastRestockDay);
        tag.putInt("SpiritExpertise", getExpertise());
        tag.putInt("SpiritTradeXp", getVillagerXp());
        tag.putInt("SpiritTradeCatalog", SpiritTrades.CATALOG_VERSION);
        if (graveCreated()) {
            tag.putLong("SpiritWorkstation", workstation.asLong());
            tag.putString("SpiritWorkstationDimension", workstationDimension.location().toString());
            tag.put("OriginalVillager", originalVillager.copy());
        }
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(EXPERTISE, net.minecraft.util.Mth.clamp(tag.getInt("SpiritExpertise"), 1, 5));
        setTradeXp(tag.getInt("SpiritTradeXp"));
        // Rebuild only obsolete catalogs; current offers retain uses, demand, and unlock order.
        if (tag.getInt("SpiritTradeCatalog") != SpiritTrades.CATALOG_VERSION) {
            offers = null;
        }
        lastRestockDay = tag.contains("SpiritRestockDay") ? tag.getLong("SpiritRestockDay") : Long.MIN_VALUE;
        workstation = tag.contains("SpiritWorkstation") ? BlockPos.of(tag.getLong("SpiritWorkstation")) : null;
        var dimension = net.minecraft.resources.ResourceLocation.tryParse(tag.getString("SpiritWorkstationDimension"));
        workstationDimension = dimension == null ? level().dimension() : net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION, dimension);
        originalVillager = tag.contains("OriginalVillager") ? tag.getCompound("OriginalVillager").copy() : null;
    }
    @Override public void die(net.minecraft.world.damagesource.DamageSource source) {
        SpiritWorkstations.release(this); super.die(source);
    }
}
