package com.chen1335.specialEffectLib.mobEffect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class TimeLimitEffect extends SpecialMobEffect {
    protected int timeLeft = 0;
    protected int totalTime = 0;

    public TimeLimitEffect(MobEffectType<?> effectType) {
        super(effectType);
    }

    public void initTime(int time) {
        timeLeft = time;
        totalTime = time;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public void tick(LivingEntity livingEntity) {
        super.tick(livingEntity);
        timeLeft--;
    }

    @Override
    public boolean isExpired() {
        return timeLeft <= 0;
    }

    @Override
    public CompoundTag save() {
        CompoundTag compoundTag = super.save();
        compoundTag.putInt("TotalTime", totalTime);
        compoundTag.putInt("TimeLeft", timeLeft);
        return compoundTag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        totalTime = compoundTag.getInt("TotalTime");
        timeLeft = compoundTag.getInt("TimeLeft");
    }
}
