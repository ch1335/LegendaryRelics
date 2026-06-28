package com.chen1335.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.registers.specialMobEffects.common.StackAbleEffect;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;

public class Doom extends StackAbleEffect {

    public Doom(MobEffectType<?> effectType) {
        super(effectType);
    }

    public Doom() {
        this(LRSpecialMobEffects.DOOM.value());
    }
}
