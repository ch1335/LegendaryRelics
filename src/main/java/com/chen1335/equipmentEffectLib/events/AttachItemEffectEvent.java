package com.chen1335.equipmentEffectLib.events;

import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.Map;

public class AttachItemEffectEvent extends Event {

    private final Map<Item, Map<EffectType<?>,BaseEffect>> capturedEffects;

    public AttachItemEffectEvent(Map<Item, Map<EffectType<?>,BaseEffect>> capturedEffect) {
        this.capturedEffects = capturedEffect;
    }

    public <T extends BaseEffect> EffectType<T> getEffectType(EffectType<T> effectType) {
        return effectType;
    }

    public void addEffect(Item item, EffectType<?> effectType, int level, EquipmentType equipmentType) {
        getEffects(item).put(effectType,effectType.create(level, equipmentType));
    }

    public void addEffect(Item item, BaseEffect baseEffect) {
        getEffects(item).put(baseEffect.getType(),baseEffect);
    }

    public Map<EffectType<?>,BaseEffect> getEffects(Item item) {
        return capturedEffects.computeIfAbsent(item, item1 -> new HashMap<>());
    }

    public Map<Item, Map<EffectType<?>,BaseEffect>> getCapturedEffects() {
        return capturedEffects;
    }
}
