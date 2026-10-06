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

    private static List<Component> pages(int tier,int secrets) {
        var chapters = EctceteraGuide.chapters(tier,secrets);
        List<Component> pages = new ArrayList<>();
        pages.add(Component.translatable(PREFIX + "cover").append("\n\n")
                .append(link(Component.translatable(PREFIX + "contents"), 1)));
        for (int contents = 0; contents < EctceteraGuide.contentsPages(chapters); contents++) {
            MutableComponent page = Component.empty()
                    .append(Component.translatable(PREFIX + "contents").withStyle(ChatFormatting.BOLD))
                    .append("\n\n");
            int begin = contents * EctceteraGuide.CHAPTERS_PER_CONTENTS_PAGE;
            int end = Math.min(chapters.size(),
                    begin + EctceteraGuide.CHAPTERS_PER_CONTENTS_PAGE);
            for (int chapter = begin; chapter < end; chapter++) {
                var entry = chapters.get(chapter);
                page.append(link(Component.translatable(PREFIX + "chapter." + entry.key()),
                        EctceteraGuide.firstPage(chapters, chapter))).append("\n");
            }
            pages.add(page);
        }
        for (int chapter = 0; chapter < chapters.size(); chapter++) {
            var entry = chapters.get(chapter);
            for (int part = 1; part <= entry.pages(); part++) {
                MutableComponent page = Component.empty()
                        .append(Component.translatable(PREFIX + "chapter." + entry.key())
                                .withStyle(ChatFormatting.BOLD))
                        .append("\n\n")
                        .append(body(entry.key(),part,tier,secrets))
                        .append("\n\n")
                        .append(link(Component.translatable(PREFIX + "contents"),
                                1 + chapter / EctceteraGuide.CHAPTERS_PER_CONTENTS_PAGE));
                pages.add(page);
            }
        }
        return pages;
    }
    private static Component body(String chapter,int part,int tier,int secrets){
        if(chapter.equals("insight") && part==2) {
            String key=(secrets&1)==0?"ectothread":(secrets&2)==0?"ghost_garb":(secrets&4)==0?"haint_garb":(secrets&8)==0?"spectralite_haint":"complete";
            return Component.translatable("insight.mortal_expansion."+key+".guidance");
        }
        if(chapter.equals("haint_garb") && part==3 && (secrets&8)==0)return Component.translatable("insight.mortal_expansion.spectralite_haint.locked");
        return Component.translatable(PREFIX+"page."+chapter+"."+part);
    }
    public static void open(net.minecraft.world.InteractionHand hand){
        Minecraft.getInstance().setScreen(new AwareBookScreen(hand));
    }
    private static final class AwareBookScreen extends BookViewScreen {
        private final net.minecraft.world.InteractionHand hand;private int tier, secrets;
        private AwareBookScreen(net.minecraft.world.InteractionHand hand){
            super(new BookAccess(pages(com.howierux.mortalexpansion.guide.EctceteraProgression.tier(Minecraft.getInstance().player.getItemInHand(hand)),
                    com.howierux.mortalexpansion.guide.SpectralInsight.secrets(Minecraft.getInstance().player.getItemInHand(hand)))));this.hand=hand;
            tier=-1;secrets=-1;
        }
        @Override public void tick(){
            super.tick();var p=Minecraft.getInstance().player;if(p==null)return;var book=p.getItemInHand(hand);
            if(!book.is(com.howierux.mortalexpansion.registry.ModItems.BOOK_OF_ECTCETERA.get())){onClose();return;}
            int newTier=com.howierux.mortalexpansion.guide.EctceteraProgression.tier(book),newSecrets=com.howierux.mortalexpansion.guide.SpectralInsight.secrets(book);
            if(newTier!=tier || newSecrets!=secrets){tier=newTier;secrets=newSecrets;setBookAccess(new BookAccess(pages(tier,secrets)));}
        }
    }
}
