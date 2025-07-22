package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.specialMobEffects.Erosion;
import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRSpecialMobEffect {
    public static DeferredRegister<MobEffectType<?>> SPECIAL_MOB_EFFECT_TYPES = DeferredRegister.create(RegisterTypes.SPECIAL_EFFECT_TYPE, LegendaryRelics.MODID);

    public static DeferredHolder<MobEffectType<?>, MobEffectType<Erosion>> EROSION = SPECIAL_MOB_EFFECT_TYPES.register("erosion", () -> new MobEffectType<>(Erosion::new));
}
