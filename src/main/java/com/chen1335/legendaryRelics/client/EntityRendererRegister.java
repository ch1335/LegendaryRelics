package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.client.module.armor.blackDragonArmor.BlackDragonArmorModel;
import com.chen1335.legendaryRelics.client.module.armor.infernoArmor.InfernoArmorModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class EntityRendererRegister {
    public static BlackDragonArmorModel<LivingEntity> BLACK_DRAGON_ARMOR_MODEL;
    public static InfernoArmorModel<LivingEntity> INFERNO_ARMOR_MODEL;

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlackDragonArmorModel.LAYER_LOCATION, BlackDragonArmorModel::createBodyLayer);
        event.registerLayerDefinition(InfernoArmorModel.LAYER_LOCATION, () -> InfernoArmorModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION));
    }

    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        BLACK_DRAGON_ARMOR_MODEL = new BlackDragonArmorModel<>(event.getContext().bakeLayer(BlackDragonArmorModel.LAYER_LOCATION));
        INFERNO_ARMOR_MODEL = new InfernoArmorModel<>(event.getContext().bakeLayer(InfernoArmorModel.LAYER_LOCATION));
    }
}
