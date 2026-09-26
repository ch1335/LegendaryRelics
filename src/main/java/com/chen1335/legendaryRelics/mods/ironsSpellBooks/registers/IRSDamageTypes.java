package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface IRSDamageTypes {
    /**
     * 灵魂飞弹伤害。
     * <p>
     * 注意：这个伤害类型<b>绝不能</b>加入 {@code neoforge:is_magic} 标签。
     * 灵魂套装的触发条件是「造成法术伤害」，如果飞弹自身也算法术伤害，
     * 飞弹命中会再次触发套装效果，导致无限递归。
     */
    ResourceKey<DamageType> SOUL_MISSILE = ResourceKey.create(Registries.DAMAGE_TYPE, LegendaryRelics.id("soul_missile"));

    static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(SOUL_MISSILE, new DamageType("soul_missile", 0.1F));
    }
}
