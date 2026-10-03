package com.howierux.mortalexpansion.guide;
import java.util.List;

/** Chapter order and page counts shared by navigation and validation. */
public final class EctceteraGuide {
    private EctceteraGuide() {}
    public record Chapter(String key, int pages) {}
    public static final int CHAPTERS_PER_CONTENTS_PAGE = 5;
    public static final List<Chapter> CHAPTERS = List.of(
            new Chapter("first_steps", 4),
            new Chapter("haunt", 7),
            new Chapter("personal", 4),
            new Chapter("biomes", 4),
            new Chapter("spiritwells", 3),
            new Chapter("graves", 10),
            new Chapter("intangibles", 4),
            new Chapter("equipment", 6),
            new Chapter("ectoplasm", 4),
            new Chapter("cauldrons", 6),
            new Chapter("salt", 2),
            new Chapter("torches", 2),
            new Chapter("sanctified", 4),
            new Chapter("wards", 4),
            new Chapter("ectchantment", 6),
            new Chapter("generators", 4),
            new Chapter("station", 4),
            new Chapter("furnaces", 5),
            new Chapter("creatures", 4),
            new Chapter("scepter", 2)
    );
    public static int contentsPages() {
        return (CHAPTERS.size() + CHAPTERS_PER_CONTENTS_PAGE - 1) / CHAPTERS_PER_CONTENTS_PAGE;
    }
    public static int firstPage(int chapter) {
        int page = 1 + contentsPages();
        for (int i = 0; i < chapter; i++) page += CHAPTERS.get(i).pages();
        return page;
    }
}
