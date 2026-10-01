package com.howierux.mortalexpansion.haunt;

import com.howierux.mortalexpansion.registry.ModAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

/**
 * Initializes natural Haunt for new chunks in the Overworld, Nether and End.
 *
 * Existing saved Haunt is retained. Previously uninitialized Nether and End
 * chunks receive their dimension baseline on first load.
 */
public final class HauntGenerationHandler {
    private HauntGenerationHandler() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (!NaturalHauntGenerator.supportsDimension(level)) {
            return;
        }

        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        if (!event.isNewChunk()) {
            // Preserve older tier-only data as well as points and Ward purification.
            if (level.dimension().equals(Level.OVERWORLD)
                    || chunk.hasData(ModAttachments.HAUNT_TIER)
                    || chunk.hasData(ModAttachments.HAUNT_PROGRESS)) return;
        }

        HauntTier initialTier = NaturalHauntGenerator.determineInitialTier(level, chunk);
        int initialValue = initialTier.midpointValue();

        chunk.setData(ModAttachments.HAUNT_TIER, initialTier.id());
        chunk.setData(
                ModAttachments.HAUNT_PROGRESS,
                HauntTier.progressFromValue(initialValue)
        );
    }
}
