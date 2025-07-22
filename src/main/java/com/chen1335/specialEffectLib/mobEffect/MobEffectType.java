package com.chen1335.specialEffectLib.mobEffect;

public class MobEffectType<T extends SpecialMobEffect> {
    private final MobEffectFactory<T> factory;

    public MobEffectType(MobEffectFactory<T> factory) {
        this.factory = factory;
    }

    public T create() {
        return factory.create(this);
    }

    public interface MobEffectFactory<T extends SpecialMobEffect> {
        T create(MobEffectType<T> effectType);
    }
}
