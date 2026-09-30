package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

/**
 * 无敌：携带者在此期间免疫一切伤害。
 *
 * <p>通用效果，任何需要"短暂无敌"的装备效果都可以施加它。
 * 本身没有任何 tick 行为，只是一个"无敌"的标记 + 剩余时间，
 * 实际免伤在 LivingIncomingDamageEvent 里处理。
 */
public class Invincibility extends TimeLimitEffect {

    public Invincibility(MobEffectType<?> effectType) {
        super(effectType);
    }

    /**
     * 重复施加时保留新实例（刷新后的持续时间），不做叠加。
     */
    public Invincibility getFinal(Invincibility theOld) {
        return this;
    }

    /**
     * 判断实体当前是否处于无敌状态。
     *
     * <p>特殊效果按 (来源UUID, 类型) 两级索引，这里跨来源遍历，
     * 因此不关心无敌是哪个装备效果施加的。
     *
     * <p>已死亡或已移除的实体一律返回 false。
     */
    public static boolean isInvincible(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.isRemoved() || living.isDeadOrDying()) {
            return false;
        }
        for (Map<MobEffectType<?>, SpecialMobEffect> effects : SpecialEffectAPI.getEntityEffectData(living).getEffects().values()) {
            if (effects.containsKey(LRSpecialMobEffects.INVINCIBILITY.get())) {
                return true;
            }
        }
        return false;
    }
}
