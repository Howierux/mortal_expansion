package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.guide.*;
import com.howierux.mortalexpansion.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import java.util.*;

/** Original spectral research atlas, backed by the existing book/Insight state. */
public final class EctceteraBookScreen {
    private EctceteraBookScreen(){}
    private static final String BOOK="book.mortal_expansion.ectcetera.",UI="gui.mortal_expansion.ectcetera.";
    public static void open(InteractionHand hand){Minecraft.getInstance().setScreen(new ResearchScreen(hand));}
    private static Component chapterTitle(String key){if(key.equals("ecto_infrastructure"))return Component.translatable("guide.mortal_expansion.ecto_infrastructure.name");if(key.equals("grave_porting"))return Component.translatable("guide.mortal_expansion.grave_porting.name");if(key.equals("mortal_golem"))return Component.translatable("guide.mortal_expansion.mortal_golem.name");if(key.equals("wisp_o_lantern"))return Component.translatable("guide.mortal_expansion.wisp_o_lantern.name");return key.equals("dreamcatcher") || key.equals("purified_dreamcatcher") || key.equals("spectralite_haint") || key.startsWith("altar_of_") || key.equals("mortality_research_station")?Component.translatable("insight.mortal_expansion."+key+".name"):Component.translatable(BOOK+"chapter."+key);}
    private static Component body(String chapter,int part,int secrets){
        if(chapter.equals("ecto_infrastructure"))return Component.translatable("guide.mortal_expansion.ecto_infrastructure.page."+part);
        if(chapter.equals("grave_porting"))return Component.translatable("guide.mortal_expansion.grave_porting.page."+part);
        if(chapter.equals("mortal_golem"))return Component.translatable("guide.mortal_expansion.mortal_golem.page."+part);
        if(chapter.equals("wisp_o_lantern"))return Component.translatable("guide.mortal_expansion.wisp_o_lantern.page."+part);
        if(chapter.equals("dreamcatcher") || chapter.equals("purified_dreamcatcher") || chapter.startsWith("altar_of_") || chapter.equals("mortality_research_station"))return Component.translatable("insight.mortal_expansion."+chapter+".body");
        if(chapter.equals("spectralite_haint"))return Component.translatable(BOOK+"page.haint_garb.3");
        if(chapter.equals("insight") && part==2){String key=(secrets&64)==0?"mortality_research_station":(secrets&1)==0?"ectothread":(secrets&2)==0?"ghost_garb":(secrets&4)==0?"haint_garb":(secrets&8)==0?"spectralite_haint":(secrets&16)==0?"altar_of_impurity":(secrets&32)==0?"altar_of_purity":"complete";return Component.translatable("insight.mortal_expansion."+key+".guidance");}
        if(chapter.equals("haint_garb") && part==3 && (secrets&8)==0)return Component.translatable("insight.mortal_expansion.spectralite_haint.locked");
        return Component.translatable(BOOK+"page."+chapter+"."+part);
    }
    private static Item icon(String key){return switch(key){
        case "first_steps","insight","mastery"->ModItems.BOOK_OF_ECTCETERA.get();case "haunt","personal","haunt_events"->Items.SOUL_LANTERN;
        case "ecto_infrastructure"->ModItems.ECTANK.get();case "grave_porting"->ModItems.GRAVE.get();case "mortal_golem"->Items.BONE_BLOCK;case "wisp_o_lantern"->ModItems.WISP_O_LANTERN.get();case "biomes"->Items.GRASS_BLOCK;case "unlock_bone"->Items.BONE;case "graves"->ModItems.GRAVE.get();
        case "intangibles"->Items.GLASS;case "creatures"->Items.ROTTEN_FLESH;case "spiritwells"->Items.NETHER_STAR;
        case "unlock_raw","ectoplasm"->ModItems.RAW_ECTOPLASM.get();case "cauldrons"->Items.CAULDRON;case "salt"->ModItems.HOLY_SALT.get();
        case "purified_dreamcatcher"->ModItems.PURIFIED_DREAMCATCHER.get();case "dreamcatcher"->ModItems.DREAMCATCHER.get();case "emf_reader"->ModItems.EMF_READER.get();case "torches"->ModItems.SPIRIT_TORCH.get();case "ectothread"->ModItems.ECTOTHREAD.get();
        case "equipment"->ModItems.BLOODSTONE_SWORD.get();case "ghost_garb"->ModItems.GHOST_GARB_HELMET.get();case "furnaces"->ModItems.HAUNT_FURNACE.get();
        case "unlock_pure","sanctified"->ModItems.PURE_ECTOPLASM.get();case "wards"->ModItems.PURIFICATION_WARD.get();
        case "ectchantment"->ModItems.ECTCHANTMENT_MACHINE.get();case "generators"->ModItems.ECTOPLASMIC_GENERATOR.get();
        case "station"->Items.SMITHING_TABLE;case "haint_garb"->ModItems.HAINT_GARB_HELMET.get();case "spectralite_haint"->ModItems.SPECTRALITE_HAINT_HELMET.get();case "mortality_research_station"->ModItems.MORTALITY_RESEARCH_STATION.get();case "altar_of_impurity"->ModItems.ALTAR_OF_IMPURITY.get();case "altar_of_purity"->ModItems.ALTAR_OF_PURITY.get();case "scepter"->ModItems.PHARAOHL_SCEPTER.get();default->Items.BOOK;
    };}
    private static final class ResearchScreen extends Screen {
        private final InteractionHand hand;private int tier=-1,secrets=-1,category,part=1,bodyScroll;
        private EctceteraGuide.Chapter entry;private double panX,panY,zoom=1;private boolean dragging;
        private Component notice=Component.empty();
        private ResearchScreen(InteractionHand hand){super(Component.translatable("item.mortal_expansion.book_of_ectcetera"));this.hand=hand;}
        @Override protected void init(){refresh();controls();}
        private void refresh(){
            var p=minecraft.player;if(p==null)return;var book=p.getItemInHand(hand);
            if(!book.is(ModItems.BOOK_OF_ECTCETERA.get())){onClose();return;}
            int nextTier=EctceteraProgression.tier(book),nextSecrets=SpectralInsight.secrets(book);
            if(nextTier!=tier || nextSecrets!=secrets){tier=nextTier;secrets=nextSecrets;

                if(entry!=null && !EctceteraResearchMap.unlocked(entry.key(),tier,secrets))entry=null;
                if(width>0)controls();
            }
        }
        @Override public void tick(){super.tick();refresh();}
        private Button add(String key,int x,int y,int w,Runnable action){var b=Button.builder(Component.translatable(UI+key),button->action.run()).bounds(x,y,w,20).build();addRenderableWidget(b);return b;}
        private void controls(){
            clearWidgets();
            if(entry==null){
                int tabs=5;int tabTop=Math.min(62,Math.max(30,height-34-tabs*24));int spacing=Math.max(20,Math.min(28,(height-100)/tabs));
                for(int c=0;c<tabs;c++){final int tab=c;add("category."+c,12,tabTop+c*spacing,86,()->{category=tab;panX=panY=0;notice=Component.empty();});}
                if(tabTop+(tabs-1)*spacing+26<=height-62)add("center",12,height-62,86,()->{panX=panY=0;zoom=1;});
            }else{
                add("back",12,62,86,()->{entry=null;notice=Component.empty();controls();});
                var previous=add("previous",width/2-104,height-46,90,()->{part--;bodyScroll=0;controls();});previous.active=part>1;
                var next=add("next",width/2+14,height-46,90,()->{part++;bodyScroll=0;controls();});next.active=part<entry.pages();
            }
            add("close",12,height-34,86,this::onClose);
        }
        private int left(){return 108;}private int top(){return 62;}private int right(){return width-12;}private int bottom(){return height-56;}
        private boolean mapContains(double x,double y){return entry==null && x>=left() && x<right() && y>=top() && y<bottom();}
        private int x(EctceteraResearchMap.Node n){return left()+(int)(panX+n.x()*zoom);}
        private int y(EctceteraResearchMap.Node n){return top()+(int)(panY+n.y()*zoom);}
        private Component label(EctceteraResearchMap.Node n){return EctceteraResearchMap.secret(n.chapter().key())!=0 && !EctceteraResearchMap.unlocked(n.chapter().key(),tier,secrets)?Component.translatable(UI+"secret"):chapterTitle(n.chapter().key());}
        private Component requirement(EctceteraResearchMap.Node n){
            if(n.chapter().key().equals("mortality_research_station"))return Component.translatable("insight.mortal_expansion.mortality_research_station.guidance");
            if(n.chapter().tier()>tier && !(n.chapter().key().equals("haint_garb") && tier>=2 && (secrets&4)!=0))
                return Component.translatable(UI+"upgrade",Component.translatable(UI+"material."+n.chapter().tier()));
            return Component.translatable(UI+"insight_required");
        }
        private void line(GuiGraphics g,int ax,int ay,int bx,int by,int color){int mid=(ax+bx)/2;g.fill(Math.min(ax,mid),ay,Math.max(ax,mid)+1,ay+1,color);g.fill(mid,Math.min(ay,by),mid+1,Math.max(ay,by)+1,color);g.fill(Math.min(mid,bx),by,Math.max(mid,bx)+1,by+1,color);}
        private void renderWidgets(GuiGraphics graphics,int mouseX,int mouseY,float delta){
            // Screen.render also blurs the framebuffer. After drawing the atlas, draw only its widgets.
            for(var widget:renderables)widget.render(graphics,mouseX,mouseY,delta);
        }
        @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta){
            renderBackground(g,mouseX,mouseY,delta);g.fill(6,6,width-6,height-6,0xF01B2227);g.fill(left()-2,top()-2,right()+2,bottom()+2,0xFF6B817A);
            g.drawString(font,title,14,17,0xCEE7D3,false);
            g.drawString(font,Component.translatable(UI+"tier",Component.translatable(UI+"tier."+Math.max(0,tier))),left(),36,0xACBDB3,false);
            if(entry==null){
                g.fill(left(),top(),right(),bottom(),0xFF242E2B);g.enableScissor(left(),top(),right(),bottom());
                for(int gx=left()+(int)(panX%32);gx<right();gx+=32)g.fill(gx,top(),gx+1,bottom(),0xFF303A35);
                for(int gy=top()+(int)(panY%32);gy<bottom();gy+=32)g.fill(left(),gy,right(),gy+1,0xFF303A35);
                var nodes=EctceteraResearchMap.nodes(category);
                for(var node:nodes)nodes.stream().filter(parent->parent.chapter().key().equals(node.parent())).findFirst().ifPresent(parent->line(g,x(parent),y(parent),x(node),y(node),EctceteraResearchMap.unlocked(node.chapter().key(),tier,secrets)?0xFF759C81:0xFF505A54));
                EctceteraResearchMap.Node hovered=null;
                for(var node:nodes){boolean unlocked=EctceteraResearchMap.unlocked(node.chapter().key(),tier,secrets);int x=x(node),y=y(node),size=(int)(15*zoom);
                    g.fill(x-size-2,y-size-2,x+size+2,y+size+2,unlocked?0xFF9ECB9F:0xFF5B6461);g.fill(x-size,y-size,x+size,y+size,unlocked?0xFF344B3B:0xFF222A27);
                    g.pose().pushPose();g.pose().translate(x-8*zoom,y-8*zoom,0);g.pose().scale((float)zoom,(float)zoom,1);
                    g.renderItem(new ItemStack(unlocked?icon(node.chapter().key()):Items.BOOK),0,0);g.pose().popPose();
                    g.drawCenteredString(font,label(node),x,y+size+5,unlocked?0xD6E6D6:0x8E9990);
                    if(mapContains(mouseX,mouseY) && Math.abs(mouseX-x)<=size+3 && Math.abs(mouseY-y)<=size+3)hovered=node;
                }
                g.disableScissor();renderWidgets(g,mouseX,mouseY,delta);
                if(hovered!=null)g.renderTooltip(font,EctceteraResearchMap.unlocked(hovered.chapter().key(),tier,secrets)?label(hovered):label(hovered).copy().append("\n").append(requirement(hovered)),mouseX,mouseY);
                g.drawString(font,notice.getString().isEmpty()?Component.translatable(UI+"controls"):notice,left(),height-34,0xB5CBB9,false);
            }else{
                g.fill(left(),top(),right(),bottom(),0xFFE0D3AF);g.drawCenteredString(font,chapterTitle(entry.key()),(left()+right())/2,top()+10,0x463D2A);
                int textWidth=Math.max(30,right()-left()-32),textY=top()+32,rows=Math.max(1,(bottom()-textY-12)/font.lineHeight);
                List<FormattedCharSequence> text=font.split(body(entry.key(),part,secrets),textWidth);bodyScroll=Math.clamp(bodyScroll,0,Math.max(0,text.size()-rows));
                g.enableScissor(left()+8,textY,right()-8,bottom()-8);for(int i=bodyScroll;i<Math.min(text.size(),bodyScroll+rows);i++)g.drawString(font,text.get(i),left()+16,textY+(i-bodyScroll)*font.lineHeight,0x463D2A,false);g.disableScissor();
                g.drawCenteredString(font,Component.translatable(UI+"page",part,entry.pages()),(left()+right())/2,height-56+3,0xCAD7CC);renderWidgets(g,mouseX,mouseY,delta);
            }
        }
        @Override public boolean mouseClicked(double mx,double my,int button){
            if(super.mouseClicked(mx,my,button))return true;
            if(button==0 && mapContains(mx,my)){
                for(var node:EctceteraResearchMap.nodes(category))if(Math.abs(mx-x(node))<=18*zoom && Math.abs(my-y(node))<=18*zoom){
                    if(EctceteraResearchMap.unlocked(node.chapter().key(),tier,secrets)){entry=node.chapter();part=1;bodyScroll=0;controls();}
                    else notice=requirement(node);return true;
                }dragging=true;return true;
            }return false;
        }
        @Override public boolean mouseDragged(double mx,double my,int button,double dx,double dy){
            if(entry==null && dragging && button==0){panX=Math.clamp(panX+dx,-650,250);panY=Math.clamp(panY+dy,-650,250);return true;}return super.mouseDragged(mx,my,button,dx,dy);
        }
        @Override public boolean mouseReleased(double mx,double my,int button){dragging=false;return super.mouseReleased(mx,my,button);}
        @Override public boolean mouseScrolled(double mx,double my,double horizontal,double vertical){
            if(entry!=null){bodyScroll=Math.max(0,bodyScroll-(int)vertical*3);return true;}
            if(mapContains(mx,my)){double next=Math.clamp(zoom+vertical*.1,.65,1.5);double ratio=next/zoom;panX=(mx-left())-((mx-left())-panX)*ratio;panY=(my-top())-((my-top())-panY)*ratio;zoom=next;return true;}
            return super.mouseScrolled(mx,my,horizontal,vertical);
        }
        @Override public boolean keyPressed(int key,int scan,int modifiers){
            if(key==256 && entry!=null){entry=null;controls();return true;}return super.keyPressed(key,scan,modifiers);
        }
        @Override public boolean isPauseScreen(){return false;}
    }
}
