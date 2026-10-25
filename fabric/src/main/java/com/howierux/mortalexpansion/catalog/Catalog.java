package com.howierux.mortalexpansion.catalog;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.util.valueproviders.UniformInt;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
public final class Catalog {
 public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("mortal_expansion",path); }
 public static net.minecraft.world.item.component.ItemAttributeModifiers swordAttributes(Tier tier,float damage,float speed) {
  return net.minecraft.world.item.component.ItemAttributeModifiers.builder()
   .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,new net.minecraft.world.entity.ai.attributes.AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,damage+tier.getAttackDamageBonus(),net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE),net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND)
   .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,new net.minecraft.world.entity.ai.attributes.AttributeModifier(Item.BASE_ATTACK_SPEED_ID,speed,net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE),net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND).build();
 }
 public static final net.minecraft.core.component.DataComponentType<Integer> BOOK_TIER=Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,id("ectcetera_tier"),net.minecraft.core.component.DataComponentType.<Integer>builder().persistent(com.mojang.serialization.Codec.intRange(0,4)).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT).build());
 public static void initialize() {
  CatalogFluids.initialize(); CatalogBlocks.initialize(); CatalogItems.initialize(); com.howierux.mortalexpansion.machine.Machines.initialize();
  Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,id("catalog"),FabricItemGroup.builder()
   .title(Component.literal("Mortal Expansion")).icon(() -> new ItemStack(CatalogItems.BOOK_OF_ECTCETERA))
   .displayItems((parameters,output) -> { CatalogItems.ALL.values().forEach(output::accept); for(int tier=1;tier<=4;tier++) { var book=new ItemStack(CatalogItems.BOOK_OF_ECTCETERA); book.set(BOOK_TIER,tier); output.accept(book); } }).build());
 }
}
