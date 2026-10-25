package com.chen1335.legendaryRelics.client.renderers.armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

/**
 * 本模组护甲通用渲染器：额外处理 geo 里挂在 armorBody 下的 belt，只在 LEGS 槽显示。
 */
public class LRArmorRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T> {

    private GeoBone belt;

    public LRArmorRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        if (this.lastModel != bakedModel) {
            this.belt = getGeoModel().getBone("belt").orElse(null);
        }
        super.grabRelevantBones(bakedModel);
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        super.applyBoneVisibilityBySlot(currentSlot);
        boolean legs = currentSlot == EquipmentSlot.LEGS;
        if (legs && this.belt != null) {
            // GeoBone#setHidden 会级联隐藏子骨；LEGS 时 armorBody 本身被隐藏，需放开它对子骨的隐藏
            GeoBone parent = this.belt.getParent();
            if (parent != null) {
                parent.setChildrenHidden(false);
            }
        }
        setBoneVisible(this.belt, legs);
    }
}
