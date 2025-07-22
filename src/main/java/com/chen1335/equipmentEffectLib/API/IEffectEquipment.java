package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;

import java.util.List;

public interface IEffectEquipment {
    default List<BaseEffect> EE$getDefaultEffects() {
        return List.of();
    }
}
