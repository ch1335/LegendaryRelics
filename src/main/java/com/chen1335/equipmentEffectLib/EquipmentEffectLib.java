package com.chen1335.equipmentEffectLib;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.API.objects.*;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.dataComponentTypes.SetEffectData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.events.SetItemSetsEffectEvent;
import com.chen1335.equipmentEffectLib.events.AttachItemEffectEvent;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.*;

public class EquipmentEffectLib {
    public static final Set<IEquipmentSource> EQUIPMENT_SOURCES = new HashSet<>();

    private static final Map<Item, Map<EffectType<?>, BaseEffect>> CAPTURED_EFFECT = new HashMap<>();
    public static final String MODID = "equipment_effect_lib";

    public static void init(IEventBus modEventBus, ModContainer modContainer) {
        EEItemEffectDataComponentTypes.init();
        EEItemDataComponentTypes.DATA_COMPONENTS.register(modEventBus);
        EEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        EquipmentTypes.EQUIPMENT_TYPE.register(modEventBus);
        modEventBus.addListener(EquipmentEffectLib::ModifyDefaultComponentsEvent);
    }


    @SubscribeEvent
    public static void ModifyDefaultComponentsEvent(ModifyDefaultComponentsEvent event) {
        SetItemSetsEffectEvent setItemSetsEffectEvent = new SetItemSetsEffectEvent();
        NeoForge.EVENT_BUS.post(setItemSetsEffectEvent);
        setItemSetsEffectEvent.getSetEffects().forEach((item, setEffectHolder) -> {
            event.modify(item, builder -> {
                builder.set(EEItemDataComponentTypes.SET_EFFECT.value(), new SetEffectData(
                        EERegisterTypes.SETS_EFFECT_TYPE.wrapAsHolder(setEffectHolder.setEffect()),
                        EERegisterTypes.EQUIPMENT_TYPE.wrapAsHolder(setEffectHolder.equipmentType())
                ));
            });
        });

        BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof IEffectEquipment effectEquipment && !effectEquipment.getDefaultEffects().isEmpty()) {
                effectEquipment.getDefaultEffects().forEach(effect -> {
                    CAPTURED_EFFECT.computeIfAbsent(item, item1 -> new LinkedHashMap<>()).put(effect.getType(), effect);
                });
            }
        });

        AttachItemEffectEvent attachItemEffectEvent = NeoForge.EVENT_BUS.post(new AttachItemEffectEvent(CAPTURED_EFFECT));
        attachItemEffectEvent.getCapturedEffects().forEach((item, baseEffects) -> {
            event.modify(item, builder -> {
                builder.set(EEItemDataComponentTypes.ITEM_EFFECT.value(), new ItemEffectsData(ImmutableMap.copyOf(baseEffects)));
            });
        });
        CAPTURED_EFFECT.clear();
    }


    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

}
