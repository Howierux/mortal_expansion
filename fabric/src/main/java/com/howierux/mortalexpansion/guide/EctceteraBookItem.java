package com.howierux.mortalexpansion.guide;
import com.howierux.mortalexpansion.catalog.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
public final class EctceteraBookItem extends Item {
 public EctceteraBookItem(Properties p){super(p.component(Catalog.BOOK_TIER,0));}
 @Override public void onCraftedBy(ItemStack stack,Level level,Player player){super.onCraftedBy(stack,level,player);if(player instanceof net.minecraft.server.level.ServerPlayer p)DarkholdProgression.crafted(p,stack);}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
  var stack=player.getItemInHand(hand);
  if(player instanceof net.minecraft.server.level.ServerPlayer p){SpectralInsight.absorb(p,stack);p.inventoryMenu.broadcastChanges();p.containerMenu.broadcastChanges();net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(p,new BookOpenPayload(hand));}
  return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
 }
 @Override public Component getName(ItemStack stack){return EctceteraProgression.tier(stack)==4?Component.translatable("item.mortal_expansion.darkhold"):super.getName(stack);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<Component> lines,TooltipFlag flag){int tier=EctceteraProgression.tier(stack);lines.add(Component.translatable("tooltip.mortal_expansion.ectcetera.tier."+tier).withStyle(net.minecraft.ChatFormatting.AQUA));if(tier>=2)lines.add(Component.translatable("tooltip.mortal_expansion.ectcetera.aware",Integer.bitCount(SpectralInsight.secrets(stack))).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));if(tier==4)lines.add(Component.translatable("tooltip.mortal_expansion.fabric.darkhold_pending").withStyle(net.minecraft.ChatFormatting.GRAY));if(tier<4)lines.add(Component.translatable("tooltip.mortal_expansion.ectcetera.upgrade",EctceteraProgression.upgradeIngredient(tier).getDescription()).withStyle(net.minecraft.ChatFormatting.GRAY));}
 @Override public boolean isFoil(ItemStack stack){return true;}
 // Missing function: non-native backpacks and seven sin powers are later slices.
}
