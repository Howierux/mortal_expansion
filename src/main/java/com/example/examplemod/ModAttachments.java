package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.HauntTier;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/** Registers persistent NeoForge data attachments used by Mortal Expansion. */
public final class ModAttachments {
    private ModAttachments() {}

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MortalExpansion.MOD_ID);

    /**
     * Stored directly on each chunk as an integer tier id.
     * Using setData() to change this value automatically marks the chunk unsaved.
     */
    public static final Supplier<AttachmentType<Integer>> HAUNT_TIER = ATTACHMENTS.register(
            "haunt_tier",
            () -> AttachmentType.builder(() -> HauntTier.VOID.id())
                    .serialize(Codec.intRange(HauntTier.MIN_ID, HauntTier.MAX_ID))
                    .build()
    );

    public static void register(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
    }
}
