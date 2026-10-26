package com.howierux.mortalexpansion.block;

import com.howierux.mortalexpansion.haunt.HauntManager;
import com.howierux.mortalexpansion.haunt.HauntTier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

/** The soil itself stores its provenance; losing farmland ends the bonus without a separate position cache. */
public final class HauntedFarmlandBlock extends FarmBlock {
    public HauntedFarmlandBlock(Properties properties){super(properties);}
    public static int growthDenominator(HauntTier tier){return switch(tier.id()){
        case 1 -> 16;case 2 -> 8;case 3 -> 4;case 4 -> 3;case 5 -> 2;case 6 -> 1;default -> 0;};}
    public static boolean keepsMoist(HauntTier tier){return tier.id()>HauntTier.SUPERNATURAL.id();}
    @Override public void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
        var tier=HauntManager.getTier(level,pos);
        if(keepsMoist(tier)){
            if(!state.canSurvive(level,pos)){FarmBlock.turnToDirt(null,state,level,pos);return;}
            if(state.getValue(MOISTURE)!=7)level.setBlock(pos,state.setValue(MOISTURE,7),2);
        }else super.randomTick(state,level,pos,random);
        if(!level.getBlockState(pos).is(this))return;
        int denominator=growthDenominator(tier);
        if(denominator>0 && random.nextInt(denominator)==0)applyBonemeal(level,pos);
    }
    public static boolean applyBonemeal(ServerLevel level,BlockPos soil){
        if(!(level.getBlockState(soil).getBlock() instanceof HauntedFarmlandBlock))return false;
        var crop=soil.above();if(!com.howierux.mortalexpansion.equipment.EquipmentRuntime.loaded(level,crop,2))return false;
        if(!level.getBlockState(crop).is(net.minecraft.tags.BlockTags.CROPS))return false;
        // Delegate to the native bonemeal helper, including framework cancellation/modded-crop hooks.
        boolean applied=net.minecraft.world.item.BoneMealItem.growCrop(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BONE_MEAL),level,crop);
        if(applied)level.levelEvent(1505,crop,15);
        return applied;
    }
}
