package com.howierux.mortalexpansion.item;
import com.howierux.mortalexpansion.haunt.HauntManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

/** Repeated native sapling bonemeal, paid once from the target chunk. */
public final class SpectralSaplingGrowth {
    public static final int COST_HUNDREDTHS=10,GROWTH_RADIUS=16,MAX_PULSES=64;
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
        HauntManager.addHundredths(server,pos,-COST_HUNDREDTHS);
        for(int pulse=1;pulse<MAX_PULSES;pulse++){
            var current=server.getBlockState(pos);
            if(!current.is(net.minecraft.tags.BlockTags.SAPLINGS)
                    || !(current.getBlock() instanceof net.minecraft.world.level.block.BonemealableBlock next)
                    || !next.isValidBonemealTarget(server,pos,current))break;
            if(!BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL),server,pos,context.getPlayer()))break;
        }
        // A blocked grower must not run an unbounded loop on the server thread.
        server.levelEvent(1505,pos,15);
        if(server.getBlockState(pos).is(net.minecraft.tags.BlockTags.SAPLINGS))
            context.getPlayer().displayClientMessage(net.minecraft.network.chat.Component.translatable("message.mortal_expansion.sapling_growth_blocked"),true);
        return InteractionResult.CONSUME;
    }
}
