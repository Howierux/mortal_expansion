package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.registry.ModAttachments;
import com.howierux.mortalexpansion.registry.ModBlockEntities;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Vanilla smelting, inventory, persistence, recipe XP and fuel handling with custom timing. */
public final class HauntFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    private HauntTier cachedTier;
    private boolean freeBurn;
    private boolean timingDirty = true;
    private int cachedRecipeTicks = -1;
    private int targetTicks = 200;

    /** Called by HauntManager only when this chunk crosses a named tier boundary. */
    public static void onChunkTierChanged(net.minecraft.world.level.chunk.LevelChunk chunk, HauntTier tier) {
        for (var blockEntity : chunk.getBlockEntities().values()) {
            if (blockEntity instanceof HauntFurnaceBlockEntity furnace) furnace.updateHauntTier(tier);
        }
    }

    private void updateHauntTier(HauntTier tier) {
        if (cachedTier == tier) return;
        cachedTier = tier;
        freeBurn = HauntFurnaceTiming.freeBurn(getBlockState().is(ModBlocks.HAINT_FURNACE.get()), tier);
        timingDirty = true;
    }

    public HauntFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HAUNT_FURNACE.get(), pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().is(ModBlocks.HAINT_FURNACE.get())
                ? "container.mortal_expansion.haint_furnace"
                : "container.mortal_expansion.haunt_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new FurnaceMenu(id, inventory, this, dataAccess);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
            HauntFurnaceBlockEntity furnace) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (furnace.getPersistentData().getLong("MortalExpansionInterferenceUntil")
                > serverLevel.getServer().overworld().getGameTime()) return;
        // Initialize once after placement/load. Later changes are pushed by HauntManager.
        if (furnace.cachedTier == null) {
            var chunk = serverLevel.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
            if (chunk == null) return;
            furnace.updateHauntTier(HauntTier.fromId(chunk.getData(ModAttachments.HAUNT_TIER)));
        }
        boolean haint = state.is(ModBlocks.HAINT_FURNACE.get());

        int recipeTicks = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,
                new SingleRecipeInput(furnace.getItem(0)), level)
                .map(recipe -> recipe.value().getCookingTime()).orElse(200);
        if (furnace.timingDirty || furnace.cachedRecipeTicks != recipeTicks) {
            furnace.targetTicks = HauntFurnaceTiming.cookTicks(recipeTicks, furnace.cachedTier);
            furnace.cachedRecipeTicks = recipeTicks;
            furnace.timingDirty = false;
        }
        int targetTicks = furnace.targetTicks;
        int oldTotal = furnace.dataAccess.get(3);
        if (oldTotal != targetTicks) {
            furnace.dataAccess.set(2, HauntFurnaceTiming.rescaleProgress(
                    furnace.dataAccess.get(2), oldTotal, targetTicks));
            furnace.dataAccess.set(3, targetTicks);
        }

        int burnBefore = furnace.dataAccess.get(0);
        int progressBefore = furnace.dataAccess.get(2);
        int inputBefore = furnace.getItem(0).getCount();

        // Vanilla decrements first. Offset that decrement BEFORE it can trigger refueling.
        if (burnBefore > 0 && furnace.freeBurn) {
            furnace.dataAccess.set(0, burnBefore + 1);
        }
        AbstractFurnaceBlockEntity.serverTick(level, pos, state, furnace);

        boolean cooked = furnace.dataAccess.get(2) > progressBefore
                || furnace.getItem(0).getCount() < inputBefore;
        // Below Demonic, Haint burns one real tick only when cooking advances.
        if (burnBefore > 0 && haint && (furnace.freeBurn || !cooked)) {
            furnace.dataAccess.set(0, burnBefore);
        }
        // Vanilla updates total time after finishing a recipe; restore our rate for the menu.
        furnace.dataAccess.set(3, targetTicks);
        BlockState currentState = level.getBlockState(pos);
        boolean burning = furnace.dataAccess.get(0) > 0;
        if (currentState.getValue(net.minecraft.world.level.block.AbstractFurnaceBlock.LIT) != burning) {
            level.setBlock(pos, currentState.setValue(
                    net.minecraft.world.level.block.AbstractFurnaceBlock.LIT, burning), 3);
        }
        furnace.setChanged();
    }
}
