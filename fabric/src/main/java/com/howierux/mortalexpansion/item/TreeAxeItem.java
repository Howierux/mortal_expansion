package com.howierux.mortalexpansion.item;

import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;

public final class TreeAxeItem extends AxeItem {
    public TreeAxeItem(Tier tier,Properties properties){super(tier,properties);}
    @Override public InteractionResult useOn(UseOnContext context){
        if(context.getItemInHand().is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_AXE)){
            var growth=SpectralSaplingGrowth.use(context);if(growth!=InteractionResult.PASS)return growth;
        }return super.useOn(context);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<Component> tooltip,TooltipFlag flag){
        super.appendHoverText(stack,context,tooltip,flag);tooltip.add(Component.translatable("tooltip.mortal_expansion.axe.tree").withStyle(net.minecraft.ChatFormatting.GRAY));
        if(stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.SPECTRALITE_AXE))tooltip.add(Component.translatable("tooltip.mortal_expansion.axe.grow",
                com.howierux.mortalexpansion.haunt.EmfScale.decimal(SpectralSaplingGrowth.COST_HUNDREDTHS)).withStyle(net.minecraft.ChatFormatting.AQUA));
    }
}
