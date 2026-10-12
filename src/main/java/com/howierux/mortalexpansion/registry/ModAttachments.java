package com.howierux.mortalexpansion.registry;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.HauntTier;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/** Registers persistent NeoForge data attachments used by Mortal Expansion. */
public final class ModAttachments {
    private ModAttachments() {}

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MortalExpansion.MOD_ID);

    /**
     * Named chunk haunt tier. Retained as a separate attachment so worlds made
     * before fine-grained haunt points keep their existing tier.
     */
    public static final Supplier<AttachmentType<Integer>> HAUNT_TIER = ATTACHMENTS.register(
            "haunt_tier",
            () -> AttachmentType.builder(() -> HauntTier.VOID.id())
                    .serialize(Codec.intRange(HauntTier.MIN_ID, HauntTier.MAX_ID))
                    .build()
    );

    /**
     * Fine-grained position within a chunk's named tier.
     *
     * -1 means this chunk predates point stratification and should initialize
     * to the midpoint of its existing tier on first access.
     */
    public static final Supplier<AttachmentType<Integer>> HAUNT_PROGRESS = ATTACHMENTS.register(
            "haunt_progress",
            () -> AttachmentType.builder(() -> -1)
                    .serialize(Codec.intRange(-1, HauntTier.POINTS_PER_TIER - 1))
                    .build()
    );

    /** New players start at Natural; saved values and death-copy behavior remain authoritative. */
    public static final Supplier<AttachmentType<Integer>> PLAYER_HAUNT_TIER = ATTACHMENTS.register(
            "player_haunt_tier",
            () -> AttachmentType.builder(() -> HauntTier.NATURAL.id())
                    .serialize(Codec.intRange(HauntTier.MIN_ID, HauntTier.MAX_ID))
                    .copyOnDeath()
                    .build()
    );

    /**
     * Fine-grained position within the player's named tier.
     * -1 migrates older tier-only player data to the midpoint on first access.
     */
    public static final Supplier<AttachmentType<Integer>> PLAYER_HAUNT_PROGRESS = ATTACHMENTS.register(
            "player_haunt_progress",
            () -> AttachmentType.builder(() -> -1)
                    .serialize(Codec.intRange(-1, HauntTier.POINTS_PER_TIER - 1))
                    .copyOnDeath()
                    .build()
    );

    /** Hundredths of a personal Haunt point, retained across logout and death. */
    public static final Supplier<AttachmentType<Integer>> PLAYER_HAUNT_FRACTION = ATTACHMENTS.register(
            "player_haunt_fraction",
            () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.intRange(0, 99))
                    .copyOnDeath()
                    .build()
    );

    /**
     * Desert-pyramid encounter keys already triggered by this player.
     * Persisted across death so each pyramid can summon Pharaohl only once
     * for each individual player.
     */
    public static final Supplier<AttachmentType<Set<String>>> PHARAOHL_PYRAMIDS = ATTACHMENTS.register(
            "pharaohl_pyramids",
            () -> AttachmentType.<Set<String>>builder(() -> new HashSet<String>())
                    .serialize(Codec.STRING.listOf().xmap(
                            list -> new HashSet<String>(list),
                            set -> java.util.List.copyOf(set)
                    ))
                    .copyOnDeath()
                    .build()
    );

    /**
     * The player's currently selected named-Grave respawn anchor.
     * Encoded as dimension|x|y|z. Empty means no Grave anchor.
     */
    public static final Supplier<AttachmentType<String>> GRAVE_RESPAWN_POINT = ATTACHMENTS.register(
            "grave_respawn_point",
            () -> AttachmentType.builder(() -> "")
                    .serialize(Codec.STRING)
                    .copyOnDeath()
                    .build()
    );

    /** Personal ambient cooldown, blessing, and effect provenance survive death/logout. */
    public static final Supplier<AttachmentType<net.minecraft.nbt.CompoundTag>> AMBIENT_HAUNT_STATE =
            ATTACHMENTS.register("ambient_haunt_state", () -> AttachmentType
                    .builder(() -> new net.minecraft.nbt.CompoundTag())
                    .serialize(net.minecraft.nbt.CompoundTag.CODEC).copyOnDeath().build());

    public static final Supplier<AttachmentType<java.util.List<com.howierux.mortalexpansion.discovery.SpectralSite>>> DISCOVERED_SITES =
            ATTACHMENTS.register("discovered_sites", () -> AttachmentType.<java.util.List<com.howierux.mortalexpansion.discovery.SpectralSite>>builder(() -> java.util.List.of())
                    .serialize(com.howierux.mortalexpansion.discovery.SpectralSite.CODEC.listOf()).copyOnDeath().build());

    public static final Supplier<AttachmentType<Integer>> ECTCETERA_KNOWLEDGE = ATTACHMENTS.register("ectcetera_knowledge",
            () -> AttachmentType.builder(() -> 0).serialize(Codec.intRange(0,127)).copyOnDeath().build());

    /** Exact hundredths retained beside existing chunk tier/progress fields; old chunks default to zero. */
    public static final Supplier<AttachmentType<Integer>> HAUNT_FRACTION = ATTACHMENTS.register("haunt_fraction",
            () -> AttachmentType.builder(() -> 0).serialize(Codec.intRange(0,99)).build());

    public static final Supplier<AttachmentType<com.howierux.mortalexpansion.quest.SpiritQuestState>> SPIRIT_QUESTS =
            ATTACHMENTS.register("spirit_quests", () -> AttachmentType.builder(() -> com.howierux.mortalexpansion.quest.SpiritQuestState.EMPTY)
                    .serialize(com.howierux.mortalexpansion.quest.SpiritQuestState.CODEC).copyOnDeath().build());

    public static final Supplier<AttachmentType<Integer>> MORTALITY_RESEARCH_BONUS=ATTACHMENTS.register("mortality_research_bonus",()->AttachmentType.builder(()->0).serialize(Codec.intRange(0,100)).copyOnDeath().build());
    public static final Supplier<AttachmentType<Integer>> MORTALITY_RESEARCH_ECTO=ATTACHMENTS.register("mortality_research_ecto",()->AttachmentType.builder(()->0).serialize(Codec.intRange(0,2)).copyOnDeath().build());
    public static void register(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
    }

    /** One-shot choice copied to the replacement player, then cleared after respawn. */
    public static final Supplier<AttachmentType<Boolean>> SPIRIT_JOURNEY_RESPAWN = ATTACHMENTS.register(
            "spirit_journey_respawn", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL).copyOnDeath().build());

    /** Actual death item entities, retained across logout and player replacement. */
    public static final Supplier<AttachmentType<String>> SPIRIT_JOURNEY_CORPSE = ATTACHMENTS.register(
            "spirit_journey_corpse", () -> AttachmentType.builder(() -> "")
                    .serialize(Codec.STRING).copyOnDeath().build());

    public static final Supplier<AttachmentType<Integer>> SPIRIT_JOURNEY_RETURN_MODE = ATTACHMENTS.register(
            "spirit_journey_return_mode", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT).copyOnDeath().build());

    public static final Supplier<AttachmentType<net.minecraft.nbt.CompoundTag>> DEFERRED_DEATH_ITEMS =
            ATTACHMENTS.register("deferred_death_items", () -> AttachmentType
                    .builder(() -> new net.minecraft.nbt.CompoundTag())
                    .serialize(net.minecraft.nbt.CompoundTag.CODEC).copyOnDeath().build());
}
