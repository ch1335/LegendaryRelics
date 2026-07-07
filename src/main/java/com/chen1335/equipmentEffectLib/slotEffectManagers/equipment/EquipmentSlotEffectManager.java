package com.chen1335.equipmentEffectLib.slotEffectManagers.equipment;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.equipmentEffectLib.slotEffectManagers.SlotEffectManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class EquipmentSlotEffectManager extends SlotEffectManager {
    public final EnumMap<EquipmentSlot, Map<EffectType<?>, BaseEffect>> enumMap = new EnumMap<>(EquipmentSlot.class);

    public EquipmentSlotEffectManager(EntityEquipmentEffectData data) {
        super(data);
    }

    public void onEquipmentChanged(LivingEntity living, EquipmentSlot slot, ItemStack from, ItemStack to) {
        super.onChanged(living,new SlotContext(slot),from,to);
    }

    @Override
    protected IEquipmentType getEquipmentType() {
        return EquipmentTypes.ARMOR_AND_HANDS;
    }

    @Override
    public Map<EffectType<?>, BaseEffect> getEffectsBySlot(ISlotContext context) {
        SlotContext context1 = (SlotContext) context;
        return enumMap.getOrDefault(context1.slot(), Map.of());
    }

    public record SlotContext(EquipmentSlot slot) implements ISlotContext {

        @Override
        public Class<? extends SlotEffectManager> getManagerClass() {
            return EquipmentSlotEffectManager.class;
        }

        @Override
        public ResourceLocation pathRL(ResourceLocation resourceLocation) {
            return resourceLocation.withSuffix(slot.getName());
        }
    }
}
