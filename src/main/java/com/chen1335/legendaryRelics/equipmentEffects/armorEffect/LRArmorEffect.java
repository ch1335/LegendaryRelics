package com.chen1335.legendaryRelics.equipmentEffects.armorEffect;

import com.chen1335.equipmentEffectLib.API.IArmorEffect;
import com.chen1335.equipmentEffectLib.effectBase.ArmorEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;

public class LRArmorEffect extends ArmorEffect implements IArmorEffect {
    public LRArmorEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }
}
