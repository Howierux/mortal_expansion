package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.client.FadingRenderTypes;
import com.howierux.mortalexpansion.fancifier.FancifierRules;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.*;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(ItemRenderer.class)
public abstract class FadingItemRenderMixin {
    @ModifyVariable(method="render",at=@At("HEAD"),argsOnly=true)
    private MultiBufferSource mortalExpansion$buffers(MultiBufferSource buffers,ItemStack stack,ItemDisplayContext display,boolean left,PoseStack poses,MultiBufferSource ignored,int light,int overlay,net.minecraft.client.resources.model.BakedModel model){
        // Legacy wrapper items already apply alpha in their dedicated renderer.
        return stack.is(com.howierux.mortalexpansion.catalog.CatalogItems.FANCIFIED_BLOCK)?buffers:FadingRenderTypes.buffers(buffers,FancifierRules.opacity(stack));
    }
}
