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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.Ingredient;
public final class CatalogArmor {
 public static final Holder<ArmorMaterial> BLOODSTONE=material("bloodstone","bloodstone_armor",false,false);
 public static final Holder<ArmorMaterial> SPECTRALITE=material("spectralite","spectralite_armor",false,true);
 public static final Holder<ArmorMaterial> GHOST_GARB=material("ghost_garb","ghost_garb_woven",true,false);
 public static final Holder<ArmorMaterial> HAINT_GARB=material("haint_garb","haint_garb_woven",true,false);
 public static final Holder<ArmorMaterial> SPECTRALITE_HAINT=material("spectralite_haint","spectralite_haint_woven",false,true);
 static Holder<ArmorMaterial> material(String id,String skin,boolean garb,boolean spectral) {
  var defense=new EnumMap<ArmorItem.Type,Integer>(ArmorItem.Type.class);
  defense.put(ArmorItem.Type.HELMET,garb?1:3); defense.put(ArmorItem.Type.CHESTPLATE,garb?3:8);
  defense.put(ArmorItem.Type.LEGGINGS,garb?2:6); defense.put(ArmorItem.Type.BOOTS,garb?1:3); defense.put(ArmorItem.Type.BODY,garb?3:11);
  return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL,Catalog.id(id),new ArmorMaterial(defense,garb||spectral?15:10,garb?SoundEvents.ARMOR_EQUIP_LEATHER:spectral?SoundEvents.ARMOR_EQUIP_NETHERITE:SoundEvents.ARMOR_EQUIP_DIAMOND,
   () -> Ingredient.of(spectral?Items.NETHERITE_INGOT:CatalogItems.ALL.get(garb?(id.equals("ghost_garb")?"ectothread":"purified_ectothread"):"bloodstone")),List.of(new ArmorMaterial.Layer(Catalog.id(skin))),garb?0:spectral?3:2,spectral?0.1F:0));
 }
}
