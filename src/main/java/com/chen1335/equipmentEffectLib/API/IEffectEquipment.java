package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;

import java.util.Map;

public interface IEffectEquipment {
    default Map<EffectType<?>, BaseEffect> getDefaultEffect() {
        return Map.of();
    }
}
