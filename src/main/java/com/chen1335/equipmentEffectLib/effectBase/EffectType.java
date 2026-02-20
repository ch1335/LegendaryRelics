package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.util.Cast;

import java.util.Objects;
import java.util.Optional;

public class EffectType<T extends BaseEffect> {
    private int cachedHashCode = 0;

    private final EffectFactory<T> factory;
    private final boolean stackable;

    @Override
    public int hashCode() {
        if (cachedHashCode == 0) {
            cachedHashCode = Objects.requireNonNull(EERegisterTypes.EQUIPMENT_EFFECT_TYPE.getKey(this)).hashCode();
        }
        return cachedHashCode;
    }

    public EffectType(EffectFactory<T> factory, boolean stackable) {
        this.factory = factory;
        this.stackable = stackable;
    }

    public EffectType(EffectFactory<T> factory) {
        this(factory, false);
    }


    public T create(int level, EquipmentType equipmentType) {
        return factory.create(this, level, equipmentType);
    }


    public interface EffectFactory<T extends BaseEffect> {
        T create(EffectType<T> effectType, int level, EquipmentType equipmentType);
    }


    public boolean isStackable() {
        return stackable;
    }

    public <A extends BaseEffect> Optional<EntityEquipmentEffectData.InfoHolder<A>> findBestEffect(LivingEntity living) {
        return Cast.cast(EquipmentEffectAPI.findBestEffect(living, this));
    }


    public boolean isNotInCooldown(LivingEntity living) {
        return EquipmentEffectCooldownManager.isNotInCooldown(living, this);
    }

}
