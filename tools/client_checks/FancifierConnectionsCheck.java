package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.fancifier.FancifierConnections;
import com.howierux.mortalexpansion.blockentity.FancifiedBlockEntity;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.model.data.ModelData;
/** Development-only actual Fusion model-data check; requires local Connected Glass/Fusion. */
@net.neoforged.fml.common.EventBusSubscriber(modid="mortal_expansion",value=net.neoforged.api.distmarker.Dist.CLIENT)
public final class FancifierConnectionsCheck {
 private static int ticks;
 @net.neoforged.bus.api.SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){
  if(!Boolean.getBoolean("mortal_expansion.connectionsCheck") || ++ticks!=120)return;
  var block=BuiltInRegistries.BLOCK.get(ResourceLocation.parse("connectedglass:clear_glass"));
  if(block==Blocks.AIR)throw new AssertionError("Connected Glass test block missing");
  var state=block.defaultBlockState();var world=new World();var first=BlockPos.ZERO;var second=first.east();
  world.states.put(first,state);var view=new FancifierConnections(world);
  var isolated=quads(state,view,first);world.states.put(second,state);var joined=quads(state,view,first);
  if(isolated.isEmpty() || isolated.equals(joined))throw new AssertionError("Actual Fusion connection did not change UVs");
  for(int a:new int[]{1,20,37,99,100})for(int b:new int[]{1,20,37,99,100}){
   place(world,first,state,a);place(world,second,state,b);
   if(!joined.equals(quads(state,view,first)))throw new AssertionError("Texture connection changed at "+a+"/"+b);
  }
  org.slf4j.LoggerFactory.getLogger("FancifierConnectionsCheck").info("FANCIFIER_CONNECTIONS_CHECK_OK: actual Fusion UV connection; 25 mixed-opacity/native/legacy combinations unchanged");
  Minecraft.getInstance().stop();
 }
 private static java.util.List<Integer> quads(BlockState state,BlockAndTintGetter level,BlockPos pos){
  var model=Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
  var data=model.getModelData(level,pos,state,ModelData.EMPTY);var result=new java.util.ArrayList<Integer>();
  for(var layer:model.getRenderTypes(state,net.minecraft.util.RandomSource.create(0),data))
   for(var quad:model.getQuads(state,Direction.NORTH,net.minecraft.util.RandomSource.create(0),data,layer))
    for(int value:quad.getVertices())result.add(value);
  return result;
 }
 private static void place(World world,BlockPos pos,BlockState state,int opacity){
  world.entities.remove(pos);
  if(opacity==100){world.states.put(pos,state);return;}
  var proxy=ModBlocks.FANCIFIED_BLOCK.get().defaultBlockState();world.states.put(pos,proxy);
  var be=new FancifiedBlockEntity(pos,proxy);var registries=RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
  var tag=new CompoundTag();tag.put("Source",new ItemStack(state.getBlock()).save(registries));
  tag.put("OriginalState",NbtUtils.writeBlockState(state));tag.putInt("Opacity",opacity);be.loadWithComponents(tag,registries);world.entities.put(pos,be);
 }
 private static final class World implements BlockAndTintGetter {
  final java.util.Map<BlockPos,BlockState> states=new java.util.HashMap<>();
  final java.util.Map<BlockPos,BlockEntity> entities=new java.util.HashMap<>();
  public BlockState getBlockState(BlockPos pos){return states.getOrDefault(pos,Blocks.AIR.defaultBlockState());}
  public BlockEntity getBlockEntity(BlockPos pos){return entities.get(pos);}
  public FluidState getFluidState(BlockPos pos){return getBlockState(pos).getFluidState();}
  public int getHeight(){return 256;}public int getMinBuildHeight(){return 0;}
  public float getShade(Direction face,boolean shade){return 1;}
  public int getBlockTint(BlockPos pos,ColorResolver resolver){return 0xffffff;}
  public net.minecraft.world.level.lighting.LevelLightEngine getLightEngine(){return null;}
  public int getBrightness(LightLayer layer,BlockPos pos){return 15;}
  public int getRawBrightness(BlockPos pos,int amount){return 15;}
 }
}
