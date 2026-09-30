package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.client.module.armor.blackDragonArmor.BlackDragonArmorModel;
import com.chen1335.legendaryRelics.client.module.armor.infernoArmor.InfernoArmorModel;
import com.chen1335.legendaryRelics.client.module.armor.soulArmor.SoulArmorModel;
import com.chen1335.legendaryRelics.client.module.armor.twistedArmor.TwistedArmorModel;
import com.chen1335.legendaryRelics.client.module.curio.HolyCloakModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class EntityRendererRegister {
    public static BlackDragonArmorModel<LivingEntity> BLACK_DRAGON_ARMOR_MODEL;
    public static InfernoArmorModel<LivingEntity> INFERNO_ARMOR_MODEL;
    public static TwistedArmorModel<LivingEntity> TWISTED_ARMOR_MODEL;
    public static SoulArmorModel<LivingEntity> SOUL_ARMOR_MODEL;
    public static HolyCloakModel<LivingEntity> HOLY_CLOAK_MODEL;

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlackDragonArmorModel.LAYER_LOCATION, BlackDragonArmorModel::createBodyLayer);
        event.registerLayerDefinition(InfernoArmorModel.LAYER_LOCATION, () -> InfernoArmorModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION));
        event.registerLayerDefinition(TwistedArmorModel.LAYER_LOCATION, () -> TwistedArmorModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION));
        event.registerLayerDefinition(SoulArmorModel.LAYER_LOCATION, () -> SoulArmorModel.createMesh(LayerDefinitions.OUTER_ARMOR_DEFORMATION));

        event.registerLayerDefinition(HolyCloakModel.LAYER_LOCATION, () -> HolyCloakModel.createMesh(new CubeDeformation(0.5F)));

    }

    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        BLACK_DRAGON_ARMOR_MODEL = new BlackDragonArmorModel<>(event.getContext().bakeLayer(BlackDragonArmorModel.LAYER_LOCATION));
        INFERNO_ARMOR_MODEL = new InfernoArmorModel<>(event.getContext().bakeLayer(InfernoArmorModel.LAYER_LOCATION));
        TWISTED_ARMOR_MODEL = new TwistedArmorModel<>(event.getContext().bakeLayer(TwistedArmorModel.LAYER_LOCATION));
        SOUL_ARMOR_MODEL = new SoulArmorModel<>(event.getContext().bakeLayer(SoulArmorModel.LAYER_LOCATION));
        HOLY_CLOAK_MODEL = new HolyCloakModel<>(event.getContext().bakeLayer(HolyCloakModel.LAYER_LOCATION));
    }
}
