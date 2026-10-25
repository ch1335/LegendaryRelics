package com.chen1335.legendaryRelics.client.renderers.armor;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.GeoModel;

/**
 * 本模组护甲通用模型：资源名对应 geo/armor/&lt;name&gt;.geo.json、textures/armor/&lt;name&gt;.png、
 * animations/&lt;name&gt;.animation.json。
 */
public class LRArmorGeoModel<T extends Item & GeoItem> extends GeoModel<T> {
    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;

    public LRArmorGeoModel(String name) {
        this.model = LegendaryRelics.id("geo/armor/" + name + ".geo.json");
        this.texture = LegendaryRelics.id("textures/armor/" + name + ".png");
        this.animation = LegendaryRelics.id("animations/" + name + ".animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return this.animation;
    }
}
