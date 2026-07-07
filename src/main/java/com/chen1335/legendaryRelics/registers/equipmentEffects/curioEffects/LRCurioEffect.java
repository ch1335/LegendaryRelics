package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.ICurioEffect;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.registers.equipmentEffects.LRBaseEffect;

public abstract class LRCurioEffect extends LRBaseEffect implements ICurioEffect {


    public LRCurioEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }
}
