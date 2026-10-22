package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.client.screen.FancifierScreen;
import com.howierux.mortalexpansion.menu.FancifierMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
@net.neoforged.fml.common.EventBusSubscriber(modid="mortal_expansion",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class FancifierPercentCheck {
 private static int ticks;private static boolean captured;private static FancifierScreen activeScreen;
 @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) throws Exception {
  if(!Boolean.getBoolean("mortal_expansion.percentCheck"))return;
  var mc=Minecraft.getInstance();
  if(++ticks==120){
   var inventory=new Inventory(null);var menu=new FancifierMenu(1,inventory);menu.getSlot(0).set(new ItemStack(Items.STONE,64));
   var screen=new FancifierScreen(menu,inventory,Component.literal("Fancifier"));activeScreen=screen;
   // Render-only host: native container ticking requires a joined player/world.
   mc.setScreen(new net.minecraft.client.gui.screens.Screen(Component.literal("Fancifier check")){
    @Override protected void init(){screen.init(minecraft,width,height);}
    @Override public void render(net.minecraft.client.gui.GuiGraphics graphics,int x,int y,float delta){screen.render(graphics,x,y,delta);}
   });
   var field=FancifierScreen.class.getDeclaredField("percentage");field.setAccessible(true);var box=(EditBox)field.get(screen);
   for(String bad:new String[]{"0","101","-1","3.5","abc","9999"}){box.setValue(bad);if(!box.getValue().equals("20"))throw new AssertionError("Accepted "+bad);}
   for(int value=1;value<=100;value++){box.setValue(Integer.toString(value));if(!box.getValue().equals(Integer.toString(value)))throw new AssertionError("Rejected "+value);}
   var sliderField=FancifierScreen.class.getDeclaredField("slider");sliderField.setAccessible(true);
   var slider=(net.minecraft.client.gui.components.AbstractSliderButton)sliderField.get(screen);
   for(int value=1;value<=100;value++){
    slider.onClick(slider.getX()+4+(slider.getWidth()-8)*(value-1)/99D,slider.getY()+5);
    if(!box.getValue().equals(Integer.toString(value)))throw new AssertionError("Slider/text mismatch at "+value);
   }
   box.setValue("37");
   org.slf4j.LoggerFactory.getLogger("FancifierPercentCheck").info("FANCIFIER_PERCENT_CHECK_OK: all 100 text and slider values, invalid input rejection, preview screen");
  }
  if(ticks==125){
   try(var image=net.minecraft.client.Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(java.nio.file.Path.of("fancifier-percent-screen.png"));}
   captured=true;
  }
  if(ticks==130 || ticks==140){
   var field=FancifierScreen.class.getDeclaredField("percentage");field.setAccessible(true);((EditBox)field.get(activeScreen)).setValue(ticks==130?"1":"9");
  }
  if(ticks==135 || ticks==145){
   try(var image=net.minecraft.client.Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(java.nio.file.Path.of("fancifier-percent-"+(ticks==135?"1":"9")+".png"));}
  }
  if(ticks>155 && captured)mc.stop();
 }
}
