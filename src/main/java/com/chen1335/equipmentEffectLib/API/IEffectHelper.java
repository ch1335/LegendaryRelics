package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import net.minecraft.world.entity.LivingEntity;
import com.chen1335.equipmentEffectLib.utils.Cast;

import java.util.Optional;

public interface IEffectHelper {
    default <T extends BaseEffect> Optional<InfoHolder<T>> findBestEffect(LivingEntity living) {
        return EquipmentEffectAPI.findBestEffect(living, Cast.cast(((BaseEffect) this).getType()));
    }

    default boolean isNotInCooldown(LivingEntity living) {
        return EquipmentEffectCooldownManager.isNotInCooldown(living, ((BaseEffect) this).getType());
    }

    default void addCooldown(LivingEntity living, int time, Runnable runnable) {
        EquipmentEffectCooldownManager.addCooldown(living, ((BaseEffect) this).getType(), time, runnable);
    }

    default void addCooldown(LivingEntity living, int time) {
        EquipmentEffectCooldownManager.addCooldown(living, ((BaseEffect) this).getType(), time);
    }

}
