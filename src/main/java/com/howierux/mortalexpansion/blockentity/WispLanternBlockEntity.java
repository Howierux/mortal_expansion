package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.haunt.WispLanternConversion;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.entity.mob.GhostEntity;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;

public final class WispLanternBlockEntity extends BlockEntity {
    public static final int SPAWN_INTERVAL=20*60*10, NEARBY_GHOST_LIMIT=4;
    private int spawnDelay=SPAWN_INTERVAL;
    public WispLanternBlockEntity(BlockPos pos, BlockState state) {super(ModBlockEntities.WISP_O_LANTERN.get(),pos,state);}
    public int spawnDelay() {return spawnDelay;}
    public static void serverTick(Level level, BlockPos pos, BlockState state, WispLanternBlockEntity lantern) {
        if(!(level instanceof ServerLevel server))return;
        if(com.howierux.mortalexpansion.haunt.MortalGolemFormation.tryForm(server,pos))return;
        if(!WispLanternConversion.nighttime(server))return;
        if(--lantern.spawnDelay<=0) {
            lantern.spawnDelay=SPAWN_INTERVAL;
            lantern.trySpawn(server);
        }
        lantern.setChanged();
    }
    public boolean trySpawn(ServerLevel level) {
        if(!WispLanternConversion.nighttime(level) || level.getDifficulty()==Difficulty.PEACEFUL
                || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                || level.getChunkSource().getChunkNow(worldPosition.getX()>>4,worldPosition.getZ()>>4)==null
                || !level.getBlockState(worldPosition).is(ModBlocks.WISP_O_LANTERN.get()))return false;
        boolean nearby=level.players().stream().anyMatch(p->p.isAlive() && !p.isSpectator()
                && p.distanceToSqr(worldPosition.getCenter())<=32*32);
        if(!nearby || level.getEntitiesOfClass(GhostEntity.class,new AABB(worldPosition).inflate(16),
                ghost->ghost.isAlive()).size()>=NEARBY_GHOST_LIMIT)return false;
        var ghost=ModEntities.GHOST.get().create(level);
        if(ghost==null)return false;
        for(int attempt=0;attempt<16;attempt++) {
            var at=worldPosition.offset(level.random.nextInt(9)-4,1+level.random.nextInt(2),level.random.nextInt(9)-4);
            if(level.getChunkSource().getChunkNow(at.getX()>>4,at.getZ()>>4)==null
                    || !level.getWorldBorder().isWithinBounds(at) || !level.isEmptyBlock(at) || !level.isEmptyBlock(at.above()))continue;
            ghost.moveTo(at.getX()+0.5,at.getY(),at.getZ()+0.5,level.random.nextFloat()*360,0);
            if(!level.noCollision(ghost))continue;
            EventHooks.finalizeMobSpawn(ghost,level,level.getCurrentDifficultyAt(at),MobSpawnType.SPAWNER,null);
            return !ghost.isSpawnCancelled() && level.addFreshEntity(ghost);
        }
        return false;
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag,registries);tag.putInt("SpawnDelay",spawnDelay);
    }
    @Override public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag,registries);
        spawnDelay=tag.contains("SpawnDelay")?Math.clamp(tag.getInt("SpawnDelay"),1,SPAWN_INTERVAL):SPAWN_INTERVAL;
    }
}
