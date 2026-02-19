package com.chen1335.legendaryRelics.API;

import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import net.minecraft.resources.ResourceLocation;

public class CooldownAbleEffectType<T extends BaseEffect> extends EffectType<T> {

    private ResourceLocation resourceLocation = null;

    public CooldownAbleEffectType(EffectFactory<T> factory, IEquipmentType equipmentType, boolean stackable) {
        super(factory, equipmentType, stackable);
    }

    public CooldownAbleEffectType(EffectFactory<T> factory, IEquipmentType equipmentType) {
        super(factory, equipmentType);
    }

    public CooldownAbleEffectType<T> cooldownIcon(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }

    public ResourceLocation getCooldownIcon() {
        return resourceLocation;
    }
}
