package com.chen1335.equipmentEffectLib.events;

import com.chen1335.equipmentEffectLib.common.SetEffectHolder;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.Map;

public class SetItemSetsEffectEvent extends Event {
    private final Map<Item, SetEffectHolder> setEffects = new HashMap<>();

    public void set(SetEffect setsEffect, EquipmentType equipmentType, Item item) {
        setEffects.put(item, new SetEffectHolder(setsEffect, equipmentType));
    }

    public void sets(SetEffect setEffect, EquipmentType equipmentType, Item... items) {
        for (Item item : items) {
            setEffects.put(item, new SetEffectHolder(setEffect, equipmentType));
        }
    }

    public Map<Item, SetEffectHolder> getSetEffects() {
        return setEffects;
    }
}
