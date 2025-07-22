package com.chen1335.equipmentEffectLib;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEDataComponentTypes;
import com.chen1335.equipmentEffectLib.common.EventHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;

public class EquipmentEffectLib {
    public static final String MODID = "equipment_effect_lib";

    public EquipmentEffectLib(IEventBus modEventBus, ModContainer modContainer) {
        EEDataComponentTypes.init();
        EEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
    }

}
