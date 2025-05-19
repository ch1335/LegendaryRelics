package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.shieldSystem.API.objects.Registries;
import com.chen1335.shieldSystem.shieldSystem.Shield;
import com.chen1335.shieldSystem.shieldSystem.UnitShield;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRShieldType {
    public static final DeferredRegister<Shield.ShieldType<?>> SHIELD_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.SHIELD_TYPE, LegendaryRelics.MODID);
    public static final DeferredHolder<Shield.ShieldType<?>, Shield.ShieldType<UnitShield>> SHIELD_REGENERATOR_SHIELD = SHIELD_TYPE_DEFERRED_REGISTER.register("shield_regenerator_shield", () -> new Shield.ShieldType<>(() -> new UnitShield(0)));

}
