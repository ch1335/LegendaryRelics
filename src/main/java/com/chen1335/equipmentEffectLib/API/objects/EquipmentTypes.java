package com.chen1335.equipmentEffectLib.API.objects;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.EquipmentEffectLib;
import com.chen1335.equipmentEffectLib.equipmentSources.CuriosSource;
import com.chen1335.equipmentEffectLib.equipmentType.ALLType;
import com.chen1335.equipmentEffectLib.equipmentType.CombineType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class EquipmentTypes {
    public static DeferredRegister<EquipmentType> EQUIPMENT_TYPE = DeferredRegister.create(EERegisterTypes.EQUIPMENT_TYPE, EquipmentEffectLib.MODID);

    public static EquipmentType HEAD = register(EQUIPMENT_TYPE, "head", livingEntity -> List.of(livingEntity.getItemBySlot(EquipmentSlot.HEAD)));
    public static EquipmentType CHEST = register(EQUIPMENT_TYPE, "chest", livingEntity -> List.of(livingEntity.getItemBySlot(EquipmentSlot.CHEST)));
    public static EquipmentType LEGS = register(EQUIPMENT_TYPE, "legs", livingEntity -> List.of(livingEntity.getItemBySlot(EquipmentSlot.LEGS)));
    public static EquipmentType FEET = register(EQUIPMENT_TYPE, "feet", livingEntity -> List.of(livingEntity.getItemBySlot(EquipmentSlot.FEET)));

    public static EquipmentType HUMANOID_ARMOR = register(EQUIPMENT_TYPE, "humanoid_armor", HEAD, CHEST, LEGS, FEET);

    public static EquipmentType MAIN_HAND = register(EQUIPMENT_TYPE, "main_hand", livingEntity -> List.of(livingEntity.getItemBySlot(EquipmentSlot.MAINHAND)));
    public static EquipmentType OFF_HAND = register(EQUIPMENT_TYPE, "off_hand", livingEntity -> List.of(livingEntity.getItemBySlot(EquipmentSlot.OFFHAND)));

    public static EquipmentType HANDS = register(EQUIPMENT_TYPE, "hand", MAIN_HAND, OFF_HAND);

    public static EquipmentType ARMOR_AND_HANDS = register(EQUIPMENT_TYPE, "armor_and_hands", HUMANOID_ARMOR, HANDS);

    public static EquipmentType CURIO = register(EQUIPMENT_TYPE, "curio", CuriosSource.INSTANCE);

    public static EquipmentType ALL = register(EQUIPMENT_TYPE, "all", ALLType.INSTANCE);

    public static EquipmentType NON = register(EQUIPMENT_TYPE,"non",livingEntity -> List.of());

    public static EquipmentType register(DeferredRegister<EquipmentType> deferredRegister, String name, IEquipmentSource source) {
        EquipmentType equipmentType = new EquipmentType(source);
        deferredRegister.register(name, () -> equipmentType);
        return equipmentType;
    }

    public static EquipmentType register(DeferredRegister<EquipmentType> deferredRegister, String name, EquipmentType... equipmentTypes) {
        EquipmentType equipmentType = new CombineType(equipmentTypes);
        deferredRegister.register(name, () -> equipmentType);
        return equipmentType;
    }

    public static EquipmentType register(DeferredRegister<EquipmentType> deferredRegister,String name, EquipmentType equipmentType) {
        deferredRegister.register(name, () -> equipmentType);
        return equipmentType;
    }
}
