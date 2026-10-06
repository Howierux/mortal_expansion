package com.howierux.mortalexpansion.item;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import com.howierux.mortalexpansion.registry.ModBlocks;
import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.block.HauntedFarmlandBlock;

/** Preserve vanilla till predicates, events, sounds and tool wear, then mark a successful farmland result. */
public final class HauntedHoeItem extends HoeItem {
    public HauntedHoeItem(Tier tier,Properties properties){super(tier,properties);}
    @Override public InteractionResult useOn(UseOnContext context){
        var result=super.useOn(context);var level=context.getLevel();var pos=context.getClickedPos();
        if(result.consumesAction() && level instanceof net.minecraft.server.level.ServerLevel server
                && level.getBlockState(pos).is(Blocks.FARMLAND)){
            int moisture=level.getBlockState(pos).getValue(FarmBlock.MOISTURE);
            if(HauntedFarmlandBlock.keepsMoist(HauntManager.getTier(server,pos)))moisture=7;
            level.setBlock(pos,ModBlocks.HAUNTED_FARMLAND.get().defaultBlockState().setValue(FarmBlock.MOISTURE,moisture),3);
        }
        return result;
    }
}
