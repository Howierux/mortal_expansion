package com.howierux.mortalexpansion.guide;
import java.util.List;

/** Tiered chapter order and page offsets shared by navigation and validation. */
public final class EctceteraGuide {
    private EctceteraGuide() {}
    public record Chapter(String key, int pages, int tier) {}
    public static final int CHAPTERS_PER_CONTENTS_PAGE = 5;
    public static final List<Chapter> CHAPTERS = List.of(
            new Chapter("first_steps", 4, 0),
            new Chapter("haunt", 7, 0),
            new Chapter("personal", 10, 0),
            new Chapter("biomes", 4, 0),
            new Chapter("haunt_events", 4, 0),
            new Chapter("unlock_bone", 1, 0),
            new Chapter("graves", 10, 1),
            new Chapter("intangibles", 4, 1),
            new Chapter("creatures", 4, 1),
            new Chapter("spiritwells", 5, 1),
            new Chapter("unlock_raw", 2, 1),
            new Chapter("ectoplasm", 4, 2),
            new Chapter("cauldrons", 6, 2),
            new Chapter("salt", 2, 2),
            new Chapter("emf_reader", 4, 2),
            new Chapter("torches", 2, 2),
            new Chapter("equipment", 6, 2),
            new Chapter("ghost_garb", 3, 2),
            new Chapter("furnaces", 5, 2),
            new Chapter("unlock_pure", 2, 2),
            new Chapter("sanctified", 4, 3),
            new Chapter("wards", 4, 3),
            new Chapter("ectchantment", 6, 3),
            new Chapter("generators", 4, 3),
            new Chapter("station", 4, 3),
            new Chapter("haint_garb", 3, 3),
            new Chapter("scepter", 2, 3),
            new Chapter("mastery", 1, 3)
    );
    public static List<Chapter> chapters(int tier) {
        int bounded = Math.clamp(tier, 0, EctceteraProgression.MAX_TIER);
        return CHAPTERS.stream().filter(chapter -> chapter.tier() <= bounded).toList();
    }
    public static int contentsPages(List<Chapter> chapters) {
        return (chapters.size() + CHAPTERS_PER_CONTENTS_PAGE - 1) / CHAPTERS_PER_CONTENTS_PAGE;
    }
    public static int firstPage(List<Chapter> chapters, int chapter) {
        int page = 1 + contentsPages(chapters);
        for (int i = 0; i < chapter; i++) page += chapters.get(i).pages();
        return page;
    }
}
