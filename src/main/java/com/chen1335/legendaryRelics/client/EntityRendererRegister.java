package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.client.module.armor.BlackDragonHelmet;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class EntityRendererRegister {
    public static BlackDragonHelmet<LivingEntity> TEST_RENDER;

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BlackDragonHelmet.LAYER_LOCATION, BlackDragonHelmet::createBodyLayer);
    }

    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        TEST_RENDER = new BlackDragonHelmet<>(event.getContext().bakeLayer(BlackDragonHelmet.LAYER_LOCATION));
    }
}
