package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.client.module.armor.infernoArmor.InfernoArmorModel;
import com.chen1335.legendaryRelics.client.module.curio.HolyCloakModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class EntityRendererRegister {
    public static InfernoArmorModel<LivingEntity> INFERNO_ARMOR_MODEL;
    public static HolyCloakModel<LivingEntity> HOLY_CLOAK_MODEL;

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(InfernoArmorModel.LAYER_LOCATION, () -> InfernoArmorModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION));

        event.registerLayerDefinition(HolyCloakModel.LAYER_LOCATION, () -> HolyCloakModel.createMesh(new CubeDeformation(1)));

    }

    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        INFERNO_ARMOR_MODEL = new InfernoArmorModel<>(event.getContext().bakeLayer(InfernoArmorModel.LAYER_LOCATION));
        HOLY_CLOAK_MODEL = new HolyCloakModel<>(event.getContext().bakeLayer(HolyCloakModel.LAYER_LOCATION));
    }
}
