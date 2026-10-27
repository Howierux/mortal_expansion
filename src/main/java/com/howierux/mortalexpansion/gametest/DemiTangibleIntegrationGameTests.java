package com.howierux.mortalexpansion.gametest;
import com.howierux.mortalexpansion.combat.*;
import com.howierux.mortalexpansion.registry.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder("mortal_expansion") @PrefixGameTestTemplate(false)
public final class DemiTangibleIntegrationGameTests {
    private static ItemStack book(GameTestHelper h){
        var result=new ItemStack(Items.ENCHANTED_BOOK);var ench=new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        ench.set(h.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModEnchantments.DEMI_TANGIBLE),1);
        result.set(DataComponents.STORED_ENCHANTMENTS,ench.toImmutable());return result;
    }
    private static ItemStack apply(GameTestHelper h,net.minecraft.server.level.ServerPlayer player,ItemStack item,ItemStack book){
        var pos=h.absolutePos(new BlockPos(4,2,4));h.getLevel().setBlockAndUpdate(pos,Blocks.ANVIL.defaultBlockState());player.setPos(pos.getCenter());
        var menu=new AnvilMenu(1,player.getInventory(),ContainerLevelAccess.create(h.getLevel(),pos));menu.getSlot(0).set(item);menu.getSlot(1).set(book);menu.createResult();
        var result=menu.getSlot(2).getItem().copy();
        h.assertTrue(!result.isEmpty(),"Actual anvil accepts Demi-Tangible on "+item.getItem());
        h.assertTrue(menu.getCost()>=2 && menu.getSlot(0).getItem().getCount()==item.getCount(),"Preview costs levels without consuming input");
        var taken=menu.getSlot(2).remove(result.getCount());menu.getSlot(2).onTake(player,taken);
        h.assertTrue(menu.getSlot(0).getItem().isEmpty() && menu.getSlot(1).getItem().isEmpty(),"Taking result consumes the input stack and one book");return result;
    }
    @GameTest(template="deferred_items_test")
    public static void book_applies_to_non_enchantable_items_and_preserves_other_restrictions(GameTestHelper h){
        var player=h.makeMockServerPlayerInLevel();var demi=h.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModEnchantments.DEMI_TANGIBLE);
        var scepter=new ItemStack(ModItems.PHARAOHL_SCEPTER.get());scepter.set(ModDataComponents.SCEPTER_CHARGE.get(),8);scepter.set(DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Unquiet"));
        h.assertFalse(scepter.supportsEnchantment(demi),"Modded weapon fixture rejects normal support");
        var mixed=book(h);var enchantments=new ItemEnchantments.Mutable(mixed.get(DataComponents.STORED_ENCHANTMENTS));var sharpness=h.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS);
        enchantments.set(sharpness,5);mixed.set(DataComponents.STORED_ENCHANTMENTS,enchantments.toImmutable());var output=apply(h,player,scepter,mixed);
        h.assertTrue(output.getTagEnchantments().getLevel(demi)==1 && output.getTagEnchantments().getLevel(sharpness)==0,"Only Demi-Tangible bypasses mixed-book restrictions");
        h.assertTrue(output.getOrDefault(ModDataComponents.SCEPTER_CHARGE.get(),0)==8 && output.getHoverName().getString().equals("Unquiet"),"Weapon components and name survive");
        var grind=new GrindstoneMenu(2,player.getInventory(),ContainerLevelAccess.create(h.getLevel(),h.absolutePos(new BlockPos(4,2,4))));
        grind.getSlot(0).set(output.copy());
        h.assertTrue(!grind.getSlot(2).getItem().isEmpty() && !DemiTangibleDamage.isDemiTangibleWeapon(grind.getSlot(2).getItem()),"Native grindstone removal leaves no hidden book-granted combat component");
        for(var item:java.util.List.of(new ItemStack(Items.APPLE,16),new ItemStack(Items.ARROW,64),new ItemStack(Items.BOW),new ItemStack(Items.CROSSBOW))){
            output=apply(h,player,item,book(h));h.assertTrue(output.getCount()==item.getCount() && DemiTangibleDamage.isDemiTangibleWeapon(output),"Any-item result retains stack and effect");
        }
        var veto=new net.neoforged.neoforge.event.AnvilUpdateEvent(scepter,book(h),null,0,player);veto.setCanceled(true);DemiTangibleAnvil.update(veto);
        h.assertFalse(veto.isCanceled() || veto.getOutput().isEmpty(),"Specific enchantment overrides generic veto");h.assertTrue(veto.getMaterialCost()==1,"One book charged");
        var ordinary=new net.neoforged.neoforge.event.AnvilUpdateEvent(scepter,new ItemStack(Items.STICK),null,0,player);ordinary.setCanceled(true);DemiTangibleAnvil.update(ordinary);
        h.assertTrue(ordinary.isCanceled() && ordinary.getOutput().isEmpty(),"Unrelated restrictions untouched");player.discard();h.succeed();
    }
    @GameTest(template="deferred_items_test")
    public static void projectile_launch_snapshots_ammo_weapon_switching_and_reload(GameTestHelper h){
        var player=h.makeMockServerPlayerInLevel();var level=h.getLevel();var bow=apply(h,player,new ItemStack(Items.BOW),book(h));
        player.setItemInHand(InteractionHand.MAIN_HAND,bow);player.setItemInHand(InteractionHand.OFF_HAND,new ItemStack(Items.ARROW,8));
        java.util.List<AbstractArrow> arrows=new java.util.ArrayList<>();java.util.function.Consumer<net.neoforged.neoforge.event.entity.EntityJoinLevelEvent> listener=event->{if(event.getEntity() instanceof AbstractArrow arrow && arrow.getOwner()==player)arrows.add(arrow);};
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(listener);
        try{
            bow.releaseUsing(level,player,bow.getUseDuration(player)-20);h.assertTrue(!arrows.isEmpty(),"Real bow release spawned arrow");var enchantedArrow=arrows.getLast();
            player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.STICK));h.assertTrue(DemiTangibleProjectiles.marked(enchantedArrow) && DemiTangibleDamage.canDamageIntangible(level.damageSources().arrow(enchantedArrow,player)),"Enchanted launcher survives weapon switching");
            var plainBow=new ItemStack(Items.BOW);player.setItemInHand(InteractionHand.MAIN_HAND,plainBow);plainBow.releaseUsing(level,player,plainBow.getUseDuration(player)-20);var plainArrow=arrows.getLast();h.assertTrue(plainArrow!=enchantedArrow,"Separate ordinary shot");
            player.setItemInHand(InteractionHand.MAIN_HAND,bow);h.assertFalse(DemiTangibleDamage.canDamageIntangible(level.damageSources().arrow(plainArrow,player)),"Ordinary shot cannot inherit later held enchantment");
            var ammo=apply(h,player,new ItemStack(Items.ARROW,8),book(h));player.setItemInHand(InteractionHand.MAIN_HAND,plainBow);player.setItemInHand(InteractionHand.OFF_HAND,ammo);plainBow.releaseUsing(level,player,plainBow.getUseDuration(player)-20);
            h.assertTrue(DemiTangibleProjectiles.marked(arrows.getLast()),"Enchanted ammo works with ordinary bow");
            var generic=new LlamaSpit(EntityType.LLAMA_SPIT,level);generic.setOwner(player);DemiTangibleProjectiles.using(bow,player,()->level.addFreshEntity(generic));h.assertTrue(DemiTangibleProjectiles.marked(generic),"Generic projectile without arrow storage inherits use context");
            var saved=new net.minecraft.nbt.CompoundTag();generic.saveWithoutId(saved);var reloaded=new LlamaSpit(EntityType.LLAMA_SPIT,level);reloaded.load(saved);h.assertTrue(DemiTangibleProjectiles.marked(reloaded),"Projectile permission saves and loads");
            var unmarked=new LlamaSpit(EntityType.LLAMA_SPIT,level);unmarked.setOwner(player);DemiTangibleProjectiles.spawned(new net.neoforged.neoforge.event.entity.EntityJoinLevelEvent(unmarked,level,true));h.assertFalse(DemiTangibleProjectiles.marked(unmarked),"Load never infers current equipment");
            var ghost=ModEntities.GHOST.get().create(level);var demon=ModEntities.DEMON.get().create(level);var source=level.damageSources().thrown(generic,player);
            h.assertTrue(ghost.hurt(source,8),"Captured projectile hurts ghost");float before=demon.getHealth();h.assertTrue(demon.hurt(source,8) && Math.abs(before-demon.getHealth()-4)<0.001F,"Demon half-damage resistance preserved");
            ghost.discard();demon.discard();generic.discard();reloaded.discard();unmarked.discard();
        }finally{net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(listener);for(var arrow:arrows)arrow.discard();player.discard();}
        h.succeed();
    }
}
