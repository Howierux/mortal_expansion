package com.howierux.mortalexpansion.machine;
import com.howierux.mortalexpansion.catalog.*;
import com.howierux.mortalexpansion.content.ContentRuntime;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import team.reborn.energy.api.base.SimpleEnergyStorage;
public final class EctchantmentEntity extends BlockEntity implements WorldlyContainer,ExtendedScreenHandlerFactory<BlockPos> {
 public static final int STATUS_IDLE=0,STATUS_NEEDS_RESOURCE=1,STATUS_INVALID_INPUT=2,STATUS_OUTPUT_READY=3;
 public static final int MAX_CHARGE=16000,FE_CAPACITY=160000;public final SimpleEnergyStorage energy;
 private final net.minecraft.core.NonNullList<ItemStack> items=net.minecraft.core.NonNullList.withSize(3,ItemStack.EMPTY);private int charge;private long observedEnergy;private int received,recentReceived;
 public EctchantmentEntity(BlockPos pos,BlockState state){super(ContentRuntime.ECTCHANT,pos,state);energy=new SimpleEnergyStorage(FE_CAPACITY,4000,0){protected void onFinalCommit(){received+=Math.max(0,(int)(amount-observedEnergy));observedEnergy=amount;setChanged();}};}
 public static int fuel(ItemStack s){return s.is(CatalogItems.RAW_ECTOPLASM)?1000:s.is(CatalogItems.PURE_ECTOPLASM)?4000:0;}
 public ItemStack result(ItemStack s){if(s.isEmpty())return ItemStack.EMPTY;Item out=s.is(Items.DIAMOND)?CatalogItems.BLOODSTONE:s.is(Items.STONE)?CatalogItems.GRAVESTONE:s.is(net.minecraft.tags.ItemTags.LOGS)&&!s.is(CatalogItems.SPIRITWOOD_LOG)?CatalogItems.SPIRITWOOD_LOG:s.is(net.minecraft.tags.ItemTags.SAPLINGS)&&!s.is(CatalogItems.SPIRITWOOD_SAPLING)?CatalogItems.SPIRITWOOD_SAPLING:s.is(Items.VINE)||s.is(Items.MOSS_BLOCK)||s.is(Items.GLOW_LICHEN)?CatalogItems.GRAVE_MOSS:null;if(out!=null)return new ItemStack(out);if(com.howierux.mortalexpansion.combat.DemiTangibleDamage.isDemiTangibleWeapon(s))return ItemStack.EMPTY;var result=s.copyWithCount(1);result.set(com.howierux.mortalexpansion.combat.CombatRuntime.DEMI_COMPONENT,true);result.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,true);return result;}
 public int charge(){return charge;}public int status(){if(items.get(0).isEmpty())return items.get(2).isEmpty()?0:3;var result=result(items.get(0));if(result.isEmpty())return 2;if(!fits(result))return 3;return charge>=4000||energy.amount>=40000?0:1;}
 private boolean fits(ItemStack result){var out=items.get(2);return out.isEmpty()?result.getCount()<=result.getMaxStackSize():ItemStack.isSameItemSameComponents(out,result)&&out.getCount()+result.getCount()<=Math.min(getMaxStackSize(),result.getMaxStackSize());}
 public void tick(){recentReceived=received;received=0;observedEnergy=energy.amount;int fuel=fuel(items.get(1));if(fuel>0&&charge+fuel<=MAX_CHARGE){items.get(1).shrink(1);charge+=fuel;setChanged();}var result=result(items.get(0));if(result.isEmpty()||!fits(result)||charge<4000&&energy.amount<40000)return;if(charge>=4000)charge-=4000;else {energy.amount-=40000;observedEnergy=energy.amount;}items.get(0).shrink(1);if(items.get(2).isEmpty())items.set(2,result);else items.get(2).grow(result.getCount());setChanged();}
 public ContainerData data(){return new ContainerData(){public int getCount(){return 6;}public int get(int i){return switch(i){case 0->charge;case 1->MAX_CHARGE;case 2->(int)energy.amount/10;case 3->FE_CAPACITY/10;case 4->recentReceived/10;default->status();};}public void set(int i,int value){}};}
 public net.minecraft.network.chat.Component getDisplayName(){return net.minecraft.network.chat.Component.translatable("block.mortal_expansion.ectchantment_machine");}
 public BlockPos getScreenOpeningData(net.minecraft.server.level.ServerPlayer p){return worldPosition;}
 public AbstractContainerMenu createMenu(int id,Inventory inv,Player p){return new EctchantmentMenu(id,inv,this,data());}
 public int getContainerSize(){return 3;}public boolean isEmpty(){return items.stream().allMatch(ItemStack::isEmpty);}public ItemStack getItem(int slot){return items.get(slot);}public ItemStack removeItem(int slot,int count){var item=ContainerHelper.removeItem(items,slot,count);if(!item.isEmpty())setChanged();return item;}public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(items,slot);}public void setItem(int slot,ItemStack s){items.set(slot,s);setChanged();}public void clearContent(){items.clear();}public boolean stillValid(Player p){return level!=null&&level.getBlockEntity(worldPosition)==this&&p.distanceToSqr(worldPosition.getCenter())<=64;}public boolean canPlaceItem(int slot,ItemStack s){return slot==0||slot==1&&fuel(s)>0;}public int[] getSlotsForFace(net.minecraft.core.Direction side){return new int[]{0,1,2};}public boolean canPlaceItemThroughFace(int slot,ItemStack s,Direction side){return canPlaceItem(slot,s);}public boolean canTakeItemThroughFace(int slot,ItemStack s,Direction side){return slot==2;}
 protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);ContainerHelper.saveAllItems(tag,items,lookup);tag.putInt("EctoplasmicCharge",charge);tag.putLong("Energy",energy.amount);}
 protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);ContainerHelper.loadAllItems(tag,items,lookup);charge=Math.clamp(tag.getInt("EctoplasmicCharge"),0,MAX_CHARGE);energy.amount=Math.clamp(tag.getLong("Energy"),0,FE_CAPACITY);observedEnergy=energy.amount;received=recentReceived=0;}
}
