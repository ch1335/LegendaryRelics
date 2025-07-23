package com.chen1335.equipmentEffectLib.common;

import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.RegisterTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.CurioEffect;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

public class EventHandler {

    @EventBusSubscriber(modid = LegendaryRelics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static class Game {
        @SubscribeEvent
        public static void onCurioChange(CurioChangeEvent event) {
            if (!event.getFrom().getItem().equals(event.getTo().getItem())) {
                event.getEntity().getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).update(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void CurioAttributeModifierEvent(CurioAttributeModifierEvent event) {
            LivingEntity livingEntity = event.getSlotContext().entity();
            if (livingEntity != null) {
                for (BaseEffect effects : event.getItemStack().getOrDefault(EEDataComponentTypes.ITEM_EFFECT_DATA.value(), ItemEffectsData.EMPTY).effects().values()) {
                    if (effects instanceof CurioEffect curioEffect) {
                        curioEffect.modifyCurioAttribute(event);
                    }
                }

            }
        }

        @SubscribeEvent
        public static void onEntityTick(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof LivingEntity living) {
                EntityEquipmentEffectData entityEquipmentEffectData = living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA);
                entityEquipmentEffectData.unStackAbleTypeMapEnumMap.get(EntityEquipmentEffectData.EquipmentType.CURIO).values().forEach(pair -> {
                    pair.getSecond().curioTick(pair.getFirst(), living);
                });
                entityEquipmentEffectData.stackAbleTypeMapEnumMap.get(EntityEquipmentEffectData.EquipmentType.CURIO).values().forEach(pair -> {
                    for (CurioEffect effect : pair.getSecond()) {
                        effect.curioTick(pair.getFirst(), living);
                    }
                });
            }
        }
    }

    @EventBusSubscriber(modid = LegendaryRelics.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class Mod {
        @SubscribeEvent
        public static void registerRegistries(NewRegistryEvent event) {
            event.register(RegisterTypes.EQUIPMENT_EFFECT_TYPE);
        }
    }
}
