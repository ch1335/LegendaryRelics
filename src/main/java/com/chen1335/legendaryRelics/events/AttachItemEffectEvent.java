package com.chen1335.legendaryRelics.events;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.Map;

public class AttachItemEffectEvent extends Event {

    private final Map<Item, ArrayList<BaseEffect>> capturedEffects;

    public AttachItemEffectEvent(Map<Item, ArrayList<BaseEffect>> capturedEffect) {
        this.capturedEffects = capturedEffect;
    }

    public <T extends BaseEffect> EffectType<T> getEffectType(EffectType<T> effectType) {
        return effectType;
    }

    public void addEffect(Item item, EffectType<?> effectType, int level) {
        getEffects(item).add(effectType.create(level));
    }

    public void addEffect(Item item, BaseEffect baseEffect) {
        getEffects(item).add(baseEffect);
    }

    public ArrayList<BaseEffect> getEffects(Item item) {
        return capturedEffects.computeIfAbsent(item, item1 -> new ArrayList<>());
    }

    public Map<Item, ArrayList<BaseEffect>> getCapturedEffects() {
        return capturedEffects;
    }
}
