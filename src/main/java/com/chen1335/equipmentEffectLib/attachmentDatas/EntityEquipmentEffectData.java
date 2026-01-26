package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
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
    public record InfoHolder<T extends BaseEffect>(ItemStack itemStack, T effect) {
    }

    public final EnumMap<EquipmentType, Map<EffectType<?>, InfoHolder<?>>> unStackAbleTypeMapEnumMap = Util.make(() -> {
        EnumMap<EquipmentType, Map<EffectType<?>, InfoHolder<?>>> enumMap = new EnumMap<>(EquipmentType.class);
        for (EquipmentType value : EquipmentType.values()) {
            enumMap.put(value, new HashMap<>());
        }
        return enumMap;
    });

    public final EnumMap<EquipmentType, Map<EffectType<?>, List<InfoHolder<?>>>> stackAbleTypeMapEnumMap = Util.make(() -> {
        EnumMap<EquipmentType, Map<EffectType<?>, List<InfoHolder<?>>>> enumMap = new EnumMap<>(EquipmentType.class);
        for (EquipmentType value : EquipmentType.values()) {
            enumMap.put(value, new HashMap<>());
        }
        return enumMap;
    });


    public List<InfoHolder<?>> collectAllCurioEffects() {
        List<InfoHolder<?>> list = new ArrayList<>();
        for (Map<EffectType<?>, InfoHolder<?>> value : unStackAbleTypeMapEnumMap.values()) {
            list.addAll(value.values());
        }
        for (Map<EffectType<?>, List<InfoHolder<?>>> value : stackAbleTypeMapEnumMap.values()) {
            value.forEach((effectType, info) -> {
                list.addAll(info);
            });
        }
        return list;
    }

    public void update(LivingEntity entity, EquipmentType equipmentType) {
        if (equipmentType.getSource() == null) {
            return;
        }
        List<ItemStack> itemStacks = equipmentType.getSource().get(entity);
        Map<EffectType<?>, InfoHolder<?>> unStackAbleEffects = new HashMap<>();
        Map<EffectType<?>, List<InfoHolder<?>>> stackAbleEffects = new HashMap<>();

        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty() && itemStack.has(EEItemDataComponentTypes.ITEM_EFFECT_DATA)) {
                EquipmentEffectAPI.getEffects(itemStack).forEach((effectType, baseEffect) -> {
                    if (effectType.getEquipmentType() == equipmentType) {
                        if (!effectType.isStackable()) {
                            unStackAbleEffects.compute(effectType, (effectTypeHolder1, oldPair) -> {
                                if (oldPair == null || baseEffect.isBetterThan(entity, itemStack, oldPair.effect, oldPair.itemStack)) {
                                    return new InfoHolder<>(itemStack, baseEffect);
                                }
                                return oldPair;
                            });
                        } else {
                            stackAbleEffects.computeIfAbsent(effectType, effectType1 -> new ArrayList<>()).add(new InfoHolder<>(itemStack, baseEffect));
                        }
                    }
                });
            }
        }

        Map<EffectType<?>, InfoHolder<?>> oldUnStackAbleEffects = unStackAbleTypeMapEnumMap.get(equipmentType);

        Map<EffectType<?>, List<InfoHolder<?>>> oldStackAbleEffects = stackAbleTypeMapEnumMap.get(equipmentType);

        unStackAbleEffects.forEach((effectType, pair) -> {
            if (!oldUnStackAbleEffects.containsKey(effectType)) {
                pair.effect.onActive(entity, pair.itemStack);
            } else {
                BaseEffect old = oldUnStackAbleEffects.get(effectType).effect;
                BaseEffect theNew = pair.effect;
                if (old.hashCode() != theNew.hashCode()) {
                    old.onDeActive(entity, pair.itemStack);
                    theNew.onActive(entity, pair.itemStack);
                }
                oldUnStackAbleEffects.remove(effectType);
            }
        });


        for (InfoHolder<?> value : oldUnStackAbleEffects.values()) {
            value.effect.onDeActive(entity, value.itemStack);
        }

        for (List<InfoHolder<?>> value : stackAbleEffects.values()) {
            for (InfoHolder<?> pair : value) {
                pair.effect.onActive(entity, pair.itemStack);
            }
        }

        for (List<InfoHolder<?>> value : oldStackAbleEffects.values()) {
            for (InfoHolder<?> info : value) {
                info.effect.onDeActive(entity, info.itemStack);
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
