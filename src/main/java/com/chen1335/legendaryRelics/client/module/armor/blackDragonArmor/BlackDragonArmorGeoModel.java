package com.chen1335.legendaryRelics.client.module.armor.blackDragonArmor;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonArmor;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * 黑龙护甲整套的 GeckoLib 模型，四个部位共用同一条 geo。
 */
public class BlackDragonArmorGeoModel extends GeoModel<BlackDragonArmor> {
    private static final ResourceLocation MODEL = LegendaryRelics.id("geo/armor/black_dragon_armor.geo.json");
    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/armor/black_dragon_armor.png");
    private static final ResourceLocation ANIMATION = LegendaryRelics.id("animations/black_dragon_armor.animation.json");

    @Override
    public ResourceLocation getModelResource(BlackDragonArmor animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(BlackDragonArmor animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(BlackDragonArmor animatable) {
        return ANIMATION;
    }
}
