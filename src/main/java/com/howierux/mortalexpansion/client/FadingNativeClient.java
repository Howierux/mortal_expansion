package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.fancifier.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import java.util.concurrent.ConcurrentHashMap;
/** Immutable snapshots are safe for chunk-mesh worker queries. No world scan or server ticker. */
@EventBusSubscriber(modid="mortal_expansion",value=Dist.CLIENT)
public final class FadingNativeClient {
    private record ChunkView(LevelChunk chunk,FadingData data){}
    private static final ConcurrentHashMap<Long,ChunkView> CHUNKS=new ConcurrentHashMap<>();
    private static volatile Object world;
    public static void initialize(){FadingWorld.clientChanged=(chunk,data)->Minecraft.getInstance().execute(()->changed(chunk,data));}
    private static void changed(LevelChunk chunk,FadingData data){
        var mc=Minecraft.getInstance();if(mc.level!=chunk.getLevel())return;
        if(world!=mc.level){CHUNKS.clear();world=mc.level;}
        var previous=CHUNKS.get(chunk.getPos().toLong());
        if(data.entries().isEmpty())CHUNKS.remove(chunk.getPos().toLong());else CHUNKS.put(chunk.getPos().toLong(),new ChunkView(chunk,data));
        int minX=chunk.getPos().getMinBlockX(),minZ=chunk.getPos().getMinBlockZ();
        // One bounded chunk-neighborhood invalidation, not one request per stored block.
        if(previous!=null || !data.entries().isEmpty())mc.levelRenderer.setBlocksDirty(minX-1,chunk.getMinBuildHeight(),minZ-1,minX+16,chunk.getMaxBuildHeight(),minZ+16);
    }
    public static int opacity(BlockPos pos){
        var mc=Minecraft.getInstance();if(mc==null || world!=mc.level)return 100;
        var view=CHUNKS.get(net.minecraft.world.level.ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4));
        return view==null?100:view.data.entries().getOrDefault(FadingWorld.index(view.chunk,pos),100);
    }
    @SubscribeEvent public static void dimension(net.neoforged.neoforge.client.event.ClientTickEvent.Post event){var level=Minecraft.getInstance().level;if(world!=level){CHUNKS.clear();world=level;}}
    @SubscribeEvent public static void levelUnload(net.neoforged.neoforge.event.level.LevelEvent.Unload event){if(event.getLevel()==world){CHUNKS.clear();world=null;}}
    @SubscribeEvent public static void unload(net.neoforged.neoforge.event.level.ChunkEvent.Unload event){if(event.getLevel()==world)CHUNKS.remove(event.getChunk().getPos().toLong());}
    @SubscribeEvent public static void leave(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event){CHUNKS.clear();world=null;}
    @SubscribeEvent public static void tooltip(net.neoforged.neoforge.event.entity.player.ItemTooltipEvent event){
        int opacity=FancifierRules.opacity(event.getItemStack());if(opacity<100)event.getToolTip().add(net.minecraft.network.chat.Component.translatable("gui.mortal_expansion.fancifier.opacity",opacity));
    }
    @SubscribeEvent public static void render(RenderLevelStageEvent event){
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)return;
        var mc=Minecraft.getInstance();if(mc.level==null || mc.level!=world)return;
        var camera=event.getCamera().getPosition();var poses=event.getPoseStack();var buffers=mc.renderBuffers().bufferSource();
        for(var view:CHUNKS.values())for(var entry:view.data.entries().entrySet()){
            var pos=FadingWorld.position(view.chunk,entry.getKey());
            if(!event.getFrustum().isVisible(new AABB(pos)))continue;
            var state=mc.level.getBlockState(pos);if(state.getRenderShape()!=RenderShape.MODEL)continue;
            poses.pushPose();poses.translate(pos.getX()-camera.x,pos.getY()-camera.y,pos.getZ()-camera.z);
            FancifiedRenderer.renderWorldModel(state,entry.getValue(),mc.level,pos,poses,buffers,LevelRenderer.getLightColor(mc.level,pos),OverlayTexture.NO_OVERLAY);
            poses.popPose();
        }
        buffers.endBatch(net.minecraft.client.renderer.RenderType.entityTranslucentCull(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS));
    }
}
