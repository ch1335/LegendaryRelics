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

    ResourceKey<DamageType> CHAIN_LIGHTNING = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("chain_lightning"));

    /** 霜锥投掷物的伤害类型，参数与标签沿用原版三叉戟。 */
    ResourceKey<DamageType> FROST_SHARD = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("frost_shard"));

    /** 冰锥的伤害类型，参数与标签参考霜锥投掷物。 */
    ResourceKey<DamageType> ICICLE = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("icicle"));

    /** 烈焰飞轮环绕体的伤害类型。 */
    ResourceKey<DamageType> ORBITING_FIRE_CHAKRAM = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("orbiting_fire_chakram"));

    /** 冰刺（破冰者冰刺波）的伤害类型。 */
    ResourceKey<DamageType> ICE_THORN = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("ice_thorn"));

    static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(EROSION, new DamageType("erosion", 0.1F));
        context.register(FLYING_REAPER, new DamageType("flying_reaper", 0.1F));
        context.register(INFERNO_SCORCH, new DamageType("inferno_scorch", 0.1F));
        context.register(FLYING_KNIFE, new DamageType("flying_knife", 0.1F));
        context.register(CHAIN_LIGHTNING, new DamageType("chain_lightning", 0.1F));
        context.register(FROST_SHARD, new DamageType("frost_shard", 0.1F));
        context.register(ICICLE, new DamageType("icicle", 0.1F));
        context.register(ORBITING_FIRE_CHAKRAM, new DamageType("orbiting_fire_chakram", 0.1F));
        context.register(ICE_THORN, new DamageType("ice_thorn", 0.1F));

    }
}
