package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.client.module.armor.BlackDragonArmorModel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class EntityRendererRegister {
    public static BlackDragonArmorModel<LivingEntity> BLACK_DRAGON_ARMOR_MODEL;

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlackDragonArmorModel.LAYER_LOCATION, BlackDragonArmorModel::createBodyLayer);
    }

    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        BLACK_DRAGON_ARMOR_MODEL = new BlackDragonArmorModel<>(event.getContext().bakeLayer(BlackDragonArmorModel.LAYER_LOCATION));
    }
}
