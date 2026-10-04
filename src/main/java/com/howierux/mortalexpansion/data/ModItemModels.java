package com.howierux.mortalexpansion.data;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModFluids;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModItemModels extends ItemModelProvider {
    public ModItemModels(PackOutput output, ExistingFileHelper helper) {
        super(output, MortalExpansion.MOD_ID, helper);
    }
    @Override protected void registerModels() {
        {
            var model = this.getBuilder("bloodstone").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "mortal_expansion:item/bloodstone");
            model.texture("layer1", "mortal_expansion:item/bloodstone_shimmer");
        }
        {
            var model = this.getBuilder("bloodstone_axe").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/bloodstone_axe");
            model.texture("layer1", "mortal_expansion:item/bloodstone_axe_shimmer");
        }
        {
            var model = this.getBuilder("bloodstone_boots").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/diamond_boots");
        }
        {
            var model = this.getBuilder("bloodstone_chestplate").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/diamond_chestplate");
        }
        {
            var model = this.getBuilder("bloodstone_helmet").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/diamond_helmet");
        }
        {
            var model = this.getBuilder("bloodstone_hoe").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/bloodstone_hoe");
            model.texture("layer1", "mortal_expansion:item/bloodstone_hoe_shimmer");
        }
        {
            var model = this.getBuilder("bloodstone_leggings").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/diamond_leggings");
        }
        {
            var model = this.getBuilder("bloodstone_ore").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/bloodstone_ore"));
        }
        {
            var model = this.getBuilder("bloodstone_ore_stone").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/bloodstone_ore_stone"));
        }
        {
            var model = this.getBuilder("bloodstone_pickaxe").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/bloodstone_pickaxe");
            model.texture("layer1", "mortal_expansion:item/bloodstone_pickaxe_shimmer");
        }
        {
            var model = this.getBuilder("bloodstone_shovel").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/bloodstone_shovel");
            model.texture("layer1", "mortal_expansion:item/bloodstone_shovel_shimmer");
        }
        {
            var model = this.getBuilder("bloodstone_sword").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/bloodstone_sword");
            model.texture("layer1", "mortal_expansion:item/bloodstone_sword_shimmer");
        }
        {
            var model = this.getBuilder("book_of_ectcetera").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/book");
        }
        {
            var model = this.getBuilder("ectchantment_machine").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/ectchantment_machine"));
        }
        {
            var model = this.getBuilder("ectoplasmic_generator").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/ectoplasmic_generator"));
        }
        {
            var model = this.getBuilder("enchantment_generator").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/enchantment_generator"));
        }
        {
            var model = this.getBuilder("grave").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave"));
        }
        {
            var model = this.getBuilder("grave_moss").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:block/glow_lichen");
        }
        {
            var model = this.getBuilder("gravestone").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/gravestone"));
        }
        {
            var model = this.getBuilder("gravestone_sword").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/gravestone_sword");
            model.texture("layer1", "mortal_expansion:item/gravestone_sword_shimmer");
        }
        {
            var model = this.getBuilder("haint_furnace").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/haint_furnace"));
        }
        {
            var model = this.getBuilder("haunt_furnace").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/haunt_furnace"));
        }
        {
            var model = this.getBuilder("holy_salt").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/sugar");
        }
        {
            var model = this.getBuilder("holy_water_bucket").parent(new ModelFile.UncheckedModelFile("neoforge:item/bucket"));
            model.customLoader(net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder::begin).fluid(ModFluids.HOLY_WATER.get());
        }
        {
            var model = this.getBuilder("liquid_ectoplasm_bucket").parent(new ModelFile.UncheckedModelFile("neoforge:item/bucket"));
            model.customLoader(net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder::begin).fluid(ModFluids.LIQUID_ECTOPLASM.get());
        }
        {
            var model = this.getBuilder("pharaohl_scepter").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/pharaohls_scepter");
        }
        {
            var model = this.getBuilder("pure_ectoplasm").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "mortal_expansion:item/pure_ectoplasm");
            model.texture("layer1", "mortal_expansion:item/pure_ectoplasm_shimmer");
        }
        {
            var model = this.getBuilder("purification_station").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purification_station"));
        }
        {
            var model = this.getBuilder("purification_ward").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purification_ward"));
        }
        {
            var model = this.getBuilder("purified_ectoplasm_bucket").parent(new ModelFile.UncheckedModelFile("neoforge:item/bucket"));
            model.customLoader(net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder::begin).fluid(ModFluids.PURIFIED_ECTOPLASM.get());
        }
        {
            var model = this.getBuilder("purified_gravestone").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_gravestone"));
        }
        {
            var model = this.getBuilder("purified_gravestone_sword").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/purified_gravestone_sword");
            model.texture("layer1", "mortal_expansion:item/purified_gravestone_sword_shimmer");
        }
        {
            var model = this.getBuilder("purified_spiritwood_button").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_button"));
        }
        {
            var model = this.getBuilder("purified_spiritwood_door").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "mortal_expansion:item/purified_spiritwood_door");
        }
        {
            var model = this.getBuilder("purified_spiritwood_fence").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_fence_inventory"));
        }
        {
            var model = this.getBuilder("purified_spiritwood_fence_gate").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_fence_gate"));
        }
        {
            var model = this.getBuilder("purified_spiritwood_log").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_log"));
        }
        {
            var model = this.getBuilder("purified_spiritwood_planks").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_planks"));
        }
        {
            var model = this.getBuilder("purified_spiritwood_pressure_plate").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_pressure_plate"));
        }
        {
            var model = this.getBuilder("purified_spiritwood_slab").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_slab"));
        }
        {
            var model = this.getBuilder("purified_spiritwood_stairs").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_stairs"));
        }
        {
            var model = this.getBuilder("purified_spiritwood_trapdoor").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_trapdoor_bottom"));
        }
        {
            var model = this.getBuilder("raw_ectoplasm").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "mortal_expansion:item/raw_ectoplasm");
            model.texture("layer1", "mortal_expansion:item/raw_ectoplasm_shimmer");
        }
        {
            var model = this.getBuilder("spectralite_axe").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/spectralite_axe");
            model.texture("layer1", "mortal_expansion:item/spectralite_axe_shimmer");
        }
        {
            var model = this.getBuilder("spectralite_boots").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/netherite_boots");
        }
        {
            var model = this.getBuilder("spectralite_chestplate").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/netherite_chestplate");
        }
        {
            var model = this.getBuilder("spectralite_helmet").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/netherite_helmet");
        }
        {
            var model = this.getBuilder("spectralite_hoe").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/spectralite_hoe");
            model.texture("layer1", "mortal_expansion:item/spectralite_hoe_shimmer");
        }
        {
            var model = this.getBuilder("spectralite_leggings").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:item/netherite_leggings");
        }
        {
            var model = this.getBuilder("spectralite_pickaxe").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/spectralite_pickaxe");
            model.texture("layer1", "mortal_expansion:item/spectralite_pickaxe_shimmer");
        }
        {
            var model = this.getBuilder("spectralite_shovel").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/spectralite_shovel");
            model.texture("layer1", "mortal_expansion:item/spectralite_shovel_shimmer");
        }
        {
            var model = this.getBuilder("spectralite_sword").parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"));
            model.texture("layer0", "mortal_expansion:item/spectralite_sword");
            model.texture("layer1", "mortal_expansion:item/spectralite_sword_shimmer");
        }
        {
            var model = this.getBuilder("spirit_torch").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "mortal_expansion:block/spirit_torch");
        }
        {
            var model = this.getBuilder("spiritwood_button").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_button_inventory"));
        }
        {
            var model = this.getBuilder("spiritwood_door").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "mortal_expansion:item/spiritwood_door");
        }
        {
            var model = this.getBuilder("spiritwood_fence").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_fence_inventory"));
        }
        {
            var model = this.getBuilder("spiritwood_fence_gate").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_fence_gate"));
        }
        {
            var model = this.getBuilder("spiritwood_leaves").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_leaves"));
        }
        {
            var model = this.getBuilder("spiritwood_log").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_log"));
        }
        {
            var model = this.getBuilder("spiritwood_planks").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_planks"));
        }
        {
            var model = this.getBuilder("spiritwood_pressure_plate").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_pressure_plate"));
        }
        {
            var model = this.getBuilder("spiritwood_sapling").parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"));
            model.texture("layer0", "minecraft:block/oak_sapling");
        }
        {
            var model = this.getBuilder("spiritwood_slab").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_slab"));
        }
        {
            var model = this.getBuilder("spiritwood_stairs").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_stairs"));
        }
        {
            var model = this.getBuilder("spiritwood_trapdoor").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_trapdoor_bottom"));
        }
        {
            var model = this.getBuilder("stone_fence").parent(new ModelFile.UncheckedModelFile("mortal_expansion:block/stone_fence_inventory"));
        }
    }
}
