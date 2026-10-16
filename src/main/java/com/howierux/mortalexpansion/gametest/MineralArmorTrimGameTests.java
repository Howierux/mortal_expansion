package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.inventory.SmithingMenu;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MineralArmorTrimGameTests {
    @GameTest(template="deferred_items_test")
    public static void vanilla_smithing_trims_all_mineral_armor_and_upgrade_preserves_trim(GameTestHelper h){
        var level=h.getLevel();var player=h.makeMockServerPlayerInLevel();player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
        var materials=level.registryAccess().lookupOrThrow(Registries.TRIM_MATERIAL);
        var patterns=level.registryAccess().lookupOrThrow(Registries.TRIM_PATTERN);
        var unbreaking=level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING);
        for(String family:new String[]{"bloodstone","spectralite","spectralite_haint"})for(String piece:new String[]{"helmet","chestplate","leggings","boots"}){
            var item=BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,family+"_"+piece));
            for(var template:new Item[]{Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE}){
                var base=new ItemStack(item);base.setDamageValue(7);base.set(DataComponents.CUSTOM_NAME,Component.literal("Trimmed keepsake"));base.enchant(unbreaking,2);
                var menu=new SmithingMenu(1,player.getInventory());
                h.assertTrue(menu.getSlot(1).mayPlace(base),"Native smithing table accepts mod armor");
                menu.getSlot(0).set(new ItemStack(template));menu.getSlot(1).set(base);menu.getSlot(2).set(new ItemStack(Items.AMETHYST_SHARD));menu.createResult();
                h.assertTrue(menu.getSlot(3).mayPickup(player),"Native trim recipe produces a usable output");
                var out=menu.getSlot(3).remove(1);menu.getSlot(3).onTake(player,out);
                var trim=out.get(DataComponents.TRIM);
                h.assertTrue(out.is(item) && trim!=null && trim.material().is(TrimMaterials.AMETHYST)
                        && trim.pattern().is(template==Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE?TrimPatterns.SENTRY:TrimPatterns.FLOW),"Correct trim pattern and material");
                h.assertTrue(out.getDamageValue()==7 && out.getHoverName().getString().equals("Trimmed keepsake")
                        && out.get(DataComponents.ENCHANTMENTS).getLevel(unbreaking)==2,"Trim application retains wear, name and enchantments");
                h.assertTrue(menu.getSlot(0).getItem().isEmpty() && menu.getSlot(1).getItem().isEmpty() && menu.getSlot(2).getItem().isEmpty(),"Trimming consumes one template, armor and material");
                if(family.equals("bloodstone")){
                    var input=new SmithingRecipeInput(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),out,new ItemStack(Items.NETHERITE_INGOT));
                    var recipe=level.getRecipeManager().getRecipeFor(RecipeType.SMITHING,input,level).orElseThrow().value();
                    var upgraded=recipe.assemble(input,level.registryAccess());
                    h.assertTrue(upgraded.is(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MortalExpansion.MOD_ID,"spectralite_"+piece)))
                            && trim.equals(upgraded.get(DataComponents.TRIM)),"Bloodstone to Spectralite smithing retains the trim");
                }
            }
        }
        player.discard();h.succeed();
    }
}
