package com.howierux.mortalexpansion.data;
import com.howierux.mortalexpansion.catalog.*;
import net.fabricmc.fabric.api.datagen.v1.*;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public final class CatalogLoot extends FabricBlockLootTableProvider {
 public CatalogLoot(FabricDataOutput o,CompletableFuture<HolderLookup.Provider> l) { super(o,l); }
 @Override public void generate() {
  CatalogBlocks.ALL.forEach((id,b) -> {
   if(id.startsWith("bloodstone_ore")) add(b,createOreDrop(b,CatalogItems.BLOODSTONE));
   else if(id.equals("wisp_o_lantern")) add(b,createSilkTouchDispatchTable(b,applyExplosionCondition(b,net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem(Items.CARVED_PUMPKIN))));
   else if(id.equals("haunted_farmland")) dropOther(b,Items.DIRT);
   else if(id.equals("spiritwood_leaves")) add(b,createLeavesDrops(b,CatalogBlocks.SPIRITWOOD_SAPLING,NORMAL_LEAVES_SAPLING_CHANCES));
   else if(id.endsWith("_door")) add(b,createDoorTable(b));
   else if(id.endsWith("_slab")) add(b,createSlabItemTable(b));
   else if(CatalogItems.ALL.containsKey(id)) dropSelf(b);
   else if(id.equals("spirit_wall_torch")) dropOther(b,CatalogItems.SPIRIT_TORCH);
   else add(b,noDrop());
  });
 }
}
