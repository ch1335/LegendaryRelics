package com.chen1335.legendaryRelics.client.module.curio;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.curios.ShadowCloak;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 暗影斗篷的 GeckoLib 模型。
 */
public class ShadowCloakGeoModel extends GeoModel<ShadowCloak> {
    private static final ResourceLocation MODEL = LegendaryRelics.id("geo/curio/shadow_cloak.geo.json");
    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/curio/shadow_cloak.png");
    private static final ResourceLocation ANIMATION = LegendaryRelics.id("animations/armor/shadow_cloak.animation.json");

    @Override
    public ResourceLocation getModelResource(ShadowCloak animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ShadowCloak animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ShadowCloak animatable) {
        return ANIMATION;
    }
}
