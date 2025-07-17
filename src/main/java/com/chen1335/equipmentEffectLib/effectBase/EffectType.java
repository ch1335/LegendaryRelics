package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;

public class EffectType<T extends BaseEffect> {


    private final EffectFactory<T> factory;
    private final EntityEquipmentEffectData.EquipmentType equipmentType;

    public EffectType(EffectFactory<T> factory, EntityEquipmentEffectData.EquipmentType equipmentType) {
        this.factory = factory;
        this.equipmentType = equipmentType;
    }

    public T create(int level) {
        return factory.create(this, level);
    }

    public EntityEquipmentEffectData.EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public interface EffectFactory<T extends BaseEffect> {
        T create(EffectType<T> effectType, int level);
    }
}
