package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.ITickAbleEffect;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.common.SlotEffectHolder;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.equipmentEffectLib.slotEffectManagers.SlotEffectManager;
import com.chen1335.equipmentEffectLib.slotEffectManagers.curio.CurioSlotEffectManager;
import com.chen1335.equipmentEffectLib.slotEffectManagers.equipment.EquipmentSlotEffectManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.TreeMultimap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.Cast;

import java.util.*;

public class EntityEquipmentEffectData {


    private final Map<EffectType<?>, List<InfoHolder<?>>> effects = new HashMap<>();

    private final Map<Class<? extends SlotEffectManager>, SlotEffectManager> managerMap = new HashMap<>();


    private final Map<EffectType<?>, Optional<InfoHolder<?>>> oldBestEffects = new HashMap<>();

    private final Map<EffectType<?>, TreeMultimap<Integer, SlotEffectHolder<?>>> effectHolders = new HashMap<>();

    public TreeMultimap<Integer, SlotEffectHolder<?>> getEffectHoldersByType(EffectType<?> type) {
        return effectHolders.computeIfAbsent(type, t -> TreeMultimap.create());
    }

    public Map<EffectType<?>, TreeMultimap<Integer, SlotEffectHolder<?>>> getEffectHolders() {
        return effectHolders;
    }

    public <T extends SlotEffectManager> T getSlotEffectManager(Class<T> clazz) {
        return (T) managerMap.get(clazz);
    }

    public Map<EffectType<?>, BaseEffect> findEffectsBySlot(ISlotContext slotContext) {
        SlotEffectManager slotEffectManager = managerMap.get(slotContext.getManagerClass());
        if (slotEffectManager != null) {
            return slotEffectManager.getEffectsBySlot(slotContext);
        }
        return Map.of();
    }

    public <T extends BaseEffect> Optional<InfoHolder<T>> getBestEffect(EffectType<T> type) {
        TreeMultimap<Integer, SlotEffectHolder<T>> effectHoldersByType = Cast.cast(getEffectHoldersByType(type));
        if (effectHoldersByType.isEmpty()) {
            return Optional.empty();
        } else {
            Integer lastKey = effectHoldersByType.keySet().last();
            NavigableSet<SlotEffectHolder<T>> slotEffectHolders = effectHoldersByType.get(lastKey);
            if (slotEffectHolders.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(slotEffectHolders.getLast().infoHolder());
            }
        }
    }

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
        for (List<InfoHolder<?>> value : effects.values()) {
            for (InfoHolder<?> info : value) {
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
                        holder.effect().activeId = null;
                        return true;
                    }
                    return false;
                });
            } else {
                List<InfoHolder<?>> orDefault = Cast.cast(getEffectsByType(effectType));
                if (!orDefault.isEmpty()) {
                    InfoHolder<?> first = orDefault.getFirst();
                    if (first.effect().activeId == baseEffect.activeId) {
                        toDeActive.put(effectType, first.effect());
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
                    toActive.put(effectType, baseEffect);
                } else {
                    InfoHolder<?> first = effectsByType.getFirst();
                    if (baseEffect.isBetterThan(entity, to, first.effect(), first.itemStack())) {
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
                baseEffect.onActive(entity, from);
                baseEffect.activeId = UUID.randomUUID();
            }
        });
    }

    {
        managerMap.put(CurioSlotEffectManager.class, new CurioSlotEffectManager(this));
        managerMap.put(EquipmentSlotEffectManager.class, new EquipmentSlotEffectManager(this));
    }
}
