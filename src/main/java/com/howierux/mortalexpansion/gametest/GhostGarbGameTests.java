package com.howierux.mortalexpansion.gametest;

import com.howierux.mortalexpansion.MortalExpansion;
import com.howierux.mortalexpansion.registry.ModItems;
import com.howierux.mortalexpansion.registry.ModEntities;
import com.howierux.mortalexpansion.haunt.effects.HauntProtection;
import com.howierux.mortalexpansion.combat.GhostGarbPhasing;
import com.howierux.mortalexpansion.entity.mob.SpiritTorchBarrierTracker;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(MortalExpansion.MOD_ID)
@PrefixGameTestTemplate(false)
public final class GhostGarbGameTests {
    private static ServerPlayer equipped(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(GameType.SURVIVAL);
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.GHOST_GARB_HELMET.get()));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.GHOST_GARB_CHESTPLATE.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.GHOST_GARB_LEGGINGS.get()));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.GHOST_GARB_BOOTS.get()));
        for (int i = 0; i < 61; i++) player.tick();
        player.doTick();
        return player;
    }
    @GameTest(template = "deferred_items_test")
    public static void garb_wall_breathing_depletes_and_refills_native_air(GameTestHelper helper) {
        var player = equipped(helper);
        var pos = helper.absolutePos(new BlockPos(5, 2, 5));
        player.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        player.setAirSupply(100);
        // Standing on a floor or merely having feet in a block does not immerse the head.
        helper.getLevel().setBlock(pos, Blocks.STONE.defaultBlockState(), 3);
        player.baseTick();
        helper.assertTrue(player.getAirSupply() == 104, "Feet-only overlap still breathes");
        helper.getLevel().setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        player.baseTick();
        helper.assertTrue(player.getAirSupply() == 103, "Head immersed in wall consumes native air");
        helper.getLevel().setBlock(pos.above(), Blocks.GLASS.defaultBlockState(), 3);
        player.baseTick();
        helper.assertTrue(player.getAirSupply() == 102, "Glass collision also requires holding breath");
        player.setAirSupply(-19); player.invulnerableTime = 0;
        float health = player.getHealth(); player.baseTick();
        helper.assertTrue(player.getAirSupply() == 0 && player.getHealth() == health - 2, "Empty wall air causes one heart of vanilla drowning damage");
        helper.getLevel().setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
        player.baseTick(); helper.assertTrue(player.getAirSupply() == 4, "Emerging refills four air per tick");
        helper.assertFalse(player.hurt(player.damageSources().drown(), 2), "Ordinary drowning immunity outside walls is unchanged");
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        helper.getLevel().setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        helper.assertFalse(com.howierux.mortalexpansion.combat.GhostGarbBreathing.headInsideWall(player), "Partial set does not use wall air mechanic");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void haint_wall_breathing_honors_respiration_and_water_breathing(GameTestHelper helper) {
        var player = equipped(helper);
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.HAINT_GARB_HELMET.get()));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.HAINT_GARB_CHESTPLATE.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.HAINT_GARB_LEGGINGS.get()));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.HAINT_GARB_BOOTS.get()));
        var respiration = helper.getLevel().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.RESPIRATION);
        player.getItemBySlot(EquipmentSlot.HEAD).enchant(respiration, 3); player.doTick();
        var pos = helper.absolutePos(new BlockPos(5, 2, 5));
        player.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        helper.getLevel().setBlock(pos.above(), Blocks.STONE.defaultBlockState(), 3);
        helper.assertTrue(player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.OXYGEN_BONUS) >= 3, "Respiration installs native oxygen bonus");
        player.setAirSupply(300); player.getRandom().setSeed(12345);
        for (int i = 0; i < 200; i++) player.baseTick();
        helper.assertTrue(player.getAirSupply() >= 200 && player.getAirSupply() < 300, "Respiration III substantially slows real air depletion");
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WATER_BREATHING, 100));
        int air = player.getAirSupply(); player.baseTick();
        helper.assertTrue(player.getAirSupply() == air, "Water Breathing holds air without refilling inside wall");
        player.removeEffect(net.minecraft.world.effect.MobEffects.WATER_BREATHING);
        player.setGameMode(GameType.CREATIVE); player.baseTick();
        helper.assertTrue(player.getAirSupply() == air, "Creative players do not lose wall air");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void torch_boundaries_follow_block_updates_immediately(GameTestHelper helper) {
        var level = helper.getLevel();
        var a = helper.absolutePos(new BlockPos(3, 3, 3));
        var b = a.east(6);
        var middle = a.east(3);
        var torch = com.howierux.mortalexpansion.registry.ModBlocks.SPIRIT_TORCH.get().defaultBlockState();
        var wallTorch = com.howierux.mortalexpansion.registry.ModBlocks.SPIRIT_WALL_TORCH.get().defaultBlockState();
        var air = Blocks.AIR.defaultBlockState();
        // Feed the same transitions as a client update; no server torch lifecycle callbacks are used.
        SpiritTorchBarrierTracker.onBlockChanged(level, a, air, torch);
        SpiritTorchBarrierTracker.onBlockChanged(level, b, air, wallTorch);
        helper.assertTrue(SpiritTorchBarrierTracker.hasBarrierColumn(level, middle.getX(), middle.getZ()), "Placement creates boundary immediately");
        SpiritTorchBarrierTracker.onBlockChanged(level, a, torch, air);
        helper.assertFalse(SpiritTorchBarrierTracker.hasBarrierColumn(level, middle.getX(), middle.getZ()), "Breaking endpoint removes boundary without waiting or changing gear");
        // A denied predicted break restores the endpoint, without double counting repeated updates.
        SpiritTorchBarrierTracker.onBlockChanged(level, a, air, torch);
        SpiritTorchBarrierTracker.onBlockChanged(level, a, air, torch);
        helper.assertTrue(SpiritTorchBarrierTracker.hasBarrierColumn(level, middle.getX(), middle.getZ()), "Server correction restores boundary");
        SpiritTorchBarrierTracker.onBlockChanged(level, b, wallTorch, air);
        helper.assertFalse(SpiritTorchBarrierTracker.hasBarrierColumn(level, middle.getX(), middle.getZ()), "Wall torch removal also clears boundary");
        SpiritTorchBarrierTracker.onBlockChanged(level, a, torch, air);
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void ectothread_recipes_yield_eight(GameTestHelper helper) {
        var cores = new net.minecraft.world.item.Item[]{ModItems.RAW_ECTOPLASM.get(), ModItems.PURE_ECTOPLASM.get()};
        var results = new net.minecraft.world.item.Item[]{ModItems.ECTOTHREAD.get(), ModItems.PURIFIED_ECTOTHREAD.get()};
        for (int n = 0; n < cores.length; n++) {
            var ingredients = new java.util.ArrayList<ItemStack>();
            for (int i = 0; i < 9; i++) ingredients.add(new ItemStack(i == 4 ? cores[n] : net.minecraft.world.item.Items.STRING));
            var input = net.minecraft.world.item.crafting.CraftingInput.of(3, 3, ingredients);
            var recipe = helper.getLevel().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING, input, helper.getLevel()).orElseThrow();
            var result = recipe.value().assemble(input, helper.getLevel().registryAccess());
            helper.assertTrue(result.is(results[n]) && result.getCount() == 8, "Eight matching threads from string ring and ectoplasm");
        }
        helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void haint_garb_matches_stats_and_full_set_benefits(GameTestHelper helper) {
        var player = equipped(helper);
        var slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        var pieces = new net.minecraft.world.item.ArmorItem[]{ModItems.HAINT_GARB_HELMET.get(), ModItems.HAINT_GARB_CHESTPLATE.get(), ModItems.HAINT_GARB_LEGGINGS.get(), ModItems.HAINT_GARB_BOOTS.get()};
        var patterns = new String[][]{{"EEE", "E E", "   "}, {"E E", "EEE", "EEE"}, {"EEE", "E E", "E E"}, {"E E", "E E", "   "}};
        for (int i = 0; i < pieces.length; i++) {
            var stack = new ItemStack(pieces[i]);
            helper.assertTrue(stack.getMaxDamage() == player.getItemBySlot(slots[i]).getMaxDamage(), "Haint and Ghost durability match");
            helper.assertTrue(stack.get(net.minecraft.core.component.DataComponents.DYED_COLOR).rgb() == 0x3AB3DA, "Light blue leather placeholder tint");
            helper.assertTrue(pieces[i].getMaterial().value().repairIngredient().get().test(new ItemStack(ModItems.PURIFIED_ECTOTHREAD.get())), "Repairs with purified thread");
            var ingredients = new java.util.ArrayList<ItemStack>();
            for (String row : patterns[i]) for (char c : row.toCharArray()) ingredients.add(c == 'E' ? new ItemStack(ModItems.PURIFIED_ECTOTHREAD.get()) : ItemStack.EMPTY);
            var input = net.minecraft.world.item.crafting.CraftingInput.of(3, 3, ingredients);
            var recipe = helper.getLevel().getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING, input, helper.getLevel()).orElseThrow();
            helper.assertTrue(recipe.value().assemble(input, helper.getLevel().registryAccess()).is(pieces[i]), "Standard armor pattern produces Haint piece");
            player.setItemSlot(slots[i], stack);
        }
        player.doTick();
        helper.assertTrue(player.getArmorValue() == 7 && GhostGarbPhasing.active(player), "Leather defense and full-set phasing");
        helper.assertFalse(player.hurt(player.damageSources().inFire(), 4), "Normal damage immunity");
        helper.assertFalse(HauntProtection.apply(player, net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true), "Negative haunt immunity");
        var ghost = ModEntities.GHOST.get().create(helper.getLevel());
        helper.assertTrue(player.hurt(player.damageSources().mobAttack(ghost), 4), "Intangible attacker still hurts wearer");
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        helper.assertFalse(GhostGarbPhasing.active(player), "Partial Haint set loses benefits");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void demi_tangible_weapon_damage_is_magic(GameTestHelper helper) {
        var attacker = helper.makeMockServerPlayerInLevel();
        var target = equipped(helper);
        attacker.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD));
        helper.assertFalse(attacker.damageSources().playerAttack(attacker).is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_MAGIC), "Ordinary weapon remains physical");
        attacker.getMainHandItem().set(com.howierux.mortalexpansion.registry.ModDataComponents.DEMI_TANGIBLE.get(), true);
        var source = attacker.damageSources().playerAttack(attacker);
        helper.assertTrue(source.is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_MAGIC)
                && source.typeHolder().is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_MAGIC), "Demi-tangible damage type carries standard magic tag");
        helper.assertTrue(source.getEntity() == attacker && source.getDirectEntity() == attacker
                && source.getWeaponItem() == attacker.getMainHandItem(), "Attacker and weapon attribution retained");
        helper.assertFalse(source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR), "Weapon still respects armor");
        helper.assertTrue(com.howierux.mortalexpansion.combat.DemiTangibleDamage.canDamageIntangible(source), "Magical player weapon bypasses intangibility");
        var zombie = net.minecraft.world.entity.EntityType.ZOMBIE.create(helper.getLevel());
        zombie.setItemSlot(EquipmentSlot.MAINHAND, attacker.getMainHandItem().copy());
        helper.assertTrue(zombie.damageSources().mobAttack(zombie).is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_MAGIC), "Mob weapon damage is magic too");
        helper.assertTrue(target.hurt(zombie.damageSources().mobAttack(zombie), 4), "Magical mob weapon hurts intangible garb wearer");
        attacker.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BLOODSTONE_SWORD.get()));
        helper.assertTrue(attacker.damageSources().playerAttack(attacker).is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_MAGIC), "Tagged Bloodstone weapon is magic");
        attacker.discard(); target.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void garb_damage_and_set_rules(GameTestHelper helper) {
        var player = equipped(helper);
        helper.assertTrue(player.getArmorValue() == 7, "Full leather defense");
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.HEAD).getMaxDamage() == 165
                && player.getItemBySlot(EquipmentSlot.CHEST).getMaxDamage() == 240
                && player.getItemBySlot(EquipmentSlot.LEGS).getMaxDamage() == 225
                && player.getItemBySlot(EquipmentSlot.FEET).getMaxDamage() == 195, "Iron durability");
        var zombie = net.minecraft.world.entity.EntityType.ZOMBIE.create(helper.getLevel());
        var ghost = ModEntities.GHOST.get().create(helper.getLevel());
        helper.assertFalse(player.hurt(player.damageSources().mobAttack(zombie), 4), "Ordinary melee blocked");
        helper.assertFalse(player.hurt(player.damageSources().inFire(), 4), "Ordinary environmental damage blocked");
        helper.assertTrue(player.hurt(player.damageSources().mobAttack(ghost), 4), "Intangible attackers hurt garb wearers");
        player.invulnerableTime = 0;
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        helper.assertFalse(GhostGarbPhasing.active(player), "Partial set has no intangibility");
        helper.assertTrue(player.hurt(player.damageSources().mobAttack(zombie), 4), "Removing a piece restores normal damage");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void garb_phases_walls_but_keeps_floors_and_barriers(GameTestHelper helper) {
        var player = equipped(helper);
        var start = helper.absolutePos(new BlockPos(5, 2, 5));
        for (int x = 4; x <= 8; x++) for (int z = 4; z <= 6; z++) helper.getLevel().setBlock(new BlockPos(x + start.getX() - 5, start.getY() - 1, z + start.getZ() - 5), Blocks.STONE.defaultBlockState(), 3);
        player.moveTo(start.getX() + .5, start.getY(), start.getZ() + .5, 0, 0);
        helper.getLevel().setBlock(start.east(), Blocks.STONE.defaultBlockState(), 3);
        helper.getLevel().setBlock(start.east().above(), Blocks.STONE.defaultBlockState(), 3);
        Vec3 before = player.position();
        player.move(net.minecraft.world.entity.MoverType.SELF, new Vec3(1, -1, 0));
        helper.assertTrue(Math.abs(player.getX() - before.x - 1) < .01 && Math.abs(player.getY() - before.y) < .01, "Wall phased while floor collision retained");
        helper.getLevel().setBlock(start.east(2), com.howierux.mortalexpansion.registry.ModBlocks.SPIRITWOOD_LOG.get().defaultBlockState(), 3);
        helper.getLevel().setBlock(start.east(2).above(), com.howierux.mortalexpansion.registry.ModBlocks.SPIRITWOOD_LOG.get().defaultBlockState(), 3);
        before = player.position();
        player.move(net.minecraft.world.entity.MoverType.SELF, new Vec3(2, 0, 0));
        helper.assertTrue(player.getX() - before.x < 1, "Spectral-solid barrier blocks swept movement");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void garb_blocks_haunt_without_blocking_benefits(GameTestHelper helper) {
        var player = equipped(helper);
        helper.assertFalse(HauntProtection.apply(player, net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true), "Negative haunt blocked");
        helper.assertTrue(HauntProtection.apply(player, net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 100, 0, false), "Beneficial effect allowed");
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        helper.assertTrue(HauntProtection.apply(player, net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 1, true), "Incomplete set loses protection");
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.GHOST_GARB_HELMET.get()));
        HauntProtection.clearNegative(player);
        helper.assertFalse(player.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN), "Equipping cleans existing haunt-owned effects");
        helper.assertTrue(player.hasEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED), "Beneficial effect preserved");
        player.discard(); helper.succeed();
    }
    @GameTest(template = "deferred_items_test")
    public static void tagged_magic_bypasses_intangibility(GameTestHelper helper) {
        var player = equipped(helper);
        helper.assertTrue(player.hurt(player.damageSources().magic(), 4), "Tagged magic damages Ghost's Garb wearer");
        player.invulnerableTime = 0;
        helper.assertFalse(player.hurt(player.damageSources().inFire(), 4), "Ordinary fire remains blocked");
        var ghost = ModEntities.GHOST.get().create(helper.getLevel());
        helper.assertTrue(ghost.hurt(player.damageSources().magic(), 4), "Same magic exception damages intangible mobs");
        for (String name : new String[]{"magic", "magical", "is_magic", "is_magical", "damage/magical"}) {
            helper.assertTrue(com.howierux.mortalexpansion.combat.DemiTangibleDamage.isMagicTag(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("example_mod", name)), "Cross-mod magic tag recognized: " + name);
        }
        helper.assertFalse(com.howierux.mortalexpansion.combat.DemiTangibleDamage.isMagicTag(
                net.minecraft.resources.ResourceLocation.parse("example_mod:non_magical")), "Unrelated tag is not a bypass");
        player.discard(); helper.succeed();
    }

}



