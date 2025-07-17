package com.chen1335.equipmentEffectLib.attachmentDatas;

import com.chen1335.equipmentEffectLib.API.objects.EEDataComponentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityEquipmentEffectData {

    public final EnumMap<EquipmentType, Map<EffectType<?>, Pair<ItemStack, BaseEffect>>> typeMapEnumMap = Util.make(() -> {
        EnumMap<EquipmentType, Map<EffectType<?>, Pair<ItemStack, BaseEffect>>> enumMap = new EnumMap<>(EquipmentType.class);
        for (EquipmentType value : EquipmentType.values()) {
            enumMap.put(value, new HashMap<>());
        }
        return enumMap;
    });

    public void update(LivingEntity entity) {
        Map<EffectType<?>, Pair<ItemStack, BaseEffect>> curioEffects = typeMapEnumMap.get(EquipmentType.CURIO);
        curioEffects.clear();
        CuriosApi.getCuriosInventory(entity).ifPresent(iCuriosItemHandler -> {
            for (int i = 0; i < iCuriosItemHandler.getEquippedCurios().getSlots(); i++) {
                ItemStack itemStack = iCuriosItemHandler.getEquippedCurios().getStackInSlot(i);
                if (itemStack.has(EEDataComponentTypes.ITEM_EFFECT_DATA)) {
                    Objects.requireNonNull(itemStack.get(EEDataComponentTypes.ITEM_EFFECT_DATA)).effects().forEach((effectType, effect) -> {
                        curioEffects.compute(effectType, (effectTypeHolder1, pair) -> {
                            if (pair == null || effect.level > pair.getSecond().level) {
                                return new Pair<>(itemStack, effect);
                            }
                            return pair;
                        });
                    });
                }
            }
        });


    }

    public enum EquipmentType {
        CURIO, ARMOR, HAND_HOLD
    }
}
