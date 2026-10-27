package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.guide.DamnedSecret;
import com.howierux.mortalexpansion.network.EnvyPayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Tab releases the mouse through a normal screen; native item tooltips, no movable container slots. */
public final class EnvyClient {
    private EnvyClient() {}
    public static void initialize(){net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.screen==null&&mc.player!=null&&com.howierux.mortalexpansion.guide.DamnedAbilities.active(mc.player,DamnedSecret.ENVY)&&mc.options.keyPlayerList.consumeClick())mc.setScreen(new Players());});}
    public static void show(EnvyPayloads.View view){
        var mc=Minecraft.getInstance();
        if(mc.screen instanceof Players || mc.screen instanceof Inventory inv && inv.view.target().equals(view.target()))mc.setScreen(new Inventory(view));
    }
    private static Component text(String key){return Component.translatable("damned.mortal_expansion."+key);}
    private static final class Players extends Screen {
        Players(){super(text("envy_players"));}
        @Override protected void init(){
            if(minecraft.getConnection()==null)return;
            var online=minecraft.getConnection().getListedOnlinePlayers().stream().sorted(java.util.Comparator.comparing(p->p.getProfile().getName(),String.CASE_INSENSITIVE_ORDER)).limit(80).toList();
            int rows=Math.max(1,Math.min(20,(height-60)/20));int columns=(online.size()+rows-1)/rows;
            int columnWidth=Math.min(150,(width-20)/Math.max(1,columns));
            for(int i=0;i<online.size();i++){
                var profile=online.get(i).getProfile();int x=width/2-columns*columnWidth/2+(i/rows)*columnWidth,y=30+(i%rows)*20;
                var b=Button.builder(Component.literal(profile.getName()),button->net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new EnvyPayloads.Request(profile.getId().toString())))
                        .bounds(x,y,columnWidth-3,18).build();b.active=!profile.getId().equals(minecraft.player.getUUID());addRenderableWidget(b);
            }
        }
        @Override public boolean keyReleased(int key,int scan,int modifiers){if(minecraft.options.keyPlayerList.matches(key,scan)){onClose();return true;}return super.keyReleased(key,scan,modifiers);}
        @Override public void render(GuiGraphics g,int x,int y,float dt){super.render(g,x,y,dt);g.drawCenteredString(font,title,width/2,12,0xDAC8F5);}
        @Override public boolean isPauseScreen(){return false;}
    }
    private static final class Inventory extends Screen {
        final EnvyPayloads.View view;
        Inventory(EnvyPayloads.View view){super(Component.translatable("damned.mortal_expansion.envy_inventory",view.name()));this.view=view;}
        @Override public void render(GuiGraphics g,int mouseX,int mouseY,float dt){
            renderBackground(g,mouseX,mouseY,dt);int left=(width-180)/2,top=(height-150)/2;
            g.fill(left-8,top-24,left+188,top+146,0xF0201830);g.drawCenteredString(font,title,width/2,top-17,0xDAC8F5);
            for(int i=0;i<view.items().size();i++){
                int x,y;
                if(i<36){x=left+i%9*20;y=top+(i/9)*20;}
                else{x=left+(i-36)*20;y=top+106;}
                g.fill(x,y,x+18,y+18,0xFF51465E);var item=view.items().get(i);g.renderItem(item,x+1,y+1);g.renderItemDecorations(font,item,x+1,y+1);
                if(mouseX>=x && mouseX<x+18 && mouseY>=y && mouseY<y+18 && !item.isEmpty())g.renderTooltip(font,item,mouseX,mouseY);
            }
            g.drawString(font,text("envy_equipment"),left,top+91,0xBBA9CC,false);
            g.drawCenteredString(font,text("envy_readonly"),width/2,top+132,0xBBA9CC);
        }
        @Override public boolean isPauseScreen(){return false;}
    }
}
