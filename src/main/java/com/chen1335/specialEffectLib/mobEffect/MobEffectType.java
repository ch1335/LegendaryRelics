package com.chen1335.specialEffectLib.mobEffect;

import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MobEffectType<T extends SpecialMobEffect> {
    private final MobEffectFactory<T> factory;
    private final boolean renderIcon;
    private final MobEffectCategory category;
    @Nullable
    private final String descriptionId;
    @Nullable
    private final Style style;

    private ResourceLocation icon;

    public MobEffectType(MobEffectFactory<T> factory, boolean renderIcon, MobEffectCategory category,
                         ResourceLocation icon, @Nullable String descriptionId, @Nullable Style style) {
        this.factory = factory;
        this.renderIcon = renderIcon;
        this.category = category;
        this.icon = icon;
        this.descriptionId = descriptionId;
        this.style = style;
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

    /** 参考 {@code MobEffect#getDescriptionId()}，未显式指定时按注册名生成。 */
    public String getDescriptionId() {
        if (descriptionId != null) {
            return descriptionId;
        }
        ResourceLocation registryId = RegisterTypes.SPECIAL_EFFECT_TYPE.getKey(this);
        return registryId == null ? "special_effect.unknown" : Util.makeDescriptionId("special_effect", registryId);
    }

    /** 显示名：自定义 style 优先，否则按效果类别上色。 */
    public MutableComponent getDisplayName() {
        MutableComponent name = Component.translatable(getDescriptionId());
        if (style != null) {
            return name.withStyle(style);
        }
        return name.withStyle(category.getTooltipFormatting());
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
        private String descriptionId;
        private Style style;

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

        public Builder<T> descriptionId(String descriptionId) {
            this.descriptionId = descriptionId;
            return this;
        }

        public Builder<T> style(Style style) {
            this.style = style;
            return this;
        }

        public Supplier<MobEffectType<T>> build() {
            return () -> new MobEffectType<>(factory, renderIcon, category, icon, descriptionId, style);
        }
    }
}
