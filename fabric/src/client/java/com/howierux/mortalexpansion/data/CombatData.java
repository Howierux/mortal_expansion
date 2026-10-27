package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.combat.*;
import com.howierux.mortalexpansion.effect.ModDamageTypes;
import com.howierux.mortalexpansion.entity.CombatEntities;
import net.fabricmc.fabric.api.datagen.v1.*;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.data.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.tags.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import java.util.concurrent.CompletableFuture;
public final class CombatData {
 public static final TagKey<Item> ENCHANTABLE=TagKey.create(Registries.ITEM,Catalog.id("enchantable/demi_tangible"));
 public static void damageTypes(BootstrapContext<DamageType> context){
  context.register(ModDamageTypes.DEMI_TANGIBLE_PLAYER,new DamageType("player",DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,.1F));
  context.register(ModDamageTypes.DEMI_TANGIBLE_MOB,new DamageType("mob",DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,.1F));
  context.register(ModDamageTypes.SANCTIFICATION,new DamageType("sanctification",DamageScaling.NEVER,0));
 }
 public static void enchantments(BootstrapContext<Enchantment> context){context.register(CombatRuntime.DEMI_ENCHANTMENT,Enchantment.enchantment(Enchantment.definition(context.lookup(Registries.ITEM).getOrThrow(ENCHANTABLE),1,1,Enchantment.constantCost(10),Enchantment.constantCost(30),4,EquipmentSlotGroup.HAND)).build(Catalog.id("demi_tangible")));}
 public static final class RegistryData extends FabricDynamicRegistryProvider {
  public RegistryData(FabricDataOutput o,CompletableFuture<HolderLookup.Provider> l){super(o,l);}
  @Override protected void configure(HolderLookup.Provider lookup,Entries entries){entries.addAll(lookup.lookupOrThrow(Registries.DAMAGE_TYPE));entries.addAll(lookup.lookupOrThrow(Registries.ENCHANTMENT));}
  @Override public String getName(){return "Spectral damage and enchantments";}
 }
 public static final class Entities extends FabricTagProvider<EntityType<?>> {
  public Entities(FabricDataOutput o,CompletableFuture<HolderLookup.Provider> l){super(o,Registries.ENTITY_TYPE,l);}
  @Override protected void addTags(HolderLookup.Provider lookup){
   var intangible=tag(com.howierux.mortalexpansion.entity.ModEntityTags.INTANGIBLE);intangible.add(EntityType.PHANTOM.builtInRegistryHolder().key(),EntityType.GHAST.builtInRegistryHolder().key());
   for(var type:new EntityType<?>[]{CombatEntities.DEMON,CombatEntities.GHOST,CombatEntities.SPIRIT,CombatEntities.ELEMENTAL,CombatEntities.HAUNT,CombatEntities.HAINT})intangible.add(type.builtInRegistryHolder().key());
   for(var type:CombatEntities.MOBS.values())if(type!=CombatEntities.MORTAL_GOLEM)tag(EntityTypeTags.SENSITIVE_TO_SMITE).add(type.builtInRegistryHolder().key());
  }
 }
 public static final class DamageTags extends FabricTagProvider<DamageType> {
  public DamageTags(FabricDataOutput o,CompletableFuture<HolderLookup.Provider> l){super(o,Registries.DAMAGE_TYPE,l);}
  @Override protected void addTags(HolderLookup.Provider lookup){
   // NeoForge dev f080fdc: preserve public tag contracts with native Fabric builders.
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,net.minecraft.resources.ResourceLocation.parse("minecraft:bypasses_armor"))).add(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:sanctification")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,net.minecraft.resources.ResourceLocation.parse("neoforge:is_magic"))).add(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:demi_tangible_player")));
   tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,net.minecraft.resources.ResourceLocation.parse("neoforge:is_magic"))).add(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,net.minecraft.resources.ResourceLocation.parse("mortal_expansion:demi_tangible_mob")));

   tag(TagKey.create(Registries.DAMAGE_TYPE,ResourceLocation.parse("c:is_magic"))).add(DamageTypes.MAGIC,DamageTypes.INDIRECT_MAGIC,DamageTypes.THORNS,DamageTypes.WITHER,DamageTypes.DRAGON_BREATH,DamageTypes.SONIC_BOOM,ModDamageTypes.DEMI_TANGIBLE_PLAYER,ModDamageTypes.DEMI_TANGIBLE_MOB,ModDamageTypes.SANCTIFICATION);
  }
 }
 /** Native codecs generate loot; only spawn-egg parent models need a small JSON envelope. */
 public static final class Assets implements DataProvider {
  private final FabricDataOutput output;private final CompletableFuture<HolderLookup.Provider> lookup;
  public Assets(FabricDataOutput o,CompletableFuture<HolderLookup.Provider> l){output=o;lookup=l;}
  @Override public String getName(){return "Spectral mob loot and spawn eggs";}
  @Override public CompletableFuture<?> run(CachedOutput cache){return lookup.thenCompose(registries->{
   var tasks=new java.util.ArrayList<CompletableFuture<?>>();var models=output.createPathProvider(PackOutput.Target.RESOURCE_PACK,"models/item");var loot=output.createPathProvider(PackOutput.Target.DATA_PACK,"loot_table/entities");
   for(var entry:CombatEntities.MOBS.entrySet()){
    String name=entry.getKey();var model=new com.google.gson.JsonObject();model.addProperty("parent","minecraft:item/template_spawn_egg");tasks.add(DataProvider.saveStable(cache,model,models.json(Catalog.id(name+"_spawn_egg"))));
    var table=LootTable.lootTable().setParamSet(LootContextParamSets.ENTITY);
    Item drop=switch(name){case "ghost","spirit","elemental","haunt","haint"->CatalogItems.RAW_ECTOPLASM;case "demon"->CatalogItems.DEMONIC_ASH;case "mummy","phantom","pharaohl"->Items.ROTTEN_FLESH;default->null;};
    if(drop!=null)table.withPool(LootPool.lootPool().add(LootItem.lootTableItem(drop)));
    if(name.equals("pharaohl"))table.withPool(LootPool.lootPool().add(LootItem.lootTableItem(CatalogItems.PHARAOHL_SCEPTER)));
    tasks.add(DataProvider.saveStable(cache,registries,LootTable.DIRECT_CODEC,table.build(),loot.json(Catalog.id(name))));
   }
   return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
  });}
 }
}
