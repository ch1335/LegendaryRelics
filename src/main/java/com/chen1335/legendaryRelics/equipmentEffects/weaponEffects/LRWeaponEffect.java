package com.chen1335.legendaryRelics.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.API.IMainHandEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.equipmentEffects.LRBaseEffect;

public abstract class LRWeaponEffect extends LRBaseEffect implements IMainHandEffect {
    public LRWeaponEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }
}
