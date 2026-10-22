package com.howierux.mortalexpansion.fancifier;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.*;
/** Cosmetic conversions preserve each original item's components. */
public final class FancifierRules {
    private FancifierRules() {}
    public static final TagKey<Block> ALLOWED = TagKey.create(Registries.BLOCK, ResourceLocation.parse("mortal_expansion:fancifiable"));
    public static boolean validOpacity(int value) { return value >= 20 && value <= 100 && value % 20 == 0; }
    public static boolean decorative(Block block) {
        Class<?> type = block.getClass();
        return type == Block.class || type == RotatedPillarBlock.class || type == SlabBlock.class
                || type == StairBlock.class || type == HalfTransparentBlock.class || type == StainedGlassBlock.class;
    }
    public static boolean accepts(ItemStack stack) {
        if (stack.is(ModItems.FANCIFIED_BLOCK.get())) return true;
        return stack.getItem() instanceof BlockItem item && item.getBlock().defaultBlockState().is(ALLOWED)
                && !item.getBlock().defaultBlockState().hasBlockEntity();
    }
    public static int opacity(ItemStack stack) {
        var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        int value = data == null ? 100 : data.copyTag().getInt("Opacity");
        return validOpacity(value) ? value : 100;
    }
    public static ItemStack original(ItemStack stack, HolderLookup.Provider registries) {
        if (!stack.is(ModItems.FANCIFIED_BLOCK.get())) return stack.copyWithCount(1);
        var data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) return ItemStack.EMPTY;
        var source = ItemStack.parseOptional(registries, data.copyTag().getCompound("Source"));
        return source.is(ModItems.FANCIFIED_BLOCK.get()) ? ItemStack.EMPTY : source.copyWithCount(1);
    }
    public static ItemStack convert(ItemStack input, int opacity, HolderLookup.Provider registries) {
        if (!validOpacity(opacity) || !accepts(input)) return ItemStack.EMPTY;
        ItemStack source = original(input, registries);
        if (!(source.getItem() instanceof BlockItem block)) return ItemStack.EMPTY;
        // Existing faded items remain restorable after a datapack changes eligibility.
        if (opacity == 100) return source.copyWithCount(input.getCount());
        if (!block.getBlock().defaultBlockState().is(ALLOWED) || block.getBlock().defaultBlockState().hasBlockEntity()) return ItemStack.EMPTY;
        return faded(source,opacity,input.getCount(),registries);
    }
    /** Also used by placed-block drops, which must survive later eligibility changes. */
    public static ItemStack faded(ItemStack source,int opacity,int count,HolderLookup.Provider registries) {
        if (source.isEmpty() || !validOpacity(opacity) || source.is(ModItems.FANCIFIED_BLOCK.get())) return ItemStack.EMPTY;
        CompoundTag tag = new CompoundTag();
        tag.putString("id", "mortal_expansion:fancified_block"); tag.put("Source", source.copyWithCount(1).save(registries)); tag.putInt("Opacity", opacity);
        ItemStack result = new ItemStack(ModItems.FANCIFIED_BLOCK.get(), count);
        result.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag)); return result;
    }
}
