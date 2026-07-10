package com.chen1335.equipmentEffectLib.slotEffectManagers;

import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import net.minecraft.resources.ResourceLocation;

public interface ISlotContext {
    Class<? extends SlotEffectManager> getManagerClass();

    ResourceLocation pathRL(ResourceLocation resourceLocation);

    boolean match(EquipmentType equipmentType);
}
