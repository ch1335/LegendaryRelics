package com.chen1335.legendaryRelics.registers.specialMobEffects.common;

import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;
import net.minecraft.world.entity.LivingEntity;

public class StackAbleEffect extends TimeLimitEffect {
    protected int decayTime = 0;

    protected int stack = 1;

    public StackAbleEffect(MobEffectType<?> effectType) {
        super(effectType);
    }

    public void setDecayTime(int decayTime) {
        this.decayTime = decayTime;
    }


    @Override
    public void tick(LivingEntity livingEntity) {
        super.tick(livingEntity);
        if (timeLeft <= 0) {
            if (stack > 1) {
                initTime(decayTime);
                stack--;
            }
        }
    }

    public <T extends StackAbleEffect> T getFinal(T theOld) {
        theOld.initTime(totalTime);
        theOld.stack = theOld.stack + stack;
        return theOld;
    }

    @Override
    public boolean isExpired() {
        return super.isExpired() && stack <= 1;
    }
}
