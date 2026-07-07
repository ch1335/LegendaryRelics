package com.chen1335.equipmentEffectLib;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEItemEffectDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.MixinsAPI.IEEItemExtension;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.events.SetItemSetsEffectEvent;
import com.chen1335.legendaryRelics.events.AttachItemEffectEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;

public class EquipmentEffectLib {
    public static final Set<IEquipmentSource> EQUIPMENT_SOURCES = new HashSet<>();

    private static final Map<Item, ArrayList<BaseEffect>> CAPTURED_EFFECT = new HashMap<>();
    public static final String MODID = "equipment_effect_lib";

    public static void init(IEventBus modEventBus, ModContainer modContainer) {
        EEItemDataComponentTypes.init();
        EEItemEffectDataComponentTypes.init();
        EEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        EquipmentTypes.EQUIPMENT_TYPE.register(modEventBus);
        modEventBus.addListener(EquipmentEffectLib::onSetup);
    }

    public static void onSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.post(new SetItemSetsEffectEvent());
        event.enqueueWork(() -> {
            BuiltInRegistries.ITEM.forEach(item -> {
                if (item instanceof IEffectEquipment effectEquipment && !effectEquipment.getDefaultEffects().isEmpty()) {
                    CAPTURED_EFFECT.put(item, new ArrayList<>(effectEquipment.getDefaultEffects()));
                }
            });

            AttachItemEffectEvent attachItemEffectEvent = NeoForge.EVENT_BUS.post(new AttachItemEffectEvent(CAPTURED_EFFECT));
            attachItemEffectEvent.getCapturedEffects().forEach((item, baseEffects) -> {
                IEEItemExtension itemExtension = (IEEItemExtension) item;
                itemExtension.EE$SetDefaultItemEffect(List.copyOf(baseEffects));
            });
            CAPTURED_EFFECT.clear();
        });
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

}
