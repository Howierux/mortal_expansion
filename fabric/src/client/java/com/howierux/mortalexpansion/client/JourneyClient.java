package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.catalog.Catalog;
import com.howierux.mortalexpansion.network.*;
import com.howierux.mortalexpansion.travel.JourneyRuntime;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.screen.v1.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
public final class JourneyClient {
 public static boolean active,eligible;private static net.minecraft.client.renderer.PostChain owned;
 private static final KeyMapping JOURNEY=KeyBindingHelper.registerKeyBinding(new KeyMapping("key.mortal_expansion.spirit_journey",org.lwjgl.glfw.GLFW.GLFW_KEY_J,"key.categories.mortal_expansion"));
 public static void send(String action,BlockPos pos,String text){if(ClientPlayNetworking.canSend(JourneyPayloads.Action.TYPE))ClientPlayNetworking.send(new JourneyPayloads.Action(action,pos,text));}
 public static void initialize(){
  ClientPlayNetworking.registerGlobalReceiver(JourneyPayloads.Ui.TYPE,(payload,context)->context.client().execute(()->{var tag=payload.data();switch(tag.getString("kind")){case "state"->{active=tag.getBoolean("active");eligible=tag.getBoolean("eligible");if(active&&context.client().screen instanceof DeathScreen)context.client().setScreen(null);}case "grave"->context.client().setScreen(new GraveNameScreen(BlockPos.of(tag.getLong("pos"))));case "journey"->context.client().setScreen(new JourneyScreen());default->{}}}));
  ClientPlayNetworking.registerGlobalReceiver(SpectralTracksPayload.TYPE,(payload,context)->context.client().execute(()->SpectralTracksClient.receive(payload)));
  ClientPlayNetworking.registerGlobalReceiver(HauntEffectPayloads.Shadow.TYPE,(payload,context)->context.client().execute(()->HauntShadowClient.handle(payload)));
  ClientPlayConnectionEvents.DISCONNECT.register((handler,client)->{active=false;eligible=false;});
  ClientTickEvents.END_CLIENT_TICK.register(mc->{if(mc.level==null){active=false;eligible=false;}while(JOURNEY.consumeClick())if(active)mc.setScreen(new JourneyScreen());HauntShadowClient.tick();});
  ScreenEvents.AFTER_INIT.register((mc,screen,width,height)->{if(screen instanceof DeathScreen){send("query",BlockPos.ZERO,"");var button=Button.builder(Component.translatable("screen.mortal_expansion.journey.title"),b->send("choose",BlockPos.ZERO,"")).bounds(width/2-100,height/4+120,200,20).build();button.active=eligible;Screens.getButtons(screen).add(button);ScreenEvents.afterTick(screen).register(s->button.active=eligible);}});
  WorldRenderEvents.AFTER_ENTITIES.register(HauntShadowClient::render);WorldRenderEvents.AFTER_TRANSLUCENT.register(SpectralTracksClient::render);
  net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(JourneyRuntime.CORPSE,PlayerCorpseRenderer::new);
  net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(JourneyRuntime.WELL,SpiritwellBeamRenderer::new);
  net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(JourneyRuntime.GRAVE,GraveRenderer::new);
  WorldRenderEvents.START.register(context->{var mc=Minecraft.getInstance();if(active){if(owned==null||mc.gameRenderer.currentEffect()!=owned){((com.howierux.mortalexpansion.mixin.JourneyPostEffectInvoker)mc.gameRenderer).mortalExpansion$loadJourneyEffect(Catalog.id("shaders/post/spirit_journey.json"));owned=mc.gameRenderer.currentEffect();}}else if(owned!=null){if(mc.gameRenderer.currentEffect()==owned){mc.gameRenderer.shutdownEffect();mc.gameRenderer.checkEntityPostEffect(mc.getCameraEntity());}owned=null;}});
 }
 public static final class JourneyScreen extends Screen {
  public JourneyScreen(){super(Component.translatable("screen.mortal_expansion.journey.title"));}
  protected void init(){addRenderableWidget(Button.builder(Component.translatable("screen.mortal_expansion.journey.well"),b->{send("well",BlockPos.ZERO,"");onClose();}).bounds(width/2-150,height/2,300,20).build());addRenderableWidget(Button.builder(Component.translatable("screen.mortal_expansion.journey.body"),b->onClose()).bounds(width/2-150,height/2+26,300,20).build());}
  public boolean isPauseScreen(){return false;}
  public void render(net.minecraft.client.gui.GuiGraphics gui,int x,int y,float partial){super.render(gui,x,y,partial);gui.drawCenteredString(font,title,width/2,height/2-65,0xBDFFD6);gui.drawCenteredString(font,Component.translatable("screen.mortal_expansion.journey.cost"),width/2,height/2-40,0xDDDDDD);gui.drawCenteredString(font,Component.translatable("screen.mortal_expansion.journey.free"),width/2,height/2-25,0xDDDDDD);}
 }
 public static final class GraveNameScreen extends Screen {
  private final BlockPos pos;private EditBox name;
  public GraveNameScreen(BlockPos pos){super(Component.translatable("screen.mortal_expansion.grave.title"));this.pos=pos;}
  protected void init(){name=new EditBox(font,width/2-100,height/2-20,200,20,Component.translatable("screen.mortal_expansion.grave.name"));name.setMaxLength(32);addRenderableWidget(name);setInitialFocus(name);addRenderableWidget(Button.builder(Component.translatable("screen.mortal_expansion.grave.engrave"),b->{if(!name.getValue().isBlank()){send("name",pos,name.getValue());onClose();}}).bounds(width/2-100,height/2+10,200,20).build());}
  public boolean isPauseScreen(){return false;}
  public void render(net.minecraft.client.gui.GuiGraphics gui,int x,int y,float partial){super.render(gui,x,y,partial);gui.drawCenteredString(font,title,width/2,height/2-65,0xFFFFFF);gui.drawCenteredString(font,Component.translatable("screen.mortal_expansion.grave.hint"),width/2,height/2-45,0xCCCCCC);}
 }
}
