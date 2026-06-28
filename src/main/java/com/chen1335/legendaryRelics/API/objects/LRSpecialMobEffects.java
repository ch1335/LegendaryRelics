package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.specialMobEffects.Doom;
import com.chen1335.legendaryRelics.registers.specialMobEffects.Erosion;
import com.chen1335.legendaryRelics.registers.specialMobEffects.InfernoScorch;
import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRSpecialMobEffects {
    private static final DeferredRegister<MobEffectType<?>> SPECIAL_MOB_EFFECT_TYPES = DeferredRegister.create(RegisterTypes.SPECIAL_EFFECT_TYPE, LegendaryRelics.MODID);

    public static void register(IEventBus eventBus) {
        SPECIAL_MOB_EFFECT_TYPES.register(eventBus);
    }

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<Erosion>> EROSION = SPECIAL_MOB_EFFECT_TYPES.register("erosion", MobEffectType.Builder.<Erosion>builder().factory(Erosion::new).renderIcon().build());

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<InfernoScorch>> INFERNO_SCORCH = SPECIAL_MOB_EFFECT_TYPES.register("inferno_scorch", MobEffectType.Builder.<InfernoScorch>builder().factory(InfernoScorch::new).renderIcon().build());

    public static final DeferredHolder<MobEffectType<?>, MobEffectType<Doom>> DOOM = SPECIAL_MOB_EFFECT_TYPES.register("doom", MobEffectType.Builder.<Doom>builder().factory(Doom::new).renderIcon().build());
}
