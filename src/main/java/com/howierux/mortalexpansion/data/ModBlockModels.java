package com.howierux.mortalexpansion.data;

import com.howierux.mortalexpansion.MortalExpansion;
import net.minecraft.resources.ResourceLocation;
import com.howierux.mortalexpansion.block.GraveBlock;
import com.howierux.mortalexpansion.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.properties.*;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** Models and state mappings authored through NeoForge's typed builders. */
public final class ModBlockModels extends BlockStateProvider {
    public ModBlockModels(PackOutput output, ExistingFileHelper helper) {
        super(output, MortalExpansion.MOD_ID, helper);
    }
    private void ectoBox(net.neoforged.neoforge.client.model.generators.BlockModelBuilder model,
            float x1,float y1,float z1,float x2,float y2,float z2,String texture){
        model.element().from(x1,y1,z1).to(x2,y2,z2).allFaces((direction,face)->face.texture(texture)).end();
    }
    private void ectoInfrastructureModels(){
        for(var block:new net.minecraft.world.level.block.Block[]{ModBlocks.ECTANK.get(),ModBlocks.PURIFIED_ECTANK.get()}){
            String name=block==ModBlocks.ECTANK.get()?"ectank":"purified_ectank";
            var model=models().getBuilder(name).parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                    .renderType("minecraft:cutout").texture("stone",modLoc("block/"+(name.equals("ectank")?"gravestone":"purified_gravestone")))
                    .texture("glass","minecraft:block/glass").texture("particle","#stone");
            ectoBox(model,0,0,0,16,2,16,"#stone");ectoBox(model,0,14,0,16,16,16,"#stone");
            for(int x:new int[]{0,14})for(int z:new int[]{0,14})ectoBox(model,x,2,z,x+2,14,z+2,"#stone");
            ectoBox(model,1,2,1,15,14,15,"#glass");simpleBlock(block,model);
        }
        var core=models().getBuilder("ecto_pipe_core").parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .renderType("minecraft:cutout").texture("stone",modLoc("block/gravestone")).texture("glass","minecraft:block/glass").texture("particle","#stone");
        ectoBox(core,5,5,5,11,11,11,"#glass");
        for(float x:new float[]{5,9.5F})for(float y:new float[]{5,9.5F})for(float z:new float[]{5,9.5F})ectoBox(core,x,y,z,x+1.5F,y+1.5F,z+1.5F,"#stone");
        var arm=models().getBuilder("ecto_pipe_arm").parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .renderType("minecraft:cutout").texture("stone",modLoc("block/gravestone")).texture("glass","minecraft:block/glass").texture("particle","#stone");
        ectoBox(arm,5,5,0,11,11,5,"#glass");ectoBox(arm,5,5,0,11,11,1.5F,"#stone");
        var multipart=getMultipartBuilder(ModBlocks.ECTO_PIPE.get());multipart.part().modelFile(core).addModel().end();
        for(var direction:Direction.values())multipart.part().modelFile(arm)
                .rotationX(direction==Direction.UP?270:direction==Direction.DOWN?90:0)
                .rotationY(direction.getAxis().isHorizontal()?((int)direction.toYRot()+180)%360:0)
                .addModel().condition(net.minecraft.world.level.block.PipeBlock.PROPERTY_BY_DIRECTION.get(direction),true).end();
        var item=models().getBuilder("ecto_pipe_inventory").parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .renderType("minecraft:cutout").texture("stone",modLoc("block/gravestone")).texture("glass","minecraft:block/glass").texture("particle","#stone");
        ectoBox(item,5,5,0,11,11,16,"#glass");ectoBox(item,5,5,0,11,11,2,"#stone");ectoBox(item,5,5,14,11,11,16,"#stone");
    }
    @Override protected void registerStatesAndModels() {
        ectoInfrastructureModels();
        var baseModels=new ModelFile[5];
        for(int stage=0;stage<=4;stage++)
            baseModels[stage]=models().cubeColumn("mortal_golem_base_"+stage,
                    stage==0?ResourceLocation.withDefaultNamespace("block/bone_block_side"):modLoc("block/mortal_golem_flesh_"+stage),
                    ResourceLocation.withDefaultNamespace("block/bone_block_top"));
        getVariantBuilder(ModBlocks.MORTAL_GOLEM_BASE.get()).forAllStates(state -> {
            var axis=state.getValue(com.howierux.mortalexpansion.block.MortalGolemBaseBlock.AXIS);
            return net.neoforged.neoforge.client.model.generators.ConfiguredModel.builder()
                    .modelFile(baseModels[state.getValue(com.howierux.mortalexpansion.block.MortalGolemBaseBlock.FLESH_STAGE)])
                    .rotationX(axis==Direction.Axis.Y?0:90).rotationY(axis==Direction.Axis.X?90:0).build();
        });
        var wisp=models().orientable("wisp_o_lantern",ResourceLocation.withDefaultNamespace("block/pumpkin_side"),
                modLoc("block/wisp_o_lantern_front_temporary"),ResourceLocation.withDefaultNamespace("block/pumpkin_top"));
        horizontalBlock(ModBlocks.WISP_O_LANTERN.get(),wisp);
        for(String name : new String[]{"dreamcatcher", "purified_dreamcatcher"}) {
        var catcher = models().getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile("minecraft:block/block")).renderType("minecraft:cutout").ao(false);
        catcher.texture("web", modLoc("item/" + name + "_temporary")).texture("particle", "#web");
        var web = catcher.element().from(0, 0, 15.5F).to(16, 16, 15.5F).shade(false);
        web.face(Direction.NORTH).texture("#web").uvs(0, 0, 16, 16);
        web.face(Direction.SOUTH).texture("#web").uvs(16, 0, 0, 16);
        horizontalBlock(name.equals("purified_dreamcatcher") ? ModBlocks.PURIFIED_DREAMCATCHER.get() : ModBlocks.DREAMCATCHER.get(), catcher);
        }
        var research = models().getBuilder("mortality_research_station").parent(new ModelFile.UncheckedModelFile("minecraft:block/block"));
        research.texture("side", temporaryTexture("mortality_research_station", "side"));
        research.texture("top", temporaryTexture("mortality_research_station", "top"));
        research.texture("particle", "#side");
        var desk = research.element().from(0, 0, 0).to(16, 12, 16);
        for (Direction direction : Direction.values()) {
            desk.face(direction).texture(direction == Direction.UP ? "#top" : "#side")
                    .uvs(0, 0, 16, 16);
        }
        simpleBlock(ModBlocks.MORTALITY_RESEARCH_STATION.get(), research);
        var impurityOff = temporaryMachine("altar_of_impurity", "altar_of_impurity", false);
        var impurityOn = temporaryMachine("altar_of_impurity_lit", "altar_of_impurity", true);
        getVariantBuilder(ModBlocks.ALTAR_OF_IMPURITY.get()).partialState().with(BlockStateProperties.LIT,false).modelForState().modelFile(impurityOff).addModel().partialState().with(BlockStateProperties.LIT,true).modelForState().modelFile(impurityOn).addModel();
        var altarOff = temporaryMachine("altar_of_purity", "altar_of_purity", false);
        var altarOn = temporaryMachine("altar_of_purity_lit", "altar_of_purity", true);
        getVariantBuilder(ModBlocks.ALTAR_OF_PURITY.get()).partialState().with(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT,false).modelForState().modelFile(altarOff).addModel().partialState().with(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT,true).modelForState().modelFile(altarOn).addModel();
        var dry=models().getBuilder("haunted_farmland").parent(new ModelFile.UncheckedModelFile("minecraft:block/farmland"));
        var wet=models().getBuilder("haunted_farmland_moist").parent(new ModelFile.UncheckedModelFile("minecraft:block/farmland_moist"));
        getVariantBuilder(com.howierux.mortalexpansion.registry.ModBlocks.HAUNTED_FARMLAND.get()).forAllStates(state ->
                net.neoforged.neoforge.client.model.generators.ConfiguredModel.builder().modelFile(
                        state.getValue(net.minecraft.world.level.block.FarmBlock.MOISTURE)==7?wet:dry).build());
        blockModels();
        blockStates();
    }
    // Keep texture mapping in the provider so regeneration and framework upgrades retain the art.
    private ResourceLocation temporaryTexture(String block, String face) {
        return modLoc("block/" + block + "_" + face + "_temporary");
    }

    private net.neoforged.neoforge.client.model.generators.BlockModelBuilder temporaryMachine(
            String modelName, String block, boolean lit) {
        return models().cube(modelName, temporaryTexture(block, "side"), temporaryTexture(block, "top"),
                temporaryTexture(block, lit ? "front_lit" : "front"), temporaryTexture(block, "side"),
                temporaryTexture(block, "side"), temporaryTexture(block, "side"))
                .texture("particle", temporaryTexture(block, "side"));
    }

    private void blockModels() {
        {
            var model = models().getBuilder("bloodstone_ore").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_all"));
            model.texture("all", "mortal_expansion:block/bloodstone_ore");
        }
        {
            var model = models().getBuilder("bloodstone_ore_stone").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_all"));
            model.texture("all", "mortal_expansion:block/bloodstone_ore_stone");
        }
        {
            temporaryMachine("ectchantment_machine", "ectchantment_machine", false);
        }
        {
            temporaryMachine("ectoplasmic_generator", "ectoplasmic_generator", false);
        }
        {
            temporaryMachine("enchantment_generator", "enchantment_generator", false);
        }
        {
            var model = models().getBuilder("grave").parent(new ModelFile.UncheckedModelFile("minecraft:block/block"));
            model.texture("texture", "mortal_expansion:block/gravestone");
            model.texture("particle", "mortal_expansion:block/gravestone");
            {
            var element = model.element().from(0F, 0F, 4F).to(16F, 16F, 12F);
            element.face(Direction.DOWN).texture("#texture").cullface(Direction.DOWN);
            element.face(Direction.UP).texture("#texture").cullface(Direction.UP);
            element.face(Direction.NORTH).texture("#texture");
            element.face(Direction.SOUTH).texture("#texture");
            element.face(Direction.WEST).texture("#texture");
            element.face(Direction.EAST).texture("#texture");
            }
        }
        {
            var model = models().getBuilder("grave_center").parent(new ModelFile.UncheckedModelFile("minecraft:block/block"));
            model.texture("texture", "mortal_expansion:block/gravestone");
            model.texture("particle", "mortal_expansion:block/gravestone");
            {
            var element = model.element().from(0F, 0F, 4F).to(16F, 16F, 12F);
            element.face(Direction.DOWN).texture("#texture").cullface(Direction.DOWN);
            element.face(Direction.UP).texture("#texture").cullface(Direction.UP);
            element.face(Direction.NORTH).texture("#texture");
            element.face(Direction.SOUTH).texture("#texture");
            element.face(Direction.WEST).texture("#texture");
            element.face(Direction.EAST).texture("#texture");
            }
        }
        {
            var model = models().getBuilder("grave_center_shimmer").parent(new ModelFile.UncheckedModelFile("minecraft:block/block")).renderType("translucent");
            model.texture("texture", "mortal_expansion:item/pure_ectoplasm_shimmer");
            model.texture("particle", "mortal_expansion:block/gravestone");
            {
            var element = model.element().from(-0.02F, -0.02F, 3.98F).to(16.02F, 16.02F, 12.02F);
            element.face(Direction.DOWN).texture("#texture");
            element.face(Direction.UP).texture("#texture");
            element.face(Direction.NORTH).texture("#texture");
            element.face(Direction.SOUTH).texture("#texture");
            element.face(Direction.WEST).texture("#texture");
            element.face(Direction.EAST).texture("#texture");
            }
        }
        {
            var model = models().getBuilder("grave_edge_high").parent(new ModelFile.UncheckedModelFile("minecraft:block/block"));
            model.texture("texture", "mortal_expansion:block/gravestone");
            model.texture("particle", "mortal_expansion:block/gravestone");
            {
            var element = model.element().from(0F, 0F, 8F).to(16F, 16F, 16F);
            element.face(Direction.DOWN).texture("#texture").cullface(Direction.DOWN);
            element.face(Direction.UP).texture("#texture").cullface(Direction.UP);
            element.face(Direction.NORTH).texture("#texture");
            element.face(Direction.SOUTH).texture("#texture");
            element.face(Direction.WEST).texture("#texture");
            element.face(Direction.EAST).texture("#texture");
            }
        }
        {
            var model = models().getBuilder("grave_edge_high_shimmer").parent(new ModelFile.UncheckedModelFile("minecraft:block/block")).renderType("translucent");
            model.texture("texture", "mortal_expansion:item/pure_ectoplasm_shimmer");
            model.texture("particle", "mortal_expansion:block/gravestone");
            {
            var element = model.element().from(-0.02F, -0.02F, 7.98F).to(16.02F, 16.02F, 16.02F);
            element.face(Direction.DOWN).texture("#texture");
            element.face(Direction.UP).texture("#texture");
            element.face(Direction.NORTH).texture("#texture");
            element.face(Direction.SOUTH).texture("#texture");
            element.face(Direction.WEST).texture("#texture");
            element.face(Direction.EAST).texture("#texture");
            }
        }
        {
            var model = models().getBuilder("grave_edge_low").parent(new ModelFile.UncheckedModelFile("minecraft:block/block"));
            model.texture("texture", "mortal_expansion:block/gravestone");
            model.texture("particle", "mortal_expansion:block/gravestone");
            {
            var element = model.element().from(0F, 0F, 0F).to(16F, 16F, 8F);
            element.face(Direction.DOWN).texture("#texture").cullface(Direction.DOWN);
            element.face(Direction.UP).texture("#texture").cullface(Direction.UP);
            element.face(Direction.NORTH).texture("#texture");
            element.face(Direction.SOUTH).texture("#texture");
            element.face(Direction.WEST).texture("#texture");
            element.face(Direction.EAST).texture("#texture");
            }
        }
        {
            var model = models().getBuilder("grave_edge_low_shimmer").parent(new ModelFile.UncheckedModelFile("minecraft:block/block")).renderType("translucent");
            model.texture("texture", "mortal_expansion:item/pure_ectoplasm_shimmer");
            model.texture("particle", "mortal_expansion:block/gravestone");
            {
            var element = model.element().from(-0.02F, -0.02F, -0.02F).to(16.02F, 16.02F, 8.02F);
            element.face(Direction.DOWN).texture("#texture");
            element.face(Direction.UP).texture("#texture");
            element.face(Direction.NORTH).texture("#texture");
            element.face(Direction.SOUTH).texture("#texture");
            element.face(Direction.WEST).texture("#texture");
            element.face(Direction.EAST).texture("#texture");
            }
        }
        {
            var model = models().getBuilder("grave_moss").ao(false).renderType("minecraft:cutout");
            model.texture("particle", "mortal_expansion:block/grave_moss_temporary");
            model.texture("grave_moss", "mortal_expansion:block/grave_moss_temporary");
            {
            var element = model.element().from(0F, 0F, 0.1F).to(16F, 16F, 0.1F);
            element.face(Direction.NORTH).texture("#grave_moss").tintindex(0).uvs(16F, 0F, 0F, 16F);
            element.face(Direction.SOUTH).texture("#grave_moss").tintindex(0).uvs(0F, 0F, 16F, 16F);
            }
        }
        {
            var model = models().getBuilder("gravestone").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_all"));
            model.texture("all", "mortal_expansion:block/gravestone");
        }
        {
            var model = models().getBuilder("haint_furnace").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_orientable"));
            model.texture("side", "minecraft:block/furnace_side");
            model.texture("top", "minecraft:block/furnace_top");
            model.texture("front", "mortal_expansion:block/haint_furnace_closed_front_temporary");
            model.element().from(0,0,0).to(16,16,16).allFaces((direction,face)->face.texture(direction==Direction.UP||direction==Direction.DOWN?"#top":direction==Direction.NORTH?"#front":"#side").tintindex(0).cullface(direction)).end();
        }
        {
            var model = models().getBuilder("haint_furnace_on").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_orientable"));
            model.texture("side", "minecraft:block/furnace_side");
            model.texture("top", "minecraft:block/furnace_top");
            model.texture("front", "mortal_expansion:block/haint_furnace_closed_front_on_temporary");
            model.element().from(0,0,0).to(16,16,16).allFaces((direction,face)->face.texture(direction==Direction.UP||direction==Direction.DOWN?"#top":direction==Direction.NORTH?"#front":"#side").tintindex(0).cullface(direction)).end();
        }
        {
            var model = models().getBuilder("haint_furnace_haunt").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_orientable"));
            model.texture("side", "minecraft:block/furnace_side");
            model.texture("top", "minecraft:block/furnace_top");
            model.texture("front", "mortal_expansion:block/haint_furnace_haunt_front_temporary");
            model.element().from(0,0,0).to(16,16,16).allFaces((direction,face)->face.texture(direction==Direction.UP||direction==Direction.DOWN?"#top":direction==Direction.NORTH?"#front":"#side").tintindex(0).cullface(direction)).end();
        }
        {
            var model = models().getBuilder("haunt_furnace").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_orientable"));
            model.texture("side", "minecraft:block/furnace_side");
            model.texture("top", "minecraft:block/furnace_top");
            model.texture("front", "minecraft:block/furnace_front");
            model.element().from(0,0,0).to(16,16,16).allFaces((direction,face)->face.texture(direction==Direction.UP||direction==Direction.DOWN?"#top":direction==Direction.NORTH?"#front":"#side").tintindex(0).cullface(direction)).end();
        }
        {
            var model = models().getBuilder("haunt_furnace_on").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_orientable"));
            model.texture("side", "minecraft:block/furnace_side");
            model.texture("top", "minecraft:block/furnace_top");
            model.texture("front", "minecraft:block/furnace_front_on");
            model.element().from(0,0,0).to(16,16,16).allFaces((direction,face)->face.texture(direction==Direction.UP||direction==Direction.DOWN?"#top":direction==Direction.NORTH?"#front":"#side").tintindex(0).cullface(direction)).end();
        }
        {
            var model = models().getBuilder("holy_salt").parent(new ModelFile.UncheckedModelFile("minecraft:block/redstone_dust_dot"));
            model.texture("particle", "minecraft:block/redstone_dust_dot");
            model.texture("line", "minecraft:block/redstone_dust_dot");
        }
        {
            var model = models().getBuilder("holy_salt_dot").ao(false);
            model.texture("particle", "minecraft:block/white_concrete_powder");
            model.texture("salt", "minecraft:block/white_concrete_powder");
            {
            var element = model.element().from(5F, 0.2F, 5F).to(11F, 0.45F, 11F).shade(false);
            element.face(Direction.UP).texture("#salt").uvs(5F, 5F, 11F, 11F);
            }
        }
        {
            var model = models().getBuilder("holy_salt_side").ao(false);
            model.texture("particle", "minecraft:block/white_concrete_powder");
            model.texture("salt", "minecraft:block/white_concrete_powder");
            {
            var element = model.element().from(6.5F, 0.2F, 0F).to(9.5F, 0.4F, 8F).shade(false);
            element.face(Direction.UP).texture("#salt").uvs(6.5F, 0F, 9.5F, 8F);
            }
        }
        {
            var model = models().getBuilder("holy_salt_up").ao(false);
            model.texture("particle", "minecraft:block/white_concrete_powder");
            model.texture("salt", "minecraft:block/white_concrete_powder");
            {
            var element = model.element().from(6.5F, 0F, 0.2F).to(9.5F, 16F, 0.4F).shade(false);
            element.face(Direction.SOUTH).texture("#salt").uvs(6.5F, 0F, 9.5F, 16F);
            }
        }
        {
            models().cube("purification_station", temporaryTexture("purification_station", "bottom"),
                    temporaryTexture("purification_station", "top"), temporaryTexture("purification_station", "front"),
                    temporaryTexture("purification_station", "side"), temporaryTexture("purification_station", "side"),
                    temporaryTexture("purification_station", "side"))
                    .texture("particle", temporaryTexture("purification_station", "side"));
        }
        {
            var model = models().getBuilder("purification_ward")
                    .parent(new ModelFile.UncheckedModelFile("minecraft:block/block"));
            for(String face : new String[]{"front", "side", "top", "bottom"})
                model.texture(face, temporaryTexture("purification_ward", face));
            model.texture("particle", "#side");
            var base = model.element().from(0, 0, 0).to(16, 6, 16);
            for(Direction direction : Direction.values()) {
                String texture = direction == Direction.UP ? "#top" : direction == Direction.DOWN ? "#bottom"
                        : direction == Direction.NORTH ? "#front" : "#side";
                base.face(direction).texture(texture).uvs(0, 0, 16, 16);
            }
            // Raised wooden carving replaces the generic Beacon glass/core; behavior stays in the block entity.
            var carving = model.element().from(4, 6, 4).to(12, 9, 12);
            for(Direction direction : Direction.values()) {
                if(direction == Direction.DOWN)continue; // Hidden against the base.
                var face = carving.face(direction).texture(direction == Direction.UP ? "#top" : "#bottom");
                if(direction == Direction.UP)face.uvs(4, 4, 12, 12);
                else face.uvs(0, 0, 16, 6);
            }
        }
        {
            var model = models().getBuilder("purified_gravestone").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_all"));
            model.texture("all", "mortal_expansion:block/purified_gravestone");
        }
        {
            var model = models().getBuilder("purified_spiritwood_button").parent(new ModelFile.UncheckedModelFile("minecraft:block/button"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_button_inventory").parent(new ModelFile.UncheckedModelFile("minecraft:block/button_inventory"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_button_pressed").parent(new ModelFile.UncheckedModelFile("minecraft:block/button_pressed"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_door_bottom_left").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_bottom_left"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_door_top");
        }
        {
            var model = models().getBuilder("purified_spiritwood_door_bottom_left_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_bottom_left_open"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_door_top");
        }
        {
            var model = models().getBuilder("purified_spiritwood_door_bottom_right").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_bottom_right"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_door_top");
        }
        {
            var model = models().getBuilder("purified_spiritwood_door_bottom_right_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_bottom_right_open"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_door_top");
        }
        {
            var model = models().getBuilder("purified_spiritwood_door_top_left").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_top_left"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_door_top");
        }
        {
            var model = models().getBuilder("purified_spiritwood_door_top_left_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_top_left_open"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_door_top");
        }
        {
            var model = models().getBuilder("purified_spiritwood_door_top_right").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_top_right"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_door_top");
        }
        {
            var model = models().getBuilder("purified_spiritwood_door_top_right_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_top_right_open"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_door_top");
        }
        {
            var model = models().getBuilder("purified_spiritwood_fence_gate").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_fence_gate"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_fence_gate_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_fence_gate_open"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_fence_gate_wall").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_fence_gate_wall"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_fence_gate_wall_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_fence_gate_wall_open"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_fence_inventory").parent(new ModelFile.UncheckedModelFile("minecraft:block/fence_inventory"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_fence_post").parent(new ModelFile.UncheckedModelFile("minecraft:block/fence_post"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_fence_side").parent(new ModelFile.UncheckedModelFile("minecraft:block/fence_side"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_log").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_column"));
            model.texture("end", "mortal_expansion:block/purified_spiritwood_log_top");
            model.texture("side", "mortal_expansion:block/purified_spiritwood_log");
        }
        {
            var model = models().getBuilder("purified_spiritwood_planks").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_all"));
            model.texture("all", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_pressure_plate").parent(new ModelFile.UncheckedModelFile("minecraft:block/pressure_plate_up"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_pressure_plate_down").parent(new ModelFile.UncheckedModelFile("minecraft:block/pressure_plate_down"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_slab").parent(new ModelFile.UncheckedModelFile("minecraft:block/slab"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("side", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_slab_top").parent(new ModelFile.UncheckedModelFile("minecraft:block/slab_top"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("side", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_stairs").parent(new ModelFile.UncheckedModelFile("minecraft:block/stairs"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("side", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_stairs_inner").parent(new ModelFile.UncheckedModelFile("minecraft:block/inner_stairs"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("side", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_stairs_outer").parent(new ModelFile.UncheckedModelFile("minecraft:block/outer_stairs"));
            model.texture("bottom", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("side", "mortal_expansion:block/purified_spiritwood_planks");
            model.texture("top", "mortal_expansion:block/purified_spiritwood_planks");
        }
        {
            var model = models().getBuilder("purified_spiritwood_trapdoor_bottom").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_trapdoor_bottom"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_trapdoor");
        }
        {
            var model = models().getBuilder("purified_spiritwood_trapdoor_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_trapdoor_open"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_trapdoor");
        }
        {
            var model = models().getBuilder("purified_spiritwood_trapdoor_top").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_trapdoor_top"));
            model.texture("texture", "mortal_expansion:block/purified_spiritwood_trapdoor");
        }
        {
            var model = models().getBuilder("spirit_torch").parent(new ModelFile.UncheckedModelFile("minecraft:block/torch")).renderType("minecraft:cutout");
            model.texture("torch", "mortal_expansion:block/spirit_torch");
        }
        {
            var model = models().getBuilder("spirit_torch_off").parent(new ModelFile.UncheckedModelFile("minecraft:block/torch")).renderType("minecraft:cutout");
            model.texture("torch", "mortal_expansion:block/spirit_torch_off");
        }
        {
            var model = models().getBuilder("spirit_wall_torch").parent(new ModelFile.UncheckedModelFile("minecraft:block/wall_torch")).renderType("minecraft:cutout");
            model.texture("torch", "mortal_expansion:block/spirit_torch");
        }
        {
            var model = models().getBuilder("spirit_wall_torch_off").parent(new ModelFile.UncheckedModelFile("minecraft:block/wall_torch")).renderType("minecraft:cutout");
            model.texture("torch", "mortal_expansion:block/spirit_torch_off");
        }
        {
            var model = models().getBuilder("spiritwell");
            model.texture("particle", "minecraft:item/nether_star");
        }
        {
            var model = models().getBuilder("spiritwood_button").parent(new ModelFile.UncheckedModelFile("minecraft:block/button"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_button_inventory").parent(new ModelFile.UncheckedModelFile("minecraft:block/button_inventory"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_button_pressed").parent(new ModelFile.UncheckedModelFile("minecraft:block/button_pressed"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_door_bottom_left").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_bottom_left"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/spiritwood_door_top");
        }
        {
            var model = models().getBuilder("spiritwood_door_bottom_left_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_bottom_left_open"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/spiritwood_door_top");
        }
        {
            var model = models().getBuilder("spiritwood_door_bottom_right").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_bottom_right"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/spiritwood_door_top");
        }
        {
            var model = models().getBuilder("spiritwood_door_bottom_right_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_bottom_right_open"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/spiritwood_door_top");
        }
        {
            var model = models().getBuilder("spiritwood_door_top_left").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_top_left"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/spiritwood_door_top");
        }
        {
            var model = models().getBuilder("spiritwood_door_top_left_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_top_left_open"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/spiritwood_door_top");
        }
        {
            var model = models().getBuilder("spiritwood_door_top_right").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_top_right"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/spiritwood_door_top");
        }
        {
            var model = models().getBuilder("spiritwood_door_top_right_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/door_top_right_open"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_door_bottom");
            model.texture("top", "mortal_expansion:block/spiritwood_door_top");
        }
        {
            var model = models().getBuilder("spiritwood_fence_gate").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_fence_gate"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_fence_gate_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_fence_gate_open"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_fence_gate_wall").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_fence_gate_wall"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_fence_gate_wall_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_fence_gate_wall_open"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_fence_inventory").parent(new ModelFile.UncheckedModelFile("minecraft:block/fence_inventory"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_fence_post").parent(new ModelFile.UncheckedModelFile("minecraft:block/fence_post"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_fence_side").parent(new ModelFile.UncheckedModelFile("minecraft:block/fence_side"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_leaves").parent(new ModelFile.UncheckedModelFile("minecraft:block/leaves")).renderType("minecraft:translucent");
            model.texture("all", "mortal_expansion:block/spiritwood_leaves_phase0");
        }
        {
            var model = models().getBuilder("spiritwood_leaves_phase0").parent(new ModelFile.UncheckedModelFile("minecraft:block/leaves")).renderType("minecraft:translucent");
            model.texture("all", "mortal_expansion:block/spiritwood_leaves_phase0");
        }
        {
            var model = models().getBuilder("spiritwood_leaves_phase1").parent(new ModelFile.UncheckedModelFile("minecraft:block/leaves")).renderType("minecraft:translucent");
            model.texture("all", "mortal_expansion:block/spiritwood_leaves_phase1");
        }
        {
            var model = models().getBuilder("spiritwood_leaves_phase2").parent(new ModelFile.UncheckedModelFile("minecraft:block/leaves")).renderType("minecraft:translucent");
            model.texture("all", "mortal_expansion:block/spiritwood_leaves_phase2");
        }
        {
            var model = models().getBuilder("spiritwood_leaves_phase3").parent(new ModelFile.UncheckedModelFile("minecraft:block/leaves")).renderType("minecraft:translucent");
            model.texture("all", "mortal_expansion:block/spiritwood_leaves_phase3");
        }
        {
            var model = models().getBuilder("spiritwood_log").parent(new ModelFile.UncheckedModelFile("minecraft:block/block"));
            model.texture("end", "minecraft:block/oak_log_top");
            model.texture("side", "minecraft:block/oak_log");
            model.texture("particle", "#side");
            {
            var element = model.element().from(0F, 0F, 0F).to(16F, 16F, 16F);
            element.face(Direction.DOWN).texture("#end").cullface(Direction.DOWN).tintindex(0);
            element.face(Direction.UP).texture("#end").cullface(Direction.UP).tintindex(0);
            element.face(Direction.NORTH).texture("#side").cullface(Direction.NORTH).tintindex(0);
            element.face(Direction.SOUTH).texture("#side").cullface(Direction.SOUTH).tintindex(0);
            element.face(Direction.WEST).texture("#side").cullface(Direction.WEST).tintindex(0);
            element.face(Direction.EAST).texture("#side").cullface(Direction.EAST).tintindex(0);
            }
        }
        {
            var model = models().getBuilder("spiritwood_planks").parent(new ModelFile.UncheckedModelFile("minecraft:block/cube_all"));
            model.texture("all", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_pressure_plate").parent(new ModelFile.UncheckedModelFile("minecraft:block/pressure_plate_up"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_pressure_plate_down").parent(new ModelFile.UncheckedModelFile("minecraft:block/pressure_plate_down"));
            model.texture("texture", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_sapling").ao(false).renderType("minecraft:cutout");
            model.texture("cross", "mortal_expansion:block/spiritwood_sapling_temporary");
            model.texture("particle", "#cross");
            {
            var element = model.element().from(0.8F, 0F, 8F).to(15.2F, 16F, 8F).shade(false).rotation().origin(8F, 8F, 8F).axis(Direction.Axis.Y).angle(45F).rescale(true).end();
            element.face(Direction.NORTH).texture("#cross").tintindex(0).uvs(0F, 0F, 16F, 16F);
            element.face(Direction.SOUTH).texture("#cross").tintindex(0).uvs(0F, 0F, 16F, 16F);
            }
            {
            var element = model.element().from(8F, 0F, 0.8F).to(8F, 16F, 15.2F).shade(false).rotation().origin(8F, 8F, 8F).axis(Direction.Axis.Y).angle(45F).rescale(true).end();
            element.face(Direction.WEST).texture("#cross").tintindex(0).uvs(0F, 0F, 16F, 16F);
            element.face(Direction.EAST).texture("#cross").tintindex(0).uvs(0F, 0F, 16F, 16F);
            }
        }
        {
            var model = models().getBuilder("spiritwood_slab").parent(new ModelFile.UncheckedModelFile("minecraft:block/slab"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_planks");
            model.texture("side", "mortal_expansion:block/spiritwood_planks");
            model.texture("top", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_slab_top").parent(new ModelFile.UncheckedModelFile("minecraft:block/slab_top"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_planks");
            model.texture("side", "mortal_expansion:block/spiritwood_planks");
            model.texture("top", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_stairs").parent(new ModelFile.UncheckedModelFile("minecraft:block/stairs"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_planks");
            model.texture("side", "mortal_expansion:block/spiritwood_planks");
            model.texture("top", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_stairs_inner").parent(new ModelFile.UncheckedModelFile("minecraft:block/inner_stairs"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_planks");
            model.texture("side", "mortal_expansion:block/spiritwood_planks");
            model.texture("top", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_stairs_outer").parent(new ModelFile.UncheckedModelFile("minecraft:block/outer_stairs"));
            model.texture("bottom", "mortal_expansion:block/spiritwood_planks");
            model.texture("side", "mortal_expansion:block/spiritwood_planks");
            model.texture("top", "mortal_expansion:block/spiritwood_planks");
        }
        {
            var model = models().getBuilder("spiritwood_trapdoor_bottom").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_trapdoor_bottom"));
            model.texture("texture", "mortal_expansion:block/spiritwood_trapdoor");
        }
        {
            var model = models().getBuilder("spiritwood_trapdoor_open").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_trapdoor_open"));
            model.texture("texture", "mortal_expansion:block/spiritwood_trapdoor");
        }
        {
            var model = models().getBuilder("spiritwood_trapdoor_top").parent(new ModelFile.UncheckedModelFile("minecraft:block/template_trapdoor_top"));
            model.texture("texture", "mortal_expansion:block/spiritwood_trapdoor");
        }
        {
            var model = models().getBuilder("stone_fence_inventory").parent(new ModelFile.UncheckedModelFile("minecraft:block/fence_inventory"));
            model.texture("texture", "minecraft:block/stone");
        }
        {
            var model = models().getBuilder("stone_fence_post").parent(new ModelFile.UncheckedModelFile("minecraft:block/fence_post"));
            model.texture("texture", "minecraft:block/stone");
        }
        {
            var model = models().getBuilder("stone_fence_side").parent(new ModelFile.UncheckedModelFile("minecraft:block/fence_side"));
            model.texture("texture", "minecraft:block/stone");
        }
    }
    private void blockStates() {
        // bloodstone_ore
        getVariantBuilder(ModBlocks.BLOODSTONE_ORE.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/bloodstone_ore")).addModel();
        // bloodstone_ore_stone
        getVariantBuilder(ModBlocks.BLOODSTONE_ORE_STONE.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/bloodstone_ore_stone")).addModel();
        // ectchantment_machine
        getVariantBuilder(ModBlocks.ECTCHANTMENT_MACHINE.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/ectchantment_machine")).addModel();
        // ectoplasmic_generator
        getVariantBuilder(ModBlocks.ECTOPLASMIC_GENERATOR.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/ectoplasmic_generator")).addModel();
        // enchantment_generator
        getVariantBuilder(ModBlocks.ENCHANTMENT_GENERATOR.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/enchantment_generator")).addModel();
        // grave
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_low")).addModel().condition(GraveBlock.DEPTH, 0).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_low_shimmer")).addModel().condition(GraveBlock.DEPTH, 0).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_high")).rotationY(180).addModel().condition(GraveBlock.DEPTH, 0).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_high_shimmer")).rotationY(180).addModel().condition(GraveBlock.DEPTH, 0).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_high")).rotationY(90).addModel().condition(GraveBlock.DEPTH, 0).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_high_shimmer")).rotationY(90).addModel().condition(GraveBlock.DEPTH, 0).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_low")).rotationY(270).addModel().condition(GraveBlock.DEPTH, 0).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_low_shimmer")).rotationY(270).addModel().condition(GraveBlock.DEPTH, 0).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_center")).addModel().condition(GraveBlock.DEPTH, 1).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_center_shimmer")).addModel().condition(GraveBlock.DEPTH, 1).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_center")).rotationY(180).addModel().condition(GraveBlock.DEPTH, 1).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_center_shimmer")).rotationY(180).addModel().condition(GraveBlock.DEPTH, 1).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_center")).rotationY(90).addModel().condition(GraveBlock.DEPTH, 1).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_center_shimmer")).rotationY(90).addModel().condition(GraveBlock.DEPTH, 1).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_center")).rotationY(270).addModel().condition(GraveBlock.DEPTH, 1).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_center_shimmer")).rotationY(270).addModel().condition(GraveBlock.DEPTH, 1).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_high")).addModel().condition(GraveBlock.DEPTH, 2).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_high_shimmer")).addModel().condition(GraveBlock.DEPTH, 2).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_low")).rotationY(180).addModel().condition(GraveBlock.DEPTH, 2).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_low_shimmer")).rotationY(180).addModel().condition(GraveBlock.DEPTH, 2).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_low")).rotationY(90).addModel().condition(GraveBlock.DEPTH, 2).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_low_shimmer")).rotationY(90).addModel().condition(GraveBlock.DEPTH, 2).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).condition(GraveBlock.NATURAL, true).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_high")).rotationY(270).addModel().condition(GraveBlock.DEPTH, 2).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).end();
        getMultipartBuilder(ModBlocks.GRAVE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_edge_high_shimmer")).rotationY(270).addModel().condition(GraveBlock.DEPTH, 2).condition(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).condition(GraveBlock.NATURAL, true).end();
        // grave_moss
        getMultipartBuilder(ModBlocks.GRAVE_MOSS.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_moss")).addModel().condition(BlockStateProperties.NORTH, true).end();
        getMultipartBuilder(ModBlocks.GRAVE_MOSS.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_moss")).rotationY(90).uvLock(true).addModel().condition(BlockStateProperties.EAST, true).end();
        getMultipartBuilder(ModBlocks.GRAVE_MOSS.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_moss")).rotationY(180).uvLock(true).addModel().condition(BlockStateProperties.SOUTH, true).end();
        getMultipartBuilder(ModBlocks.GRAVE_MOSS.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_moss")).rotationY(270).uvLock(true).addModel().condition(BlockStateProperties.WEST, true).end();
        getMultipartBuilder(ModBlocks.GRAVE_MOSS.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_moss")).rotationX(270).uvLock(true).addModel().condition(BlockStateProperties.UP, true).end();
        getMultipartBuilder(ModBlocks.GRAVE_MOSS.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/grave_moss")).rotationX(90).uvLock(true).addModel().condition(BlockStateProperties.DOWN, true).end();
        // gravestone
        getVariantBuilder(ModBlocks.GRAVESTONE.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/gravestone")).addModel();
        // Haint's power-source state is synchronized by the server, including all facing directions.
        getVariantBuilder(ModBlocks.HAINT_FURNACE.get()).forAllStates(state -> {
            boolean lit=state.getValue(BlockStateProperties.LIT);
            boolean haunt=state.getValue(com.howierux.mortalexpansion.block.HaintFurnaceBlock.HAUNT_POWERED);
            String model=lit?(haunt?"haint_furnace_haunt":"haint_furnace_on"):"haint_furnace";
            return net.neoforged.neoforge.client.model.generators.ConfiguredModel.builder()
                    .modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/"+model))
                    .rotationY(((int)state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()+180)%360)
                    .build();
        });
        // haunt_furnace
        getVariantBuilder(ModBlocks.HAUNT_FURNACE.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).with(BlockStateProperties.LIT, false).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/haunt_furnace")).addModel();
        getVariantBuilder(ModBlocks.HAUNT_FURNACE.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).with(BlockStateProperties.LIT, true).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/haunt_furnace_on")).addModel();
        getVariantBuilder(ModBlocks.HAUNT_FURNACE.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).with(BlockStateProperties.LIT, false).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/haunt_furnace")).rotationY(90).addModel();
        getVariantBuilder(ModBlocks.HAUNT_FURNACE.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).with(BlockStateProperties.LIT, true).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/haunt_furnace_on")).rotationY(90).addModel();
        getVariantBuilder(ModBlocks.HAUNT_FURNACE.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).with(BlockStateProperties.LIT, false).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/haunt_furnace")).rotationY(180).addModel();
        getVariantBuilder(ModBlocks.HAUNT_FURNACE.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).with(BlockStateProperties.LIT, true).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/haunt_furnace_on")).rotationY(180).addModel();
        getVariantBuilder(ModBlocks.HAUNT_FURNACE.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).with(BlockStateProperties.LIT, false).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/haunt_furnace")).rotationY(270).addModel();
        getVariantBuilder(ModBlocks.HAUNT_FURNACE.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).with(BlockStateProperties.LIT, true).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/haunt_furnace_on")).rotationY(270).addModel();
        // holy_salt
        getMultipartBuilder(ModBlocks.HOLY_SALT.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/holy_salt_dot")).addModel().useOr().nestedGroup().condition(RedStoneWireBlock.EAST, RedstoneSide.NONE).condition(RedStoneWireBlock.NORTH, RedstoneSide.NONE).condition(RedStoneWireBlock.SOUTH, RedstoneSide.NONE).condition(RedStoneWireBlock.WEST, RedstoneSide.NONE).end().nestedGroup().condition(RedStoneWireBlock.EAST, RedstoneSide.SIDE, RedstoneSide.UP).condition(RedStoneWireBlock.NORTH, RedstoneSide.SIDE, RedstoneSide.UP).end().nestedGroup().condition(RedStoneWireBlock.EAST, RedstoneSide.SIDE, RedstoneSide.UP).condition(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE, RedstoneSide.UP).end().nestedGroup().condition(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE, RedstoneSide.UP).condition(RedStoneWireBlock.WEST, RedstoneSide.SIDE, RedstoneSide.UP).end().nestedGroup().condition(RedStoneWireBlock.NORTH, RedstoneSide.SIDE, RedstoneSide.UP).condition(RedStoneWireBlock.WEST, RedstoneSide.SIDE, RedstoneSide.UP).end().end();
        getMultipartBuilder(ModBlocks.HOLY_SALT.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/holy_salt_side")).addModel().condition(RedStoneWireBlock.NORTH, RedstoneSide.SIDE, RedstoneSide.UP).end();
        getMultipartBuilder(ModBlocks.HOLY_SALT.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/holy_salt_side")).rotationY(180).addModel().condition(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE, RedstoneSide.UP).end();
        getMultipartBuilder(ModBlocks.HOLY_SALT.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/holy_salt_side")).rotationY(90).addModel().condition(RedStoneWireBlock.EAST, RedstoneSide.SIDE, RedstoneSide.UP).end();
        getMultipartBuilder(ModBlocks.HOLY_SALT.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/holy_salt_side")).rotationY(270).addModel().condition(RedStoneWireBlock.WEST, RedstoneSide.SIDE, RedstoneSide.UP).end();
        getMultipartBuilder(ModBlocks.HOLY_SALT.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/holy_salt_up")).addModel().condition(RedStoneWireBlock.NORTH, RedstoneSide.UP).end();
        getMultipartBuilder(ModBlocks.HOLY_SALT.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/holy_salt_up")).rotationY(90).addModel().condition(RedStoneWireBlock.EAST, RedstoneSide.UP).end();
        getMultipartBuilder(ModBlocks.HOLY_SALT.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/holy_salt_up")).rotationY(180).addModel().condition(RedStoneWireBlock.SOUTH, RedstoneSide.UP).end();
        getMultipartBuilder(ModBlocks.HOLY_SALT.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/holy_salt_up")).rotationY(270).addModel().condition(RedStoneWireBlock.WEST, RedstoneSide.UP).end();
        // holy_water_cauldron
        getVariantBuilder(ModBlocks.HOLY_WATER_CAULDRON.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("minecraft:block/water_cauldron_full")).addModel();
        // liquid_ectoplasm_cauldron
        getVariantBuilder(ModBlocks.LIQUID_ECTOPLASM_CAULDRON.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("minecraft:block/water_cauldron_full")).addModel();
        // purification_station
        getVariantBuilder(ModBlocks.PURIFICATION_STATION.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purification_station")).addModel();
        // purification_ward
        getVariantBuilder(ModBlocks.PURIFICATION_WARD.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purification_ward")).addModel();
        // purified_ectoplasm_cauldron
        getVariantBuilder(ModBlocks.PURIFIED_ECTOPLASM_CAULDRON.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("minecraft:block/water_cauldron_full")).addModel();
        // purified_gravestone
        getVariantBuilder(ModBlocks.PURIFIED_GRAVESTONE.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_gravestone")).addModel();
        // purified_spiritwood_button
        buttonBlock(ModBlocks.PURIFIED_SPIRITWOOD_BUTTON.get(), model("purified_spiritwood_button"), model("purified_spiritwood_button_pressed"));
        // purified_spiritwood_door
        doorBlock(ModBlocks.PURIFIED_SPIRITWOOD_DOOR.get(),
                model("purified_spiritwood_door_bottom_left"), model("purified_spiritwood_door_bottom_left_open"), model("purified_spiritwood_door_bottom_right"), model("purified_spiritwood_door_bottom_right_open"), model("purified_spiritwood_door_top_left"), model("purified_spiritwood_door_top_left_open"), model("purified_spiritwood_door_top_right"), model("purified_spiritwood_door_top_right_open"));
        // purified_spiritwood_fence
        getMultipartBuilder(ModBlocks.PURIFIED_SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_fence_post")).addModel().end();
        getMultipartBuilder(ModBlocks.PURIFIED_SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_fence_side")).uvLock(true).addModel().condition(BlockStateProperties.NORTH, true).end();
        getMultipartBuilder(ModBlocks.PURIFIED_SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_fence_side")).rotationY(90).uvLock(true).addModel().condition(BlockStateProperties.EAST, true).end();
        getMultipartBuilder(ModBlocks.PURIFIED_SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_fence_side")).rotationY(180).uvLock(true).addModel().condition(BlockStateProperties.SOUTH, true).end();
        getMultipartBuilder(ModBlocks.PURIFIED_SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_fence_side")).rotationY(270).uvLock(true).addModel().condition(BlockStateProperties.WEST, true).end();
        // purified_spiritwood_fence_gate
        fenceGateBlock(ModBlocks.PURIFIED_SPIRITWOOD_FENCE_GATE.get(), model("purified_spiritwood_fence_gate"), model("purified_spiritwood_fence_gate_open"), model("purified_spiritwood_fence_gate_wall"), model("purified_spiritwood_fence_gate_wall_open"));
        // purified_spiritwood_log
        getVariantBuilder(ModBlocks.PURIFIED_SPIRITWOOD_LOG.get()).partialState().with(BlockStateProperties.AXIS, Direction.Axis.X).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_log")).rotationX(90).rotationY(90).addModel();
        getVariantBuilder(ModBlocks.PURIFIED_SPIRITWOOD_LOG.get()).partialState().with(BlockStateProperties.AXIS, Direction.Axis.Y).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_log")).addModel();
        getVariantBuilder(ModBlocks.PURIFIED_SPIRITWOOD_LOG.get()).partialState().with(BlockStateProperties.AXIS, Direction.Axis.Z).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_log")).rotationX(90).addModel();
        // purified_spiritwood_planks
        getVariantBuilder(ModBlocks.PURIFIED_SPIRITWOOD_PLANKS.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/purified_spiritwood_planks")).addModel();
        // purified_spiritwood_pressure_plate
        pressurePlateBlock(ModBlocks.PURIFIED_SPIRITWOOD_PRESSURE_PLATE.get(), model("purified_spiritwood_pressure_plate"), model("purified_spiritwood_pressure_plate_down"));
        // purified_spiritwood_slab
        slabBlock(ModBlocks.PURIFIED_SPIRITWOOD_SLAB.get(), model("purified_spiritwood_slab"), model("purified_spiritwood_slab_top"), model("purified_spiritwood_planks"));
        // purified_spiritwood_stairs
        legacyStairs(ModBlocks.PURIFIED_SPIRITWOOD_STAIRS.get(), "purified_spiritwood");
        // purified_spiritwood_trapdoor
        trapdoorBlock(ModBlocks.PURIFIED_SPIRITWOOD_TRAPDOOR.get(), model("purified_spiritwood_trapdoor_bottom"), model("purified_spiritwood_trapdoor_top"), model("purified_spiritwood_trapdoor_open"), false);
        // spirit_torch
        getVariantBuilder(ModBlocks.SPIRIT_TORCH.get()).partialState().with(BlockStateProperties.LIT, true).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_torch")).addModel();
        getVariantBuilder(ModBlocks.SPIRIT_TORCH.get()).partialState().with(BlockStateProperties.LIT, false).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_torch_off")).addModel();
        // spirit_wall_torch
        getVariantBuilder(ModBlocks.SPIRIT_WALL_TORCH.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).with(BlockStateProperties.LIT, true).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_wall_torch")).rotationY(0).addModel();
        getVariantBuilder(ModBlocks.SPIRIT_WALL_TORCH.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).with(BlockStateProperties.LIT, false).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_wall_torch_off")).rotationY(0).addModel();
        getVariantBuilder(ModBlocks.SPIRIT_WALL_TORCH.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).with(BlockStateProperties.LIT, true).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_wall_torch")).rotationY(90).addModel();
        getVariantBuilder(ModBlocks.SPIRIT_WALL_TORCH.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).with(BlockStateProperties.LIT, false).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_wall_torch_off")).rotationY(90).addModel();
        getVariantBuilder(ModBlocks.SPIRIT_WALL_TORCH.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).with(BlockStateProperties.LIT, true).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_wall_torch")).rotationY(180).addModel();
        getVariantBuilder(ModBlocks.SPIRIT_WALL_TORCH.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).with(BlockStateProperties.LIT, false).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_wall_torch_off")).rotationY(180).addModel();
        getVariantBuilder(ModBlocks.SPIRIT_WALL_TORCH.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).with(BlockStateProperties.LIT, true).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_wall_torch")).rotationY(270).addModel();
        getVariantBuilder(ModBlocks.SPIRIT_WALL_TORCH.get()).partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).with(BlockStateProperties.LIT, false).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spirit_wall_torch_off")).rotationY(270).addModel();
        // spiritwell
        getVariantBuilder(ModBlocks.SPIRITWELL.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwell")).addModel();
        // spiritwood_button
        buttonBlock(ModBlocks.SPIRITWOOD_BUTTON.get(), model("spiritwood_button"), model("spiritwood_button_pressed"));
        // spiritwood_door
        doorBlock(ModBlocks.SPIRITWOOD_DOOR.get(),
                model("spiritwood_door_bottom_left"), model("spiritwood_door_bottom_left_open"), model("spiritwood_door_bottom_right"), model("spiritwood_door_bottom_right_open"), model("spiritwood_door_top_left"), model("spiritwood_door_top_left_open"), model("spiritwood_door_top_right"), model("spiritwood_door_top_right_open"));
        // spiritwood_fence
        getMultipartBuilder(ModBlocks.SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_fence_post")).addModel().end();
        getMultipartBuilder(ModBlocks.SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_fence_side")).uvLock(true).addModel().condition(BlockStateProperties.NORTH, true).end();
        getMultipartBuilder(ModBlocks.SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_fence_side")).rotationY(90).uvLock(true).addModel().condition(BlockStateProperties.EAST, true).end();
        getMultipartBuilder(ModBlocks.SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_fence_side")).rotationY(180).uvLock(true).addModel().condition(BlockStateProperties.SOUTH, true).end();
        getMultipartBuilder(ModBlocks.SPIRITWOOD_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_fence_side")).rotationY(270).uvLock(true).addModel().condition(BlockStateProperties.WEST, true).end();
        // spiritwood_fence_gate
        fenceGateBlock(ModBlocks.SPIRITWOOD_FENCE_GATE.get(), model("spiritwood_fence_gate"), model("spiritwood_fence_gate_open"), model("spiritwood_fence_gate_wall"), model("spiritwood_fence_gate_wall_open"));
        // spiritwood_leaves
        getVariantBuilder(ModBlocks.SPIRITWOOD_LEAVES.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_leaves_phase0")).weight(1).nextModel().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_leaves_phase1")).weight(1).nextModel().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_leaves_phase2")).weight(1).nextModel().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_leaves_phase3")).weight(1).addModel();
        // spiritwood_log
        getVariantBuilder(ModBlocks.SPIRITWOOD_LOG.get()).partialState().with(BlockStateProperties.AXIS, Direction.Axis.X).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_log")).rotationX(90).rotationY(90).addModel();
        getVariantBuilder(ModBlocks.SPIRITWOOD_LOG.get()).partialState().with(BlockStateProperties.AXIS, Direction.Axis.Y).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_log")).addModel();
        getVariantBuilder(ModBlocks.SPIRITWOOD_LOG.get()).partialState().with(BlockStateProperties.AXIS, Direction.Axis.Z).modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_log")).rotationX(90).addModel();
        // spiritwood_planks
        getVariantBuilder(ModBlocks.SPIRITWOOD_PLANKS.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_planks")).addModel();
        // spiritwood_pressure_plate
        pressurePlateBlock(ModBlocks.SPIRITWOOD_PRESSURE_PLATE.get(), model("spiritwood_pressure_plate"), model("spiritwood_pressure_plate_down"));
        // spiritwood_sapling
        getVariantBuilder(ModBlocks.SPIRITWOOD_SAPLING.get()).partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/spiritwood_sapling")).addModel();
        // spiritwood_slab
        slabBlock(ModBlocks.SPIRITWOOD_SLAB.get(), model("spiritwood_slab"), model("spiritwood_slab_top"), model("spiritwood_planks"));
        // spiritwood_stairs
        legacyStairs(ModBlocks.SPIRITWOOD_STAIRS.get(), "spiritwood");
        // spiritwood_trapdoor
        trapdoorBlock(ModBlocks.SPIRITWOOD_TRAPDOOR.get(), model("spiritwood_trapdoor_bottom"), model("spiritwood_trapdoor_top"), model("spiritwood_trapdoor_open"), false);
        // stone_fence
        getMultipartBuilder(ModBlocks.STONE_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/stone_fence_post")).addModel().end();
        getMultipartBuilder(ModBlocks.STONE_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/stone_fence_side")).uvLock(true).addModel().condition(BlockStateProperties.NORTH, true).end();
        getMultipartBuilder(ModBlocks.STONE_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/stone_fence_side")).rotationY(90).uvLock(true).addModel().condition(BlockStateProperties.EAST, true).end();
        getMultipartBuilder(ModBlocks.STONE_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/stone_fence_side")).rotationY(180).uvLock(true).addModel().condition(BlockStateProperties.SOUTH, true).end();
        getMultipartBuilder(ModBlocks.STONE_FENCE.get()).part().modelFile(new ModelFile.UncheckedModelFile("mortal_expansion:block/stone_fence_side")).rotationY(270).uvLock(true).addModel().condition(BlockStateProperties.WEST, true).end();
    }

    private ModelFile model(String name) {
        return models().getExistingFile(modLoc("block/" + name));
    }
    private void legacyStairs(net.minecraft.world.level.block.StairBlock block, String wood) {
        // Existing bottom corner states have a 90-degree offset from NeoForge's
        // default helper. Preserve their appearance during this data-only migration.
        getVariantBuilder(block).forAllStatesExcept(state -> {
            var shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
            var half = state.getValue(BlockStateProperties.HALF);
            int rotation = (int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise().toYRot();
            if (shape == StairsShape.INNER_RIGHT || shape == StairsShape.OUTER_RIGHT) rotation += 90;
            rotation %= 360;
            String suffix = switch (shape) {
                case STRAIGHT -> "";
                case INNER_LEFT, INNER_RIGHT -> "_inner";
                case OUTER_LEFT, OUTER_RIGHT -> "_outer";
            };
            return net.neoforged.neoforge.client.model.generators.ConfiguredModel.builder()
                    .modelFile(model(wood + "_stairs" + suffix)).rotationX(half == Half.TOP ? 180 : 0)
                    .rotationY(rotation).uvLock(rotation != 0 || half == Half.TOP).build();
        }, BlockStateProperties.WATERLOGGED);
    }
}
