package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.guide.EctceteraGuide;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.*;
import java.util.ArrayList;
import java.util.List;

/** Vanilla page turning, clickable chapters, and links back to contents. */
public final class EctceteraBookScreen {
    private EctceteraBookScreen() {}
    private static final String PREFIX = "book.mortal_expansion.ectcetera.";

    private static MutableComponent link(Component label, int page) {
        return label.copy().withStyle(style -> style.withColor(ChatFormatting.DARK_BLUE)
                .withUnderlined(true).withClickEvent(
                        new ClickEvent(ClickEvent.Action.CHANGE_PAGE, Integer.toString(page + 1))));
    }

    public static void open() {
        List<Component> pages = new ArrayList<>();
        pages.add(Component.translatable(PREFIX + "cover").append("\n\n")
                .append(link(Component.translatable(PREFIX + "contents"), 1)));
        for (int contents = 0; contents < EctceteraGuide.contentsPages(); contents++) {
            MutableComponent page = Component.empty()
                    .append(Component.translatable(PREFIX + "contents").withStyle(ChatFormatting.BOLD))
                    .append("\n\n");
            int begin = contents * EctceteraGuide.CHAPTERS_PER_CONTENTS_PAGE;
            int end = Math.min(EctceteraGuide.CHAPTERS.size(),
                    begin + EctceteraGuide.CHAPTERS_PER_CONTENTS_PAGE);
            for (int chapter = begin; chapter < end; chapter++) {
                var entry = EctceteraGuide.CHAPTERS.get(chapter);
                page.append(link(Component.translatable(PREFIX + "chapter." + entry.key()),
                        EctceteraGuide.firstPage(chapter))).append("\n");
            }
            pages.add(page);
        }
        for (int chapter = 0; chapter < EctceteraGuide.CHAPTERS.size(); chapter++) {
            var entry = EctceteraGuide.CHAPTERS.get(chapter);
            for (int part = 1; part <= entry.pages(); part++) {
                MutableComponent page = Component.empty()
                        .append(Component.translatable(PREFIX + "chapter." + entry.key())
                                .withStyle(ChatFormatting.BOLD))
                        .append("\n\n")
                        .append(Component.translatable(PREFIX + "page." + entry.key() + "." + part))
                        .append("\n\n")
                        .append(link(Component.translatable(PREFIX + "contents"),
                                1 + chapter / EctceteraGuide.CHAPTERS_PER_CONTENTS_PAGE));
                pages.add(page);
            }
        }
        Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.BookAccess(pages)));
    }
}
