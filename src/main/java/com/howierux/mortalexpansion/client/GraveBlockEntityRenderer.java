package com.howierux.mortalexpansion.client;

import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.blockentity.GraveBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public final class GraveBlockEntityRenderer
        implements BlockEntityRenderer<GraveBlockEntity> {

    private final Font font;

    public GraveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(
            GraveBlockEntity grave,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        String text = grave.getInscription();
        if (text == null || text.isBlank()) {
            return;
        }

        Direction facing = grave.getBlockState().getValue(GraveBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.70D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0.0D, 0.0D, -0.257D);
        poseStack.scale(-0.0105F, -0.0105F, 0.0105F);

        float x = -font.width(text) / 2.0F;
        font.drawInBatch(
                text,
                x,
                0.0F,
                0xFF202020,
                false,
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.POLYGON_OFFSET,
                0,
                packedLight
        );

        poseStack.popPose();
    }
}
