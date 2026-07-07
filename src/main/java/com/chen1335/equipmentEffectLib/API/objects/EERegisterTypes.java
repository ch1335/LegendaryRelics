package com.chen1335.equipmentEffectLib.API.objects;

import com.chen1335.equipmentEffectLib.EquipmentEffectLib;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class EERegisterTypes {
    public static final ResourceKey<Registry<EffectType<?>>> EQUIPMENT_EFFECT_TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(EquipmentEffectLib.MODID, "equipment_effect_type"));
    public static final Registry<EffectType<?>> EQUIPMENT_EFFECT_TYPE = new RegistryBuilder<>(EQUIPMENT_EFFECT_TYPE_KEY)
            .sync(true)
            .create();

    public static final ResourceKey<Registry<SetEffect>> SETS_EFFECT_TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(EquipmentEffectLib.MODID, "sets_effect"));
    public static final Registry<SetEffect> SETS_EFFECT_TYPE = new RegistryBuilder<>(SETS_EFFECT_TYPE_KEY)
            .sync(true)
            .create();

    public static final ResourceKey<Registry<EquipmentType>> EQUIPMENT_TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(EquipmentEffectLib.MODID, "equipment_type"));
    public static final Registry<EquipmentType> EQUIPMENT_TYPE = new RegistryBuilder<>(EQUIPMENT_TYPE_KEY)
            .sync(true)
            .create();
}
