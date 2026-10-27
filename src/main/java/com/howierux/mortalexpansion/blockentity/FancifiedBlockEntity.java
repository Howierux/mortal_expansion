package com.howierux.mortalexpansion.blockentity;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.fancifier.FancifierRules;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
public final class FancifiedBlockEntity extends BlockEntity {
    private ItemStack source = ItemStack.EMPTY;
    private BlockState original = Blocks.STONE.defaultBlockState();
    private int opacity = 100;
    public FancifiedBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.FANCIFIED_BLOCK.get(),pos,state); }
    public BlockState original() { return original; }
    public int opacity() { return opacity; }
    public void configure(ItemStack item, BlockState placed, int alpha) {
        if (!(item.getItem() instanceof BlockItem b) || !b.getBlock().defaultBlockState().is(FancifierRules.ALLOWED)
                || placed.hasBlockEntity() || !FancifierRules.validOpacity(alpha)) return;
        source = item.copyWithCount(1); original = placed; opacity = alpha; setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);
            level.getChunkSource().getLightEngine().checkBlock(worldPosition);
        }
    }
    public ItemStack asItem() {
        return level == null || source.isEmpty() ? ItemStack.EMPTY : FancifierRules.faded(source,opacity,1,level.registryAccess());
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag,registries);
        if (!source.isEmpty()) tag.put("Source",source.save(registries));
        tag.put("OriginalState",NbtUtils.writeBlockState(original)); tag.putInt("Opacity",opacity);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag,registries);
        source = ItemStack.parseOptional(registries,tag.getCompound("Source"));
        opacity = tag.getInt("Opacity"); if (!FancifierRules.validOpacity(opacity)) opacity = 100;
        if (source.getItem() instanceof BlockItem b && b.getBlock() != ModBlocks.FANCIFIED_BLOCK.get() && !b.getBlock().defaultBlockState().hasBlockEntity()) {
            original = tag.contains("OriginalState") ? NbtUtils.readBlockState(registries.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("OriginalState")) : b.getBlock().defaultBlockState();
            if (original.getBlock() != b.getBlock()) original = b.getBlock().defaultBlockState();
        } else { source = ItemStack.EMPTY; original = Blocks.STONE.defaultBlockState(); }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
