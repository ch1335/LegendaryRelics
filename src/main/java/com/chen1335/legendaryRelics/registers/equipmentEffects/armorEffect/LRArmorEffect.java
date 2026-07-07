package com.chen1335.legendaryRelics.registers.equipmentEffects.armorEffect;

import com.chen1335.equipmentEffectLib.API.IArmorEffect;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.registers.equipmentEffects.LRBaseEffect;

public abstract class LRArmorEffect extends LRBaseEffect implements IArmorEffect {
    public LRArmorEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }
}
