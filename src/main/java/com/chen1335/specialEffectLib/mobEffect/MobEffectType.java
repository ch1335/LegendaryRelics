package com.chen1335.specialEffectLib.mobEffect;

import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.Objects;
import java.util.function.Supplier;

public class MobEffectType<T extends SpecialMobEffect> {
    private final MobEffectFactory<T> factory;
    private final boolean renderIcon;
    private final MobEffectCategory category;

    private ResourceLocation icon;

    public MobEffectType(MobEffectFactory<T> factory, boolean renderIcon, MobEffectCategory category,ResourceLocation icon) {
        this.factory = factory;
        this.renderIcon = renderIcon;
        this.category = category;
        this.icon = icon;
    }

    public ResourceLocation getIcon() {
        if (icon != null) {
            return icon;
        }
        ResourceLocation registryId = RegisterTypes.SPECIAL_EFFECT_TYPE.getKey(this);
        if (registryId == null) return null;
        icon = ResourceLocation.fromNamespaceAndPath(
                registryId.getNamespace(),
                "textures/icon/special_effects/" + registryId.getPath() + ".png"
        );
        return icon;

    }

    public T create() {
        return factory.create(this);
    }

    public boolean shouldRenderIcon() {
        return renderIcon;
    }

    public MobEffectCategory getCategory() {
        return category;
    }

    public interface MobEffectFactory<T extends SpecialMobEffect> {
        T create(MobEffectType<T> effectType);
    }

    public static class Builder<T extends SpecialMobEffect> {
        private MobEffectFactory<T> factory;
        private boolean renderIcon = false;
        private MobEffectCategory category = MobEffectCategory.NEUTRAL;
        private ResourceLocation icon;
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

        public Builder<T> category(MobEffectCategory category) {
            this.category = category;
            return this;
        }

        public Builder<T> category(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }


        public Supplier<MobEffectType<T>> build() {
            return () -> new MobEffectType<>(factory, renderIcon, category,icon);
        }
    }
}
