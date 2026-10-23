package com.howierux.mortalexpansion.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.*;
import com.howierux.mortalexpansion.network.EnvyPayloads;
/** Dev-only render probe. Copy into the client source tree temporarily; never ship in the jar. */
@net.neoforged.fml.common.EventBusSubscriber(modid="mortal_expansion",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class EnvyScreenCheck {
 private static int ticks;
 @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post e) throws Exception {
  if(!Boolean.getBoolean("mortal_expansion.envyCheck"))return;
  var mc=Minecraft.getInstance();ticks++;
  if(ticks==100){
   var items=new java.util.ArrayList<ItemStack>();for(int i=0;i<41;i++)items.add(ItemStack.EMPTY);
   items.set(0,new ItemStack(Items.DIAMOND,17));items.set(1,new ItemStack(Items.COOKED_BEEF,32));
   items.set(9,new ItemStack(Items.IRON_PICKAXE));items.set(36,new ItemStack(Items.DIAMOND_BOOTS));items.set(37,new ItemStack(Items.DIAMOND_LEGGINGS));
   items.set(38,new ItemStack(Items.DIAMOND_CHESTPLATE));items.set(39,new ItemStack(Items.DIAMOND_HELMET));items.set(40,new ItemStack(Items.SHIELD));
   var ctor=Class.forName("com.howierux.mortalexpansion.client.EnvyClient$Inventory").getDeclaredConstructor(EnvyPayloads.View.class);ctor.setAccessible(true);
   var screen=(Screen)ctor.newInstance(new EnvyPayloads.View(java.util.UUID.randomUUID().toString(),"HauntedPlayer",items));mc.setScreen(screen);
   if(!screen.children().isEmpty())throw new AssertionError("Read-only vision unexpectedly contains interactive slots/widgets");
   org.slf4j.LoggerFactory.getLogger("EnvyScreenCheck").info("ENVY_SCREEN_CHECK_OK: read-only native item render, 41 snapshot slots, armor/offhand, no container");
  }
  if(ticks==110)try(var image=net.minecraft.client.Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(java.nio.file.Path.of("envy-screen-check.png"));}
  if(ticks>120)mc.stop();
 }
}
