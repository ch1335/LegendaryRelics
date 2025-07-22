package com.chen1335.legendaryRelics.equipmentEffects;

import com.chen1335.equipmentEffectLib.effectBase.CurioEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class LREquipmentEffectBase extends CurioEffect {
    public LREquipmentEffectBase(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public ResourceLocation getCooldownIcon() {
        return MissingTextureAtlasSprite.getLocation();
    }
}
