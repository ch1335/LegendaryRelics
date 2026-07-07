package com.chen1335.equipmentEffectLib.slotEffectManagers;

import net.minecraft.resources.ResourceLocation;

public interface ISlotContext {
    Class<? extends SlotEffectManager> getManagerClass();

    ResourceLocation pathRL(ResourceLocation resourceLocation);
}
