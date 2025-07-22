package com.chen1335.specialEffectLib;

import com.chen1335.specialEffectLib.API.objects.SEAttachmentTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

public class SpecialEffectLib {
    public static final String MODID = "special_effect_lib";

    public SpecialEffectLib(IEventBus modEventBus, ModContainer modContainer) {
        SEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
    }
}
