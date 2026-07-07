package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import net.minecraft.world.entity.LivingEntity;
import com.chen1335.equipmentEffectLib.utils.Cast;

import java.util.Optional;

public class EffectType<T extends BaseEffect> {

    private final EffectFactory<T> factory;
    private final boolean stackable;

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

    public Optional<InfoHolder<T>> findBestEffect(LivingEntity living) {
        return Cast.cast(EquipmentEffectAPI.findBestEffect(living, this));
    }


    public boolean isNotInCooldown(LivingEntity living) {
        return EquipmentEffectCooldownManager.isNotInCooldown(living, this);
    }

}
