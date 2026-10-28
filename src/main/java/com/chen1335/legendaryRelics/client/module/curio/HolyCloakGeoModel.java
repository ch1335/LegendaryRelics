package com.chen1335.legendaryRelics.client.module.curio;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.curios.HolyCloak;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 神圣斗篷的 GeckoLib 模型。
 */
public class HolyCloakGeoModel extends GeoModel<HolyCloak> {
    private static final ResourceLocation MODEL = LegendaryRelics.id("geo/curio/holy_cloak.geo.json");
    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/curio/holy_cloak.png");
    private static final ResourceLocation ANIMATION = LegendaryRelics.id("animations/holy_cloak.animation.json");

    @Override
    public ResourceLocation getModelResource(HolyCloak animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(HolyCloak animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HolyCloak animatable) {
        return ANIMATION;
    }
}
