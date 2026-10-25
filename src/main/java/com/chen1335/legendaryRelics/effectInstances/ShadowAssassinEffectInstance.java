package com.chen1335.legendaryRelics.effectInstances;

import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect.ShadowAssassinArmorSetEffect;
import net.minecraft.world.entity.LivingEntity;

/**
 * 蓄势待发：记录距离上次攻击的时间与叠层计时。
 */
public class ShadowAssassinEffectInstance extends EffectInstance {

    public static final int ATTACK_COOLDOWN_TICKS = 60;

    public int ticksSinceAttack = 0;
    public int stackTicks = 0;
    public int lastAttackTick = -1;
    public float attackDamageBonus = 0;
    public float attackCritChance = 0;
    public float attackCritDamage = 0;

    public ShadowAssassinEffectInstance(int piece) {
        super(piece);
    }

    @Override
    public void tick(LivingEntity living) {
        ShadowAssassinArmorSetEffect.tickStacks(living, this);
    }

    public void resetAttackTimer() {
        ticksSinceAttack = 0;
        stackTicks = 0;
    }

    public boolean canGainStack() {
        return ticksSinceAttack >= ATTACK_COOLDOWN_TICKS;
    }
}
