package com.chen1335.equipmentEffectLib.events;

import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemExtension;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.common.SetEffectHolder;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

public class SetItemSetsEffectEvent extends Event {
    public void set(SetEffect setsEffect, EquipmentType equipmentType, Item item) {
        ((IEEItemExtension) item).EE$SetSetsEffect(new SetEffectHolder(setsEffect, equipmentType));
    }

    public void sets(SetEffect setEffect, EquipmentType equipmentType, Item... items) {
        for (Item item : items) {
            set(setEffect, equipmentType, item);
        }
    }
}
