package com.chen1335.equipmentEffectLib.equipmentType;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;

public class EquipmentType implements IEquipmentType {

    private final IEquipmentSource source;

    public EquipmentType(IEquipmentSource source) {
        this.source = source;
    }

    @Override
    public IEquipmentSource source() {
        return source;
    }

    @Override
    public boolean match(IEquipmentType equipmentType) {
        return equipmentType == this;
    }
}
