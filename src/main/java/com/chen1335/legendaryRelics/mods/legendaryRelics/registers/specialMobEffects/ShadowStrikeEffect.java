package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects;

import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;

/**
 * 暗影突袭的待消耗状态：攻击命中时读取并移除。
 */
public class ShadowStrikeEffect extends TimeLimitEffect {

    public ShadowStrikeEffect(MobEffectType<?> effectType) {
        super(effectType);
    }
}
