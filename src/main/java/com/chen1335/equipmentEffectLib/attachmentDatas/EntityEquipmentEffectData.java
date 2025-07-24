package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.objects.EEDataComponentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.CurioEffect;
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

    public final EnumMap<EquipmentType, Map<EffectType<?>, List<Pair<ItemStack, BaseEffect>>>> stackAbleTypeMapEnumMap = Util.make(() -> {
        EnumMap<EquipmentType, Map<EffectType<?>, List<Pair<ItemStack, BaseEffect>>>> enumMap = new EnumMap<>(EquipmentType.class);
        for (EquipmentType value : EquipmentType.values()) {
            enumMap.put(value, new HashMap<>());
        }
        return enumMap;
    });


    public List<Pair<ItemStack, BaseEffect>> collectAllCurioEffects() {
        List<Pair<ItemStack, BaseEffect>> list = new ArrayList<>();
        for (Map<EffectType<?>, Pair<ItemStack, BaseEffect>> value : unStackAbleTypeMapEnumMap.values()) {
            list.addAll(value.values());
        }
        for (Map<EffectType<?>, List<Pair<ItemStack, BaseEffect>>> value : stackAbleTypeMapEnumMap.values()) {
            value.forEach((effectType, pairList) -> {
                list.addAll(pairList);
            });
        }
        return list;
    }

    public void updateCurio(LivingEntity entity) {
        Map<EffectType<?>, Pair<ItemStack, BaseEffect>> unStackAbleEffects = new HashMap<>();
        Map<EffectType<?>, List<Pair<ItemStack, BaseEffect>>> stackAbleEffects = new HashMap<>();
        CuriosApi.getCuriosInventory(entity).ifPresent(iCuriosItemHandler -> {
            for (int i = 0; i < iCuriosItemHandler.getEquippedCurios().getSlots(); i++) {
                ItemStack itemStack = iCuriosItemHandler.getEquippedCurios().getStackInSlot(i);
                if (itemStack.has(EEDataComponentTypes.ITEM_EFFECT_DATA)) {
                    Objects.requireNonNull(itemStack.get(EEDataComponentTypes.ITEM_EFFECT_DATA)).effects().forEach((effectType, effect) -> {
                        if (effectType.getEquipmentType() == EquipmentType.CURIO) {
                            if (effect instanceof CurioEffect curioEffect) {
                                if (!effectType.isStackable()) {
                                    unStackAbleEffects.compute(effectType, (effectTypeHolder1, oldPair) -> {
                                        if (oldPair == null || curioEffect.isBetterThan(entity, itemStack, oldPair.getSecond(), oldPair.getFirst())) {
                                            return new Pair<>(itemStack, curioEffect);
                                        }
                                        return oldPair;
                                    });
                                } else {
                                    stackAbleEffects.computeIfAbsent(effectType, effectType1 -> new ArrayList<>()).add(Pair.of(itemStack, effect));
                                }
                            }
                        }
                    });
                }
            }
        });


        Map<EffectType<?>, Pair<ItemStack, BaseEffect>> oldUnStackAbleEffects = unStackAbleTypeMapEnumMap.get(EquipmentType.CURIO);

        Map<EffectType<?>, List<Pair<ItemStack, BaseEffect>>> oldStackAbleEffects = stackAbleTypeMapEnumMap.get(EquipmentType.CURIO);

        unStackAbleEffects.forEach((effectType, pair) -> {
            if (!oldUnStackAbleEffects.containsKey(effectType)) {
                pair.getSecond().onActive(entity, pair.getFirst());
            } else {
                BaseEffect old = oldUnStackAbleEffects.get(effectType).getSecond();
                BaseEffect theNew = pair.getSecond();
                if (old.hashCode() != theNew.hashCode()) {
                    old.onDeActive(entity, pair.getFirst());
                    theNew.onActive(entity, pair.getFirst());
                }
                oldUnStackAbleEffects.remove(effectType);
            }
        });


        for (Pair<ItemStack, BaseEffect> value : oldUnStackAbleEffects.values()) {
            value.getSecond().onDeActive(entity, value.getFirst());
        }

        for (List<Pair<ItemStack, BaseEffect>> value : stackAbleEffects.values()) {
            for (Pair<ItemStack, BaseEffect> pair : value) {
                pair.getSecond().onActive(entity, pair.getFirst());
            }
        }

        for (List<Pair<ItemStack, BaseEffect>> value : oldStackAbleEffects.values()) {
            for (Pair<ItemStack, BaseEffect> pair : value) {
                pair.getSecond().onDeActive(entity, pair.getFirst());
            }
        }

        oldUnStackAbleEffects.clear();
        oldUnStackAbleEffects.putAll(unStackAbleEffects);

        oldStackAbleEffects.clear();
        oldStackAbleEffects.putAll(stackAbleEffects);
    }

    public void updateArmorEffect(LivingEntity entity) {
        Map<EffectType<?>, Pair<ItemStack, BaseEffect>> unStackAbleEffects = new HashMap<>();
        Map<EffectType<?>, List<Pair<ItemStack, BaseEffect>>> stackAbleEffects = new HashMap<>();


        for (ItemStack armorItemStack : entity.getArmorSlots()) {
            if (!armorItemStack.isEmpty()) {
                if (armorItemStack.has(EEDataComponentTypes.ITEM_EFFECT_DATA)) {
                    Map<EffectType<?>, BaseEffect> effects = Objects.requireNonNull(armorItemStack.get(EEDataComponentTypes.ITEM_EFFECT_DATA)).effects();
                    effects.forEach((effectType, baseEffect) -> {
                        if (effectType.getEquipmentType() == EquipmentType.ARMOR) {
                            if (!effectType.isStackable()) {
                                Pair<ItemStack, BaseEffect> oldPair = unStackAbleEffects.get(effectType);
                                if (oldPair == null) {
                                    unStackAbleEffects.put(effectType, Pair.of(armorItemStack, baseEffect));
                                } else {
                                    if (baseEffect.isBetterThan(entity, armorItemStack, oldPair.getSecond(), oldPair.getFirst())) {
                                        unStackAbleEffects.put(effectType, Pair.of(armorItemStack, baseEffect));
                                    }
                                }
                            } else {
                                stackAbleEffects.computeIfAbsent(effectType, effectType1 -> new ArrayList<>()).add(Pair.of(armorItemStack, baseEffect));
                            }
                        }
                    });
                }
            }
        }


        Map<EffectType<?>, Pair<ItemStack, BaseEffect>> oldUnStackAbleEffects = unStackAbleTypeMapEnumMap.get(EquipmentType.ARMOR);

        Map<EffectType<?>, List<Pair<ItemStack, BaseEffect>>> oldStackAbleEffects = stackAbleTypeMapEnumMap.get(EquipmentType.ARMOR);

        unStackAbleEffects.forEach((effectType, pair) -> {
            if (!oldUnStackAbleEffects.containsKey(effectType)) {
                pair.getSecond().onActive(entity, pair.getFirst());
            } else {
                BaseEffect old = oldUnStackAbleEffects.get(effectType).getSecond();
                BaseEffect theNew = pair.getSecond();
                if (old.hashCode() != theNew.hashCode()) {
                    old.onDeActive(entity, pair.getFirst());
                    theNew.onActive(entity, pair.getFirst());
                }
                oldUnStackAbleEffects.remove(effectType);
            }
        });


        for (Pair<ItemStack, BaseEffect> value : oldUnStackAbleEffects.values()) {
            value.getSecond().onDeActive(entity, value.getFirst());
        }

        for (List<Pair<ItemStack, BaseEffect>> value : stackAbleEffects.values()) {
            for (Pair<ItemStack, BaseEffect> pair : value) {
                pair.getSecond().onActive(entity, pair.getFirst());
            }
        }

        for (List<Pair<ItemStack, BaseEffect>> value : oldStackAbleEffects.values()) {
            for (Pair<ItemStack, BaseEffect> pair : value) {
                pair.getSecond().onDeActive(entity, pair.getFirst());
            }
        }

        oldUnStackAbleEffects.clear();
        oldUnStackAbleEffects.putAll(unStackAbleEffects);

        oldStackAbleEffects.clear();
        oldStackAbleEffects.putAll(stackAbleEffects);
    }


    public enum EquipmentType {
        CURIO, ARMOR, WEAPON
    }
}
