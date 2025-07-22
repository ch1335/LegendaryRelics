package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEDataComponentTypes;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class EquipmentEffectAPI {
    public static <T extends BaseEffect> Optional<Pair<ItemStack, T>> findBestEffect(LivingEntity living, EffectType<T> effectType) {
        return Optional.ofNullable((Pair<ItemStack, T>) living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).unStackAbleTypeMapEnumMap.get(effectType.getEquipmentType()).get(effectType));
    }

    public static <T extends BaseEffect> Optional<Pair<ItemStack, List<T>>> findStackableEffect(LivingEntity living, EffectType<T> effectType) {
        return Optional.ofNullable((Pair<ItemStack, List<T>>) (Object) living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).stackAbleTypeMapEnumMap.get(effectType.getEquipmentType()).get(effectType));
    }

    public static <T extends BaseEffect> Optional<T> findItemEffect(ItemStack itemStack, EffectType<T> effectType) {
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }
        @Nullable ItemEffectsData effectsData = itemStack.get(EEDataComponentTypes.ITEM_EFFECT_DATA);
        if (effectsData == null) {
            return Optional.empty();
        }
        BaseEffect baseEffect = effectsData.effects().get(effectType);
        if (baseEffect == null) {
            return Optional.empty();
        }

        return (Optional<T>) Optional.of(baseEffect);
    }

    public static <T extends BaseEffect> Optional<Map<EffectType<?>, BaseEffect>> findItemEffects(ItemStack itemStack, EffectType<T> effectType) {
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }
        @Nullable ItemEffectsData effectsData = itemStack.get(EEDataComponentTypes.ITEM_EFFECT_DATA);
        if (effectsData == null) {
            return Optional.empty();
        }else {
            return Optional.ofNullable(effectsData.effects());
        }
    }
}
