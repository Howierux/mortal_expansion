package com.howierux.mortalexpansion.blockentity;

import com.howierux.mortalexpansion.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class EnchantmentGeneratorBlockEntity extends BlockEntity {
    public static final int ENERGY_CAPACITY = 400_000;
    public static final int MAX_OUTPUT_PER_TICK = 4_000;

    private static final int FE_SCALE = 1_000;
    private static final String ENERGY_TAG = "Energy";
    private static final String REMAINING_FE_TAG = "RemainingFE";
    private static final String RATE_TAG = "GenerationRate";
    private static final String OUTPUT_TAG = "FinishedItem";

    private final GeneratorEnergyStorage energyStorage =
            new GeneratorEnergyStorage(ENERGY_CAPACITY, MAX_OUTPUT_PER_TICK);

    private int remainingEnergyToGenerate;
    private int generationPerTick;
    private ItemStack finishedItem = ItemStack.EMPTY;

    public EnchantmentGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENCHANTMENT_GENERATOR.get(), pos, state);
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getRemainingEnergyToGenerate() {
        return remainingEnergyToGenerate;
    }

    public int getGenerationPerTick() {
        return generationPerTick;
    }

    public boolean isProcessing() {
        return remainingEnergyToGenerate > 0;
    }

    public boolean hasFinishedItem() {
        return !finishedItem.isEmpty() && !isProcessing();
    }

    public boolean canAccept(ItemStack stack) {
        return !isProcessing()
                && finishedItem.isEmpty()
                && !EnchantmentHelper.getEnchantmentsForCrafting(stack).isEmpty();
    }

    public boolean insertEnchantedItem(ItemStack source) {
        if (!canAccept(source)) {
            return false;
        }

        ItemEnchantments enchantments =
                EnchantmentHelper.getEnchantmentsForCrafting(source);

        long totalFe = 0L;
        long rate = 0L;

        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            int level = Math.max(1, enchantments.getLevel(enchantment));
            int baseValue = Math.max(1, enchantment.value().getMinCost(1));

            totalFe += (long) baseValue * level * level * FE_SCALE;
            rate += (long) baseValue * level * 2L;
        }

        remainingEnergyToGenerate = (int)Math.min(Integer.MAX_VALUE, Math.max(1L, totalFe));
        generationPerTick = (int)Math.min(
                MAX_OUTPUT_PER_TICK,
                Math.max(20L, rate)
        );

        finishedItem = source.is(Items.ENCHANTED_BOOK)
                ? source.transmuteCopy(Items.BOOK, 1)
                : source.copyWithCount(1);

        finishedItem.remove(DataComponents.ENCHANTMENTS);
        finishedItem.remove(DataComponents.STORED_ENCHANTMENTS);

        setChanged();
        return true;
    }

    public ItemStack takeFinishedItem() {
        if (!hasFinishedItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = finishedItem;
        finishedItem = ItemStack.EMPTY;
        setChanged();
        return result;
    }

    public ContainerData createMenuData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> energyStorage.getEnergyStored() / 10;
                    case 1 -> ENERGY_CAPACITY / 10;
                    case 2 -> remainingEnergyToGenerate / 10;
                    case 3 -> generationPerTick;
                    case 4 -> hasFinishedItem() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {}

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            EnchantmentGeneratorBlockEntity blockEntity
    ) {
        if (blockEntity.remainingEnergyToGenerate > 0) {
            int requested = Math.min(
                    blockEntity.generationPerTick,
                    blockEntity.remainingEnergyToGenerate
            );
            int accepted = blockEntity.energyStorage.generate(requested);

            if (accepted > 0) {
                blockEntity.remainingEnergyToGenerate -= accepted;
                if (blockEntity.remainingEnergyToGenerate <= 0) {
                    blockEntity.remainingEnergyToGenerate = 0;
                    blockEntity.generationPerTick = 0;
                }
                blockEntity.setChanged();
            }
        }

        blockEntity.pushEnergy(level, pos);
    }

    private void pushEnergy(Level level, BlockPos pos) {
        int remainingOutput = Math.min(
                MAX_OUTPUT_PER_TICK,
                energyStorage.getEnergyStored()
        );

        for (Direction direction : Direction.values()) {
            if (remainingOutput <= 0) {
                break;
            }

            IEnergyStorage target = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK,
                    pos.relative(direction),
                    direction.getOpposite()
            );

            if (target == null || !target.canReceive()) {
                continue;
            }

            int available = energyStorage.extractEnergy(remainingOutput, true);
            if (available <= 0) {
                break;
            }

            int accepted = target.receiveEnergy(available, false);
            if (accepted > 0) {
                energyStorage.extractEnergy(accepted, false);
                remainingOutput -= accepted;
            }
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains(ENERGY_TAG)) {
            energyStorage.deserializeNBT(registries, tag.get(ENERGY_TAG));
        }

        remainingEnergyToGenerate = Math.max(0, tag.getInt(REMAINING_FE_TAG));
        generationPerTick = Math.max(0, tag.getInt(RATE_TAG));

        finishedItem = tag.contains(OUTPUT_TAG)
                ? ItemStack.parseOptional(registries, tag.getCompound(OUTPUT_TAG))
                : ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put(ENERGY_TAG, energyStorage.serializeNBT(registries));
        tag.putInt(REMAINING_FE_TAG, remainingEnergyToGenerate);
        tag.putInt(RATE_TAG, generationPerTick);

        if (!finishedItem.isEmpty()) {
            tag.put(OUTPUT_TAG, finishedItem.save(registries));
        }
    }

    private final class GeneratorEnergyStorage extends EnergyStorage {
        private GeneratorEnergyStorage(int capacity, int maxExtract) {
            super(capacity, 0, maxExtract);
        }

        private int generate(int amount) {
            int accepted = Math.min(capacity - energy, Math.max(0, amount));
            if (accepted > 0) {
                energy += accepted;
            }
            return accepted;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (extracted > 0 && !simulate) {
                EnchantmentGeneratorBlockEntity.this.setChanged();
            }
            return extracted;
        }
    }
}
