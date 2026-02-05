package com.chen1335.specialEffectLib.mobEffect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        totalTime = buffer.readInt();
        timeLeft = buffer.readInt();
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(totalTime);
        buffer.writeInt(timeLeft);
    }
}
