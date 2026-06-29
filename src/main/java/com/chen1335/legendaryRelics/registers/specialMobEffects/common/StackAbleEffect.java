package com.chen1335.legendaryRelics.registers.specialMobEffects.common;

import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

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
                onStackChange(livingEntity);
            }
        }
    }

    protected void onStackChange(LivingEntity livingEntity) {

    }

    public <T extends StackAbleEffect> T getFinal(T theOld) {
        theOld.initTime(totalTime);
        theOld.stack = theOld.stack + stack;
        theOld.stack = Math.min(theOld.stack, getMaxStack());
        return theOld;
    }

    @Override
    public boolean isExpired() {
        return super.isExpired() && stack <= 1;
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        decayTime = buffer.readInt();
        stack = buffer.readInt();
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(decayTime);
        buffer.writeInt(stack);
    }

    @Override
    public CompoundTag save() {
        CompoundTag save = super.save();
        save.putInt("decayTime", decayTime);
        save.putInt("stack", stack);
        return save;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        decayTime = compoundTag.getInt("decayTime");
        stack = compoundTag.getInt("stack");
    }

    @Override
    public String getString() {
        return String.valueOf(stack);
    }

    public int getStack() {
        return stack;
    }

    public int getMaxStack() {
        return Integer.MAX_VALUE;
    }
}
