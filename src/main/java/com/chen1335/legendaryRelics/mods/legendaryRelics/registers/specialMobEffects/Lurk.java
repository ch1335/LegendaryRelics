package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects;

import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;

/**
 * 隐匿：暗影刺客套装效果施加的状态。当前只完成注册，具体行为在后续阶段补充。
 */
public class Lurk extends TimeLimitEffect {

    public Lurk(MobEffectType<?> effectType) {
        super(effectType);
    }

    /**
     * 重复施加时用新实例（刷新持续时间），不做叠加。
     */
    public Lurk getFinal(Lurk theOld) {
        return this;
    }
}
