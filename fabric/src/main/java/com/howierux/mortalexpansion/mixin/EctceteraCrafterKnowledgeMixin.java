package com.howierux.mortalexpansion.mixin;
import com.howierux.mortalexpansion.guide.*;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(CrafterBlock.class)
public abstract class EctceteraCrafterKnowledgeMixin {
 @Inject(method="dispenseFrom",at=@At("HEAD"),cancellable=true)
 private void gate(BlockState state,ServerLevel level,BlockPos pos,CallbackInfo ci){
  if(!(level.getBlockEntity(pos) instanceof net.minecraft.world.level.block.entity.CrafterBlockEntity crafter))return;
  var slots=new java.util.ArrayList<net.minecraft.world.item.ItemStack>();for(int i=0;i<9;i++)slots.add(crafter.getItem(i));
  var recipe=level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,CraftingInput.of(3,3,slots),level);if(recipe.isEmpty())return;
  int bit=InsightSecret.required(recipe.get().id());if(bit!=0&&(KnowledgeData.get((AttachmentTarget)crafter,KnowledgeData.CRAFTER)&bit)==0){level.levelEvent(1050,pos,0);ci.cancel();}
 }
}
