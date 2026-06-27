package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.ITickAbleEffect;
import com.chen1335.equipmentEffectLib.common.InfoHolder;
import com.chen1335.equipmentEffectLib.common.SlotEffectHolder;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.equipmentEffectLib.slotEffectManagers.SlotEffectManager;
import com.chen1335.equipmentEffectLib.slotEffectManagers.curio.CurioSlotEffectManager;
import com.chen1335.equipmentEffectLib.slotEffectManagers.equipment.EquipmentSlotEffectManager;
import com.chen1335.equipmentEffectLib.utils.Cast;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.TreeMultimap;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class EntityEquipmentEffectData {

    private final Map<Class<? extends SlotEffectManager>, SlotEffectManager> managerMap = new HashMap<>();


    private final Map<EffectType<?>, InfoHolder<?>> oldBestEffects = new HashMap<>();

    private final Map<EffectType<?>, TreeMultimap<Integer, SlotEffectHolder<?>>> effectHolders = new HashMap<>();

    public TreeMultimap<Integer, SlotEffectHolder<?>> getEffectHoldersByType(EffectType<?> type) {
        return effectHolders.computeIfAbsent(type, t -> TreeMultimap.create());
    }


    public Map<EffectType<?>, TreeMultimap<Integer, SlotEffectHolder<?>>> getEffectHolders() {
        return effectHolders;
    }

    public <T extends SlotEffectManager> T getSlotEffectManager(Class<T> clazz) {
        return Cast.cast(managerMap.get(clazz));
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
        for (TreeMultimap<Integer, SlotEffectHolder<?>> value : effectHolders.values()) {
            for (SlotEffectHolder<?> slotEffectHolder : value.values()) {
                list.add(slotEffectHolder.infoHolder());
            }
        }
        return ImmutableList.copyOf(list);
    }

    public <T extends BaseEffect> List<InfoHolder<T>> getEffectsByType(EffectType<T> effectType) {
        List<InfoHolder<T>> list = new ArrayList<>();
        for (SlotEffectHolder<?> value : getEffectHoldersByType(effectType).values()) {
            list.add(Cast.cast(value.infoHolder()));
        }
        return list;
    }

    public void tick(LivingEntity living) {
        for (TreeMultimap<Integer, SlotEffectHolder<?>> value : effectHolders.values()) {
            for (SlotEffectHolder<?> slotEffectHolder : value.values()) {
                InfoHolder<?> infoHolder = slotEffectHolder.infoHolder();
                if (infoHolder.effect() instanceof ITickAbleEffect tickAbleEffect) {
                    tickAbleEffect.effectTick(infoHolder.itemStack(), living);
                }
            }
        }
    }

    {
        managerMap.put(CurioSlotEffectManager.class, new CurioSlotEffectManager(this));
        managerMap.put(EquipmentSlotEffectManager.class, new EquipmentSlotEffectManager(this));
    }


    public void onUpdated(LivingEntity living, Set<EffectType<?>> changedTypes) {
        for (EffectType<?> changedType : changedTypes) {
            getBestEffect(changedType).ifPresent(infoHolder -> {
                InfoHolder<?> old = oldBestEffects.get(changedType);
                if (old == null) {
                    infoHolder.effect().onActive(living, infoHolder.itemStack());
                    oldBestEffects.put(changedType, infoHolder);
                } else if (old != infoHolder) {
                    old.effect().onDeActive(living, old.itemStack());
                    infoHolder.effect().onActive(living, infoHolder.itemStack());
                    oldBestEffects.put(changedType, infoHolder);
                }
            });

        }
    }
}
