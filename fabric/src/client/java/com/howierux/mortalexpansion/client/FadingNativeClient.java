package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.fancifier.*;
import com.howierux.mortalexpansion.catalog.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.AABB;
import java.util.concurrent.ConcurrentHashMap;
/** Immutable snapshots are safe for chunk-mesh worker queries. No world scan or server ticker. */
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
    private static final java.util.Map<Long,LevelChunk> LOADED=new java.util.HashMap<>();
    public static void fabricInitialize(){initialize();
     net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD.register((level,chunk)->{if(world!=level){CHUNKS.clear();LOADED.clear();world=level;}LOADED.put(chunk.getPos().toLong(),chunk);});
     net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_UNLOAD.register((level,chunk)->{LOADED.remove(chunk.getPos().toLong());CHUNKS.remove(chunk.getPos().toLong());});
     net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc->{if(world!=mc.level){CHUNKS.clear();LOADED.clear();world=mc.level;}if(mc.level==null||mc.level.getGameTime()%5!=0)return;for(var chunk:LOADED.values()){var data=((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget)chunk).getAttachedOrCreate(com.howierux.mortalexpansion.content.ContentRuntime.FADING_BLOCKS);var previous=CHUNKS.get(chunk.getPos().toLong());if(previous==null?!data.entries().isEmpty():previous.data()!=data)changed(chunk,data);}});
     net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack,context,type,lines)->{int alpha=FancifierRules.opacity(stack);if(alpha<100)lines.add(net.minecraft.network.chat.Component.translatable("gui.mortal_expansion.fancifier.opacity",alpha));});
     net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AFTER_TRANSLUCENT.register(FadingNativeClient::render);
    }
    public static void render(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext event){

        var mc=Minecraft.getInstance();if(mc.level==null || mc.level!=world)return;
        var camera=event.camera().getPosition();var poses=event.matrixStack();var buffers=mc.renderBuffers().bufferSource();
        for(var view:CHUNKS.values())for(var entry:view.data.entries().entrySet()){
            var pos=FadingWorld.position(view.chunk,entry.getKey());
            if(!event.frustum().isVisible(new AABB(pos)))continue;
            var state=mc.level.getBlockState(pos);if(state.getRenderShape()!=RenderShape.MODEL)continue;
            poses.pushPose();poses.translate(pos.getX()-camera.x,pos.getY()-camera.y,pos.getZ()-camera.z);
            FancifiedRenderer.renderWorldModel(state,entry.getValue(),mc.level,pos,poses,buffers,LevelRenderer.getLightColor(mc.level,pos),OverlayTexture.NO_OVERLAY);
            poses.popPose();
        }
        var material=net.minecraft.client.renderer.RenderType.entityTranslucentCull(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS);
        buffers.endBatch(FadingRenderTypes.material(material));
        buffers.endBatch(FadingRenderTypes.material(material,1));
    }
}
