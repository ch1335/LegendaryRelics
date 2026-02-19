package com.chen1335.equipmentEffectLib.common;

import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;

public record SetEffectHolder(SetEffect setEffect, EquipmentType equipmentType) {
    public static SetEffectHolder of(SetEffect setEffect, EquipmentType equipmentType) {
        return new SetEffectHolder(setEffect, equipmentType);
    }
}
