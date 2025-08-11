package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;

import java.util.Objects;

public class EffectType<T extends BaseEffect> {
    private int cachedHashCode = 0;

    private final EffectFactory<T> factory;
    private final EntityEquipmentEffectData.EquipmentType equipmentType;
    private final boolean stackable;

    @Override
    public int hashCode() {
        if (cachedHashCode == 0) {
            cachedHashCode = Objects.requireNonNull(RegisterTypes.EQUIPMENT_EFFECT_TYPE.getKey(this)).hashCode();
        }
        return cachedHashCode;
    }

    public EffectType(EffectFactory<T> factory, EntityEquipmentEffectData.EquipmentType equipmentType, boolean stackable) {
        this.factory = factory;
        this.equipmentType = equipmentType;
        this.stackable = stackable;
    }

    public EffectType(EffectFactory<T> factory, EntityEquipmentEffectData.EquipmentType equipmentType) {
        this(factory, equipmentType, false);
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

    public boolean isStackable() {
        return stackable;
    }
}
