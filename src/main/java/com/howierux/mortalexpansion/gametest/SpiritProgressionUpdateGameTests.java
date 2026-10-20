package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.combat.BloodstoneToolScaling;
import com.howierux.mortalexpansion.entity.mob.SpiritTrades;
import com.howierux.mortalexpansion.haunt.*;
import com.howierux.mortalexpansion.haunt.effects.*;
import com.howierux.mortalexpansion.registry.*;
import com.howierux.mortalexpansion.blockentity.AltarOfPurityBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SpiritProgressionUpdateGameTests {
    @GameTest(template="deferred_items_test")
    public static void bloodstone_diamond_baselines_and_personal_tiers(GameTestHelper h) {
        var player=h.makeMockServerPlayerInLevel();
        Item[] tools={ModItems.BLOODSTONE_SWORD.get(),ModItems.BLOODSTONE_PICKAXE.get(),ModItems.BLOODSTONE_AXE.get(),ModItems.BLOODSTONE_SHOVEL.get(),ModItems.BLOODSTONE_HOE.get()};
        double[] damages={7,5,9,5.5,1},speeds={1.6,1.2,1,1,4};
        for(int tool=0;tool<tools.length;tool++) {
            var stack=new ItemStack(tools[tool]);player.setItemSlot(EquipmentSlot.MAINHAND,stack);
            double[] base={1,4};
            stack.get(DataComponents.ATTRIBUTE_MODIFIERS).forEach(EquipmentSlot.MAINHAND,(attribute,modifier)->{
                if(attribute.equals(Attributes.ATTACK_DAMAGE))base[0]+=modifier.amount();
                if(attribute.equals(Attributes.ATTACK_SPEED))base[1]+=modifier.amount();
            });
            h.assertTrue(Math.abs(base[0]-damages[tool])<0.00001 && Math.abs(base[1]-speeds[tool])<0.00001,"Each Bloodstone tool starts at its Diamond counterpart's damage and attack speed");
            for(var tier:HauntTier.values()) {
                PlayerHauntManager.setTier(player,tier);BloodstoneToolScaling.update(player);
                double factor=0.9+0.1*tier.id();
                var damage=player.getAttribute(Attributes.ATTACK_DAMAGE).getModifier(BloodstoneToolScaling.DAMAGE);
                var speed=player.getAttribute(Attributes.ATTACK_SPEED).getModifier(BloodstoneToolScaling.SPEED);
                h.assertTrue(tier==HauntTier.NATURAL ? damage==null && speed==null : Math.abs(damage.amount()-(factor-1))<0.00001 && Math.abs(speed.amount()-(factor-1))<0.00001,
                        "Personal tier scales attack attributes from Void 90% to Eldritch 150%");
                var mining=new PlayerEvent.BreakSpeed(player,Blocks.STONE.defaultBlockState(),8,player.blockPosition());
                BloodstoneToolScaling.mining(mining);
                h.assertTrue(Math.abs(mining.getNewSpeed()-8*factor)<0.00001,"Mining receives the same tier multiplier");
            }
        }
        player.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.IRON_SWORD));BloodstoneToolScaling.update(player);
        h.assertTrue(!player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(BloodstoneToolScaling.DAMAGE)
                && !player.getAttribute(Attributes.ATTACK_SPEED).hasModifier(BloodstoneToolScaling.SPEED),"Switching tools removes transient bonuses");
        player.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void helpful_visitation_gifts_boon_and_both_altars_burn_sixty_seconds(GameTestHelper h) {
        var player=h.makeMockServerPlayerInLevel();PlayerHauntManager.setTier(player,HauntTier.DEAD_RISING);
        h.assertTrue(HauntEventController.tryEffect(player,HauntEffectKind.HELPFUL_VISITATION),"Visitation is an active benefit");
        int count=player.getInventory().items.stream().filter(s->s.is(ModItems.SPIRIT_BOON.get())).mapToInt(ItemStack::getCount).sum();
        h.assertTrue(count==1 && !HauntEventController.ready(player,false),"One boon delivered and benefit cooldown spent");
        for(var block:new net.minecraft.world.level.block.Block[]{ModBlocks.ALTAR_OF_PURITY.get(),ModBlocks.ALTAR_OF_IMPURITY.get()}) {
            var pos=h.absolutePos(new BlockPos(4,2,4));h.getLevel().setBlock(pos,Blocks.AIR.defaultBlockState(),3);
            h.getLevel().setBlock(pos,block.defaultBlockState(),3);
            var altar=(AltarOfPurityBlockEntity)h.getLevel().getBlockEntity(pos);var offering=new ItemStack(ModItems.SPIRIT_BOON.get(),2);
            h.assertTrue(PurityOfferings.burnTicks(offering,altar.isImpure())==1200,"Either altar values the boon at sixty seconds");
            altar.getItems().setStackInSlot(0,offering);
            AltarOfPurityBlockEntity.serverTick(h.getLevel(),pos,altar.getBlockState(),altar);
            h.assertTrue(altar.getBurnTicks()==1199 && altar.getItems().getStackInSlot(0).getCount()==1,"One boon consumed, adding 1200 ticks before the first burn tick");
        }
        player.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void spirits_have_varied_offers_and_keep_old_stock_on_migration(GameTestHelper h) {
        var catalogs=new java.util.HashSet<String>();
        for(int seed=0;seed<16;seed++) {
            var offers=new MerchantOffers();SpiritTrades.populate(offers,5,seed);
            h.assertTrue(offers.size()==20,"Four offers per expertise tier");
            catalogs.add(MerchantOffers.CODEC.encodeStart(h.getLevel().registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE),offers).getOrThrow().toString());
        }
        h.assertTrue(catalogs.size()>1,"Different Spirits have different trade selections");
        var spirit=ModEntities.SPIRIT.get().create(h.getLevel());
        var first=spirit.getOffers().getFirst();first.increaseUses();
        var saved=spirit.saveWithoutId(new net.minecraft.nbt.CompoundTag());saved.putInt("SpiritTradeCatalog",2);
        // Emulate the old novice catalog: only its two original offers.
        var old=new MerchantOffers();old.add(first);old.add(spirit.getOffers().get(1));
        saved.put("Offers",MerchantOffers.CODEC.encodeStart(h.getLevel().registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE),old).getOrThrow());
        var restored=ModEntities.SPIRIT.get().create(h.getLevel());restored.load(saved);
        h.assertTrue(restored.getOffers().size()==4 && restored.getOffers().getFirst().getUses()==1,"Old offers/stock survive while two varied offers are added");
        var again=ModEntities.SPIRIT.get().create(h.getLevel());again.load(restored.saveWithoutId(new net.minecraft.nbt.CompoundTag()));
        h.assertTrue(again.getOffers().size()==4 && again.getOffers().getFirst().getUses()==1,"Reload does not reroll or duplicate added trades");
        h.succeed();
    }
}
