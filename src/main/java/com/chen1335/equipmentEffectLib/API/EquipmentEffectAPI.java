package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class EquipmentEffectAPI {
    public static <T extends BaseEffect> Optional<Pair<ItemStack, T>> findBestEffect(LivingEntity living, EffectType<T> effectType) {
        return Optional.ofNullable((Pair<ItemStack, T>) living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).typeMapEnumMap.get(effectType.getEquipmentType()).get(effectType));
    }
}
