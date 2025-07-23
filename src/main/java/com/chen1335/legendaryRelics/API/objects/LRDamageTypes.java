package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface LRDamageTypes {
    ResourceKey<DamageType> EROSION = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("erosion"));

    static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(EROSION, new DamageType("erosion", 0.1F));
    }
}
