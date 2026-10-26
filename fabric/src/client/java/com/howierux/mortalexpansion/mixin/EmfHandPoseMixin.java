package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.haunt.EmfReaderItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Vanilla has no first-person transform callback; keep the reader face visible in either hand. */
@Mixin(ItemInHandRenderer.class)
public abstract class EmfHandPoseMixin {
 @Shadow public abstract void renderItem(LivingEntity entity,ItemStack stack,ItemDisplayContext context,boolean left,PoseStack pose,MultiBufferSource buffers,int light);
 @Inject(method="renderArmWithItem",at=@At("HEAD"),cancellable=true)
 private void readerPose(AbstractClientPlayer player,float partial,float pitch,InteractionHand hand,float swing,ItemStack stack,float equip,PoseStack pose,MultiBufferSource buffers,int light,CallbackInfo ci){
  if(!(stack.getItem() instanceof EmfReaderItem))return;
  boolean left=(hand==InteractionHand.MAIN_HAND?player.getMainArm():player.getMainArm().getOpposite())==HumanoidArm.LEFT;
  boolean personal=EmfReaderItem.personal(stack,player);int side=left?-1:1;
  pose.pushPose();pose.translate(side*(personal?.30F:.42F),(personal?-.25F:-.35F)-equip*.6F,personal?-.62F:-.78F);
  if(personal)pose.mulPose(Axis.XP.rotationDegrees(-15));
  renderItem(player,stack,left?ItemDisplayContext.FIRST_PERSON_LEFT_HAND:ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,left,pose,buffers,light);pose.popPose();ci.cancel();
 }
}
