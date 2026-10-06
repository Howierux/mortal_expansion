package com.howierux.mortalexpansion.item;
import com.howierux.mortalexpansion.haunt.HauntManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

/** Normal bonemeal for saplings, with a fractional cost from their chunk. */
public final class SpectralSaplingGrowth {
    public static final int COST_HUNDREDTHS=10,GROWTH_RADIUS=16;
    private SpectralSaplingGrowth(){}
    public static InteractionResult use(UseOnContext context){
        var level=context.getLevel();var pos=context.getClickedPos();var state=level.getBlockState(pos);
        if(!state.is(net.minecraft.tags.BlockTags.SAPLINGS) || !(state.getBlock() instanceof net.minecraft.world.level.block.BonemealableBlock growable)
                || !growable.isValidBonemealTarget(level,pos,state))return InteractionResult.PASS;
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(!(level instanceof net.minecraft.server.level.ServerLevel server) || context.getPlayer()==null || !server.isAreaLoaded(pos,GROWTH_RADIUS))return InteractionResult.FAIL;
        if(HauntManager.getHundredths(server,pos)<COST_HUNDREDTHS){
            context.getPlayer().displayClientMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.sapling_haunt_low"),true);return InteractionResult.FAIL;
        }
        if(!BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL),server,pos,context.getPlayer()))return InteractionResult.FAIL;
        HauntManager.addHundredths(server,pos,-COST_HUNDREDTHS);server.levelEvent(1505,pos,15);return InteractionResult.CONSUME;
    }
}
