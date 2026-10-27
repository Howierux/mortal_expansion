package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.blockentity.HauntFurnaceBlockEntity;
import com.howierux.mortalexpansion.mixin.HauntFurnaceAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HauntPoolsGameTests {
    private static net.minecraft.server.level.ServerPlayer player(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel(); player.setGameMode(GameType.SURVIVAL);
        player.setPos(helper.absolutePos(new BlockPos(7,2,7)).getCenter().add(0,-0.5,0));
        player.getRandom().setSeed(42); return player;
    }
    private static void floor(GameTestHelper h) {
        for (int x=0;x<15;x++) for(int z=0;z<15;z++) {
            h.setBlock(new BlockPos(x,1,z),Blocks.STONE);
            for(int y=2;y<10;y++) h.setBlock(new BlockPos(x,y,z),Blocks.AIR);
        }
    }
    @GameTest(template="deferred_items_test")
    public static void pools_have_independent_cooldowns_and_missing_functions_never_trigger(GameTestHelper h) {
        var p=player(h); PlayerHauntManager.setTier(p,HauntTier.DEMONIC);
        h.assertTrue(HauntEventController.tryEffect(p,HauntEffectKind.HIT),"Risk succeeds");
        h.assertTrue(HauntEventController.ready(p,false),"Risk does not spend benefit cooldown");
        h.assertTrue(HauntEventController.tryEffect(p,HauntEffectKind.GOOD_POTION),"Independent benefit succeeds during risk cooldown");
        h.assertFalse(HauntEventController.ready(p,false),"Benefit has its own cooldown");
        var state=HauntProtection.state(p);
        h.assertTrue(state.getLong("CooldownUntil")==HauntProtection.now(p)+1200 && state.getLong("BenefitCooldownUntil")==HauntProtection.now(p)+1200,"Both sixty-second deadlines persist separately");
        var saved=p.saveWithoutId(new net.minecraft.nbt.CompoundTag()); p.load(saved);
        h.assertTrue(HauntProtection.state(p).equals(state),"Both pool deadlines survive save/load");
        for(var kind:HauntEffectKind.values()) if(!kind.implemented()) {
            h.assertFalse(kind.eligible(6,false),"Missing functions are excluded even at Eldritch");
            h.assertFalse(HauntEffects.perform(p,kind),"No fake success or hint for missing functions");
        }
        for(boolean risk:new boolean[]{true,false}) {
            var low=HauntEventController.choices(1,false,risk);
            h.assertTrue(HauntEventController.choices(6,false,risk).containsAll(low),"Higher tiers retain earlier effects");
            h.assertTrue(HauntEventController.choices(0,false,risk).isEmpty(),"Void rolls neither pool");
            h.assertTrue(HauntEventController.choices(6,false,risk).stream().allMatch(k->k.negative==risk),"Pools never mix");
        }
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void empowerment_only_increases_spectral_melee_and_expires(GameTestHelper h) {
        var p=player(h); var victim=player(h);
        p.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));
        h.assertFalse(HauntEffects.perform(p,HauntEffectKind.EMPOWERMENT),"Ordinary weapon is ineligible");
        p.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(ModItems.SPECTRALITE_SWORD.get()));
        h.assertTrue(HauntEffects.perform(p,HauntEffectKind.EMPOWERMENT),"Spectral weapon empowered");
        var event=new net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent(victim,
                new net.neoforged.neoforge.common.damagesource.DamageContainer(p.damageSources().playerAttack(p),8));
        AdditionalHauntEffects.damage(event);
        h.assertTrue(event.getAmount()==10,"Twenty-five percent melee bonus");
        p.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));
        var ordinary=new net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent(victim,
                new net.neoforged.neoforge.common.damagesource.DamageContainer(p.damageSources().playerAttack(p),8));
        AdditionalHauntEffects.damage(ordinary);h.assertTrue(ordinary.getAmount()==8,"Switching weapons cannot carry the bonus to ordinary weapons");
        var state=HauntProtection.state(p);state.putLong("EmpoweredUntil",HauntProtection.now(p)-1);HauntProtection.state(p,state);
        p.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(ModItems.SPECTRALITE_SWORD.get()));
        var expired=new net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent(victim,
                new net.neoforged.neoforge.common.damagesource.DamageContainer(p.damageSources().playerAttack(p),8));
        AdditionalHauntEffects.damage(expired);h.assertTrue(expired.getAmount()==8,"Expired empowerment has no effect");
        p.discard();victim.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void interference_preserves_inputs_fuel_progress_and_resumes(GameTestHelper h) {
        floor(h);var p=player(h);var pos=new BlockPos(5,2,5);h.setBlock(pos,ModBlocks.HAUNT_FURNACE.get());
        var f=(HauntFurnaceBlockEntity)h.getBlockEntity(pos);
        f.setItem(0,new ItemStack(Items.RAW_IRON,2));f.setItem(1,new ItemStack(Items.COAL));
        var a=(HauntFurnaceAccessor)(Object)f;
        HauntFurnaceBlockEntity.serverTick(h.getLevel(),f.getBlockPos(),f.getBlockState(),f);
        int fuel=a.mortalExpansion$litTime(),progress=a.mortalExpansion$progress();
        h.assertTrue(HauntEffects.perform(p,HauntEffectKind.INTERFERENCE),"Nearby haunt furnace pauses");
        h.assertFalse(HauntEffects.finishFurnace(h.getLevel(),f),"Beneficial completion cannot bypass interference");
        HauntFurnaceBlockEntity.serverTick(h.getLevel(),f.getBlockPos(),f.getBlockState(),f);
        h.assertTrue(f.getItem(0).getCount()==2 && f.getItem(2).isEmpty() && a.mortalExpansion$litTime()==fuel && a.mortalExpansion$progress()==progress,"Pause conserves materials, fuel and progress");
        f.getPersistentData().putLong("MortalExpansionInterferenceUntil",0);
        HauntFurnaceBlockEntity.serverTick(h.getLevel(),f.getBlockPos(),f.getBlockState(),f);
        h.assertTrue(a.mortalExpansion$progress()>progress,"Cooking resumes after deadline");
        p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void garb_blocks_risks_keeps_benefits_and_clears_timed_disturbances(GameTestHelper h) {
        var p=player(h);PlayerHauntManager.setTier(p,HauntTier.ELDRITCH);
        HauntEffects.perform(p,HauntEffectKind.UNWANTED_COMPANY);HauntEffects.perform(p,HauntEffectKind.VEIL_BREACH);
        var state=HauntProtection.state(p);state.putLong("MarkedUntil",HauntProtection.now(p)+400);state.putLong("EmpoweredUntil",HauntProtection.now(p)+400);HauntProtection.state(p,state);
        p.setItemSlot(EquipmentSlot.HEAD,new ItemStack(ModItems.GHOST_GARB_HELMET.get()));
        p.setItemSlot(EquipmentSlot.CHEST,new ItemStack(ModItems.GHOST_GARB_CHESTPLATE.get()));
        p.setItemSlot(EquipmentSlot.LEGS,new ItemStack(ModItems.GHOST_GARB_LEGGINGS.get()));
        p.setItemSlot(EquipmentSlot.FEET,new ItemStack(ModItems.GHOST_GARB_BOOTS.get()));
        HauntProtection.clearNegative(p);
        state=HauntProtection.state(p);
        h.assertTrue(!state.contains("CompanyUntil") && !state.contains("MarkedUntil") && !state.contains("BreachAt"),"Protection cancels owned pending/timed risks");
        h.assertTrue(state.getLong("EmpoweredUntil")>HauntProtection.now(p),"Benefits survive cleansing");
        for(var kind:HauntEffectKind.values()) if(kind.negative) h.assertFalse(HauntEventController.tryEffect(p,kind),"Full garb suppresses every risk");
        h.assertFalse(HauntEventController.choices(6,true,false).isEmpty(),"Benefit pool remains eligible");
        p.discard();h.succeed();
    }
    // Spawn-cap assertions must not share a batch with tests creating hostile mobs.
    @GameTest(template="deferred_items_test",batch="haunt_encounter")
    public static void loaded_trace_and_bounded_encounter_respect_conditions(GameTestHelper h) {
        floor(h);var p=player(h);
        h.setBlock(new BlockPos(6,2,6),ModBlocks.GRAVESTONE.get());
        h.assertTrue(AdditionalHauntEffects.whispers(p),"Nearby loaded grave receives a directional whisper and particles");
        p.setPos(p.getX(),p.getY()+200,p.getZ());
        h.assertFalse(AdditionalHauntEffects.whispers(p),"No fictional trace when nothing nearby");
        p.setPos(h.absolutePos(new BlockPos(7,2,7)).getCenter().add(0,-0.5,0));
        h.assertTrue(AdditionalHauntEffects.spawnEncounter(p,3,600,false)>0,"Safe bounded encounter spawns");
        var mobs=h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.Mob.class,p.getBoundingBox().inflate(48),m->m.getPersistentData().hasUUID(AdditionalHauntEffects.TEMP_OWNER));
        h.assertTrue(mobs.size()<=3 && !mobs.isEmpty(),"Breach never exceeds three spirits");
        h.assertTrue(AdditionalHauntEffects.spawnEncounter(p,3,600,false)==0,"Existing encounters prevent duplicate groups");
        for(var m:mobs) h.assertTrue(m.getPersistentData().getLong(AdditionalHauntEffects.EXPIRES)==HauntProtection.now(p)+600,"Encounter expires without permanent nuisance");
        AdditionalHauntEffects.cleanup(h.getLevel(),HauntProtection.now(p)+601);
        h.assertTrue(mobs.stream().allMatch(net.minecraft.world.entity.Entity::isRemoved),"All expired encounter entities are removed safely in one cleanup pass");
        p.discard();h.succeed();
    }
}

