package com.chen1335.equipmentEffectLib;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEDataComponentTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

public class EquipmentEffect {
    public static final String MODID = LegendaryRelics.MODID;

    public EquipmentEffect(IEventBus modEventBus, ModContainer modContainer) {
        EEDataComponentTypes.init();
        EEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
    }

}
