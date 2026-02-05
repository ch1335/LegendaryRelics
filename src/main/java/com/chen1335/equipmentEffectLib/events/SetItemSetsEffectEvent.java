package com.chen1335.equipmentEffectLib.events;

import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemExtension;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

public class SetItemSetsEffectEvent extends Event {
    public void set(Item item, SetEffect setsEffect) {
        ((IEEItemExtension) item).EE$SetSetsEffect(setsEffect);
    }

    public void sets(SetEffect setEffect, Item... items) {
        for (Item item : items) {
            set(item, setEffect);
        }
    }
}
