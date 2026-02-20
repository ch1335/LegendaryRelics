package com.chen1335.legendaryRelics.API;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import net.minecraft.resources.ResourceLocation;

public class CooldownAbleEffectType<T extends BaseEffect> extends EffectType<T> {

    private ResourceLocation resourceLocation = null;

    public CooldownAbleEffectType(EffectFactory<T> factory, boolean stackable) {
        super(factory, stackable);
    }

    public CooldownAbleEffectType(EffectFactory<T> factory) {
        super(factory);
    }

    public CooldownAbleEffectType<T> cooldownIcon(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }

    public ResourceLocation getCooldownIcon() {
        return resourceLocation;
    }
}
