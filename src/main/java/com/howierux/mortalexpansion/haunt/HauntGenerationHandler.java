package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

/**
 * Initializes biome-derived haunt when a brand-new Overworld chunk is first loaded.
 *
 * Existing chunks are deliberately left unchanged. Older tier-only chunks are
 * migrated lazily to the midpoint of their existing tier by HauntManager.
 */
public final class HauntGenerationHandler {
    private HauntGenerationHandler() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.isNewChunk()) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!level.dimension().equals(Level.OVERWORLD)) {
            return;
        }

        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        HauntTier initialTier = NaturalHauntGenerator.determineInitialTier(chunk);
        int initialValue = initialTier.midpointValue();

        chunk.setData(ModAttachments.HAUNT_TIER, initialTier.id());
        chunk.setData(
                ModAttachments.HAUNT_PROGRESS,
                HauntTier.progressFromValue(initialValue)
        );
    }
}
