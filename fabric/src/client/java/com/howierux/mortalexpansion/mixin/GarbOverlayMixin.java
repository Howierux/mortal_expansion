package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.combat.GhostGarbPhasing;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ScreenEffectRenderer.class)
public abstract class GarbOverlayMixin {
 @Inject(method="renderTex",at=@At("HEAD"),cancellable=true) private static void mortalExpansion$clearWall(TextureAtlasSprite sprite,PoseStack pose,CallbackInfo ci){var p=Minecraft.getInstance().player;if(p!=null&&GhostGarbPhasing.active(p))ci.cancel();}
}
