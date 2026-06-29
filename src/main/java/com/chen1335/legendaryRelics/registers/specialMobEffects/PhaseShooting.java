package com.chen1335.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.registers.specialMobEffects.common.StackAbleEffect;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;

public class PhaseShooting extends StackAbleEffect {
    private double drawSpeedPerStack;
    private double arrowDamagePerStack;
    public PhaseShooting(MobEffectType<?> effectType) {
        super(effectType);
    }
}
