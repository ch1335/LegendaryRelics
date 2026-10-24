package com.chen1335.legendaryRelics.client.module.armor.blackDragonArmor;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonArmor;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BlackDragonArmorRenderer extends GeoArmorRenderer<BlackDragonArmor> {

    private GeoBone belt;

    public BlackDragonArmorRenderer(GeoModel<BlackDragonArmor> model) {
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
        // belt 挂在 armorBody 下，属于护腿，只有 LEGS 时显示
        setBoneVisible(this.belt, currentSlot == EquipmentSlot.LEGS);
    }
}
