package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.ICurioEffect;

public abstract class CurioEffect extends BaseEffect implements ICurioEffect {
    public CurioEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }
}
