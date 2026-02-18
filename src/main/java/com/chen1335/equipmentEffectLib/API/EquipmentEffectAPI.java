package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemExtension;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Cast;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class EquipmentEffectAPI {
    public static <T extends BaseEffect> Optional<EntityEquipmentEffectData.InfoHolder<T>> findBestEffect(LivingEntity living, EffectType<T> effectType) {
        @Nullable List<EntityEquipmentEffectData.InfoHolder<T>> list = living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).getEffectsByType(effectType);
        return list == null ? Optional.empty() : Optional.of(list.getFirst());
    }

    public static <T extends BaseEffect> Optional<List<EntityEquipmentEffectData.InfoHolder<T>>> findStackableEffect(LivingEntity living, EffectType<T> effectType) {
        @Nullable List<EntityEquipmentEffectData.InfoHolder<T>> list = living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).getEffectsByType(effectType);
        return list == null ? Optional.empty() : Optional.of(list);
    }


    public static boolean haveEffects(ItemStack itemStack) {
        return itemStack.has(EEItemDataComponentTypes.ITEM_EFFECT_DATA) || itemStack.has(EEItemDataComponentTypes.ITEM_EFFECT_DATA_ADDITION);
    }

    public static <T extends BaseEffect> Optional<T> findItemEffect(ItemStack itemStack, EffectType<T> effectType) {
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }
        Map<EffectType<?>, BaseEffect> effects = getEffects(itemStack);
        BaseEffect baseEffect = effects.get(effectType);
        if (baseEffect == null) {
            return Optional.empty();
        }

        return Optional.of(Cast.cast(baseEffect));
    }

    public static Map<EffectType<?>, BaseEffect> getEffects(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Map.of();
        }
        Map<EffectType<?>, BaseEffect> map = new LinkedHashMap<>();

        @Nullable ItemEffectsData effectsData1 = itemStack.get(EEItemDataComponentTypes.ITEM_EFFECT_DATA);
        if (effectsData1 != null) {
            map.putAll(effectsData1.effects());
        }
        @Nullable ItemEffectsData effectsData2 = itemStack.get(EEItemDataComponentTypes.ITEM_EFFECT_DATA_ADDITION);
        if (effectsData2 != null) {
            map.putAll(effectsData2.effects());
        }
        Map<EffectType<?>, BaseEffect> subEffects = new LinkedHashMap<>();
        for (BaseEffect value : map.values()) {
            if (value instanceof ISubEffectProvider subEffectProvider) {
                for (BaseEffect subEffect : subEffectProvider.getSubEffects(itemStack)) {
                    subEffects.put(subEffect.getType(), subEffect);
                }
            }
        }
        map.putAll(subEffects);
        return ImmutableMap.copyOf(map);
    }

    @Nullable
    public static <T extends BaseEffect> T getEffect(ItemStack itemStack, EffectType<T> type) {
        return Cast.cast(getEffects(itemStack).get(type));
    }

    @Nullable
    public static SetEffect getItemSetEffect(ItemStack itemStack) {
        return ((IEEItemExtension) itemStack.getItem()).EE$GetSetsEffect();
    }


    public static void updateEntitySetEffect(@NotNull LivingEntity living) {
        living.getData(EEAttachmentTypes.ENTITY_SETS_EFFECT_DATA.get()).update(living);
    }

    public static void updateEntityEquipmentEffect(@NotNull LivingEntity living, EntityEquipmentEffectData.IEquipmentType equipmentType) {
        living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).update(living, equipmentType);
    }

    public static void updateEntityEquipmentEffectSameItem(@NotNull LivingEntity living, EntityEquipmentEffectData.IEquipmentType equipmentType, ItemStack from, ItemStack to) {
        living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).updateSameItem(equipmentType, from, to);
    }
}
