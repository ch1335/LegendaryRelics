package com.chen1335.equipmentEffectLib;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEItemEffectDataComponentTypes;
import com.chen1335.equipmentEffectLib.equipmentSources.ArmorSource;
import com.chen1335.equipmentEffectLib.equipmentSources.CuriosSource;
import com.chen1335.equipmentEffectLib.events.SetItemSetsEffectEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashSet;
import java.util.Set;

public class EquipmentEffectLib {
    public static final Set<IEquipmentSource> EQUIPMENT_SOURCES = new HashSet<>();

    public static final String MODID = "equipment_effect_lib";

    public static void init(IEventBus modEventBus, ModContainer modContainer){
        EEItemDataComponentTypes.init();
        EEItemEffectDataComponentTypes.init();
        EEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        modEventBus.addListener(EquipmentEffectLib::onSetup);
    }
    public static void onSetup(FMLCommonSetupEvent event) {
        EQUIPMENT_SOURCES.add(ArmorSource.INSTANCE);
        EQUIPMENT_SOURCES.add(CuriosSource.INSTANCE);
        NeoForge.EVENT_BUS.post(new SetItemSetsEffectEvent());
    }


}
