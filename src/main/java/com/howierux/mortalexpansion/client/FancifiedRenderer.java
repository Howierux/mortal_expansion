package com.howierux.mortalexpansion.client;
import com.howierux.mortalexpansion.blockentity.FancifiedBlockEntity;
import com.howierux.mortalexpansion.fancifier.FancifierRules;
import com.howierux.mortalexpansion.registry.ModItems;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
@EventBusSubscriber(modid="mortal_expansion",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class FancifiedRenderer implements BlockEntityRenderer<FancifiedBlockEntity> {
    public FancifiedRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public void render(FancifiedBlockEntity be,float delta,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        renderBlock(be.original(),be.opacity(),poses,buffers,light,overlay);
    }
    private static void renderBlock(BlockState state,int opacity,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        var translucent = RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);
        var alpha = new AlphaConsumer(buffers.getBuffer(translucent),opacity/100F);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state,poses,type->alpha,light,overlay,
                net.neoforged.neoforge.client.model.data.ModelData.EMPTY,translucent);
    }
    @SubscribeEvent public static void itemExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if(renderer==null)renderer=new BlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels()) {
                    @Override public void renderByItem(ItemStack stack,ItemDisplayContext display,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
                        var level=Minecraft.getInstance().level;if(level==null)return;
                        var original=FancifierRules.original(stack,level.registryAccess());
                        if(original.getItem() instanceof BlockItem item)renderBlock(item.getBlock().defaultBlockState(),FancifierRules.opacity(stack),poses,buffers,light,overlay);
                    }
                };
                return renderer;
            }
        },ModItems.FANCIFIED_BLOCK.get());
    }
    /** Keep baked vertex colors, UVs and lighting; multiply only alpha. */
    private record AlphaConsumer(VertexConsumer delegate,float alpha) implements VertexConsumer {
        @Override public VertexConsumer addVertex(float x,float y,float z){delegate.addVertex(x,y,z);return this;}
        @Override public VertexConsumer setColor(int r,int g,int b,int a){delegate.setColor(r,g,b,Math.round(a*alpha));return this;}
        @Override public VertexConsumer setUv(float u,float v){delegate.setUv(u,v);return this;}
        @Override public VertexConsumer setUv1(int u,int v){delegate.setUv1(u,v);return this;}
        @Override public VertexConsumer setUv2(int u,int v){delegate.setUv2(u,v);return this;}
        @Override public VertexConsumer setNormal(float x,float y,float z){delegate.setNormal(x,y,z);return this;}
    }
}
