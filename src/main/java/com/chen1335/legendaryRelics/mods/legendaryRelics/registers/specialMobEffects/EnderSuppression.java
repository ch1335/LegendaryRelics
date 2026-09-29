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
 * 末影抑制：携带者在此期间无法进行传送。
 *
 * <p>本身没有任何 tick 行为，只是一个"禁止传送"的标记 + 剩余时间。
 * 同维度传送由 mixin 拦截，跨维度传送由 EntityTravelToDimensionEvent 拦截。
 */
public class EnderSuppression extends TimeLimitEffect {

    public EnderSuppression(MobEffectType<?> effectType) {
        super(effectType);
    }

    /**
     * 重复施加时保留新实例（刷新后的持续时间），不做叠加。
     */
    public EnderSuppression getFinal(EnderSuppression theOld) {
        return this;
    }

    /**
     * 判断实体当前是否处于末影抑制中。
     *
     * <p>特殊效果按 (来源UUID, 类型) 两级索引，这里跨来源遍历，
     * 因此不关心效果是哪个攻击者施加的。
     *
     * <p>已死亡或已移除的实体一律返回 false，避免死亡 / 重生流程中的位移被误拦。
     */
    public static boolean isSuppressed(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.isRemoved() || living.isDeadOrDying()) {
            return false;
        }
        for (Map<MobEffectType<?>, SpecialMobEffect> effects : SpecialEffectAPI.getEntityEffectData(living).getEffects().values()) {
            if (effects.containsKey(LRSpecialMobEffects.ENDER_SUPPRESSION.get())) {
                return true;
            }
        }
        return false;
    }
}
