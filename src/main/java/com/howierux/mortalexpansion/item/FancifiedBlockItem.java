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
    /** Legacy wrapped items place the actual native block and adopt the new fade metadata. */
    @Override public InteractionResult place(BlockPlaceContext context) {
        var wrapped=context.getItemInHand();
        var source=FancifierRules.original(wrapped,context.getLevel().registryAccess());
        if(!(source.getItem() instanceof BlockItem item))return InteractionResult.FAIL;
        source.setCount(wrapped.getCount());int count=source.getCount();
        source.set(com.howierux.mortalexpansion.registry.ModDataComponents.FADING.get(),FancifierRules.opacity(wrapped));
        var hit=new net.minecraft.world.phys.BlockHitResult(context.getClickLocation(),context.getClickedFace(),context.getClickedPos(),context.isInside());
        var nativeContext=new BlockPlaceContext(context.getLevel(),context.getPlayer(),context.getHand(),source,hit);
        var result=item.place(nativeContext);if(result.consumesAction())wrapped.shrink(count-source.getCount());return result;
    }
    @Override public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        var id = data == null ? null : ResourceLocation.tryParse(data.copyTag().getCompound("Source").getString("id"));
        return id == null ? super.getName(stack) : Component.translatable("item.mortal_expansion.fancified_block.named",BuiltInRegistries.ITEM.get(id).getDescription(),FancifierRules.opacity(stack));
    }
}
