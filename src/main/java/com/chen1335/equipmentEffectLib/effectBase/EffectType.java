package com.chen1335.equipmentEffectLib.effectBase;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.ITypeWithEntityData;
import com.chen1335.equipmentEffectLib.common.SlotEffectHolder;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.function.Supplier;

public class EffectType<T extends BaseEffect> {

    private final EffectFactory<T> factory;
    private Supplier<ITypeWithEntityData<?>> dataFactory = null;
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

    public Optional<SlotEffectHolder<T>> findBestEffect(LivingEntity living) {
        return EquipmentEffectAPI.findBestEffect(living, this);
    }


    public boolean isNotInCooldown(LivingEntity living) {
        return EquipmentEffectCooldownManager.isNotInCooldown(living, this);
    }

}
