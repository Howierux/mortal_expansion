package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.block.MortalGolemBaseBlock;
import com.howierux.mortalexpansion.blockentity.MortalGolemBaseBlockEntity;
import com.howierux.mortalexpansion.catalog.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;

/** World construction, separate from golem AI and rendering. */
public final class MortalGolemFormation {
    private MortalGolemFormation(){}
    public static net.minecraft.world.InteractionResult apply(net.minecraft.world.entity.player.Player player,net.minecraft.world.level.Level level,net.minecraft.world.InteractionHand hand,net.minecraft.world.phys.BlockHitResult hit){
        var pos=hit.getBlockPos();var stack=player.getItemInHand(hand);var state=level.getBlockState(pos);
        if(!stack.is(Items.ROTTEN_FLESH)||!(state.is(Blocks.BONE_BLOCK)||state.is(com.howierux.mortalexpansion.catalog.CatalogBlocks.MORTAL_GOLEM_BASE))||player.isSpectator()||!player.mayBuild()||!player.mayUseItemAt(pos,hit.getDirection(),stack))return InteractionResult.PASS;
        if(!(level instanceof ServerLevel server))return InteractionResult.SUCCESS;if(!server.mayInteract(player,pos))return InteractionResult.FAIL;
        if(state.is(Blocks.BONE_BLOCK)&&!server.setBlock(pos,com.howierux.mortalexpansion.catalog.CatalogBlocks.MORTAL_GOLEM_BASE.defaultBlockState().setValue(MortalGolemBaseBlock.AXIS,state.getValue(RotatedPillarBlock.AXIS)),3))return InteractionResult.FAIL;
        if(!(server.getBlockEntity(pos) instanceof MortalGolemBaseBlockEntity base))return InteractionResult.FAIL;int added=base.addFlesh(1);if(!player.getAbilities().instabuild)stack.shrink(added);player.displayClientMessage(Component.translatable("message.mortal_expansion.mortal_golem.flesh",base.flesh()),true);tryForm(server,pos.above());return InteractionResult.CONSUME;
    }
    public static void regeneration(ServerLevel level,net.minecraft.world.item.ItemStack potion,net.minecraft.world.phys.Vec3 impact){var effects=potion.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS,net.minecraft.world.item.alchemy.PotionContents.EMPTY).getAllEffects();boolean regen=false;for(var effect:effects)if(effect.getEffect().equals(net.minecraft.world.effect.MobEffects.REGENERATION)&&effect.getDuration()>0)regen=true;if(!regen)return;var center=BlockPos.containing(impact);for(var head:BlockPos.betweenClosed(center.offset(-4,-2,-4),center.offset(4,2,4)))if(head.getCenter().distanceToSqr(impact)<16&&Math.abs(head.getCenter().y-impact.y)<=2&&level.hasChunkAt(head)&&level.getBlockState(head).is(Blocks.CARVED_PUMPKIN))tryForm(level,head,true);}
    public static boolean tryForm(ServerLevel level,BlockPos head){return tryForm(level,head,false);}
    private static boolean tryForm(ServerLevel level,BlockPos head,boolean allowCarvedPumpkin){
        if(level.getChunkSource().getChunkNow(head.getX()>>4,head.getZ()>>4)==null
                || !(level.getBlockState(head).is(com.howierux.mortalexpansion.catalog.CatalogBlocks.WISP_O_LANTERN)
                || (allowCarvedPumpkin && level.getBlockState(head).is(Blocks.CARVED_PUMPKIN)))
                || !(level.getBlockEntity(head.below()) instanceof MortalGolemBaseBlockEntity base)
                || base.flesh()<16)return false;
        var golem=com.howierux.mortalexpansion.entity.CombatEntities.MORTAL_GOLEM.create(level);
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
