package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AltarBenefitsGameTests {
    private static AltarOfPurityBlockEntity altar(GameTestHelper h){h.setBlock(new BlockPos(7,2,7),ModBlocks.ALTAR_OF_PURITY.get());return (AltarOfPurityBlockEntity)h.getLevel().getBlockEntity(h.absolutePos(new BlockPos(7,2,7)));}
    private static net.minecraft.server.level.ServerPlayer player(GameTestHelper h,AltarOfPurityBlockEntity a){var p=h.makeMockServerPlayerInLevel();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.setPos(a.getBlockPos().getCenter());PlayerHauntManager.setTier(p,HauntTier.SUPERNATURAL);return p;}
    private static void tick(GameTestHelper h,AltarOfPurityBlockEntity a){AltarOfPurityBlockEntity.serverTick(h.getLevel(),a.getBlockPos(),a.getBlockState(),a);}
    @GameTest(template="deferred_items_test")
    public static void benefit_roll_blesses_one_offering_without_spending_extra_items_or_risk_cooldown(GameTestHelper h){
        var a=altar(h);var p=player(h,a);a.getItems().setStackInSlot(0,new ItemStack(ModItems.HOLY_SALT.get(),2));
        p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD,new ItemStack(ModItems.GHOST_GARB_HELMET.get()));p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST,new ItemStack(ModItems.GHOST_GARB_CHESTPLATE.get()));p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS,new ItemStack(ModItems.GHOST_GARB_LEGGINGS.get()));p.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET,new ItemStack(ModItems.GHOST_GARB_BOOTS.get()));
        h.assertTrue(HauntProtection.protectedPlayer(p) && HauntEventController.tryEffect(p,HauntEffectKind.OFFERING_BLESSING),"Garb does not suppress the positive offering event");
        h.assertTrue(a.hasBlessedOffering() && HauntEventController.ready(p,true) && !HauntEventController.ready(p,false),"Benefit starts only its own cooldown");
        tick(h,a);h.assertTrue(a.getBurnTicks()==1799 && a.getItems().getStackInSlot(0).getCount()==1 && !a.hasBlessedOffering(),"One consumed salt grants 50 percent extra time exactly once");
        for(int i=0;i<250;i++)tick(h,a);h.assertTrue(a.getBurnTicks()==2749,"The subsequent offering has its ordinary duration");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void gifts_are_real_items_and_shared_chunk_cooldown_prevents_repeat_players(GameTestHelper h){
        var a=altar(h);var p=player(h,a);h.assertTrue(HauntEffects.perform(p,HauntEffectKind.OFFERING_GIFT),"Gift event finds a loaded nearby altar");
        h.assertTrue(a.getItems().getStackInSlot(0).is(ModItems.HOLY_SALT.get()) && a.getItems().getStackInSlot(0).getCount()==1,"One real Holy Salt is delivered to input");
        var other=player(h,a);h.assertFalse(HauntEffects.perform(other,HauntEffectKind.OFFERING_GIFT),"Another player cannot bypass shared world cooldown");
        h.assertTrue(a.getItems().getStackInSlot(0).getCount()==1,"Blocked gift cannot duplicate materials");p.discard();other.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void unavailable_altars_storage_and_existing_blessings_fail_without_mutating(GameTestHelper h){
        var a=altar(h);var p=player(h,a);h.assertFalse(a.blessNextOffering(),"Empty input cannot receive a burn blessing");
        a.getItems().setStackInSlot(0,new ItemStack(ModItems.SPIRITWOOD_LOG.get(),2));h.assertFalse(a.addOfferingGift(),"Gift never overwrites another offering type");
        h.assertTrue(a.getItems().getStackInSlot(0).getCount()==2,"Incompatible input remains intact");
        a.getItems().setStackInSlot(0,new ItemStack(ModItems.HOLY_SALT.get(),64));h.assertFalse(a.addOfferingGift(),"Full input refuses the gift without losing items");
        var tag=a.saveWithoutMetadata(h.getLevel().registryAccess());tag.putInt("BurnTicks",AltarOfPurityBlockEntity.MAX_BURN_TICKS);a.loadAdditional(tag,h.getLevel().registryAccess());h.assertFalse(a.blessNextOffering(),"Full reserve refuses a bonus that cannot fit");
        tag.putInt("BurnTicks",0);a.loadAdditional(tag,h.getLevel().registryAccess());h.assertTrue(a.blessNextOffering() && !a.blessNextOffering(),"Pending blessings cannot stack");
        p.setPos(p.position().add(50000,0,50000));h.assertFalse(HauntEffects.perform(p,HauntEffectKind.OFFERING_GIFT),"Absent/out-of-range altars cannot produce fake success");p.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void pending_blessing_survives_save_then_clears_on_consumption(GameTestHelper h){
        var a=altar(h);a.getItems().setStackInSlot(0,new ItemStack(ModItems.PURE_ECTOPLASM.get()));h.assertTrue(a.blessNextOffering(),"Purified offering receives blessing");
        var tag=a.saveWithoutMetadata(h.getLevel().registryAccess());a.loadAdditional(tag,h.getLevel().registryAccess());h.assertTrue(a.hasBlessedOffering(),"Pending blessing is persistent");
        tick(h,a);h.assertTrue(a.getBurnTicks()==3599 && !a.hasBlessedOffering(),"Purified fuel adds 180 seconds rather than 120");
        tag=a.saveWithoutMetadata(h.getLevel().registryAccess());a.loadAdditional(tag,h.getLevel().registryAccess());h.assertFalse(a.hasBlessedOffering(),"Used blessing does not reappear after loading");h.succeed();
    }
}
