package com.chen1335.specialEffectLib.mobEffect;

import com.chen1335.legendaryRelics.registers.specialMobEffects.Erosion;

import java.util.function.Supplier;

public class MobEffectType<T extends SpecialMobEffect> {
    private final MobEffectFactory<T> factory;
    private final boolean renderIcon;

    public MobEffectType(MobEffectFactory<T> factory, boolean renderIcon) {
        this.factory = factory;
        this.renderIcon = renderIcon;
    }

    public T create() {
        return factory.create(this);
    }

    public boolean shouldRenderIcon() {
        return renderIcon;
    }

    public interface MobEffectFactory<T extends SpecialMobEffect> {
        T create(MobEffectType<T> effectType);
    }

    public static class Builder<T extends SpecialMobEffect> {
        private MobEffectFactory<T> factory;
        private boolean renderIcon = false;

        public static <BT extends SpecialMobEffect> Builder<BT> builder() {
            return new Builder<>();
        }

        public Builder<T> factory(MobEffectFactory<T> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<T> renderIcon() {
            this.renderIcon = true;
            return this;
        }

        public Supplier<MobEffectType<T>> build() {
            return () -> new MobEffectType<>(factory, renderIcon);
        }
    }
}
