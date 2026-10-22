package com.howierux.mortalexpansion.item;
import com.howierux.mortalexpansion.blockentity.FancifiedBlockEntity;
import com.howierux.mortalexpansion.fancifier.FancifierRules;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
public final class FancifiedBlockItem extends BlockItem {
    public FancifiedBlockItem(Block block,Properties properties) { super(block,properties); }
    @Override public InteractionResult place(BlockPlaceContext context) {
        var source = FancifierRules.original(context.getItemInHand(),context.getLevel().registryAccess());
        if (!(source.getItem() instanceof BlockItem block) || !FancifierRules.accepts(source)) return InteractionResult.FAIL;
        var state = block.getBlock().getStateForPlacement(context);
        if (state == null || state.hasBlockEntity()) return InteractionResult.FAIL;
        var pos = context.getClickedPos().immutable(); int alpha = FancifierRules.opacity(context.getItemInHand());
        var result = super.place(context);
        if (result.consumesAction() && context.getLevel().getBlockEntity(pos) instanceof FancifiedBlockEntity be) be.configure(source,state,alpha);
        return result;
    }
    @Override public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        var id = data == null ? null : ResourceLocation.tryParse(data.copyTag().getCompound("Source").getString("id"));
        return id == null ? super.getName(stack) : Component.translatable("item.mortal_expansion.fancified_block.named",BuiltInRegistries.ITEM.get(id).getDescription(),FancifierRules.opacity(stack));
    }
}
