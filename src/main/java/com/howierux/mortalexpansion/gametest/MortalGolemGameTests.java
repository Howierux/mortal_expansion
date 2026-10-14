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
    public static void flesh_applies_one_at_a_time_preserves_axis_caps_and_survives_save(GameTestHelper h) {
        var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
        var pos=h.absolutePos(new BlockPos(5,2,5));p.setPos(pos.getCenter().add(8,0,0));
        h.getLevel().setBlock(pos,Blocks.BONE_BLOCK.defaultBlockState().setValue(RotatedPillarBlock.AXIS,Direction.Axis.X),3);
        p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.ROTTEN_FLESH,64));
        for(int flesh=1;flesh<=64;flesh++) {
            var event=new PlayerInteractEvent.RightClickBlock(p,InteractionHand.MAIN_HAND,pos,new BlockHitResult(pos.getCenter(),Direction.UP,pos,false));
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
            var frame=(MortalGolemBaseBlockEntity)h.getLevel().getBlockEntity(pos);
            h.assertTrue(event.isCanceled() && frame.flesh()==flesh
                    && p.getMainHandItem().getCount()==64-flesh,"Each use adds and consumes exactly one flesh");
            h.assertTrue(frame.getBlockState().getValue(MortalGolemBaseBlock.FLESH_STAGE)==flesh/16
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
    public static void all_four_entity_stages_persist_and_only_smiteable_targets_are_attacked(GameTestHelper h) {
        var player=h.makeMockServerPlayerInLevel();
        for(int tier=1;tier<=4;tier++) {
            var golem=ModEntities.MORTAL_GOLEM.get().create(h.getLevel());golem.setFlesh(tier*16);
            golem.setTarget(player);
            h.assertTrue(golem.getTarget()==null && !golem.doHurtTarget(player),"Construct cannot target or damage a player");
            h.assertTrue(golem.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)==15.0*(tier+1)/4.0,"Damage scales in quarter steps to 1.25 times Iron Golem strength");
            h.assertTrue(golem.getMaxHealth()==tier*25 && golem.getHealth()==tier*25,"Flesh tiers scale health equally before and after formation");
            var zombie=net.minecraft.world.entity.EntityType.ZOMBIE.create(h.getLevel());
            var cow=net.minecraft.world.entity.EntityType.COW.create(h.getLevel());
            golem.setTarget(zombie);h.assertTrue(golem.getTarget()==zombie,"Smiteable mobs are valid targets");
            golem.setTarget(cow);h.assertTrue(golem.getTarget()==null && !golem.doHurtTarget(cow),"Other mobs remain safe");
            zombie.discard();cow.discard();
            golem.setHealth(tier*10);
            var data=new net.minecraft.nbt.CompoundTag();golem.addAdditionalSaveData(data);
            var copy=ModEntities.MORTAL_GOLEM.get().create(h.getLevel());copy.readAdditionalSaveData(data);
            h.assertTrue(copy.flesh()==tier*16 && copy.fleshTier()==tier && copy.isPersistenceRequired()
                    && copy.getHealth()==tier*10 && copy.getMaxHealth()==tier*25,"All flesh appearances survive entity save/load");
            golem.discard();copy.discard();
        }
        var node=EctceteraResearchMap.nodes(3).stream().filter(n->n.chapter().key().equals("mortal_golem")).findFirst().orElseThrow();
        h.assertTrue(node.parent().equals("sanctified") && EctceteraResearchMap.unlocked("mortal_golem",3,0)
                && !EctceteraResearchMap.unlocked("mortal_golem",2,127),"Entry is on the Purified tab without an extra secret gate");
        player.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void flesh_repairs_scale_and_spawned_upgrades_match_construction(GameTestHelper h) {
        var player=h.makeMockServerPlayerInLevel();player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
        for(int tier=1;tier<=4;tier++) {
            var golem=ModEntities.MORTAL_GOLEM.get().create(h.getLevel());golem.setFlesh(tier*16);
            golem.setHealth(1);player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.ROTTEN_FLESH,2));
            golem.mobInteract(player,InteractionHand.MAIN_HAND);
            h.assertTrue(Math.abs(golem.getHealth()-(1+tier*25/16.0F))<0.001
                    && player.getMainHandItem().getCount()==1 && golem.flesh()==tier*16,"Damaged golems repair without increasing flesh at every tier");
            HauntManager.setTier(h.getLevel(),golem.blockPosition(),HauntTier.SUPERNATURAL);
            float before=golem.getHealth();golem.repairFromHaunt();
            h.assertTrue(golem.getHealth()==before,"Below Dead Rising has no automatic repair");
            HauntManager.setTier(h.getLevel(),golem.blockPosition(),HauntTier.DEAD_RISING);
            golem.tickCount=99;golem.aiStep();
            h.assertTrue(golem.getHealth()==before,"Regeneration waits for its five-second interval");
            golem.tickCount=100;golem.aiStep();
            h.assertTrue(Math.abs(golem.getHealth()-before-tier*0.25F)<0.001,"Dead Rising restores one percent at the scheduled interval");
            golem.discard();
        }
        var fed=ModEntities.MORTAL_GOLEM.get().create(h.getLevel());
        player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.ROTTEN_FLESH,64));
        for(int i=17;i<=64;i++)fed.mobInteract(player,InteractionHand.MAIN_HAND);
        var built=ModEntities.MORTAL_GOLEM.get().create(h.getLevel());built.setFlesh(64);
        h.assertTrue(fed.flesh()==64 && fed.getMaxHealth()==built.getMaxHealth() && fed.getHealth()==built.getHealth(),
                "Feeding an awakened golem reaches the same tier and health as preparing all flesh before awakening");
        int remaining=player.getMainHandItem().getCount();fed.mobInteract(player,InteractionHand.MAIN_HAND);
        h.assertTrue(player.getMainHandItem().getCount()==remaining,"Full-health tier-four golems consume no flesh");
        fed.discard();built.discard();player.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void spiders_creepers_and_endermen_ignore_golems_but_not_other_targets(GameTestHelper h) {
        var golem=ModEntities.MORTAL_GOLEM.get().create(h.getLevel());
        var cow=net.minecraft.world.entity.EntityType.COW.create(h.getLevel());
        for(var type:java.util.List.of(net.minecraft.world.entity.EntityType.SPIDER,net.minecraft.world.entity.EntityType.CAVE_SPIDER,
                net.minecraft.world.entity.EntityType.CREEPER,net.minecraft.world.entity.EntityType.ENDERMAN)) {
            var mob=type.create(h.getLevel());mob.setTarget(cow);
            h.assertTrue(mob.getTarget()==cow,"Other targets remain available");
            mob.setTarget(golem);h.assertTrue(mob.getTarget()==null,"Excluded mob family cannot target a Mortal Golem");
            mob.discard();
        }
        var zombie=net.minecraft.world.entity.EntityType.ZOMBIE.create(h.getLevel());
        zombie.setTarget(golem);h.assertTrue(zombie.getTarget()==golem,"Smiteable mobs can fight back");
        float before=zombie.getHealth();golem.doHurtTarget(zombie);
        h.assertTrue(zombie.getHealth()<before,"Golem attacks deal real damage to Smite-sensitive enemies");
        var ghost=ModEntities.GHOST.get().create(h.getLevel());
        var ordinary=net.minecraft.world.entity.EntityType.IRON_GOLEM.create(h.getLevel());
        float ghostHealth=ghost.getHealth();ordinary.doHurtTarget(ghost);
        h.assertTrue(ghost.getHealth()==ghostHealth,"Ordinary golem attacks remain blocked by intangibility");
        golem.setTarget(ghost);h.assertTrue(golem.getTarget()==ghost,"Intangible mobs are valid targets");
        var source=golem.damageSources().mobAttack(golem);
        h.assertTrue(source.is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_MAGIC) && source.getEntity()==golem,
                "Mortal Golem melee is magic and retains attacker attribution");
        golem.doHurtTarget(ghost);h.assertTrue(ghost.getHealth()<ghostHealth,"Mortal Golem attacks damage intangible ghosts");
        h.assertTrue(golem.canAttackType(net.minecraft.world.entity.EntityType.GHAST),"Intangible targets need not also be Smite-sensitive");
        ghost.discard();ordinary.discard();zombie.discard();golem.discard();cow.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void lure_items_in_either_hand_activate_registered_follow_goal_without_consumption(GameTestHelper h) {
        var player=h.makeMockServerPlayerInLevel();var origin=h.absolutePos(new BlockPos(5,2,5));
        player.setPos(origin.getCenter().add(4,0,0));
        for(var item:java.util.List.of(Items.ROTTEN_FLESH,Items.BONE,ModItems.RAW_ECTOPLASM.get(),ModItems.PURE_ECTOPLASM.get())) {
            for(var hand:InteractionHand.values()) {
                var golem=ModEntities.MORTAL_GOLEM.get().create(h.getLevel());golem.setPos(origin.getCenter());
                player.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.EMPTY);player.setItemInHand(InteractionHand.OFF_HAND,ItemStack.EMPTY);
                var goal=(net.minecraft.world.entity.ai.goal.TemptGoal)golem.goalSelector.getAvailableGoals().stream()
                        .map(net.minecraft.world.entity.ai.goal.WrappedGoal::getGoal)
                        .filter(g->g instanceof net.minecraft.world.entity.ai.goal.TemptGoal).findFirst().orElseThrow();
                player.setItemInHand(hand,new ItemStack(Items.STICK));
                h.assertFalse(goal.canUse(),"Ordinary held items do not lure the golem");
                player.setItemInHand(hand,new ItemStack(item,3));
                h.assertTrue(goal.canUse(),"Each configured item lures from either hand");goal.start();goal.tick();
                h.assertTrue(goal.isRunning() && player.getItemInHand(hand).getCount()==3 && golem.flesh()==16,
                        "Following neither consumes the lure nor changes flesh");
                goal.stop();golem.discard();
            }
        }
        player.discard();h.succeed();
    }
    private static void splash(GameTestHelper h,BlockPos head,net.minecraft.core.Holder<net.minecraft.world.item.alchemy.Potion> type,
            net.minecraft.world.item.Item item,boolean canceled){
        var potion=new net.minecraft.world.entity.projectile.ThrownPotion(h.getLevel(),head.getX()+0.5,head.getY()+0.5,head.getZ()+0.5);
        var stack=new ItemStack(item);stack.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS,
                new net.minecraft.world.item.alchemy.PotionContents(type));potion.setItem(stack);
        var event=new net.neoforged.neoforge.event.entity.ProjectileImpactEvent(potion,
                new BlockHitResult(head.getCenter(),Direction.UP,head,false));event.setCanceled(canceled);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);potion.discard();
    }
    @GameTest(template="deferred_items_test")
    public static void regeneration_splash_awakens_carved_frames_and_rejected_builds_keep_components(GameTestHelper h){
        var frame=base(h,15);var head=frame.getBlockPos().above();
        h.getLevel().setBlock(head,Blocks.CARVED_PUMPKIN.defaultBlockState(),3);
        splash(h,head,net.minecraft.world.item.alchemy.Potions.REGENERATION,Items.SPLASH_POTION,false);
        h.assertTrue(frame.flesh()==15 && h.getLevel().getBlockState(head).is(Blocks.CARVED_PUMPKIN),"Incomplete frames cannot awaken");
        frame.addFlesh(17);
        splash(h,head,net.minecraft.world.item.alchemy.Potions.HEALING,Items.SPLASH_POTION,false);
        splash(h,head,net.minecraft.world.item.alchemy.Potions.REGENERATION,Items.LINGERING_POTION,false);
        splash(h,head,net.minecraft.world.item.alchemy.Potions.REGENERATION,Items.SPLASH_POTION,true);
        h.assertTrue(!frame.isRemoved(),"Other effects, lingering potions and canceled impacts leave the build alone");
        h.getLevel().setBlock(head.above(),Blocks.STONE.defaultBlockState(),3);
        splash(h,head,net.minecraft.world.item.alchemy.Potions.REGENERATION,Items.SPLASH_POTION,false);
        h.assertTrue(frame.flesh()==32 && h.getLevel().getBlockState(head).is(Blocks.CARVED_PUMPKIN),"Obstructed splash formation preserves flesh and pumpkin");
        h.getLevel().setBlock(head.above(),Blocks.AIR.defaultBlockState(),3);
        HauntManager.setTier(h.getLevel(),head,HauntTier.VOID);
        splash(h,head,net.minecraft.world.item.alchemy.Potions.LONG_REGENERATION,Items.SPLASH_POTION,false);
        var golems=h.getLevel().getEntitiesOfClass(MortalGolemEntity.class,new AABB(head).inflate(4));
        h.assertTrue(golems.size()==1 && golems.getFirst().flesh()==32 && golems.getFirst().getHealth()==50
                && h.getLevel().getBlockState(head).isAir() && h.getLevel().getBlockState(head.below()).isAir()
                && HauntManager.getTier(h.getLevel(),head)==HauntTier.VOID,"Regeneration forms exactly one selected-tier golem without a Haunt requirement or cost");
        golems.forEach(net.minecraft.world.entity.Entity::discard);h.succeed();
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
