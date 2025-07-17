package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;

import java.util.List;
import java.util.Map;

public interface IEffectEquipment {
    default List<BaseEffect> getDefaultEffect() {
        return List.of();
    }
}
