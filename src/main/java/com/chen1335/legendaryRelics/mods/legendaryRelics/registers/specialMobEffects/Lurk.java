package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.common.StackAbleEffect;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import net.minecraft.world.entity.LivingEntity;

/**
 * 隐匿：暗影刺客套装的潜伏层数，本身不提供任何属性；
 * 属性修正由 {@code ShadowAssassinArmorSetEffect} 在 LivingGetAttributeAdjustmentEvent 里按严格近战结算。
 */
public class Lurk extends StackAbleEffect {

    public static final int MAX_STACKS = 10;

    public Lurk(MobEffectType<?> effectType) {
        super(effectType);
    }

    @Override
    public void tick(LivingEntity livingEntity) {
        // 层数只在攻击时清空，不随时间衰减
    }

    @Override
    public boolean isExpired() {
        return this.stack <= 0;
    }

    @Override
    public float getPercentage() {
        return -1;
    }

    @Override
    public int getMaxStack() {
        return MAX_STACKS;
    }

    @Override
    public <T extends StackAbleEffect> T getFinal(T theOld) {
        return super.getFinal(theOld);
    }
}
