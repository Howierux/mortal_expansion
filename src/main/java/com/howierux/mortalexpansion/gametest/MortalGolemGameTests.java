package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.block.MortalGolemBaseBlock;
import com.howierux.mortalexpansion.blockentity.MortalGolemBaseBlockEntity;
import com.howierux.mortalexpansion.entity.mob.MortalGolemEntity;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.guide.EctceteraResearchMap;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.gametest.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MortalGolemGameTests {
    private static MortalGolemBaseBlockEntity base(GameTestHelper h,int flesh) {
        var pos=h.absolutePos(new BlockPos(5,2,5));
        for(var at:BlockPos.betweenClosed(pos.offset(-2,0,-2),pos.offset(2,3,2)))h.getLevel().setBlock(at,Blocks.AIR.defaultBlockState(),3);
        h.getLevel().setBlock(pos,ModBlocks.MORTAL_GOLEM_BASE.get().defaultBlockState(),3);
        var base=(MortalGolemBaseBlockEntity)h.getLevel().getBlockEntity(pos);base.addFlesh(flesh);return base;
    }
    @GameTest(template="deferred_items_test")
    public static void flesh_applies_in_sixteens_preserves_axis_caps_and_survives_save(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
        var pos=h.absolutePos(new BlockPos(5,2,5));p.setPos(pos.getCenter().add(8,0,0));
        h.getLevel().setBlock(pos,Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS,Direction.Axis.X),3);
        p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.ROTTEN_FLESH,64));
        for(int tier=1;tier<=4;tier++) {
            var event=new PlayerInteractEvent.RightClickBlock(p,InteractionHand.MAIN_HAND,pos,new BlockHitResult(pos.getCenter(),Direction.UP,pos,false));
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
            var frame=(MortalGolemBaseBlockEntity)h.getLevel().getBlockEntity(pos);
            h.assertTrue(event.isCanceled() && frame.flesh()==tier*16
                    && p.getMainHandItem().getCount()==64-tier*16,"Each use consumes exactly sixteen flesh");
            h.assertTrue(frame.getBlockState().getValue(MortalGolemBaseBlock.FLESH_STAGE)==tier
                    && frame.getBlockState().getValue(MortalGolemBaseBlock.AXIS)==Direction.Axis.X,"Visible stages retain original bone orientation");
        }
        var frame=(MortalGolemBaseBlockEntity)h.getLevel().getBlockEntity(pos);
        p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.ROTTEN_FLESH,8));
        MortalGolemFormation.applyFlesh(new PlayerInteractEvent.RightClickBlock(p,InteractionHand.MAIN_HAND,pos,new BlockHitResult(pos.getCenter(),Direction.UP,pos,false)));
        h.assertTrue(frame.flesh()==64 && p.getMainHandItem().getCount()==8,"Full frame never wastes flesh");
        var restored=new MortalGolemBaseBlockEntity(pos,frame.getBlockState());
        restored.loadAdditional(frame.saveWithoutMetadata(h.getLevel().registryAccess()),h.getLevel().registryAccess());
        h.assertTrue(restored.flesh()==64,"Applied flesh persists through save/load");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void incomplete_or_blocked_frames_keep_resources_then_placed_head_forms_once(GameTestHelper h) {
        var frame=base(h,15);var pos=frame.getBlockPos();var head=pos.above();
        h.getLevel().setBlock(head,ModBlocks.WISP_O_LANTERN.get().defaultBlockState(),3);
        h.assertFalse(MortalGolemFormation.tryForm(h.getLevel(),head),"Less than sixteen flesh cannot form");
        frame.addFlesh(1);h.getLevel().setBlock(head.above(),Blocks.STONE.defaultBlockState(),3);
        h.assertFalse(MortalGolemFormation.tryForm(h.getLevel(),head),"Blocked headroom cannot form");
        h.assertTrue(frame.flesh()==16 && h.getLevel().getBlockState(head).is(ModBlocks.WISP_O_LANTERN.get()),"Blocked construction consumes neither component");
        h.getLevel().setBlock(head.above(),Blocks.AIR.defaultBlockState(),3);
        ModBlocks.WISP_O_LANTERN.get().setPlacedBy(h.getLevel(),head,h.getLevel().getBlockState(head),null,new ItemStack(ModItems.WISP_O_LANTERN.get()));
        var golems=h.getLevel().getEntitiesOfClass(MortalGolemEntity.class,new AABB(pos).inflate(4));
        h.assertTrue(golems.size()==1 && golems.getFirst().flesh()==16
                && h.getLevel().getBlockState(pos).isAir() && h.getLevel().getBlockState(head).isAir(),"A placed head consumes both blocks for one golem");
        h.assertFalse(MortalGolemFormation.tryForm(h.getLevel(),head),"A consumed build cannot duplicate");
        golems.forEach(net.minecraft.world.entity.Entity::discard);h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void natural_wisp_awakening_creates_the_selected_flesh_tier_and_keeps_conversion_cost(GameTestHelper h) {
        var level=h.getLevel();long time=level.getDayTime();
        try {
            var frame=base(h,48);var head=frame.getBlockPos().above();level.setDayTime(18000);
            HauntManager.setValue(level,head,20);
            level.setBlock(head,Blocks.CARVED_PUMPKIN.defaultBlockState(),3);
            h.assertTrue(WispLanternConversion.convert(level,head),"Natural pumpkin awakening succeeds above the frame");
            var golems=level.getEntitiesOfClass(MortalGolemEntity.class,new AABB(frame.getBlockPos()).inflate(4));
            h.assertTrue(golems.size()==1 && golems.getFirst().flesh()==48 && golems.getFirst().fleshTier()==3
                    && HauntManager.getValue(level,head)==19,"Natural formation preserves the flesh tier and pays the existing single Haunt point");
            golems.forEach(net.minecraft.world.entity.Entity::discard);
        } finally {level.setDayTime(time);}
        h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void all_four_entity_stages_persist_and_have_no_combat_or_retaliation(GameTestHelper h) {
        var player=h.makeMockServerPlayerInLevel();
        for(int tier=1;tier<=4;tier++) {
            var golem=ModEntities.MORTAL_GOLEM.get().create(h.getLevel());golem.setFlesh(tier*16);
            golem.setTarget(player);
            h.assertTrue(golem.getTarget()==null && !golem.doHurtTarget(player),"Passive construct cannot target or damage a player");
            var data=new net.minecraft.nbt.CompoundTag();golem.addAdditionalSaveData(data);
            var copy=ModEntities.MORTAL_GOLEM.get().create(h.getLevel());copy.readAdditionalSaveData(data);
            h.assertTrue(copy.flesh()==tier*16 && copy.fleshTier()==tier && copy.isPersistenceRequired(),"All flesh appearances survive entity save/load");
            golem.discard();copy.discard();
        }
        var node=EctceteraResearchMap.nodes(3).stream().filter(n->n.chapter().key().equals("mortal_golem")).findFirst().orElseThrow();
        h.assertTrue(node.parent().equals("sanctified") && EctceteraResearchMap.unlocked("mortal_golem",3,0)
                && !EctceteraResearchMap.unlocked("mortal_golem",2,127),"Entry is on the Purified tab without an extra secret gate");
        player.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void canceled_entity_spawn_leaves_the_complete_build_intact(GameTestHelper h) {
        var frame=base(h,64);var head=frame.getBlockPos().above();
        h.getLevel().setBlock(head,ModBlocks.WISP_O_LANTERN.get().defaultBlockState(),3);
        java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityJoinLevelEvent> cancel=event->{
            if(event.getEntity() instanceof MortalGolemEntity)event.setCanceled(true);
        };
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(cancel);
        try {
            h.assertFalse(MortalGolemFormation.tryForm(h.getLevel(),head),"Canceled spawn cannot consume the build");
            h.assertTrue(frame.flesh()==64 && h.getLevel().getBlockState(head).is(ModBlocks.WISP_O_LANTERN.get())
                    && !frame.isRemoved(),"All flesh and both blocks remain available after rejection");
        } finally {net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(cancel);}
        h.succeed();
    }
}
