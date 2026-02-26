package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface LRDamageTypes {
    ResourceKey<DamageType> EROSION = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("erosion"));
    ResourceKey<DamageType> INFERNO_SCORCH = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("inferno_scorch"));

    ResourceKey<DamageType> FLYING_REAPER = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("flying_reaper"));

    ResourceKey<DamageType> FLYING_KNIFE = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("flying_knife"));

    static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(EROSION, new DamageType("erosion", 0.1F));
        context.register(FLYING_REAPER, new DamageType("flying_reaper", 0.1F));
        context.register(INFERNO_SCORCH, new DamageType("inferno_scorch", 0.1F));
        context.register(FLYING_KNIFE, new DamageType("flying_knife", 0.1F));

    }
}
