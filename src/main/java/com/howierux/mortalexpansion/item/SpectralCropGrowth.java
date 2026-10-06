package com.howierux.mortalexpansion.item;

import com.howierux.mortalexpansion.haunt.HauntManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

/** Bounded native bonemeal applications, paid from the clicked crop's chunk. */
public final class SpectralCropGrowth {
    public static final int COST_PER_PULSE=1,MAX_PULSES=16;
    private SpectralCropGrowth(){}
    public static boolean eligible(Level level,BlockPos pos,BlockState state){
        if(!state.is(net.minecraft.tags.BlockTags.CROPS) || !(state.getBlock() instanceof BonemealableBlock growable))return false;
        if(state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state))return false;
        if(state.getBlock() instanceof StemBlock && state.getValue(StemBlock.AGE)>=7)return false;
        return growable.isValidBonemealTarget(level,pos,state);
    }
    public static InteractionResult use(UseOnContext context){
        var level=context.getLevel();var pos=context.getClickedPos();
        if(!eligible(level,pos,level.getBlockState(pos)))return InteractionResult.PASS;
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(!(level instanceof ServerLevel server) || context.getPlayer()==null || !server.isAreaLoaded(pos,2))return InteractionResult.FAIL;
        int pulses=0;
        for(int i=0;i<MAX_PULSES;i++){
            if(!eligible(server,pos,server.getBlockState(pos)) || HauntManager.getValue(server,pos)<COST_PER_PULSE)break;
            if(!BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL),server,pos,context.getPlayer()))break;
            HauntManager.addPoints(server,pos,-COST_PER_PULSE);pulses++;
            server.levelEvent(1505,pos,15);
        }
        if(eligible(server,pos,server.getBlockState(pos)) && HauntManager.getValue(server,pos)<COST_PER_PULSE)
            context.getPlayer().displayClientMessage(Component.translatable("message.mortal_expansion.crop_haunt_empty"),true);
        return pulses>0?InteractionResult.CONSUME:InteractionResult.FAIL;
    }
}
