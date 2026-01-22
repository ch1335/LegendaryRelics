package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentSources.ArmorSource;
import com.chen1335.equipmentEffectLib.equipmentSources.CuriosSource;
import com.chen1335.equipmentEffectLib.equipmentSources.MainHand;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

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

    public void update(LivingEntity entity, EquipmentType equipmentType) {
        if (equipmentType.getSource() == null) {
            return;
        }
        List<ItemStack> itemStacks = equipmentType.getSource().get(entity);
        Map<EffectType<?>, Pair<ItemStack, BaseEffect>> unStackAbleEffects = new HashMap<>();
        Map<EffectType<?>, List<Pair<ItemStack, BaseEffect>>> stackAbleEffects = new HashMap<>();

        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty() && itemStack.has(EEItemDataComponentTypes.ITEM_EFFECT_DATA)) {
                itemStack.getOrDefault(EEItemDataComponentTypes.ITEM_EFFECT_DATA, ItemEffectsData.EMPTY).effects().forEach((effectType, baseEffect) -> {
                    if (effectType.getEquipmentType() == equipmentType) {
                        if (!effectType.isStackable()) {
                            unStackAbleEffects.compute(effectType, (effectTypeHolder1, oldPair) -> {
                                if (oldPair == null || baseEffect.isBetterThan(entity, itemStack, oldPair.getSecond(), oldPair.getFirst())) {
                                    return new Pair<>(itemStack, baseEffect);
                                }
                                return oldPair;
                            });
                        } else {
                            stackAbleEffects.computeIfAbsent(effectType, effectType1 -> new ArrayList<>()).add(Pair.of(itemStack, baseEffect));
                        }
                    }
                });
            }
        }

        Map<EffectType<?>, Pair<ItemStack, BaseEffect>> oldUnStackAbleEffects = unStackAbleTypeMapEnumMap.get(equipmentType);

        Map<EffectType<?>, List<Pair<ItemStack, BaseEffect>>> oldStackAbleEffects = stackAbleTypeMapEnumMap.get(equipmentType);

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

    public interface IEquipmentType {
        IEquipmentSource getSource();
    }

    public enum EquipmentType implements IEquipmentType {
        CURIO(CuriosSource.INSTANCE),
        ARMOR(ArmorSource.INSTANCE),
        MAIN_HIND(MainHand.INSTANCE);

        private final IEquipmentSource source;

        EquipmentType(IEquipmentSource source) {
            this.source = source;
        }

        @Override
        public IEquipmentSource getSource() {
            return source;
        }
    }
}
