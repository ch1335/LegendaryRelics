package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.objects.EEDataComponentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.*;

public class EntityEquipmentEffectData {

    public final EnumMap<EquipmentType, Map<EffectType<?>, Pair<ItemStack, BaseEffect>>> unStackAbleTypeMapEnumMap = Util.make(() -> {
        EnumMap<EquipmentType, Map<EffectType<?>, Pair<ItemStack, BaseEffect>>> enumMap = new EnumMap<>(EquipmentType.class);
        for (EquipmentType value : EquipmentType.values()) {
            enumMap.put(value, new HashMap<>());
        }
        return enumMap;
    });

    public final EnumMap<EquipmentType, Map<EffectType<?>, Pair<ItemStack, List<BaseEffect>>>> stackAbleTypeMapEnumMap = Util.make(() -> {
        EnumMap<EquipmentType, Map<EffectType<?>, Pair<ItemStack, List<BaseEffect>>>> enumMap = new EnumMap<>(EquipmentType.class);
        for (EquipmentType value : EquipmentType.values()) {
            enumMap.put(value, new HashMap<>());
        }
        return enumMap;
    });

    public void update(LivingEntity entity) {
        Map<EffectType<?>, Pair<ItemStack, BaseEffect>> curioEffects = new HashMap<>();
        stackAbleTypeMapEnumMap.values().forEach(Map::clear);
        CuriosApi.getCuriosInventory(entity).ifPresent(iCuriosItemHandler -> {
            for (int i = 0; i < iCuriosItemHandler.getEquippedCurios().getSlots(); i++) {
                ItemStack itemStack = iCuriosItemHandler.getEquippedCurios().getStackInSlot(i);
                if (itemStack.has(EEDataComponentTypes.ITEM_EFFECT_DATA)) {
                    Objects.requireNonNull(itemStack.get(EEDataComponentTypes.ITEM_EFFECT_DATA)).effects().forEach((effectType, effect) -> {
                        if (!effectType.isStackable()) {
                            curioEffects.compute(effectType, (effectTypeHolder1, oldPair) -> {
                                if (oldPair == null || effect.isBetterThan(entity, itemStack, oldPair)) {
                                    return new Pair<>(itemStack, effect);
                                }
                                return oldPair;
                            });
                        } else {
                            stackAbleTypeMapEnumMap.get(EquipmentType.CURIO).computeIfAbsent(effectType, effectType1 -> new Pair<>(itemStack, new ArrayList<>())).getSecond().add(effect);
                        }
                    });
                }
            }
        });

        Map<EffectType<?>, Pair<ItemStack, BaseEffect>> oldCurioEffects = unStackAbleTypeMapEnumMap.get(EquipmentType.CURIO);

        curioEffects.forEach((effectType, pair) -> {
            if (!oldCurioEffects.containsKey(effectType)) {
                pair.getSecond().onActive(entity, pair.getFirst());
            } else {
                BaseEffect old = oldCurioEffects.get(effectType).getSecond();
                BaseEffect theNew = pair.getSecond();
                if (old.hashCode() != theNew.hashCode()) {
                    old.onDeActive(entity, pair.getFirst());
                    theNew.onActive(entity, pair.getFirst());
                }
                oldCurioEffects.remove(effectType);
            }
        });
        for (Pair<ItemStack, BaseEffect> value : oldCurioEffects.values()) {
            value.getSecond().onDeActive(entity, value.getFirst());
        }
        oldCurioEffects.clear();
        oldCurioEffects.putAll(curioEffects);

    }

    public enum EquipmentType {
        CURIO, ARMOR, HAND_HOLD
    }
}
