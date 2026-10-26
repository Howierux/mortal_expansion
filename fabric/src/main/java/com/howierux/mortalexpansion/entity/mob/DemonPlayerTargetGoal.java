package com.howierux.mortalexpansion.entity.mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
/** Native target search with immediate loss of tracking behind spectral-solid screens. */
public final class DemonPlayerTargetGoal extends NearestAttackableTargetGoal<Player> {
    private final DemonEntity demon;
    public DemonPlayerTargetGoal(DemonEntity demon){
        super(demon,Player.class,10,true,false,target->!demon.isAllied());this.demon=demon;
    }
    @Override public boolean canContinueToUse(){
        return demon.getTarget() instanceof Player player && !player.isCreative() && !player.isSpectator()
                && !demon.isAllied() && demon.hasLineOfSight(player) && super.canContinueToUse();
    }
}
