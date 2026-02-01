package com.chen1335.equipmentEffectLib.common;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.ICurioEffect;
import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
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

import java.util.List;

public class EventHandler {

    @EventBusSubscriber(modid = LegendaryRelics.MODID)
    public static class Game {
        @SubscribeEvent
        public static void onCurioChange(CurioChangeEvent event) {
            boolean updateTotal = false;
            boolean updateSameItem = false;

            if (!event.getFrom().is(event.getTo().getItem())) {
                if (EquipmentEffectAPI.haveEffects(event.getFrom()) || EquipmentEffectAPI.haveEffects(event.getTo())) {
                    updateTotal = true;
                }
            } else {
                if (EquipmentEffectAPI.getEffects(event.getFrom()).values().hashCode() == EquipmentEffectAPI.getEffects(event.getTo()).values().hashCode()) {
                    updateSameItem = true;
                }
            }

            if (updateSameItem) {
                EquipmentEffectAPI.updateEntityEquipmentEffectSameItem(event.getEntity(), EntityEquipmentEffectData.EquipmentType.CURIO, event.getFrom(), event.getTo());

            } else if (updateTotal) {
                EquipmentEffectAPI.updateEntityEquipmentEffect(event.getEntity(), EntityEquipmentEffectData.EquipmentType.CURIO);
            }


            if (EquipmentEffectAPI.getItemSetEffect(event.getFrom()) != EquipmentEffectAPI.getItemSetEffect(event.getTo())) {
                EquipmentEffectAPI.updateEntitySetEffect(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
            EquipmentSlot.Type type = event.getSlot().getType();
            boolean updateTotal = false;
            boolean updateSameItem = false;
            if (!event.getFrom().is(event.getTo().getItem())) {
                if (EquipmentEffectAPI.haveEffects(event.getFrom()) || EquipmentEffectAPI.haveEffects(event.getTo())) {
                    updateTotal = true;
                }
            } else {
                if (EquipmentEffectAPI.getEffects(event.getFrom()).values().hashCode() == EquipmentEffectAPI.getEffects(event.getTo()).values().hashCode()) {
                    updateSameItem = true;
                }
            }

            if (updateSameItem) {
                switch (type) {
                    case HUMANOID_ARMOR -> {
                        EquipmentEffectAPI.updateEntityEquipmentEffectSameItem(event.getEntity(), EntityEquipmentEffectData.EquipmentType.ARMOR, event.getFrom(), event.getTo());
                    }
                    case HAND -> {
                        EquipmentEffectAPI.updateEntityEquipmentEffectSameItem(event.getEntity(), EntityEquipmentEffectData.EquipmentType.MAIN_HIND, event.getFrom(), event.getTo());
                    }
                }
            } else if (updateTotal) {
                switch (type) {
                    case HUMANOID_ARMOR -> {
                        EquipmentEffectAPI.updateEntityEquipmentEffect(event.getEntity(), EntityEquipmentEffectData.EquipmentType.ARMOR);
                    }
                    case HAND -> {
                        EquipmentEffectAPI.updateEntityEquipmentEffect(event.getEntity(), EntityEquipmentEffectData.EquipmentType.MAIN_HIND);
                    }
                }
            }

            if (EquipmentEffectAPI.getItemSetEffect(event.getFrom()) != EquipmentEffectAPI.getItemSetEffect(event.getTo())) {
                EquipmentEffectAPI.updateEntitySetEffect(event.getEntity());
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
                living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA).tick(living);
                EntityEquipmentEffectData entityEquipmentEffectData = living.getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA);


                for (List<EntityEquipmentEffectData.InfoHolder<?>> value : entityEquipmentEffectData.getEffects(EntityEquipmentEffectData.EquipmentType.CURIO).values()) {
                    for (EntityEquipmentEffectData.InfoHolder<?> info : value) {
                        if (info.effect() instanceof ICurioEffect curioEffect) {
                            curioEffect.curioTick(info.itemStack(), living);
                        }
                    }
                }
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
