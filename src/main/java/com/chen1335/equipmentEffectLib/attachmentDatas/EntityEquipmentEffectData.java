package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.ITickAbleEffect;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Cast;

import java.util.*;

public class EntityEquipmentEffectData {


    public record InfoHolder<T extends BaseEffect>(ItemStack itemStack, T effect) {
        public static final Codec<InfoHolder<? extends BaseEffect>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.CODEC.fieldOf("itemStack").forGetter(InfoHolder::itemStack),
                BaseEffect.CODEC.fieldOf("effect").forGetter(InfoHolder::effect)
        ).apply(instance, InfoHolder::new));
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

        Map<EffectType<?>, BaseEffect> toDeActive = new HashMap<>();
        Map<EffectType<?>, BaseEffect> toActive = new HashMap<>();
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
                        toDeActive.put(effectType, first.effect);
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
                        toActive.put(effectType, baseEffect);
                    }
                }
            }
        });

        toDeActive.forEach((effectType, baseEffect) -> {
            if (!toActive.containsKey(effectType)) {
                baseEffect.onDeActive(entity, from);
                baseEffect.activeId = null;
            } else if (baseEffect.activeId != toActive.get(effectType).activeId) {
                baseEffect.onDeActive(entity, from);
                baseEffect.activeId = null;
            }
        });

        toActive.forEach((effectType, baseEffect) -> {
            if (!toDeActive.containsKey(effectType)) {
                baseEffect.onActive(entity, from);
                baseEffect.activeId = UUID.randomUUID();
            } else if (baseEffect.activeId != toDeActive.get(effectType).activeId) {
                baseEffect.onDeActive(entity, from);
                baseEffect.activeId = UUID.randomUUID();
            }
        });
    }

}
