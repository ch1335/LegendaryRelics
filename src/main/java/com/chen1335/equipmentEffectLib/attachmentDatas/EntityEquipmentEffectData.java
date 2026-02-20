package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.ITickAbleEffect;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Cast;

import java.util.*;

public class EntityEquipmentEffectData {


    public static class InfoHolder<T extends BaseEffect> {
        private ItemStack itemStack;
        private T effect;

        public InfoHolder(ItemStack itemStack, T effect) {
            this.itemStack = itemStack;
            this.effect = effect;
        }

        public T effect() {
            return effect;
        }

        public ItemStack itemStack() {
            return itemStack;
        }

        private void setEffect(T effect) {
            this.effect = effect;
        }

        private void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

    private final Map<EffectType<?>, List<InfoHolder<?>>> effects = new HashMap<>();

    public List<InfoHolder<?>> collectAllEffects() {
        List<InfoHolder<?>> list = new ArrayList<>();
        for (List<InfoHolder<?>> entry1 : effects.values()) {
            list.addAll(entry1);
        }
        return ImmutableList.copyOf(list);
    }

    public <T extends BaseEffect> List<InfoHolder<T>> getEffectsByType(EffectType<T> effectType) {
        return Cast.cast(effects.computeIfAbsent(effectType, effectType1 -> new ArrayList<>()));
    }

    public void tick(LivingEntity living) {
        for (List<EntityEquipmentEffectData.InfoHolder<?>> value : effects.values()) {
            for (EntityEquipmentEffectData.InfoHolder<?> info : value) {
                if (info.effect() instanceof ITickAbleEffect tickAbleEffect) {
                    tickAbleEffect.effectTick(info.itemStack(), living);
                }
            }
        }
    }

    //更新所有效果
    public void update(LivingEntity entity, ItemStack from, ItemStack to, EquipmentType matchType) {
        Map<EffectType<?>, BaseEffect> fromEffects = new HashMap<>();
        EquipmentEffectAPI.getEffects(from).forEach((effectType, baseEffect) -> {
            if (baseEffect.getEquipmentType().match(matchType)) {
                fromEffects.put(effectType, baseEffect);
            }
        });

        Map<EffectType<?>, BaseEffect> toEffects = new HashMap<>();
        EquipmentEffectAPI.getEffects(to).forEach((effectType, baseEffect) -> {
            if (baseEffect.getEquipmentType().match(matchType)) {
                toEffects.put(effectType, baseEffect);
            }
        });

        fromEffects.forEach((effectType, baseEffect) -> {
            if (effectType.isStackable()) {
                getEffectsByType(effectType).removeIf(holder -> {
                    if (holder.effect().activeId == baseEffect.activeId) {
                        baseEffect.onDeActive(entity, from);
                        holder.effect.activeId = null;
                        return true;
                    }
                    return false;
                });
            } else {
                List<InfoHolder<?>> orDefault = Cast.cast(getEffectsByType(effectType));
                if (!orDefault.isEmpty()) {
                    InfoHolder<?> first = orDefault.getFirst();
                    if (first.effect.activeId == baseEffect.activeId) {
                        baseEffect.onDeActive(entity, from);
                        first.effect.activeId = null;
                        orDefault.removeFirst();
                    }
                }
            }
        });

        toEffects.forEach((effectType, baseEffect) -> {
            if (effectType.isStackable()) {
                baseEffect.onActive(entity, to);
                baseEffect.activeId = UUID.randomUUID();
                getEffectsByType(effectType).add(Cast.cast(new InfoHolder<>(to, baseEffect)));
            } else {
                List<? extends InfoHolder<?>> effectsByType = getEffectsByType(effectType);
                if (effectsByType.isEmpty()) {
                    effectsByType.add(Cast.cast(new InfoHolder<>(to, baseEffect)));
                } else {
                    InfoHolder<?> first = effectsByType.getFirst();
                    if (baseEffect.isBetterThan(entity, to, first.effect(), first.itemStack)) {
                        effectsByType.set(0, Cast.cast(new InfoHolder<>(to, baseEffect)));
                        baseEffect.onActive(entity, to);
                        baseEffect.activeId = UUID.randomUUID();
                    }
                }
            }
        });
    }

}
