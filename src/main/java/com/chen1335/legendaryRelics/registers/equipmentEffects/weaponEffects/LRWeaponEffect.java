package com.chen1335.legendaryRelics.registers.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.API.IMainHandEffect;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.registers.equipmentEffects.LRBaseEffect;

public abstract class LRWeaponEffect extends LRBaseEffect implements IMainHandEffect {
    public LRWeaponEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }
}
