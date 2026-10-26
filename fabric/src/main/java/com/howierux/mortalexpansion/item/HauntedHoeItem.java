package com.howierux.mortalexpansion.item;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;

import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.block.HauntedFarmlandBlock;

/** Preserve vanilla till predicates, events, sounds and tool wear, then mark a successful farmland result. */
public final class HauntedHoeItem extends HoeItem {
    public HauntedHoeItem(Tier tier,Properties properties){super(tier,properties);}
    @Override public void appendHoverText(net.minecraft.world.item.ItemStack stack,TooltipContext context,
            java.util.List<Component> tooltip,net.minecraft.world.item.TooltipFlag flag){
        super.appendHoverText(stack,context,tooltip,flag);
        if(stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_HOE))
            tooltip.add(Component.translatable("tooltip.mortal_expansion.spectralite_hoe.growth",SpectralCropGrowth.COST_PER_PULSE).withStyle(net.minecraft.ChatFormatting.AQUA));
    }
    @Override public InteractionResult useOn(UseOnContext context){
        if(context.getItemInHand().is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_HOE)){
            var growth=SpectralCropGrowth.use(context);if(growth!=InteractionResult.PASS)return growth;
        }
        var result=super.useOn(context);var level=context.getLevel();var pos=context.getClickedPos();
        if(result.consumesAction() && level instanceof net.minecraft.server.level.ServerLevel server
                && level.getBlockState(pos).is(Blocks.FARMLAND)){
            int moisture=level.getBlockState(pos).getValue(FarmBlock.MOISTURE);
            if(HauntedFarmlandBlock.keepsMoist(HauntManager.getTier(server,pos)))moisture=7;
            level.setBlock(pos,com.howierux.mortalexpansion.catalog.CatalogBlocks.HAUNTED_FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE,moisture),3);
        }
        return result;
    }
}
