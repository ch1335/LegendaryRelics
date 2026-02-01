package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.ICurioEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.equipmentEffects.LRBaseEffect;

public abstract class LRCurioEffectBase extends LRBaseEffect implements ICurioEffect {

    public LRCurioEffectBase(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

}
