package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.block.MortalGolemBaseBlock;
import com.howierux.mortalexpansion.blockentity.MortalGolemBaseBlockEntity;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** World construction, separate from golem AI and rendering. */
public final class MortalGolemFormation {
    private MortalGolemFormation(){}
    @SubscribeEvent public static void applyFlesh(PlayerInteractEvent.RightClickBlock event){
        var level=event.getLevel();var pos=event.getPos();var state=level.getBlockState(pos);
        var player=event.getEntity();var stack=player.getItemInHand(event.getHand());
        if(!stack.is(Items.ROTTEN_FLESH) || !(state.is(Blocks.BONE_BLOCK) || state.is(ModBlocks.MORTAL_GOLEM_BASE.get()))
                || !player.mayBuild() || !player.mayUseItemAt(pos,event.getFace(),stack))return;
        event.setCanceled(true);event.setCancellationResult(InteractionResult.SUCCESS);
        if(!(level instanceof ServerLevel server))return;
        if(state.is(Blocks.BONE_BLOCK)) {
            var frame=ModBlocks.MORTAL_GOLEM_BASE.get().defaultBlockState()
                    .setValue(MortalGolemBaseBlock.AXIS,state.getValue(RotatedPillarBlock.AXIS));
            if(!level.setBlock(pos,frame,3))return;
        }
        if(!(level.getBlockEntity(pos) instanceof MortalGolemBaseBlockEntity base))return;
        int added=base.addFlesh(1);
        if(!player.getAbilities().instabuild)stack.shrink(added);
        player.displayClientMessage(Component.translatable("message.mortal_expansion.mortal_golem.flesh",base.flesh()),true);
        tryForm(server,pos.above());
    }
    @SubscribeEvent public static void protectFromUnrelatedMobs(
            net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent event) {
        if (event.getNewAboutToBeSetTarget() instanceof com.howierux.mortalexpansion.entity.mob.MortalGolemEntity
                && (event.getEntity() instanceof net.minecraft.world.entity.monster.Spider
                || event.getEntity() instanceof net.minecraft.world.entity.monster.Creeper
                || event.getEntity() instanceof net.minecraft.world.entity.monster.EnderMan)) {
            event.setNewAboutToBeSetTarget(null);
        }
    }
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.LOWEST)
    public static void regenerationSplash(net.neoforged.neoforge.event.entity.ProjectileImpactEvent event){
        if(event.isCanceled() || !(event.getProjectile() instanceof net.minecraft.world.entity.projectile.ThrownPotion potion)
                || !(potion.level() instanceof ServerLevel level) || !potion.getItem().is(Items.SPLASH_POTION))return;
        var contents=potion.getItem().getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS,
                net.minecraft.world.item.alchemy.PotionContents.EMPTY);
        boolean regeneration=false;
        for(var effect:contents.getAllEffects()) {
            if(effect.getEffect().equals(net.minecraft.world.effect.MobEffects.REGENERATION) && effect.getDuration()>0)regeneration=true;
        }
        if(!regeneration)return;
        var impact=event.getRayTraceResult().getLocation();var center=BlockPos.containing(impact);
        // Match splash reach: four blocks radially, with a two-block vertical band.
        for(var head:BlockPos.betweenClosed(center.offset(-4,-2,-4),center.offset(4,2,4))){
            if(head.getCenter().distanceToSqr(impact)<16 && Math.abs(head.getCenter().y-impact.y)<=2
                    && level.getChunkSource().getChunkNow(head.getX()>>4,head.getZ()>>4)!=null
                    && level.getBlockState(head).is(Blocks.CARVED_PUMPKIN))tryForm(level,head,true);
        }
    }
    public static boolean tryForm(ServerLevel level,BlockPos head){return tryForm(level,head,false);}
    private static boolean tryForm(ServerLevel level,BlockPos head,boolean allowCarvedPumpkin){
        if(level.getChunkSource().getChunkNow(head.getX()>>4,head.getZ()>>4)==null
                || !(level.getBlockState(head).is(ModBlocks.WISP_O_LANTERN.get())
                || (allowCarvedPumpkin && level.getBlockState(head).is(Blocks.CARVED_PUMPKIN)))
                || !(level.getBlockEntity(head.below()) instanceof MortalGolemBaseBlockEntity base)
                || base.flesh()<16)return false;
        var golem=ModEntities.MORTAL_GOLEM.get().create(level);
        if(golem==null)return false;
        var bottom=head.below();golem.setFlesh(base.flesh());
        golem.moveTo(bottom.getX()+0.5,bottom.getY(),bottom.getZ()+0.5,0,0);
        AABB box=golem.getBoundingBox();
        if(box.maxY>level.getMaxBuildHeight() || !level.getWorldBorder().isWithinBounds(box))return false;
        for(var at:BlockPos.betweenClosed(BlockPos.containing(box.minX,box.minY,box.minZ),
                BlockPos.containing(box.maxX-0.001,box.maxY-0.001,box.maxZ-0.001))){
            if(level.getChunkSource().getChunkNow(at.getX()>>4,at.getZ()>>4)==null)return false;
            if(at.equals(head) || at.equals(bottom))continue;
            var state=level.getBlockState(at);
            if(!state.getCollisionShape(level,at).isEmpty() || !state.getFluidState().isEmpty())return false;
        }
        if(!level.getEntities(golem,box,e->e.isAlive() && e.isPickable()).isEmpty())return false;
        // Register first: a canceled spawn must leave both construction blocks intact.
        if(!level.addFreshEntity(golem))return false;
        level.setBlock(bottom,Blocks.AIR.defaultBlockState(),3);
        level.setBlock(head,Blocks.AIR.defaultBlockState(),3);
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL,
                bottom.getX()+0.5,bottom.getY()+1,bottom.getZ()+0.5,12,0.5,0.8,0.5,0.02);
        return true;
    }
}
