package com.chen1335.equipmentEffectLib.API;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;
import com.chen1335.equipmentEffectLib.common.SlotEffectHolder;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.dataComponentTypes.SetEffectData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.equipmentEffectLib.utils.Cast;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class EquipmentEffectAPI {
    public static <T extends BaseEffect> Optional<SlotEffectHolder<T>> findBestEffect(LivingEntity living, EffectType<T> effectType) {
        List<SlotEffectHolder<T>> list = living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).getEffectsByType(effectType);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public static <T extends BaseEffect> Optional<List<SlotEffectHolder<T>>> findStackableEffect(LivingEntity living, EffectType<T> effectType) {
        List<SlotEffectHolder<T>> list = living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).getEffectsByType(effectType);
        return list.isEmpty() ? Optional.empty() : Optional.of(list);
    }


    public static boolean haveEffects(ItemStack itemStack) {
        return itemStack.has(EEItemDataComponentTypes.ITEM_EFFECT) || itemStack.has(EEItemDataComponentTypes.ITEM_EFFECT_ADDITION);
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
        return getEffects(itemStack, EquipmentTypes.ALL);
    }

    public static Map<EffectType<?>, BaseEffect> getEffects(ItemStack itemStack, IEquipmentType equipmentType) {
        if (itemStack.isEmpty()) {
            return Map.of();
        }
        Map<EffectType<?>, BaseEffect> map = new LinkedHashMap<>();

        @Nullable ItemEffectsData effectsData1 = itemStack.get(EEItemDataComponentTypes.ITEM_EFFECT);
        if (effectsData1 != null) {
            map.putAll(effectsData1.effects());
        }
        @Nullable ItemEffectsData effectsData2 = itemStack.get(EEItemDataComponentTypes.ITEM_EFFECT_ADDITION);
        if (effectsData2 != null) {
            map.putAll(effectsData2.effects());
        }
        Map<EffectType<?>, BaseEffect> subEffects = new LinkedHashMap<>();
        for (BaseEffect value : map.values()) {
            if (value instanceof ISubEffectProvider<?> subEffectProvider) {
                for (BaseEffect subEffect : subEffectProvider.getSubEffects(itemStack)) {
                    subEffects.put(subEffect.getType(), subEffect);
                }
            }
        }
        map.putAll(subEffects);
        map.values().removeIf(baseEffect -> !equipmentType.contain(baseEffect.getEquipmentType()));
        return ImmutableMap.copyOf(map);
    }

    @Nullable
    public static <T extends BaseEffect> T getEffect(ItemStack itemStack, EffectType<T> type) {
        return Cast.cast(getEffects(itemStack).get(type));
    }

    @Nullable
    public static SetEffectData getItemSetEffect(ItemStack itemStack) {
        return itemStack.get(EEItemDataComponentTypes.SET_EFFECT);
    }


    public static void updateEntitySetEffect(@NotNull LivingEntity living, ISlotContext slotContext, ItemStack from, ItemStack eventTo) {
        living.getData(EEAttachmentTypes.ENTITY_SETS_EFFECT_DATA.get()).update(living,slotContext, from,  eventTo);
    }

}
