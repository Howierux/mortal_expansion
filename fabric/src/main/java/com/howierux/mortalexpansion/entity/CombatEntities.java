package com.howierux.mortalexpansion.entity;
import com.howierux.mortalexpansion.entity.mob.*;
import com.howierux.mortalexpansion.catalog.Catalog;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import java.util.*;
/** Fabric accepts a null vanilla DFU name; the registry supplies each persistent namespaced ID.
 * Entity registration and native attribute defaults remain independent of client models. */
public final class CombatEntities {
 public static final Map<String,EntityType<? extends Mob>> MOBS=new LinkedHashMap<>();
 public static final List<Item> EGGS=new ArrayList<>();
 private static <T extends Mob> EntityType<T> mob(String name,EntityType.EntityFactory<T> factory,MobCategory category,float width,float height){var type=Registry.register(BuiltInRegistries.ENTITY_TYPE,Catalog.id(name),EntityType.Builder.of(factory,category).sized(width,height).clientTrackingRange(10).build(null));MOBS.put(name,type);return type;}
 public static final EntityType<GhostEntity> GHOST=mob("ghost",GhostEntity::new,MobCategory.MONSTER,.6F,1.95F);
 public static final EntityType<EmpoweredPhantomEntity> PHANTOM=mob("phantom",EmpoweredPhantomEntity::new,MobCategory.MONSTER,.6F,1.95F);
 public static final EntityType<SpiritEntity> SPIRIT=mob("spirit",SpiritEntity::new,MobCategory.CREATURE,.6F,1.8F);
 public static final EntityType<ElementalEntity> ELEMENTAL=mob("elemental",ElementalEntity::new,MobCategory.CREATURE,.8F,2.1F);
 public static final EntityType<HauntEntity> HAUNT=mob("haunt",HauntEntity::new,MobCategory.MONSTER,.8F,2.1F);
 public static final EntityType<HaintEntity> HAINT=mob("haint",HaintEntity::new,MobCategory.MONSTER,1,2.4F);
 public static final EntityType<MummyEntity> MUMMY=mob("mummy",MummyEntity::new,MobCategory.MONSTER,.6F,1.95F);
 public static final EntityType<PharaohlEntity> PHARAOHL=mob("pharaohl",PharaohlEntity::new,MobCategory.MONSTER,.9F,2.5F);
 public static final EntityType<DemonEntity> DEMON=mob("demon",DemonEntity::new,MobCategory.MONSTER,.6F,1.95F);
 public static final EntityType<MortalGolemEntity> MORTAL_GOLEM=mob("mortal_golem",MortalGolemEntity::new,MobCategory.CREATURE,1.4F,2.7F);
 public static final EntityType<SpectralBoltEntity> SPECTRAL_BOLT=Registry.register(BuiltInRegistries.ENTITY_TYPE,Catalog.id("spectral_bolt"),EntityType.Builder.<SpectralBoltEntity>of(SpectralBoltEntity::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(8).updateInterval(2).build(null));
 public static void initialize(){
  FabricDefaultAttributeRegistry.register(GHOST,GhostEntity.createAttributes());FabricDefaultAttributeRegistry.register(PHANTOM,EmpoweredPhantomEntity.createAttributes());FabricDefaultAttributeRegistry.register(SPIRIT,SpiritEntity.createAttributes());FabricDefaultAttributeRegistry.register(ELEMENTAL,ElementalEntity.createAttributes());FabricDefaultAttributeRegistry.register(HAUNT,HauntEntity.createAttributes());FabricDefaultAttributeRegistry.register(HAINT,HaintEntity.createAttributes());FabricDefaultAttributeRegistry.register(MUMMY,MummyEntity.createAttributes());FabricDefaultAttributeRegistry.register(PHARAOHL,PharaohlEntity.createAttributes());FabricDefaultAttributeRegistry.register(DEMON,DemonEntity.createAttributes());FabricDefaultAttributeRegistry.register(MORTAL_GOLEM,MortalGolemEntity.createAttributes());
  MOBS.forEach((name,type)->EGGS.add(Registry.register(BuiltInRegistries.ITEM,Catalog.id(name+"_spawn_egg"),new SpawnEggItem(type,0x486452,0xB4E9C2,new Item.Properties()))));
 }
}
