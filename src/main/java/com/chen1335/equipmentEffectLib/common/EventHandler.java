package com.chen1335.equipmentEffectLib.common;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.ICurioEffect;
import com.chen1335.equipmentEffectLib.API.objects.EEAttachmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.curio.CurioSlotEffectManager;
import com.chen1335.equipmentEffectLib.slotEffectManagers.equipment.EquipmentSlotEffectManager;
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

import java.util.Map;
import java.util.Objects;

@EventBusSubscriber(modid = LegendaryRelics.MODID)

public class EventHandler {
    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        boolean needUpdate = false;

        if (!event.getFrom().is(event.getTo().getItem())) {
            if (EquipmentEffectAPI.haveEffects(event.getFrom()) || EquipmentEffectAPI.haveEffects(event.getTo())) {
                needUpdate = true;
            }
        } else {
            Map<EffectType<?>, BaseEffect> fromEffects = EquipmentEffectAPI.getEffects(event.getFrom());
            Map<EffectType<?>, BaseEffect> toEffects = EquipmentEffectAPI.getEffects(event.getTo());
            if (fromEffects.values().hashCode() != toEffects.values().hashCode()) {
                needUpdate = true;
            }
        }

        if (needUpdate) {
            EntityEquipmentEffectData data = event.getEntity().getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA);
            CurioSlotEffectManager slotEffectManager = data.getSlotEffectManager(CurioSlotEffectManager.class);
            slotEffectManager.onCurioChanged(event.getEntity(), event.getIdentifier(), event.getSlotIndex(), event.getFrom(), event.getTo());
        }


        if (!Objects.equals(EquipmentEffectAPI.getItemSetEffect(event.getFrom()), EquipmentEffectAPI.getItemSetEffect(event.getTo()))) {
            EquipmentEffectAPI.updateEntitySetEffect(event.getEntity(), new CurioSlotEffectManager.SlotContext(event.getIdentifier(), event.getSlotIndex()), event.getFrom(), event.getTo());
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        EquipmentSlot.Type type = event.getSlot().getType();
        boolean updateTotal = false;
        if (!event.getFrom().is(event.getTo().getItem())) {
            if (EquipmentEffectAPI.haveEffects(event.getFrom()) || EquipmentEffectAPI.haveEffects(event.getTo())) {
                updateTotal = true;
            }
        } else {
            Map<EffectType<?>, BaseEffect> fromEffects = EquipmentEffectAPI.getEffects(event.getFrom());
            Map<EffectType<?>, BaseEffect> toEffects = EquipmentEffectAPI.getEffects(event.getTo());
            if (fromEffects.values().hashCode() != toEffects.values().hashCode()) {
                updateTotal = true;
            }
        }

        if (updateTotal) {
            EntityEquipmentEffectData data = event.getEntity().getData(EEAttachmentTypes.ENTITY_EQUIPMENT_EFFECT_DATA);
            EquipmentSlotEffectManager slotEffectManager = data.getSlotEffectManager(EquipmentSlotEffectManager.class);
            slotEffectManager.onEquipmentChanged(event.getEntity(), event.getSlot(), event.getFrom(), event.getTo());
        }

        if (!Objects.equals(EquipmentEffectAPI.getItemSetEffect(event.getFrom()), EquipmentEffectAPI.getItemSetEffect(event.getTo()))) {
            EquipmentEffectAPI.updateEntitySetEffect(event.getEntity(), new EquipmentSlotEffectManager.SlotContext(event.getSlot()), event.getFrom(), event.getTo());
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
            living.getData(EEAttachmentTypes.ENTITY_SETS_EFFECT_DATA).tick(living);
        }
    }


    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(EERegisterTypes.EQUIPMENT_EFFECT_TYPE);
        event.register(EERegisterTypes.SETS_EFFECT_TYPE);
        event.register(EERegisterTypes.EQUIPMENT_TYPE);
    }
}
