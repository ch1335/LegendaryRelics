package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Cast;

import java.util.Optional;

public interface IEffectHelper {
    default <T extends BaseEffect> Optional<Pair<ItemStack, T>> findBestEffect(LivingEntity living) {
        return Cast.cast(EquipmentEffectAPI.findBestEffect(living, getEffectType()));
    }

    default boolean isNotInCooldown(LivingEntity living) {
        return EquipmentEffectCooldownManager.isNotInCooldown(living, getEffectType());
    }

    default void addCooldown(LivingEntity living, int time, Runnable runnable) {
        EquipmentEffectCooldownManager.addCooldown(living, getEffectType(), time, runnable);
    }

    default void addCooldown(LivingEntity living, int time) {
        EquipmentEffectCooldownManager.addCooldown(living, getEffectType(), time);
    }

    EffectType<?> getEffectType();
}
