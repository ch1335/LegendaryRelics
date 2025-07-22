package com.chen1335.specialEffectLib.API.objects;

import com.chen1335.specialEffectLib.SpecialEffectLib;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class RegisterTypes {
    public static final ResourceKey<Registry<MobEffectType<?>>> SPECIAL_EFFECT_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(SpecialEffectLib.MODID, "special_effect"));
    public static final Registry<MobEffectType<?>> SPECIAL_EFFECT_TYPE = new RegistryBuilder<>(SPECIAL_EFFECT_KEY)
            .sync(true)
            .create();
}
