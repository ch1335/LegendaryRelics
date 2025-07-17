package com.chen1335.equipmentEffectLib.API.objects;

import com.chen1335.equipmentEffectLib.EquipmentEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class RegisterTypes {
    public static final ResourceKey<Registry<EffectType<?>>> EQUIPMENT_EFFECT_TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(EquipmentEffect.MODID, "equipment_effect_type"));
    public static final Registry<EffectType<?>> EQUIPMENT_EFFECT_TYPE = new RegistryBuilder<>(EQUIPMENT_EFFECT_TYPE_KEY)
            .sync(true)
            .create();
}
