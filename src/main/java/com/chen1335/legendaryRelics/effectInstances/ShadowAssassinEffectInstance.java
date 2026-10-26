package com.chen1335.legendaryRelics.effectInstances;

import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect.ShadowAssassinArmorSetEffect;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.world.entity.LivingEntity;

/**
 * 蓄势待发：记录距离上次攻击的时间与叠层计时。
 */
public class ShadowAssassinEffectInstance extends EffectInstance {

    public static final int ATTACK_COOLDOWN_TICKS = 60;

    public int ticksSinceAttack = 0;
    public int stackTicks = 0;
    public int lastAttackTick = -1;
    public float attackDamageMultiplier = 0;
    public float attackCritChance = 0;
    public float attackCritDamage = 0;

    public ShadowAssassinEffectInstance(int piece) {
        super(piece);
    }

    @Override
    public void tick(LivingEntity living) {
        ShadowAssassinArmorSetEffect.tickStacks(living, this);
    }

    @Override
    public void onPieceUpdate(LivingEntity living, int piece) {
        if (piece < 2) {
            clearStacks(living);
        }
    }

    @Override
    public void onRemove(LivingEntity living) {
        clearStacks(living);
    }

    private void clearStacks(LivingEntity living) {
        resetAttackTimer();
        lastAttackTick = -1;
        attackDamageMultiplier = 0;
        attackCritChance = 0;
        attackCritDamage = 0;
        SpecialEffectAPI.removeEffect(living, living.getUUID(), LRSpecialMobEffects.LURK.value());
    }

    public void resetAttackTimer() {
        ticksSinceAttack = 0;
        stackTicks = 0;
    }

    public boolean canGainStack() {
        return ticksSinceAttack >= ATTACK_COOLDOWN_TICKS;
    }
}
