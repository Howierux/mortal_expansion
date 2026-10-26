package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.catalog.*;
import net.fabricmc.fabric.api.datagen.v1.*;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.*;
import java.util.concurrent.CompletableFuture;

/** Native tags keep ordinary mining, wood connections, enchanting and armor trims functional. */
public final class CatalogBlockTags extends FabricTagProvider<Block> {
 public CatalogBlockTags(FabricDataOutput o,CompletableFuture<HolderLookup.Provider> l) { super(o,Registries.BLOCK,l); }
 @Override protected void addTags(HolderLookup.Provider lookup) {
  var spectralSolid=tag(com.howierux.mortalexpansion.entity.mob.SpectralPhasingRules.INTANGIBLE_SOLID);
  CatalogBlocks.ALL.forEach((id,b) -> {
   if(id.contains("spiritwood")||id.equals("gravestone")||id.equals("purified_gravestone")||id.equals("grave")||id.equals("holy_salt"))spectralSolid.add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
   if(id.contains("spiritwood")&&!id.endsWith("leaves")&&!id.endsWith("sapling")) tag(BlockTags.MINEABLE_WITH_AXE).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
   else if(!id.contains("water")&&!id.contains("ectoplasm")&&!id.contains("torch")&&!id.contains("salt")&&!id.contains("dreamcatcher")&&!id.equals("grave_moss")&&!id.endsWith("leaves")&&!id.endsWith("sapling")) tag(BlockTags.MINEABLE_WITH_PICKAXE).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
   if(id.endsWith("_log")) { tag(BlockTags.LOGS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); tag(BlockTags.LOGS_THAT_BURN).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); }
   if(id.endsWith("_planks")) tag(BlockTags.PLANKS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
   if(id.endsWith("_leaves")) tag(BlockTags.LEAVES).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
   if(id.endsWith("_sapling")) tag(BlockTags.SAPLINGS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
   if(id.endsWith("_fence")) tag(BlockTags.FENCES).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
   if(id.contains("spiritwood")&&id.endsWith("_fence")) tag(BlockTags.WOODEN_FENCES).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
   if(id.endsWith("_fence_gate")) tag(BlockTags.FENCE_GATES).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
   if(id.endsWith("_stairs")) { tag(BlockTags.STAIRS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); tag(BlockTags.WOODEN_STAIRS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); }
   if(id.endsWith("_slab")) { tag(BlockTags.SLABS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); tag(BlockTags.WOODEN_SLABS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); }
   if(id.endsWith("_door")) { tag(BlockTags.DOORS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); tag(BlockTags.WOODEN_DOORS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); }
   if(id.endsWith("_trapdoor")) { tag(BlockTags.TRAPDOORS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); tag(BlockTags.WOODEN_TRAPDOORS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); }
   if(id.endsWith("_button")) { tag(BlockTags.BUTTONS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); tag(BlockTags.WOODEN_BUTTONS).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); }
   if(id.endsWith("_pressure_plate")) { tag(BlockTags.PRESSURE_PLATES).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); tag(BlockTags.WOODEN_PRESSURE_PLATES).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id))); }
   if(id.startsWith("bloodstone_ore")) tag(BlockTags.NEEDS_IRON_TOOL).add(net.minecraft.resources.ResourceKey.create(Registries.BLOCK,Catalog.id(id)));
  });
 }
 public static final class Items extends FabricTagProvider<Item> {
  public Items(FabricDataOutput o,CompletableFuture<HolderLookup.Provider> l) { super(o,Registries.ITEM,l); }
  @Override protected void addTags(HolderLookup.Provider lookup) {
   var demi=tag(com.howierux.mortalexpansion.combat.ModCombatItemTags.DEMI_TANGIBLE);var armor=tag(com.howierux.mortalexpansion.combat.ModCombatItemTags.SPECTRAL_PROTECTIVE_ARMOR);
   CatalogItems.ALL.forEach((id,item)->{if((id.startsWith("bloodstone_")||id.startsWith("spectralite_"))&&(item instanceof net.minecraft.world.item.DiggerItem||item instanceof net.minecraft.world.item.SwordItem)||id.equals("gravestone_sword")||id.equals("purified_gravestone_sword"))demi.add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));if(id.startsWith("spectralite_")&&item instanceof net.minecraft.world.item.ArmorItem)armor.add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));});
   var enchantable=tag(CombatData.ENCHANTABLE);for(var item:net.minecraft.core.registries.BuiltInRegistries.ITEM)if(item!=net.minecraft.world.item.Items.AIR)enchantable.add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item)));
   tag(com.howierux.mortalexpansion.entity.mob.MortalGolemEntity.LURE_ITEMS).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,net.minecraft.resources.ResourceLocation.withDefaultNamespace("rotten_flesh")),net.minecraft.resources.ResourceKey.create(Registries.ITEM,net.minecraft.resources.ResourceLocation.withDefaultNamespace("bone")),net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id("raw_ectoplasm")),net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id("pure_ectoplasm")));
   var offerings=tag(com.howierux.mortalexpansion.haunt.PurityOfferings.TAG);
   for(String id:new String[]{"raw_ectoplasm","spirit_boon","spiritwood_log","purified_spiritwood_log","grave_moss","holy_salt","pure_ectoplasm"})offerings.add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));
   tag(com.howierux.mortalexpansion.haunt.PurityOfferings.IMPURITY).addTag(com.howierux.mortalexpansion.haunt.PurityOfferings.TAG);
   tag(com.howierux.mortalexpansion.machine.PurificationStationEntity.REAGENTS).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id("grave_moss")),net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id("holy_salt")));
   CatalogItems.ALL.forEach((id,item) -> {
    if(item instanceof ArmorItem a) {
     tag(ItemTags.TRIMMABLE_ARMOR).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id))); tag(ItemTags.ARMOR_ENCHANTABLE).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));
     tag(switch(a.getType()) {case HELMET -> ItemTags.HEAD_ARMOR; case CHESTPLATE -> ItemTags.CHEST_ARMOR; case LEGGINGS -> ItemTags.LEG_ARMOR; default -> ItemTags.FOOT_ARMOR;}).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));
    }
    if(item instanceof SwordItem) tag(ItemTags.SWORDS).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));
    if(item instanceof PickaxeItem) tag(ItemTags.PICKAXES).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));
    if(item instanceof AxeItem) tag(ItemTags.AXES).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));
    if(item instanceof ShovelItem) tag(ItemTags.SHOVELS).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));
    if(item instanceof HoeItem) tag(ItemTags.HOES).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id)));
    if(item instanceof TieredItem || item instanceof ArmorItem) { tag(ItemTags.DURABILITY_ENCHANTABLE).add(net.minecraft.resources.ResourceKey.create(Registries.ITEM,Catalog.id(id))); }
   });
  }
 }
}
