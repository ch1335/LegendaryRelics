package com.chen1335.equipmentEffectLib.common;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.ICurioEffect;
import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EEItemDataComponentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.dataComponentTypes.ItemEffectsData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

public class EventHandler {

    @EventBusSubscriber(modid = LegendaryRelics.MODID)
    public static class Game {
        @SubscribeEvent
        public static void onCurioChange(CurioChangeEvent event) {
            if (EquipmentEffectAPI.haveEffects(event.getFrom()) || EquipmentEffectAPI.haveEffects(event.getTo())) {
                event.getEntity().getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).update(event.getEntity(), EntityEquipmentEffectData.EquipmentType.CURIO);
                event.getEntity().getData(EEAttachmentTypes.ENTITY_SETS_EFFECT_DATA.get()).update(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
            EquipmentSlot.Type type = event.getSlot().getType();
            if (EquipmentEffectAPI.haveEffects(event.getFrom()) || EquipmentEffectAPI.haveEffects(event.getTo())) {
                switch (type) {
                    case HUMANOID_ARMOR -> {
                        event.getEntity().getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).update(event.getEntity(), EntityEquipmentEffectData.EquipmentType.ARMOR);
                    }
                    case HAND -> {
                        event.getEntity().getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).update(event.getEntity(), EntityEquipmentEffectData.EquipmentType.MAIN_HIND);
                    }
                }
                event.getEntity().getData(EEAttachmentTypes.ENTITY_SETS_EFFECT_DATA.get()).update(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void CurioAttributeModifierEvent(CurioAttributeModifierEvent event) {
            LivingEntity livingEntity = event.getSlotContext().entity();
            if (livingEntity != null) {
                for (BaseEffect effects : EquipmentEffectAPI.getEffects(event.getItemStack()).values()) {
                    if (effects instanceof ICurioEffect curioEffect) {
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
                    if (pair.effect() instanceof ICurioEffect curioEffect) {
                        curioEffect.curioTick(pair.itemStack(), living);
                    }
                });
                entityEquipmentEffectData.stackAbleTypeMapEnumMap.get(EntityEquipmentEffectData.EquipmentType.CURIO).values().forEach(pair -> {
                    for (EntityEquipmentEffectData.InfoHolder<?> info : pair) {
                        if (info.effect() instanceof ICurioEffect curioEffect) {
                            curioEffect.curioTick(info.itemStack(), living);
                        }
                    }
                });
            }
        }
    }

    @EventBusSubscriber(modid = LegendaryRelics.MODID)
    public static class Mod {
        @SubscribeEvent
        public static void registerRegistries(NewRegistryEvent event) {
            event.register(EERegisterTypes.EQUIPMENT_EFFECT_TYPE);
            event.register(EERegisterTypes.SETS_EFFECT_TYPE);
        }
    }
}
